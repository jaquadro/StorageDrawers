package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.chameleon.block.properties.UnlistedModelData;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.DrawerStateModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.config.PlayerConfigSetting;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerInventoryHelper;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import com.jaquadro.minecraft.storagedrawers.item.*;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BlockDrawers extends BlockContainer implements INetworked
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public static final IUnlistedProperty<DrawerStateModelData> STATE_MODEL = UnlistedModelData.create(DrawerStateModelData.class);

    private static final AxisAlignedBB AABB_NORTH_HALF = new AxisAlignedBB(0, 0, .5, 1, 1, 1);
    private static final AxisAlignedBB AABB_SOUTH_HALF = new AxisAlignedBB(0, 0, 0, 1, 1, .5);
    private static final AxisAlignedBB AABB_WEST_HALF = new AxisAlignedBB(.5, 0, 0, 1, 1, 1);
    private static final AxisAlignedBB AABB_EAST_HALF = new AxisAlignedBB(0, 0, 0, .5, 1, 1);

    @SideOnly(Side.CLIENT)
    private StatusModelData[] statusInfo;

    private long ignoreEventTime;

    private static final ThreadLocal<Boolean> inTileLookup = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue () {
            return false;
        }
    };

    public BlockDrawers (String registryName, String blockName) {
        this(Material.WOOD, registryName, blockName);
    }

    protected BlockDrawers (Material material, String registryName, String blockName) {
        super(material);

        this.useNeighborBrightness = true;

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setSoundType(SoundType.WOOD);
        setUnlocalizedName(blockName);
        setRegistryName(registryName);
        setLightOpacity(255);

        initDefaultState();
    }

    protected void initDefaultState () {
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    public boolean retrimBlock (World world, BlockPos pos, ItemStack prototype) {
        return false;
    }

    public BlockType retrimType () {
        return BlockType.Drawers;
    }

    // TODO: ABSTRACT?
    public int getDrawerCount (IBlockState state) {
        return 0;
    }

    public boolean isHalfDepth (IBlockState state) {
        return false;
    }

    public EnumFacing getDirection (IBlockAccess blockAccess, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(blockAccess, pos);
        return (tile != null) ? EnumFacing.getFront(tile.getDirection()) : EnumFacing.NORTH;
    }

    @SideOnly(Side.CLIENT)
    public void initDynamic () { }

    @SideOnly(Side.CLIENT)
    public StatusModelData getStatusInfo (IBlockState state) {
        return null;
    }

    @Override
    public EnumBlockRenderType getRenderType (IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer () {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(blockAccess, pos);
        if (tile != null && isHalfDepth(state)) {
            switch (EnumFacing.getFront(tile.getDirection())) {
                case NORTH:
                    return AABB_NORTH_HALF;
                case SOUTH:
                    return AABB_SOUTH_HALF;
                case WEST:
                    return AABB_WEST_HALF;
                case EAST:
                    return AABB_EAST_HALF;
            }
        }

        return FULL_BLOCK_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList (IBlockState state, World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> list, @Nullable Entity entityIn, boolean p_185477_7_) {
        addCollisionBoxToList(pos, aabb, list, getBoundingBox(state, world, pos));
    }

    @Override
    public void onBlockAdded (World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            IBlockState blockNorth = world.getBlockState(pos.north());
            IBlockState blockSouth = world.getBlockState(pos.south());
            IBlockState blockWest = world.getBlockState(pos.west());
            IBlockState blockEast = world.getBlockState(pos.east());

            EnumFacing facing = state.getValue(FACING);

            if (facing == EnumFacing.NORTH && blockNorth.isFullBlock() && !blockSouth.isFullBlock())
                facing = EnumFacing.SOUTH;
            if (facing == EnumFacing.SOUTH && blockSouth.isFullBlock() && !blockNorth.isFullBlock())
                facing = EnumFacing.NORTH;
            if (facing == EnumFacing.WEST && blockWest.isFullBlock() && !blockEast.isFullBlock())
                facing = EnumFacing.EAST;
            if (facing == EnumFacing.EAST && blockEast.isFullBlock() && !blockWest.isFullBlock())
                facing = EnumFacing.WEST;

            TileEntityDrawers tile = getTileEntitySafe(world, pos);
            tile.setDirection(facing.ordinal());
            tile.markDirty();

            world.setBlockState(pos, state.withProperty(FACING, facing));
        }

        super.onBlockAdded(world, pos, state);
    }

    @Override
    public void onBlockPlacedBy (World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
        EnumFacing facing = entity.getHorizontalFacing().getOpposite();

        TileEntityDrawers tile = getTileEntitySafe(world, pos);
        tile.setDirection(facing.ordinal());
        tile.markDirty();

        if (itemStack.hasDisplayName())
            tile.setInventoryName(itemStack.getDisplayName());

        world.setBlockState(pos, state.withProperty(FACING, facing), 3);
    }

    @Override
    public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack item = player.getHeldItem(hand);
        if (hand == EnumHand.OFF_HAND)
            return false;

        if (world.isRemote && Minecraft.getSystemTime() == ignoreEventTime) {
            ignoreEventTime = 0;
            return false;
        }

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, pos);

        if (!SecurityManager.hasAccess(player.getGameProfile(), tileDrawers))
            return false;

        if (StorageDrawers.config.cache.debugTrace) {
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "BlockDrawers.onBlockActivated");
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item.isEmpty()) ? "  null item" : "  " + item.toString());
        }

        if (!item.isEmpty()) {
            if (item.getItem() instanceof ItemTrim && player.isSneaking()) {
                if (!retrimBlock(world, pos, item))
                    return false;

                if (!player.capabilities.isCreativeMode) {
                    item.shrink(1);
                    if (item.getCount() <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }

                return true;
            }
            else if (item.getItem() == ModItems.upgradeStorage || item.getItem() == ModItems.upgradeStatus || item.getItem() == ModItems.upgradeVoid ||
               item.getItem() == ModItems.upgradeCreative || item.getItem() == ModItems.upgradeRedstone) {
                if (!tileDrawers.addUpgrade(item)) {
                    if (!world.isRemote)
                        player.sendStatusMessage(new TextComponentTranslation("storagedrawers.msg.maxUpgrades"), true);

                    return false;
                }

                world.notifyBlockUpdate(pos, state, state, 3);

                if (!player.capabilities.isCreativeMode) {
                    item.shrink(1);
                    if (item.getCount() <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }

                return true;
            }
            else if (item.getItem() == ModItems.drawerKey) {
                boolean locked = tileDrawers.isItemLocked(LockAttribute.LOCK_POPULATED);
                tileDrawers.setItemLocked(LockAttribute.LOCK_POPULATED, !locked);
                tileDrawers.setItemLocked(LockAttribute.LOCK_EMPTY, !locked);

                return true;
            }
            else if (item.getItem() == ModItems.shroudKey) {
                tileDrawers.setIsShrouded(!tileDrawers.isShrouded());
                return true;
            }
            else if (item.getItem() == ModItems.quantifyKey) {
                tileDrawers.setIsShowingQuantity(!tileDrawers.isShowingQuantity());
                return true;
            }
            else if (item.getItem() instanceof ItemPersonalKey) {
                String securityKey = ((ItemPersonalKey) item.getItem()).getSecurityProviderKey(item.getItemDamage());
                ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);

                if (tileDrawers.getOwner() == null) {
                    tileDrawers.setOwner(player.getPersistentID());
                    tileDrawers.setSecurityProvider(provider);
                }
                else if (SecurityManager.hasOwnership(player.getGameProfile(), tileDrawers)) {
                    tileDrawers.setOwner(null);
                    tileDrawers.setSecurityProvider(null);
                }
                else
                    return false;
                return true;
            }
            else if (item.getItem() == ModItems.tape)
                return false;
        }
        else if (item.isEmpty() && player.isSneaking()) {
            if (tileDrawers.isSealed()) {
                tileDrawers.setIsSealed(false);
                return true;
            }
            else if (StorageDrawers.config.cache.enableDrawerUI) {
                player.openGui(StorageDrawers.instance, GuiHandler.drawersGuiID, world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }

        if (tileDrawers.getDirection() != side.ordinal())
            return false;

        if (tileDrawers.isSealed())
            return false;

        int slot = getDrawerSlot(getDrawerCount(state), side.ordinal(), hitX, hitY, hitZ);
        tileDrawers.interactPutItemsIntoSlot(slot, player);

        if (item.isEmpty())
            player.setHeldItem(hand, ItemStack.EMPTY);

        return true;
    }

    protected int getDrawerSlot (int drawerCount, int side, float hitX, float hitY, float hitZ) {
        return 0;
    }

    protected boolean hitTop (float hitY) {
        return hitY > .5;
    }

    protected boolean hitLeft (int side, float hitX, float hitZ) {
        switch (side) {
            case 2:
                return hitX > .5;
            case 3:
                return hitX < .5;
            case 4:
                return hitZ < .5;
            case 5:
                return hitZ > .5;
            default:
                return true;
        }
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (worldIn.isRemote) {
            return;
        }

        if (StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "onBlockClicked");

        RayTraceResult rayResult = net.minecraftforge.common.ForgeHooks.rayTraceEyes(playerIn, ((EntityPlayerMP) playerIn).interactionManager.getBlockReachDistance() + 1);
        if (rayResult == null)
            return;

        EnumFacing side = rayResult.sideHit;

        // adjust hitVec for drawers
        float hitX = (float)(rayResult.hitVec.xCoord - pos.getX());
        float hitY = (float)(rayResult.hitVec.yCoord - pos.getY());
        float hitZ = (float)(rayResult.hitVec.zCoord - pos.getZ());

        TileEntityDrawers tileDrawers = getTileEntitySafe(worldIn, pos);
        if (tileDrawers.getDirection() != side.ordinal())
            return;
    
        if (tileDrawers.isSealed())
            return;
    
        if (!SecurityManager.hasAccess(playerIn.getGameProfile(), tileDrawers))
            return;

        int slot = getDrawerSlot(getDrawerCount(worldIn.getBlockState(pos)), side.ordinal(), hitX, hitY, hitZ);
        IDrawer drawer = tileDrawers.getDrawer(slot);

        ItemStack item;
        Map<String, PlayerConfigSetting<?>> configSettings = ConfigManager.serverPlayerConfigSettings.get(playerIn.getUniqueID());
        boolean invertShift = false;
        if (configSettings != null) {
            PlayerConfigSetting<Boolean> setting = (PlayerConfigSetting<Boolean>) configSettings.get("invertShift");
            if (setting != null) {
                invertShift = setting.value;
            }
        }
        if (playerIn.isSneaking() != invertShift)
            item = tileDrawers.takeItemsFromSlot(slot, drawer.getStoredItemStackSize());
        else
            item = tileDrawers.takeItemsFromSlot(slot, 1);

        if (StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item.isEmpty()) ? "  null item" : "  " + item.toString());

        IBlockState state = worldIn.getBlockState(pos);
        if (!item.isEmpty()) {
            if (!playerIn.inventory.addItemStackToInventory(item)) {
                dropItemStack(worldIn, pos.offset(side), playerIn, item);
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            }
            else
                worldIn.playSound(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, .2f, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * .7f + 1) * 2);
        }
    }

    @Override
    public boolean rotateBlock (World world, BlockPos pos, EnumFacing axis) {
        TileEntityDrawers tile = getTileEntitySafe(world, pos);
        if (tile.isSealed()) {
            dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
            world.setBlockToAir(pos);
            return true;
        }

        boolean result = super.rotateBlock(world, pos, axis);
        if (result)
            tile.setDirection(world.getBlockState(pos).getValue(FACING).getIndex());

        return result;
    }

    @Override
    public boolean isSideSolid (IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return true;

        if (isHalfDepth(state))
            return side.getOpposite().ordinal() == tile.getDirection();

        if (side == EnumFacing.DOWN) {
            Block blockUnder = world.getBlockState(pos.down()).getBlock();
            if (blockUnder instanceof BlockChest || blockUnder instanceof BlockEnderChest)
                return false;
        }

        return side.ordinal() != tile.getDirection();
    }

    private void dropItemStack (World world, BlockPos pos, EntityPlayer player, @Nonnull ItemStack stack) {
        EntityItem entity = new EntityItem(world, pos.getX() + .5f, pos.getY() + .1f, pos.getZ() + .5f, stack);
        entity.addVelocity(-entity.motionX, -entity.motionY, -entity.motionZ);
        world.spawnEntity(entity);
    }

    @Override
    public int damageDropped (IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean removedByPlayer (IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (player.capabilities.isCreativeMode) {
            float blockReachDistance = 0;
            if (world.isRemote) {
                blockReachDistance = Minecraft.getMinecraft().playerController.getBlockReachDistance() + 1;
            } else {
                blockReachDistance = (float) ((EntityPlayerMP) player).interactionManager.getBlockReachDistance() + 1;
            }

            RayTraceResult rayResult = net.minecraftforge.common.ForgeHooks.rayTraceEyes(player, blockReachDistance + 1);
            if (rayResult == null || getDirection(world, pos) != rayResult.sideHit)
                world.setBlockState(pos, net.minecraft.init.Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
            else
                onBlockClicked(world, pos, player);

            return false;
        }

        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void breakBlock (World world, BlockPos pos, IBlockState state) {
        TileEntityDrawers tile = getTileEntity(world, pos);

        if (tile != null && !tile.isSealed()) {
            for (int i = 0; i < tile.getUpgradeSlotCount(); i++) {
                ItemStack stack = tile.getUpgrade(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemUpgradeCreative)
                        continue;
                    spawnAsEntity(world, pos, stack);
                }
            }

            if (!tile.isVending())
                DrawerInventoryHelper.dropInventoryItems(world, pos, tile);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    @Nonnull
    public List<ItemStack> getDrops (IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(getMainDrop(world, pos, state));

        return drops;
    }

    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        ItemStack drop = new ItemStack(Item.getItemFromBlock(this), 1, state.getBlock().getMetaFromState(state));

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return drop;

        NBTTagCompound data = drop.getTagCompound();
        if (data == null)
            data = new NBTTagCompound();

        if (tile.isSealed()) {
            NBTTagCompound tiledata = new NBTTagCompound();
            tile.writeToNBT(tiledata);
            data.setTag("tile", tiledata);
        }

        drop.setTagCompound(data);
        return drop;
    }

    @Override
    public void harvestBlock (World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, @Nonnull ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        world.setBlockToAir(pos);
    }

    @Override
    public float getExplosionResistance (World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile != null) {
            for (int slot = 0; slot < 5; slot++) {
                ItemStack stack = tile.getUpgrade(slot);
                if (stack.isEmpty() || !(stack.getItem() instanceof ItemUpgradeStorage))
                    continue;

                if (EnumUpgradeStorage.byMetadata(stack.getMetadata()) != EnumUpgradeStorage.OBSIDIAN)
                    continue;

                return 1000;
            }
        }

        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    public TileEntityDrawers getTileEntity (IBlockAccess blockAccess, BlockPos pos) {
        if (inTileLookup.get())
            return null;

        inTileLookup.set(true);
        TileEntity tile = blockAccess.getTileEntity(pos);
        inTileLookup.set(false);

        return (tile instanceof TileEntityDrawers) ? (TileEntityDrawers) tile : null;
    }

    public TileEntityDrawers getTileEntitySafe (World world, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = (TileEntityDrawers) createNewTileEntity(world, 0);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects (IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        if (getDirection(worldObj, target.getBlockPos()) == target.sideHit)
            return true;

        return super.addHitEffects(state, worldObj, target, manager);
    }

    @Override
    public boolean addDestroyEffects (World world, BlockPos pos, ParticleManager manager) {
        //TileEntityDrawers tile = getTileEntity(world, pos);
        //if (tile != null && !tile.getWillDestroy())
        //    return true;

        return super.addDestroyEffects(world, pos, manager);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower (IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower (IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        if (!canProvidePower(state))
            return 0;

        TileEntityDrawers tile = getTileEntity(worldIn, pos);
        if (tile == null || !tile.isRedstone())
            return 0;

        return tile.getRedstoneLevel();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower (IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return (side == EnumFacing.UP) ? getWeakPower(state, worldIn, pos, side) : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState (IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return state;

        EnumFacing facing = EnumFacing.getFront(tile.getDirection());
        if (facing.getAxis() == EnumFacing.Axis.Y)
            facing = EnumFacing.NORTH;

        return state.withProperty(FACING, facing);
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (!(state instanceof IExtendedBlockState))
            return state;

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState)state).withProperty(STATE_MODEL, new DrawerStateModelData(tile));
    }
}
