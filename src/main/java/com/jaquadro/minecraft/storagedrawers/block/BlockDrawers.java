package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawersComp;
import com.jaquadro.minecraft.storagedrawers.item.ItemKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BlockDrawers extends HorizontalBlock implements INetworked
{

    // TODO: TE.getModelData()
    //public static final IUnlistedProperty<DrawerStateModelData> STATE_MODEL = UnlistedModelData.create(DrawerStateModelData.class);

    private static final VoxelShape AABB_FULL = Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
    private static final VoxelShape AABB_NORTH_FULL = VoxelShapes.combineAndSimplify(AABB_FULL, Block.makeCuboidShape(1, 1, 0, 15, 15, 1), IBooleanFunction.ONLY_FIRST);
    private static final VoxelShape AABB_SOUTH_FULL = VoxelShapes.combineAndSimplify(AABB_FULL, Block.makeCuboidShape(1, 1, 15, 15, 15, 16), IBooleanFunction.ONLY_FIRST);
    private static final VoxelShape AABB_WEST_FULL = VoxelShapes.combineAndSimplify(AABB_FULL, Block.makeCuboidShape(0, 1, 1, 1, 15, 15), IBooleanFunction.ONLY_FIRST);
    private static final VoxelShape AABB_EAST_FULL = VoxelShapes.combineAndSimplify(AABB_FULL, Block.makeCuboidShape(15, 1, 1, 16, 15, 15), IBooleanFunction.ONLY_FIRST);
    private static final VoxelShape AABB_NORTH_HALF = Block.makeCuboidShape(0, 0, 8, 16, 16, 16);
    private static final VoxelShape AABB_SOUTH_HALF = Block.makeCuboidShape(0, 0, 0, 16, 16, 8);
    private static final VoxelShape AABB_WEST_HALF = Block.makeCuboidShape(8, 0, 0, 16, 16, 16);
    private static final VoxelShape AABB_EAST_HALF = Block.makeCuboidShape(0, 0, 0, 8, 16, 16);

    private final int drawerCount;
    private final boolean halfDepth;
    private final int storageUnits;

    public final AxisAlignedBB[] slotGeometry;
    public final AxisAlignedBB[] countGeometry;
    public final AxisAlignedBB[] labelGeometry;

    //@SideOnly(Side.CLIENT)
    //private StatusModelData[] statusInfo;

    private long ignoreEventTime;

    private static final ThreadLocal<Boolean> inTileLookup = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue () {
            return false;
        }
    };

    public BlockDrawers (int drawerCount, boolean halfDepth, int storageUnits, Block.Properties properties) {
        super(properties);
        this.setDefaultState(stateContainer.getBaseState()
            .with(HORIZONTAL_FACING, Direction.NORTH));

        this.drawerCount = drawerCount;
        this.halfDepth = halfDepth;
        this.storageUnits = storageUnits;

        slotGeometry = new AxisAlignedBB[drawerCount];
        countGeometry = new AxisAlignedBB[drawerCount];
        labelGeometry = new AxisAlignedBB[drawerCount];

        for (int i = 0; i < drawerCount; i++) {
            slotGeometry[i] = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
            countGeometry[i] = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
            labelGeometry[i] = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        }
    }

    @Override
    protected void fillStateContainer (StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    public boolean retrimBlock (World world, BlockPos pos, ItemStack prototype) {
        return false;
    }

    public BlockType retrimType () {
        return BlockType.Drawers;
    }

    // TODO: ABSTRACT?  Still need BlockState?
    public int getDrawerCount () {
        return drawerCount;
    }

    public boolean isHalfDepth () {
        return halfDepth;
    }

    public int getStorageUnits() {
        return storageUnits;
    }

    @OnlyIn(Dist.CLIENT)
    public void initDynamic () { }

    /*@OnlyIn(Dist.CLIENT)
    public StatusModelData getStatusInfo (BlockState state) {
        return null;
    }*/

    /*@Override
    public BlockRenderLayer getRenderLayer () {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }*/

    //@Override
    //public boolean canRenderInLayer (IBlockState state, BlockRenderLayer layer) {
    //    return layer == BlockRenderLayer.CUTOUT_MIPPED || layer == BlockRenderLayer.TRANSLUCENT;
    //}


    @Override
    public VoxelShape getShape (BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.get(HORIZONTAL_FACING);
        switch (direction) {
            case EAST:
                return halfDepth ? AABB_EAST_HALF : AABB_EAST_FULL;
            case WEST:
                return halfDepth ? AABB_WEST_HALF : AABB_WEST_FULL;
            case SOUTH:
                return halfDepth ? AABB_SOUTH_HALF : AABB_SOUTH_FULL;
            case NORTH:
            default:
                return halfDepth ? AABB_NORTH_HALF : AABB_NORTH_FULL;
        }
    }



    @Override
    public BlockState getStateForPlacement (BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    /*@Override
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
    }*/

    @Override
    public void onBlockPlacedBy (World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("tile")) {
            TileEntityDrawers tile = getTileEntity(world, pos);
            if (tile != null)
                tile.readPortable(stack.getTag().getCompound("tile"));
        }

        if (stack.hasDisplayName()) {
            TileEntityDrawers tile = getTileEntity(world, pos);
            //if (tile != null)
            //    tile.setCustomName(stack.getDisplayName());
        }

        if (entity.getHeldItemOffhand().getItem() == ModItems.DRAWER_KEY) {
            TileEntityDrawers tile = getTileEntity(world, pos);
            if (tile != null) {
                IDrawerAttributes _attrs = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(new EmptyDrawerAttributes());
                if (_attrs instanceof IDrawerAttributesModifiable) {
                    IDrawerAttributesModifiable attrs = (IDrawerAttributesModifiable) _attrs;
                    attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
                    attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);
                }
            }
        }
    }

    @Override
    public boolean isReplaceable (BlockState state, BlockItemUseContext useContext) {
        if (useContext.getPlayer() != null && useContext.getPlayer().isCreative() && useContext.getHand() == Hand.OFF_HAND) {
            double blockReachDistance = useContext.getPlayer().getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 1;
            BlockRayTraceResult result = rayTraceEyes(useContext.getWorld(), useContext.getPlayer(), blockReachDistance);

            if (result.getType() == RayTraceResult.Type.MISS || result.getFace() != state.get(HORIZONTAL_FACING))
                useContext.getWorld().setBlockState(useContext.getPos(), Blocks.AIR.getDefaultState(), useContext.getWorld().isRemote ? 11 : 3);
            else
                onBlockClicked(state, useContext.getWorld(), useContext.getPos(), useContext.getPlayer());

            return false;
        }

        return super.isReplaceable(state, useContext);
    }

    @Override
    public ActionResultType onBlockActivated (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack item = player.getHeldItem(hand);
        if (hand == Hand.OFF_HAND)
            return ActionResultType.PASS;

        if (world.isRemote && Util.milliTime() == ignoreEventTime) {
            ignoreEventTime = 0;
            return ActionResultType.PASS;
        }

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, pos);

        //if (!SecurityManager.hasAccess(player.getGameProfile(), tileDrawers))
        //    return false;

        if (CommonConfig.GENERAL.debugTrace.get()) {
            StorageDrawers.log.info("BlockDrawers.onBlockActivated");
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item.toString());
        }


        if (!item.isEmpty()) {
            if (item.getItem() instanceof ItemKey)
                return ActionResultType.PASS;

            /*if (item.getItem() instanceof ItemTrim && player.isSneaking()) {
                if (!retrimBlock(world, pos, item))
                    return false;

                if (!player.capabilities.isCreativeMode) {
                    item.shrink(1);
                    if (item.getCount() <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }

                return true;
            }*/
            if (item.getItem() instanceof ItemUpgrade) {
                if (!tileDrawers.upgrades().canAddUpgrade(item)) {
                    if (!world.isRemote)
                        player.sendStatusMessage(new TranslationTextComponent("message.storagedrawers.cannot_add_upgrade"), true);

                    return ActionResultType.PASS;
                }

                if (!tileDrawers.upgrades().addUpgrade(item)) {
                    if (!world.isRemote)
                        player.sendStatusMessage(new TranslationTextComponent("message.storagedrawers.max_upgrades"), true);

                    return ActionResultType.PASS;
                }

                world.notifyBlockUpdate(pos, state, state, 3);

                if (!player.isCreative()) {
                    item.shrink(1);
                    if (item.getCount() <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }

                return ActionResultType.SUCCESS;
            }
            /*else if (item.getItem() instanceof ItemPersonalKey) {
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
            }*/
        }
        else if (item.isEmpty() && player.isShiftKeyDown()) {
            /*if (tileDrawers.isSealed()) {
                tileDrawers.setIsSealed(false);
                return true;
            }
            else if (StorageDrawers.config.cache.enableDrawerUI) {
                player.openGui(StorageDrawers.instance, GuiHandler.drawersGuiID, world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }*/

            if (CommonConfig.GENERAL.enableUI.get() && !world.isRemote) {
                NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider()
                {
                    @Override
                    public ITextComponent getDisplayName () {
                        return new TranslationTextComponent(getTranslationKey());
                    }

                    @Nullable
                    @Override
                    public Container createMenu (int windowId, PlayerInventory playerInv, PlayerEntity playerEntity) {
                        if (drawerCount == 1)
                            return new ContainerDrawers1(windowId, playerInv, tileDrawers);
                        else if (drawerCount == 2)
                            return new ContainerDrawers2(windowId, playerInv, tileDrawers);
                        else if (drawerCount == 4)
                            return new ContainerDrawers4(windowId, playerInv, tileDrawers);
                        else if (drawerCount == 3 && BlockDrawers.this instanceof BlockCompDrawers)
                            return new ContainerDrawersComp(windowId, playerInv, tileDrawers);
                        return null;
                    }
                }, extraData -> {
                    extraData.writeBlockPos(pos);
                });
                return ActionResultType.SUCCESS;
            }
        }

        if (state.get(HORIZONTAL_FACING) != hit.getFace())
            return ActionResultType.PASS;

        //if (tileDrawers.isSealed())
        //    return false;

        int slot = getDrawerSlot(hit);
        tileDrawers.interactPutItemsIntoSlot(slot, player);

        if (item.isEmpty())
            player.setHeldItem(hand, ItemStack.EMPTY);

        return ActionResultType.SUCCESS;
    }

    protected final int getDrawerSlot (BlockRayTraceResult hit) {
        return getDrawerSlot(hit.getFace(), normalizeHitVec(hit.getHitVec()));
    }

    private Vec3d normalizeHitVec (Vec3d hit) {
        return new Vec3d(
            ((hit.x < 0) ? hit.x - Math.floor(hit.x) : hit.x) % 1,
            ((hit.y < 0) ? hit.y - Math.floor(hit.y) : hit.y) % 1,
            ((hit.z < 0) ? hit.z - Math.floor(hit.z) : hit.z) % 1
        );
    }

    protected int getDrawerSlot (Direction side, Vec3d hit) {
        return 0;
    }

    protected boolean hitTop (double hitY) {
        return hitY > .5;
    }

    protected boolean hitLeft (Direction side, double hitX, double hitZ) {
        switch (side) {
            case NORTH:
                return hitX > .5;
            case SOUTH:
                return hitX < .5;
            case WEST:
                return hitZ < .5;
            case EAST:
                return hitZ > .5;
            default:
                return true;
        }
    }

    protected BlockRayTraceResult rayTraceEyes(World world, PlayerEntity player, double length) {
        Vec3d eyePos = player.getEyePosition(1);
        Vec3d lookPos = player.getLook(1);
        Vec3d endPos = eyePos.add(lookPos.x * length, lookPos.y * length, lookPos.z * length);
        RayTraceContext context = new RayTraceContext(eyePos, endPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player);
        return world.rayTraceBlocks(context);
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
        if (worldIn.isRemote)
            return;

        if (CommonConfig.GENERAL.debugTrace.get())
            StorageDrawers.log.info("onBlockClicked");

        BlockRayTraceResult rayResult = rayTraceEyes(worldIn, playerIn, playerIn.getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 1);
        if (rayResult.getType() == RayTraceResult.Type.MISS)
            return;

        Direction side = rayResult.getFace();

        TileEntityDrawers tileDrawers = getTileEntitySafe(worldIn, pos);
        if (state.get(HORIZONTAL_FACING) != rayResult.getFace())
            return;

        //if (tileDrawers.isSealed())
        //    return;

        //if (!SecurityManager.hasAccess(playerIn.getGameProfile(), tileDrawers))
        //    return;

        int slot = getDrawerSlot(rayResult);
        IDrawer drawer = tileDrawers.getDrawer(slot);

        ItemStack item;
        //Map<String, PlayerConfigSetting<?>> configSettings = ConfigManager.serverPlayerConfigSettings.get(playerIn.getUniqueID());
        boolean invertShift = false;
        /*if (configSettings != null) {
            PlayerConfigSetting<Boolean> setting = (PlayerConfigSetting<Boolean>) configSettings.get("invertShift");
            if (setting != null) {
                invertShift = setting.value;
            }
        }*/
        if (playerIn.isShiftKeyDown() != invertShift)
            item = tileDrawers.takeItemsFromSlot(slot, drawer.getStoredItemStackSize());
        else
            item = tileDrawers.takeItemsFromSlot(slot, 1);

        if (CommonConfig.GENERAL.debugTrace.get())
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item.toString());

        if (!item.isEmpty()) {
            if (!playerIn.inventory.addItemStackToInventory(item)) {
                dropItemStack(worldIn, pos.offset(side), playerIn, item);
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            }
            else
                worldIn.playSound(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, .2f, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * .7f + 1) * 2);
        }
    }

    private void dropItemStack (World world, BlockPos pos, PlayerEntity player, @Nonnull ItemStack stack) {
        ItemEntity entity = new ItemEntity(world, pos.getX() + .5f, pos.getY() + .3f, pos.getZ() + .5f, stack);
        Vec3d motion = entity.getMotion();
        entity.addVelocity(-motion.x, -motion.y, -motion.z);
        world.addEntity(entity);
    }



    @Override
    public void onReplaced (BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntityDrawers tile = getTileEntity(world, pos);

        if (tile != null) {
            /*for (int i = 0; i < tile.upgrades().getSlotCount(); i++) {
                ItemStack stack = tile.upgrades().getUpgrade(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof ItemUpgradeCreative)
                        continue;
                    spawnAsEntity(world, pos, stack);
                }
            }*/

            //if (!tile.getDrawerAttributes().isUnlimitedVending())
            //    DrawerInventoryHelper.dropInventoryItems(world, pos, tile.getGroup());
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public boolean removedByPlayer (BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        if (player.isCreative()) {
            if (creativeCanBreakBlock(state, world, pos, player))
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
            else
                onBlockClicked(state, world, pos, player);

            return false;
        }

        return willHarvest || super.removedByPlayer(state, world, pos, player, false, fluid);
    }

    @Override
    public void harvestBlock (World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.removeBlock(pos, false);
    }

    public boolean creativeCanBreakBlock (BlockState state, World world, BlockPos pos, PlayerEntity player) {
        double blockReachDistance = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 1;

        BlockRayTraceResult rayResult = rayTraceEyes(world, player, blockReachDistance + 1);
        if (rayResult.getType() == RayTraceResult.Type.MISS || state.get(HORIZONTAL_FACING) != rayResult.getFace())
            return true;
        else
            return false;
    }

    @Override
    public List<ItemStack> getDrops (BlockState state, LootContext.Builder builder) {
        List<ItemStack> items = new ArrayList<>();
        items.add(getMainDrop(state, (TileEntityDrawers)builder.get(LootParameters.BLOCK_ENTITY)));
        return items;
    }

    protected ItemStack getMainDrop (BlockState state, TileEntityDrawers tile) {
        ItemStack drop = new ItemStack(this);
        if (tile == null)
            return drop;

        CompoundNBT data = drop.getTag();
        if (data == null)
            data = new CompoundNBT();

        boolean hasContents = false;
        for (int i = 0; i < tile.getGroup().getDrawerCount(); i++) {
            IDrawer drawer = tile.getGroup().getDrawer(i);
            if (drawer != null && !drawer.isEmpty())
                hasContents = true;
        }
        for (int i = 0; i < tile.upgrades().getSlotCount(); i++) {
            if (!tile.upgrades().getUpgrade(i).isEmpty())
                hasContents = true;
        }

        if (hasContents) {
            CompoundNBT tiledata = new CompoundNBT();
            tile.write(tiledata);

            tiledata.remove("x");
            tiledata.remove("y");
            tiledata.remove("z");

            data.put("tile", tiledata);
            drop.setTag(data);
        }

        return drop;
    }

    /*@Override
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
    }*/

    @Override
    public boolean hasTileEntity (BlockState state) {
        return true;
    }

    public TileEntityDrawers getTileEntity (IBlockReader blockAccess, BlockPos pos) {
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
            tile = (TileEntityDrawers) createTileEntity(world.getBlockState(pos), world);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }

    /*@Override
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
    }*/

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower (BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower (BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
        if (!canProvidePower(state))
            return 0;

        TileEntityDrawers tile = getTileEntity(blockAccess, pos);
        if (tile == null || !tile.isRedstone())
            return 0;

        return tile.getRedstoneLevel();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower (BlockState state, IBlockReader worldIn, BlockPos pos, Direction side) {
        return (side == Direction.UP) ? getWeakPower(state, worldIn, pos, side) : 0;
    }


}
