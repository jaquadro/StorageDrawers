package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<ChunkPos, NBTTagCompound> chunksToRemove = new ConcurrentHashMap();
    private final Set<ChunkPos> pendingAnvilChunksCoordinates = Collections.<ChunkPos>newSetFromMap(new ConcurrentHashMap());
    /** Save directory for chunks using the Anvil format */
    public final File chunkSaveLocation;
    private final DataFixer dataFixer;
    private boolean savingExtraData;

    public AnvilChunkLoader(File chunkSaveLocationIn, DataFixer dataFixerIn)
    {
        this.chunkSaveLocation = chunkSaveLocationIn;
        this.dataFixer = dataFixerIn;
    }

    public boolean chunkExists(World world, int x, int z)
    {
        ChunkPos chunkcoordintpair = new ChunkPos(x, z);

        if (this.pendingAnvilChunksCoordinates.contains(chunkcoordintpair))
        {
            for(ChunkPos pendingChunkCoord : this.chunksToRemove.keySet())
            {
                if (pendingChunkCoord.equals(chunkcoordintpair))
                {
                    return true;
                }
            }
        }

        return RegionFileCache.createOrLoadRegionFile(this.chunkSaveLocation, x, z).chunkExists(x & 31, z & 31);
    }


    /**
     * Loads the specified(XZ) chunk into the specified world.
     */
    @Nullable
    public Chunk loadChunk(World worldIn, int x, int z) throws IOException
    {
        Object[] data = this.loadChunk__Async(worldIn, x, z);

        if (data != null)
        {
            Chunk chunk = (Chunk) data[0];
            NBTTagCompound nbttagcompound = (NBTTagCompound) data[1];
            this.loadEntities(worldIn, nbttagcompound.getCompoundTag("Level"), chunk);
            return chunk;
        }

        return null;
    }

    public Object[] loadChunk__Async(World worldIn, int x, int z) throws IOException
    {
        ChunkPos chunkpos = new ChunkPos(x, z);
        NBTTagCompound nbttagcompound = (NBTTagCompound)this.chunksToRemove.get(chunkpos);

        if (nbttagcompound == null)
        {
            DataInputStream datainputstream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, x, z);

            if (datainputstream == null)
            {
                return null;
            }

            nbttagcompound = this.dataFixer.process(FixTypes.CHUNK, CompressedStreamTools.read(datainputstream));
        }

        return this.checkedReadChunkFromNBT__Async(worldIn, x, z, nbttagcompound);
    }

    /**
     * Wraps readChunkFromNBT. Checks the coordinates and several NBT tags.
     */
    protected Chunk checkedReadChunkFromNBT(World worldIn, int x, int z, NBTTagCompound compound)
    {
        Object[] data = this.checkedReadChunkFromNBT__Async(worldIn, x, z, compound);
        return data != null ? (Chunk)data[0] : null;
    }

    protected Object[] checkedReadChunkFromNBT__Async(World worldIn, int x, int z, NBTTagCompound compound)
    {
        if (!compound.hasKey("Level", 10))
        {
            LOGGER.error("Chunk file at {},{} is missing level data, skipping", new Object[] {Integer.valueOf(x), Integer.valueOf(z)});
            return null;
        }
        else
        {
            NBTTagCompound nbttagcompound = compound.getCompoundTag("Level");

            if (!nbttagcompound.hasKey("Sections", 9))
            {
                LOGGER.error("Chunk file at {},{} is missing block data, skipping", new Object[] {Integer.valueOf(x), Integer.valueOf(z)});
                return null;
            }
            else
            {
                Chunk chunk = this.readChunkFromNBT(worldIn, nbttagcompound);

                if (!chunk.isAtLocation(x, z))
                {
                    LOGGER.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", new Object[] {Integer.valueOf(x), Integer.valueOf(z), Integer.valueOf(x), Integer.valueOf(z), Integer.valueOf(chunk.xPosition), Integer.valueOf(chunk.zPosition)});
                    nbttagcompound.setInteger("xPos", x);
                    nbttagcompound.setInteger("zPos", z);

                    // Have to move tile entities since we don't load them at this stage
                    NBTTagList _tileEntities = nbttagcompound.getTagList("TileEntities", 10);

                    if (_tileEntities != null)
                    {
                        for (int te = 0; te < _tileEntities.tagCount(); te++)
                        {
                            NBTTagCompound _nbt = (NBTTagCompound) _tileEntities.getCompoundTagAt(te);
                            _nbt.setInteger("x", x * 16 + (_nbt.getInteger("x") - chunk.xPosition * 16));
                            _nbt.setInteger("z", z * 16 + (_nbt.getInteger("z") - chunk.zPosition * 16));
                        }
                    }

                    chunk = this.readChunkFromNBT(worldIn, nbttagcompound);
                }

                Object[] data = new Object[2];
                data[0] = chunk;
                data[1] = compound;
                // event is fired in ChunkIOProvider.callStage2 since it must be fired after TE's load.
                // MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(chunk, par4NBTTagCompound));
                return data;
            }
        }
    }

    public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException
    {
        worldIn.checkSessionLock();

        try
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound.setTag("Level", nbttagcompound1);
            nbttagcompound.setInteger("DataVersion", 512);
            this.writeChunkToNBT(chunkIn, worldIn, nbttagcompound1);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Save(chunkIn, nbttagcompound));
            this.addChunkToPending(chunkIn.getChunkCoordIntPair(), nbttagcompound);
        }
        catch (Exception exception)
        {
            LOGGER.error((String)"Failed to save chunk", (Throwable)exception);
        }
    }

    protected void addChunkToPending(ChunkPos pos, NBTTagCompound compound)
    {
        if (!this.pendingAnvilChunksCoordinates.contains(pos))
        {
            this.chunksToRemove.put(pos, compound);
        }

        ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
    }

    /**
     * Returns a boolean stating if the write was unsuccessful.
     */
    public boolean writeNextIO()
    {
        if (this.chunksToRemove.isEmpty())
        {
            if (this.savingExtraData)
            {
                LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[] {this.chunkSaveLocation.getName()});
            }

            return false;
        }
        else
        {
            ChunkPos chunkpos = (ChunkPos)this.chunksToRemove.keySet().iterator().next();
            boolean lvt_3_1_;

            try
            {
                this.pendingAnvilChunksCoordinates.add(chunkpos);
                NBTTagCompound nbttagcompound = (NBTTagCompound)this.chunksToRemove.remove(chunkpos);

                if (nbttagcompound != null)
                {
                    try
                    {
                        this.writeChunkData(chunkpos, nbttagcompound);
                    }
                    catch (Exception exception)
                    {
                        LOGGER.error((String)"Failed to save chunk", (Throwable)exception);
                    }
                }

                lvt_3_1_ = true;
            }
            finally
            {
                this.pendingAnvilChunksCoordinates.remove(chunkpos);
            }

            return lvt_3_1_;
        }
    }

    private void writeChunkData(ChunkPos pos, NBTTagCompound compound) throws IOException
    {
        DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, pos.chunkXPos, pos.chunkZPos);
        CompressedStreamTools.write(compound, dataoutputstream);
        dataoutputstream.close();
    }

    /**
     * Save extra data associated with this Chunk not normally saved during autosave, only during chunk unload.
     * Currently unused.
     */
    public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException
    {
    }

    /**
     * Called every World.tick()
     */
    public void chunkTick()
    {
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unused.
     */
    public void saveExtraData()
    {
        try
        {
            this.savingExtraData = true;

            while (this.writeNextIO());
        }
        finally
        {
            this.savingExtraData = false;
        }
    }

    public static void registerFixes(DataFixer fixer)
    {
        fixer.registerWalker(FixTypes.CHUNK, new IDataWalker()
        {
            public NBTTagCompound process(IDataFixer fixer, NBTTagCompound compound, int versionIn)
            {
                if (compound.hasKey("Level", 10))
                {
                    NBTTagCompound nbttagcompound = compound.getCompoundTag("Level");

                    if (nbttagcompound.hasKey("Entities", 9))
                    {
                        NBTTagList nbttaglist = nbttagcompound.getTagList("Entities", 10);

                        for (int i = 0; i < nbttaglist.tagCount(); ++i)
                        {
                            nbttaglist.set(i, fixer.process(FixTypes.ENTITY, (NBTTagCompound)nbttaglist.get(i), versionIn));
                        }
                    }

                    if (nbttagcompound.hasKey("TileEntities", 9))
                    {
                        NBTTagList nbttaglist1 = nbttagcompound.getTagList("TileEntities", 10);

                        for (int j = 0; j < nbttaglist1.tagCount(); ++j)
                        {
                            nbttaglist1.set(j, fixer.process(FixTypes.BLOCK_ENTITY, (NBTTagCompound)nbttaglist1.get(j), versionIn));
                        }
                    }
                }

                return compound;
            }
        });
    }

    /**
     * Writes the Chunk passed as an argument to the NBTTagCompound also passed, using the World argument to retrieve
     * the Chunk's last update time.
     */
    private void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound compound)
    {
        compound.setInteger("xPos", chunkIn.xPosition);
        compound.setInteger("zPos", chunkIn.zPosition);
        compound.setLong("LastUpdate", worldIn.getTotalWorldTime());
        compound.setIntArray("HeightMap", chunkIn.getHeightMap());
        compound.setBoolean("TerrainPopulated", chunkIn.isTerrainPopulated());
        compound.setBoolean("LightPopulated", chunkIn.isLightPopulated());
        compound.setLong("InhabitedTime", chunkIn.getInhabitedTime());
        ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getBlockStorageArray();
        NBTTagList nbttaglist = new NBTTagList();
        boolean flag = !worldIn.provider.hasNoSky();

        for (ExtendedBlockStorage extendedblockstorage : aextendedblockstorage)
        {
            if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Y", (byte)(extendedblockstorage.getYLocation() >> 4 & 255));
                byte[] abyte = new byte[4096];
                NibbleArray nibblearray = new NibbleArray();
                NibbleArray nibblearray1 = extendedblockstorage.getData().getDataForNBT(abyte, nibblearray);
                nbttagcompound.setByteArray("Blocks", abyte);
                nbttagcompound.setByteArray("Data", nibblearray.getData());

                if (nibblearray1 != null)
                {
                    nbttagcompound.setByteArray("Add", nibblearray1.getData());
                }

                nbttagcompound.setByteArray("BlockLight", extendedblockstorage.getBlocklightArray().getData());

                if (flag)
                {
                    nbttagcompound.setByteArray("SkyLight", extendedblockstorage.getSkylightArray().getData());
                }
                else
                {
                    nbttagcompound.setByteArray("SkyLight", new byte[extendedblockstorage.getBlocklightArray().getData().length]);
                }

                nbttaglist.appendTag(nbttagcompound);
            }
        }

        compound.setTag("Sections", nbttaglist);
        compound.setByteArray("Biomes", chunkIn.getBiomeArray());
        chunkIn.setHasEntities(false);
        NBTTagList nbttaglist1 = new NBTTagList();

        for (int i = 0; i < chunkIn.getEntityLists().length; ++i)
        {
            for (Entity entity : chunkIn.getEntityLists()[i])
            {
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                if (entity.writeToNBTOptional(nbttagcompound2))
                {
                    try
                    {
                    chunkIn.setHasEntities(true);
                    nbttaglist1.appendTag(nbttagcompound2);
                    }
                    catch (Exception e)
                    {
                        net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, e,
                                "An Entity type %s has thrown an exception trying to write state. It will not persist. Report this to the mod author",
                                entity.getClass().getName());
                    }
                }
            }
        }

        compound.setTag("Entities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();

        for (TileEntity tileentity : chunkIn.getTileEntityMap().values())
        {
            try
            {
            NBTTagCompound nbttagcompound3 = tileentity.writeToNBT(new NBTTagCompound());
            nbttaglist2.appendTag(nbttagcompound3);
            }
            catch (Exception e)
            {
                net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, e,
                        "A TileEntity type %s has throw an exception trying to write state. It will not persist. Report this to the mod author",
                        tileentity.getClass().getName());
            }
        }

        compound.setTag("TileEntities", nbttaglist2);
        List<NextTickListEntry> list = worldIn.getPendingBlockUpdates(chunkIn, false);

        if (list != null)
        {
            long j = worldIn.getTotalWorldTime();
            NBTTagList nbttaglist3 = new NBTTagList();

            for (NextTickListEntry nextticklistentry : list)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(nextticklistentry.getBlock());
                nbttagcompound1.setString("i", resourcelocation == null ? "" : resourcelocation.toString());
                nbttagcompound1.setInteger("x", nextticklistentry.position.getX());
                nbttagcompound1.setInteger("y", nextticklistentry.position.getY());
                nbttagcompound1.setInteger("z", nextticklistentry.position.getZ());
                nbttagcompound1.setInteger("t", (int)(nextticklistentry.scheduledTime - j));
                nbttagcompound1.setInteger("p", nextticklistentry.priority);
                nbttaglist3.appendTag(nbttagcompound1);
            }

            compound.setTag("TileTicks", nbttaglist3);
        }
    }

    /**
     * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World.
     * Returns the created Chunk.
     */
    private Chunk readChunkFromNBT(World worldIn, NBTTagCompound compound)
    {
        int i = compound.getInteger("xPos");
        int j = compound.getInteger("zPos");
        Chunk chunk = new Chunk(worldIn, i, j);
        chunk.setHeightMap(compound.getIntArray("HeightMap"));
        chunk.setTerrainPopulated(compound.getBoolean("TerrainPopulated"));
        chunk.setLightPopulated(compound.getBoolean("LightPopulated"));
        chunk.setInhabitedTime(compound.getLong("InhabitedTime"));
        NBTTagList nbttaglist = compound.getTagList("Sections", 10);
        int k = 16;
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[16];
        boolean flag = !worldIn.provider.hasNoSky();

        for (int l = 0; l < nbttaglist.tagCount(); ++l)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(l);
            int i1 = nbttagcompound.getByte("Y");
            ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(i1 << 4, flag);
            byte[] abyte = nbttagcompound.getByteArray("Blocks");
            NibbleArray nibblearray = new NibbleArray(nbttagcompound.getByteArray("Data"));
            NibbleArray nibblearray1 = nbttagcompound.hasKey("Add", 7) ? new NibbleArray(nbttagcompound.getByteArray("Add")) : null;
            extendedblockstorage.getData().setDataFromNBT(abyte, nibblearray, nibblearray1);
            extendedblockstorage.setBlocklightArray(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));

            if (flag)
            {
                extendedblockstorage.setSkylightArray(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
            }

            extendedblockstorage.removeInvalidBlocks();
            aextendedblockstorage[i1] = extendedblockstorage;
        }

        chunk.setStorageArrays(aextendedblockstorage);

        if (compound.hasKey("Biomes", 7))
        {
            chunk.setBiomeArray(compound.getByteArray("Biomes"));
        }

        // End this method here and split off entity loading to another method
        return chunk;
    }

    public void loadEntities(World worldIn, NBTTagCompound compound, Chunk chunk)
    {
        NBTTagList nbttaglist1 = compound.getTagList("Entities", 10);

        if (nbttaglist1 != null)
        {
            for (int j1 = 0; j1 < nbttaglist1.tagCount(); ++j1)
            {
                NBTTagCompound nbttagcompound1 = nbttaglist1.getCompoundTagAt(j1);
                readChunkEntity(nbttagcompound1, worldIn, chunk);
                chunk.setHasEntities(true);
            }
        }

        NBTTagList nbttaglist2 = compound.getTagList("TileEntities", 10);

        if (nbttaglist2 != null)
        {
            for (int k1 = 0; k1 < nbttaglist2.tagCount(); ++k1)
            {
                NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(k1);
                TileEntity tileentity = TileEntity.create(worldIn, nbttagcompound2);

                if (tileentity != null)
                {
                    chunk.addTileEntity(tileentity);
                }
            }
        }

        if (compound.hasKey("TileTicks", 9))
        {
            NBTTagList nbttaglist3 = compound.getTagList("TileTicks", 10);

            if (nbttaglist3 != null)
            {
                for (int l1 = 0; l1 < nbttaglist3.tagCount(); ++l1)
                {
                    NBTTagCompound nbttagcompound3 = nbttaglist3.getCompoundTagAt(l1);
                    Block block;

                    if (nbttagcompound3.hasKey("i", 8))
                    {
                        block = Block.getBlockFromName(nbttagcompound3.getString("i"));
                    }
                    else
                    {
                        block = Block.getBlockById(nbttagcompound3.getInteger("i"));
                    }

                    worldIn.scheduleBlockUpdate(new BlockPos(nbttagcompound3.getInteger("x"), nbttagcompound3.getInteger("y"), nbttagcompound3.getInteger("z")), block, nbttagcompound3.getInteger("t"), nbttagcompound3.getInteger("p"));
                }
            }
        }
    }

    @Nullable
    public static Entity readChunkEntity(NBTTagCompound compound, World worldIn, Chunk chunkIn)
    {
        Entity entity = createEntityFromNBT(compound, worldIn);

        if (entity == null)
        {
            return null;
        }
        else
        {
            chunkIn.addEntity(entity);

            if (compound.hasKey("Passengers", 9))
            {
                NBTTagList nbttaglist = compound.getTagList("Passengers", 10);

                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    Entity entity1 = readChunkEntity(nbttaglist.getCompoundTagAt(i), worldIn, chunkIn);

                    if (entity1 != null)
                    {
                        entity1.startRiding(entity, true);
                    }
                }
            }

            return entity;
        }
    }

    @Nullable
    public static Entity readWorldEntityPos(NBTTagCompound compound, World worldIn, double x, double y, double z, boolean attemptSpawn)
    {
        Entity entity = createEntityFromNBT(compound, worldIn);

        if (entity == null)
        {
            return null;
        }
        else
        {
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);

            if (attemptSpawn && !worldIn.spawnEntity(entity))
            {
                return null;
            }
            else
            {
                if (compound.hasKey("Passengers", 9))
                {
                    NBTTagList nbttaglist = compound.getTagList("Passengers", 10);

                    for (int i = 0; i < nbttaglist.tagCount(); ++i)
                    {
                        Entity entity1 = readWorldEntityPos(nbttaglist.getCompoundTagAt(i), worldIn, x, y, z, attemptSpawn);

                        if (entity1 != null)
                        {
                            entity1.startRiding(entity, true);
                        }
                    }
                }

                return entity;
            }
        }
    }

    @Nullable
    protected static Entity createEntityFromNBT(NBTTagCompound compound, World worldIn)
    {
        try
        {
            return EntityList.createEntityFromNBT(compound, worldIn);
        }
        catch (RuntimeException var3)
        {
            return null;
        }
    }

    public static void spawnEntity(Entity entityIn, World worldIn)
    {
        if (worldIn.spawnEntity(entityIn) && entityIn.isBeingRidden())
        {
            for (Entity entity : entityIn.getPassengers())
            {
                spawnEntity(entity, worldIn);
            }
        }
    }

    @Nullable
    public static Entity readWorldEntity(NBTTagCompound compound, World worldIn, boolean p_186051_2_)
    {
        Entity entity = createEntityFromNBT(compound, worldIn);

        if (entity == null)
        {
            return null;
        }
        else if (p_186051_2_ && !worldIn.spawnEntity(entity))
        {
            return null;
        }
        else
        {
            if (compound.hasKey("Passengers", 9))
            {
                NBTTagList nbttaglist = compound.getTagList("Passengers", 10);

                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    Entity entity1 = readWorldEntity(nbttaglist.getCompoundTagAt(i), worldIn, p_186051_2_);

                    if (entity1 != null)
                    {
                        entity1.startRiding(entity, true);
                    }
                }
            }

            return entity;
        }
    }
}