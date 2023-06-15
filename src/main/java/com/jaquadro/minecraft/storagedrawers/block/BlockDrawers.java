package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.chameleon.block.properties.UnlistedModelData;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.DrawerStateModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
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
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
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
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        setTranslationKey(blockName);
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
        return (tile != null) ? EnumFacing.byIndex(tile.getDirection()) : EnumFacing.NORTH;
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
    public @NotNull BlockRenderLayer getRenderLayer () {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean canRenderInLayer (IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT_MIPPED || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(blockAccess, pos);
        if (tile != null && isHalfDepth(state)) {
            switch (EnumFacing.byIndex(tile.getDirection())) {
                case NORTH -> {
                    return AABB_NORTH_HALF;
                }
                case SOUTH -> {
                    return AABB_SOUTH_HALF;
                }
                case WEST -> {
                    return AABB_WEST_HALF;
                }
                case EAST -> {
                    return AABB_EAST_HALF;
                }
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

        if (entity.getHeldItemOffhand().getItem() == ModItems.drawerKey) {
            IDrawerAttributes _attrs = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY, null);
            if (_attrs instanceof IDrawerAttributesModifiable) {
                IDrawerAttributesModifiable attrs = (IDrawerAttributesModifiable) _attrs;
                attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
                attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);
            }
        }
    }

    private boolean isInvertedHand (EntityPlayer player) {
        Map<String, PlayerConfigSetting<?>> configSettings = ConfigManager.serverPlayerConfigSettings.get(player.getUniqueID());
        boolean invertHand = StorageDrawers.config.cache.invertClick;
        if (configSettings != null) {
            PlayerConfigSetting<Boolean> setting = (PlayerConfigSetting<Boolean>) configSettings.get("invertClick");
            if (setting != null) {
                invertHand = setting.value;
            }
        }

        return invertHand;
    }

    @Override
    public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hand == EnumHand.OFF_HAND)
            return false;

        if (!isInvertedHand(player))
            return insertOrApplyItem(world, pos, state, player, side, hitX, hitY, hitZ);

        extractItem(world, pos, player, side, hitX, hitY, hitZ);
        return true;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (worldIn.isRemote)
            return;
        if (StorageDrawers.config.cache.debugTrace)
            StorageDrawers.log.info("onBlockClicked");

        RayTraceResult rayResult = net.minecraftforge.common.ForgeHooks.rayTraceEyes(playerIn, ((EntityPlayerMP) playerIn).interactionManager.getBlockReachDistance() + 1);
        if (rayResult == null)
            return;

        EnumFacing side = rayResult.sideHit;

        // adjust hitVec for drawers
        float hitX = (float)(rayResult.hitVec.x - pos.getX());
        float hitY = (float)(rayResult.hitVec.y - pos.getY());
        float hitZ = (float)(rayResult.hitVec.z - pos.getZ());

        if (!isInvertedHand(playerIn)) {
            if (worldIn.isRemote)
                return;
            extractItem(worldIn, pos, playerIn, side, hitX, hitY, hitZ);
        }
        else
            insertOrApplyItem(worldIn, pos, worldIn.getBlockState(pos), playerIn, side, hitX, hitY, hitZ);
    }

    public boolean insertOrApplyItem (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
        if (world.isRemote && Minecraft.getSystemTime() == ignoreEventTime) {
            ignoreEventTime = 0;
            return false;
        }

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, pos);

        if (!SecurityManager.hasAccess(player.getGameProfile(), tileDrawers))
            return false;

        if (StorageDrawers.config.cache.debugTrace) {
            StorageDrawers.log.info("BlockDrawers.onBlockActivated");
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item.toString());
        }

        if (!item.isEmpty()) {
            if (item.getItem() instanceof ItemKey)
                return false;

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
            else if (item.getItem() instanceof ItemUpgrade) {
                if (!tileDrawers.upgrades().canAddUpgrade(item)) {
                    if (!world.isRemote)
                        player.sendStatusMessage(new TextComponentTranslation("storagedrawers.msg.cannotAddUpgrade"), true);

                    return false;
                }

                if (!tileDrawers.upgrades().addUpgrade(item)) {
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
            player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);

        return true;
    }

    protected int getDrawerSlot (int drawerCount, int side, float hitX, float hitY, float hitZ) {
        return 0;
    }

    protected boolean hitTop (float hitY) {
        return hitY > .5;
    }

    protected boolean hitLeft (int side, float hitX, float hitZ) {
        return switch (side) {
            case 2 -> hitX > .5;
            case 3 -> hitX < .5;
            case 4 -> hitZ < .5;
            case 5 -> hitZ > .5;
            default -> true;
        };
    }

    public void extractItem(World worldIn, BlockPos pos, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
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
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item.toString());

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
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape (IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return BlockFaceShape.SOLID;

        if (isHalfDepth(state))
            return side.getOpposite().ordinal() == tile.getDirection() ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;

        if (side == EnumFacing.DOWN) {
            Block blockUnder = world.getBlockState(pos.down()).getBlock();
            if (blockUnder instanceof BlockChest || blockUnder instanceof BlockEnderChest)
                return BlockFaceShape.UNDEFINED;
        }

        return side.ordinal() != tile.getDirection() ? BlockFaceShape.SOLID : BlockFaceShape.BOWL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid (IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        return getBlockFaceShape(world, state, pos, side) == BlockFaceShape.SOLID;
    }

    private void dropItemStack (World world, BlockPos pos, EntityPlayer player, @Nonnull ItemStack stack) {
        EntityItem entity = new EntityItem(world, pos.getX() + .5f, pos.getY() + .3f, pos.getZ() + .5f, stack);
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

        if (tile != null && !tile.isSealed() && !StorageDrawers.config.cache.keepContentsOnBreak) {
            for (int i = 0; i < tile.upgrades().getSlotCount(); i++) {
                ItemStack stack = tile.upgrades().getUpgrade(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemUpgradeCreative)
                        continue;
                    spawnAsEntity(world, pos, stack);
                }
            }

            if (!tile.getDrawerAttributes().isUnlimitedVending())
                DrawerInventoryHelper.dropInventoryItems(world, pos, tile.getGroup());
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public void getDrops (NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(getMainDrop(world, pos, state));
    }

    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        ItemStack drop = new ItemStack(Item.getItemFromBlock(this), 1, state.getBlock().getMetaFromState(state));

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return drop;

        NBTTagCompound data = drop.getTagCompound();
        if (data == null)
            data = new NBTTagCompound();

        boolean hasContents = false;
        if (StorageDrawers.config.cache.keepContentsOnBreak) {
            for (int i = 0; i < tile.getGroup().getDrawerCount(); i++) {
                IDrawer drawer = tile.getGroup().getDrawer(i);
                if (drawer != null && !drawer.isEmpty())
                    hasContents = true;
            }
            for (int i = 0; i < tile.upgrades().getSlotCount(); i++) {
                if (!tile.upgrades().getUpgrade(i).isEmpty())
                    hasContents = true;
            }
        }

        if (tile.isSealed() || (StorageDrawers.config.cache.keepContentsOnBreak && hasContents)) {
            NBTTagCompound tiledata = new NBTTagCompound();
            tile.writeToNBT(tiledata);
            data.setTag("tile", tiledata);
        }

        drop.setTagCompound(data);
        return drop;
    }

    @Override
    public ItemStack getPickBlock (IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack drop = getMainDrop(world, pos, state);

        // If no tile data was written. Note that hasTagCompound returns false if the tag compound is null.
        if (!drop.hasTagCompound() || !Objects.requireNonNull(drop.getTagCompound()).hasKey("tile"))
            return drop;

        // Remove tile data
        NBTTagCompound compound = drop.getTagCompound();
        compound.removeTag("tile");
        drop.setTagCompound(compound);

        return drop;
    }

    public ItemStack getWailaTOPBlock(World world, BlockPos pos, IBlockState state) {
        ItemStack drop = getMainDrop(world, pos, state);

        // If no tile data was written. Note that hasTagCompound returns false if the tag compound is null.
        if (!drop.hasTagCompound() || !Objects.requireNonNull(drop.getTagCompound()).hasKey("tile"))
            return drop;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(pos);

        // If we don't need to remove tile data
        if (tile != null && tile.isSealed())
            return drop;

        // Remove tile data
        NBTTagCompound compound = drop.getTagCompound();
        compound.removeTag("tile");
        drop.setTagCompound(compound);

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
                ItemStack stack = tile.upgrades().getUpgrade(slot);
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
    @SideOnly(Side.CLIENT)
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

        EnumFacing facing = EnumFacing.byIndex(tile.getDirection());
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
