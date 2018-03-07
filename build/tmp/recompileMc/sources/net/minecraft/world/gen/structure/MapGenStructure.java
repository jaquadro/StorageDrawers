package net.minecraft.world.gen.structure;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public abstract class MapGenStructure extends MapGenBase
{
    private MapGenStructureData structureData;
    /**
     * Used to store a list of all structures that have been recursively generated. Used so that during recursive
     * generation, the structure generator can avoid generating structures that intersect ones that have already been
     * placed.
     */
    protected Long2ObjectMap<StructureStart> structureMap = new Long2ObjectOpenHashMap(1024);

    public abstract String getStructureName();

    /**
     * Recursively called by generate()
     */
    protected final synchronized void recursiveGenerate(World worldIn, final int chunkX, final int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn)
    {
        this.initializeStructureData(worldIn);

        if (!this.structureMap.containsKey(ChunkPos.asLong(chunkX, chunkZ)))
        {
            this.rand.nextInt();

            try
            {
                if (this.canSpawnStructureAtCoords(chunkX, chunkZ))
                {
                    StructureStart structurestart = this.getStructureStart(chunkX, chunkZ);
                    this.structureMap.put(ChunkPos.asLong(chunkX, chunkZ), structurestart);

                    if (structurestart.isSizeableStructure())
                    {
                        this.setStructureStart(chunkX, chunkZ, structurestart);
                    }
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception preparing structure feature");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Feature being prepared");
                crashreportcategory.setDetail("Is feature chunk", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return MapGenStructure.this.canSpawnStructureAtCoords(chunkX, chunkZ) ? "True" : "False";
                    }
                });
                crashreportcategory.addCrashSection("Chunk location", String.format("%d,%d", new Object[] {Integer.valueOf(chunkX), Integer.valueOf(chunkZ)}));
                crashreportcategory.setDetail("Chunk pos hash", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return String.valueOf(ChunkPos.asLong(chunkX, chunkZ));
                    }
                });
                crashreportcategory.setDetail("Structure type", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return MapGenStructure.this.getClass().getCanonicalName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    public synchronized boolean generateStructure(World worldIn, Random randomIn, ChunkPos chunkCoord)
    {
        this.initializeStructureData(worldIn);
        int i = (chunkCoord.chunkXPos << 4) + 8;
        int j = (chunkCoord.chunkZPos << 4) + 8;
        boolean flag = false;

        for (StructureStart structurestart : this.structureMap.values())
        {
            if (structurestart.isSizeableStructure() && structurestart.isValidForPostProcess(chunkCoord) && structurestart.getBoundingBox().intersectsWith(i, j, i + 15, j + 15))
            {
                structurestart.generateStructure(worldIn, randomIn, new StructureBoundingBox(i, j, i + 15, j + 15));
                structurestart.notifyPostProcessAt(chunkCoord);
                flag = true;
                this.setStructureStart(structurestart.getChunkPosX(), structurestart.getChunkPosZ(), structurestart);
            }
        }

        return flag;
    }

    public boolean isInsideStructure(BlockPos pos)
    {
        this.initializeStructureData(this.world);
        return this.getStructureAt(pos) != null;
    }

    protected StructureStart getStructureAt(BlockPos pos)
    {
        label24:

        for (StructureStart structurestart : this.structureMap.values())
        {
            if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().isVecInside(pos))
            {
                Iterator<StructureComponent> iterator = structurestart.getComponents().iterator();

                while (true)
                {
                    if (!iterator.hasNext())
                    {
                        continue label24;
                    }

                    StructureComponent structurecomponent = (StructureComponent)iterator.next();

                    if (structurecomponent.getBoundingBox().isVecInside(pos))
                    {
                        break;
                    }
                }

                return structurestart;
            }
        }

        return null;
    }

    public boolean isPositionInStructure(World worldIn, BlockPos pos)
    {
        this.initializeStructureData(worldIn);

        for (StructureStart structurestart : this.structureMap.values())
        {
            if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().isVecInside(pos))
            {
                return true;
            }
        }

        return false;
    }

    public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos)
    {
        this.world = worldIn;
        this.initializeStructureData(worldIn);
        this.rand.setSeed(worldIn.getSeed());
        long i = this.rand.nextLong();
        long j = this.rand.nextLong();
        long k = (long)(pos.getX() >> 4) * i;
        long l = (long)(pos.getZ() >> 4) * j;
        this.rand.setSeed(k ^ l ^ worldIn.getSeed());
        this.recursiveGenerate(worldIn, pos.getX() >> 4, pos.getZ() >> 4, 0, 0, (ChunkPrimer)null);
        double d0 = Double.MAX_VALUE;
        BlockPos blockpos = null;

        for (StructureStart structurestart : this.structureMap.values())
        {
            if (structurestart.isSizeableStructure())
            {
                StructureComponent structurecomponent = (StructureComponent)structurestart.getComponents().get(0);
                BlockPos blockpos1 = structurecomponent.getBoundingBoxCenter();
                double d1 = blockpos1.distanceSq(pos);

                if (d1 < d0)
                {
                    d0 = d1;
                    blockpos = blockpos1;
                }
            }
        }

        if (blockpos != null)
        {
            return blockpos;
        }
        else
        {
            List<BlockPos> list = this.getCoordList();

            if (list != null)
            {
                BlockPos blockpos2 = null;

                for (BlockPos blockpos3 : list)
                {
                    double d2 = blockpos3.distanceSq(pos);

                    if (d2 < d0)
                    {
                        d0 = d2;
                        blockpos2 = blockpos3;
                    }
                }

                return blockpos2;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Returns a list of other locations at which the structure generation has been run, or null if not relevant to this
     * structure generator.
     */
    protected List<BlockPos> getCoordList()
    {
        return null;
    }

    protected void initializeStructureData(World worldIn)
    {
        if (this.structureData == null)
        {
            this.structureData = (MapGenStructureData)worldIn.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, this.getStructureName());

            if (this.structureData == null)
            {
                this.structureData = new MapGenStructureData(this.getStructureName());
                worldIn.getPerWorldStorage().setData(this.getStructureName(), this.structureData);
            }
            else
            {
                NBTTagCompound nbttagcompound = this.structureData.getTagCompound();

                for (String s : nbttagcompound.getKeySet())
                {
                    NBTBase nbtbase = nbttagcompound.getTag(s);

                    if (nbtbase.getId() == 10)
                    {
                        NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbtbase;

                        if (nbttagcompound1.hasKey("ChunkX") && nbttagcompound1.hasKey("ChunkZ"))
                        {
                            int i = nbttagcompound1.getInteger("ChunkX");
                            int j = nbttagcompound1.getInteger("ChunkZ");
                            StructureStart structurestart = MapGenStructureIO.getStructureStart(nbttagcompound1, worldIn);

                            if (structurestart != null)
                            {
                                this.structureMap.put(ChunkPos.asLong(i, j), structurestart);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setStructureStart(int chunkX, int chunkZ, StructureStart start)
    {
        this.structureData.writeInstance(start.writeStructureComponentsToNBT(chunkX, chunkZ), chunkX, chunkZ);
        this.structureData.markDirty();
    }

    protected abstract boolean canSpawnStructureAtCoords(int chunkX, int chunkZ);

    protected abstract StructureStart getStructureStart(int chunkX, int chunkZ);
}