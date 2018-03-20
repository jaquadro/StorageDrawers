package net.minecraft.tileentity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity implements net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map < String, Class <? extends TileEntity >> nameToClassMap = Maps. < String, Class <? extends TileEntity >> newHashMap();
    private static final Map < Class <? extends TileEntity > , String > classToNameMap = Maps. < Class <? extends TileEntity > , String > newHashMap();
    /** the instance of the world the tile entity is in. */
    protected World world;
    protected BlockPos pos = BlockPos.ORIGIN;
    protected boolean tileEntityInvalid;
    private int blockMetadata = -1;
    /** the Block type that this TileEntity is contained within */
    protected Block blockType;

    /**
     * Adds a new two-way mapping between the class and its string name in both hashmaps.
     */
    public static void addMapping(Class <? extends TileEntity > cl, String id)
    {
        if (nameToClassMap.containsKey(id))
        {
            throw new IllegalArgumentException("Duplicate id: " + id);
        }
        else
        {
            nameToClassMap.put(id, cl);
            classToNameMap.put(cl, id);
        }
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorld()
    {
        return this.world;
    }

    /**
     * Sets the worldObj for this tileEntity.
     */
    public void setWorld(World worldIn)
    {
        this.world = worldIn;
    }

    /**
     * Returns true if the worldObj isn't null.
     */
    public boolean hasWorld()
    {
        return this.world != null;
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        this.pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
        if (compound.hasKey("ForgeData")) this.customTileData = compound.getCompoundTag("ForgeData");
        if (this.capabilities != null && compound.hasKey("ForgeCaps")) this.capabilities.deserializeNBT(compound.getCompoundTag("ForgeCaps"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        return this.writeInternal(compound);
    }

    private NBTTagCompound writeInternal(NBTTagCompound compound)
    {
        String s = (String)classToNameMap.get(this.getClass());

        if (s == null)
        {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        else
        {
            compound.setString("id", s);
            compound.setInteger("x", this.pos.getX());
            compound.setInteger("y", this.pos.getY());
            compound.setInteger("z", this.pos.getZ());
            if (this.customTileData != null) compound.setTag("ForgeData", this.customTileData);
            if (this.capabilities != null) compound.setTag("ForgeCaps", this.capabilities.serializeNBT());
            return compound;
        }
    }

    public static TileEntity create(World worldIn, NBTTagCompound compound)
    {
        TileEntity tileentity = null;
        String s = compound.getString("id");
        Class <? extends TileEntity > oclass = null;

        try
        {
            oclass = nameToClassMap.get(s);

            if (oclass != null)
            {
                tileentity = (TileEntity)oclass.newInstance();
            }
        }
        catch (Throwable throwable1)
        {
            LOGGER.error("Failed to create block entity {}", new Object[] {s, throwable1});
            net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, throwable1,
                    "A TileEntity %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                    s, oclass.getName());
        }

        if (tileentity != null)
        {
            try
            {
                tileentity.setWorldCreate(worldIn);
                tileentity.readFromNBT(compound);
            }
            catch (Throwable throwable)
            {
                LOGGER.error("Failed to load data for block entity {}", new Object[] {s, throwable});
                net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, throwable,
                        "A TileEntity %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                        s, oclass.getName());
                tileentity = null;
            }
        }
        else
        {
            LOGGER.warn("Skipping BlockEntity with id {}", new Object[] {s});
        }

        return tileentity;
    }

    protected void setWorldCreate(World worldIn)
    {
    }

    public int getBlockMetadata()
    {
        if (this.blockMetadata == -1)
        {
            IBlockState iblockstate = this.world.getBlockState(this.pos);
            this.blockMetadata = iblockstate.getBlock().getMetaFromState(iblockstate);
        }

        return this.blockMetadata;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        if (this.world != null)
        {
            IBlockState iblockstate = this.world.getBlockState(this.pos);
            this.blockMetadata = iblockstate.getBlock().getMetaFromState(iblockstate);
            this.world.markChunkDirty(this.pos, this);

            if (this.getBlockType() != Blocks.AIR)
            {
                this.world.updateComparatorOutputLevel(this.pos, this.getBlockType());
            }
        }
    }

    /**
     * Returns the square of the distance between this entity and the passed in coordinates.
     */
    public double getDistanceSq(double x, double y, double z)
    {
        double d0 = (double)this.pos.getX() + 0.5D - x;
        double d1 = (double)this.pos.getY() + 0.5D - y;
        double d2 = (double)this.pos.getZ() + 0.5D - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 4096.0D;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    /**
     * Gets the block type at the location of this entity (client-only).
     */
    public Block getBlockType()
    {
        if (this.blockType == null && this.world != null)
        {
            this.blockType = this.world.getBlockState(this.pos).getBlock();
        }

        return this.blockType;
    }

    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return null;
    }

    public NBTTagCompound getUpdateTag()
    {
        return this.writeInternal(new NBTTagCompound());
    }

    public boolean isInvalid()
    {
        return this.tileEntityInvalid;
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
        this.tileEntityInvalid = true;
    }

    /**
     * validates a tile entity
     */
    public void validate()
    {
        this.tileEntityInvalid = false;
    }

    public boolean receiveClientEvent(int id, int type)
    {
        return false;
    }

    public void updateContainingBlockInfo()
    {
        this.blockType = null;
        this.blockMetadata = -1;
    }

    public void addInfoToCrashReport(CrashReportCategory reportCategory)
    {
        reportCategory.setDetail("Name", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return (String)TileEntity.classToNameMap.get(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
            }
        });

        if (this.world != null)
        {
            CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.getBlockType(), this.getBlockMetadata());
            reportCategory.setDetail("Actual block type", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    int i = Block.getIdFromBlock(TileEntity.this.world.getBlockState(TileEntity.this.pos).getBlock());

                    try
                    {
                        return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(i), Block.getBlockById(i).getUnlocalizedName(), Block.getBlockById(i).getClass().getCanonicalName()});
                    }
                    catch (Throwable var3)
                    {
                        return "ID #" + i;
                    }
                }
            });
            reportCategory.setDetail("Actual block data value", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    IBlockState iblockstate = TileEntity.this.world.getBlockState(TileEntity.this.pos);
                    int i = iblockstate.getBlock().getMetaFromState(iblockstate);

                    if (i < 0)
                    {
                        return "Unknown? (Got " + i + ")";
                    }
                    else
                    {
                        String s = String.format("%4s", new Object[] {Integer.toBinaryString(i)}).replace(" ", "0");
                        return String.format("%1$d / 0x%1$X / 0b%2$s", new Object[] {Integer.valueOf(i), s});
                    }
                }
            });
        }
    }

    public void setPos(BlockPos posIn)
    {
        if (posIn instanceof BlockPos.MutableBlockPos || posIn instanceof BlockPos.PooledMutableBlockPos)
        {
            LOGGER.warn((String)"Tried to assign a mutable BlockPos to a block entity...", (Throwable)(new Error(posIn.getClass().toString())));
            posIn = new BlockPos(posIn);
        }

        this.pos = posIn;
    }

    public boolean onlyOpsCanSetNbt()
    {
        return false;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    @Nullable
    public ITextComponent getDisplayName()
    {
        return null;
    }

    public void rotate(Rotation p_189667_1_)
    {
    }

    public void mirror(Mirror p_189668_1_)
    {
    }

    // -- BEGIN FORGE PATCHES --
    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt)
    {
    }

    /**
     * Called when the chunk's TE update tag, gotten from {@link #getUpdateTag()}, is received on the client.
     * <p>
     * Used to handle this tag in a special way. By default this simply calls {@link #readFromNBT(NBTTagCompound)}.
     *
     * @param tag The {@link NBTTagCompound} sent from {@link #getUpdateTag()}
     */
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.readFromNBT(tag);
    }

    /**
     * Called when the chunk this TileEntity is on is Unloaded.
     */
    public void onChunkUnload()
    {
    }

    private boolean isVanilla = getClass().getName().startsWith("net.minecraft.");
    /**
     * Called from Chunk.setBlockIDWithMetadata and Chunk.fillChunk, determines if this tile entity should be re-created when the ID, or Metadata changes.
     * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
     *
     * @param world Current world
     * @param pos Tile's world position
     * @param oldState The old ID of the block
     * @param newState The new ID of the block (May be the same)
     * @return true forcing the invalidation of the existing TE, false not to invalidate the existing TE
     */
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return isVanilla ? (oldState.getBlock() != newSate.getBlock()) : oldState != newSate;
    }

    public boolean shouldRenderInPass(int pass)
    {
        return pass == 0;
    }

    /**
     * Sometimes default render bounding box: infinite in scope. Used to control rendering on {@link TileEntitySpecialRenderer}.
     */
    public static final net.minecraft.util.math.AxisAlignedBB INFINITE_EXTENT_AABB = new net.minecraft.util.math.AxisAlignedBB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    /**
     * Return an {@link AxisAlignedBB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link TileEntity}
     * Defaults to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block
     * at this location.
     *
     * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
     */
    @SideOnly(Side.CLIENT)
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox()
    {
        net.minecraft.util.math.AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        Block type = getBlockType();
        BlockPos pos = getPos();
        if (type == Blocks.ENCHANTING_TABLE)
        {
            bb = new net.minecraft.util.math.AxisAlignedBB(pos, pos.add(1, 1, 1));
        }
        else if (type == Blocks.CHEST || type == Blocks.TRAPPED_CHEST)
        {
            bb = new net.minecraft.util.math.AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
        }
        else if (type == Blocks.STRUCTURE_BLOCK)
        {
            bb = INFINITE_EXTENT_AABB;
        }
        else if (type != null && type != Blocks.BEACON)
        {
            net.minecraft.util.math.AxisAlignedBB cbb = null;
            try
            {
                cbb = world.getBlockState(getPos()).getCollisionBoundingBox(world, pos).addCoord(pos.getX(), pos.getY(), pos.getZ());
            }
            catch (Exception e)
            {
                // We have to capture any exceptions that may occur here because BUKKIT servers like to send
                // the tile entity data BEFORE the chunk data, you know, the OPPOSITE of what vanilla does!
                // So we can not GARENTEE that the world state is the real state for the block...
                // So, once again in the long line of US having to accommodate BUKKIT breaking things,
                // here it is, assume that the TE is only 1 cubic block. Problem with this is that it may
                // cause the TileEntity renderer to error further down the line! But alas, nothing we can do.
                cbb = new net.minecraft.util.math.AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(1, 1, 1));
            }
            if (cbb != null) bb = cbb;
        }
        return bb;
    }

    /**
     * Checks if this tile entity knows how to render its 'breaking' overlay effect.
     * If this returns true, The TileEntitySpecialRenderer will be called again with break progress set.
     * @return True to re-render tile with breaking effect.
     */
    public boolean canRenderBreaking()
    {
        Block block = this.getBlockType();
        return (block instanceof net.minecraft.block.BlockChest ||
                block instanceof net.minecraft.block.BlockEnderChest ||
                block instanceof net.minecraft.block.BlockSign ||
                block instanceof net.minecraft.block.BlockSkull);
    }

    private NBTTagCompound customTileData;

    /**
     * Gets a {@link NBTTagCompound} that can be used to store custom data for this tile entity.
     * It will be written, and read from disc, so it persists over world saves.
     *
     * @return A compound tag for custom data
     */
    public NBTTagCompound getTileData()
    {
        if (this.customTileData == null)
        {
            this.customTileData = new NBTTagCompound();
        }
        return this.customTileData;
    }

    /**
     * Determines if the player can overwrite the NBT data of this tile entity while they place it using a ItemStack.
     * Added as a fix for MC-75630 - Exploit with signs and command blocks
     * @return True to prevent NBT copy, false to allow.
     */
    public boolean restrictNBTCopy()
    {
        return this instanceof TileEntityCommandBlock ||
               this instanceof TileEntityMobSpawner ||
               this instanceof TileEntitySign;
    }


    /**
     * Called from the Chunk when this is first added to the world. Override instead of adding
     * if (firstTick) stuff in update. Happens after validate and after it has been placed into the Chunk tileEntity
     * map.
     */
    public void onLoad()
    {
        // NOOP
    }

    /**
     * If the TileEntitySpecialRenderer associated with this TileEntity can be batched in with another renderers, and won't access the GL state.
     * If TileEntity returns true, then TESR should have the same functionality as (and probably extend) the FastTESR class.
     */
    public boolean hasFastRenderer()
    {
        return false;
    }

    private net.minecraftforge.common.capabilities.CapabilityDispatcher capabilities;
    public TileEntity()
    {
        capabilities = net.minecraftforge.event.ForgeEventFactory.gatherCapabilities(this);
    }

    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
        return capabilities == null ? false : capabilities.hasCapability(capability, facing);
    }

    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        return capabilities == null ? null : capabilities.getCapability(capability, facing);
    }

    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.readFromNBT(nbt);
    }

    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        this.writeToNBT(ret);
        return ret;
    }

    static
    {
        addMapping(TileEntityFurnace.class, "Furnace");
        addMapping(TileEntityChest.class, "Chest");
        addMapping(TileEntityEnderChest.class, "EnderChest");
        addMapping(BlockJukebox.TileEntityJukebox.class, "RecordPlayer");
        addMapping(TileEntityDispenser.class, "Trap");
        addMapping(TileEntityDropper.class, "Dropper");
        addMapping(TileEntitySign.class, "Sign");
        addMapping(TileEntityMobSpawner.class, "MobSpawner");
        addMapping(TileEntityNote.class, "Music");
        addMapping(TileEntityPiston.class, "Piston");
        addMapping(TileEntityBrewingStand.class, "Cauldron");
        addMapping(TileEntityEnchantmentTable.class, "EnchantTable");
        addMapping(TileEntityEndPortal.class, "Airportal");
        addMapping(TileEntityBeacon.class, "Beacon");
        addMapping(TileEntitySkull.class, "Skull");
        addMapping(TileEntityDaylightDetector.class, "DLDetector");
        addMapping(TileEntityHopper.class, "Hopper");
        addMapping(TileEntityComparator.class, "Comparator");
        addMapping(TileEntityFlowerPot.class, "FlowerPot");
        addMapping(TileEntityBanner.class, "Banner");
        addMapping(TileEntityStructure.class, "Structure");
        addMapping(TileEntityEndGateway.class, "EndGateway");
        addMapping(TileEntityCommandBlock.class, "Control");
    }
}