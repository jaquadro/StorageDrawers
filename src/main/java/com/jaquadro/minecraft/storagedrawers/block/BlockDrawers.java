package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.DetachedDrawerData;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawersComp;
import com.jaquadro.minecraft.storagedrawers.item.*;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BlockDrawers extends FaceSlotBlock implements INetworked, EntityBlock
{
    private static final VoxelShape AABB_NORTH_FULL = Shapes.join(Shapes.block(), Block.box(1, 1, 0, 15, 15, 1), BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_SOUTH_FULL = Shapes.join(Shapes.block(), Block.box(1, 1, 15, 15, 15, 16), BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_WEST_FULL = Shapes.join(Shapes.block(), Block.box(0, 1, 1, 1, 15, 15), BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_EAST_FULL = Shapes.join(Shapes.block(), Block.box(15, 1, 1, 16, 15, 15), BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_NORTH_HALF = Block.box(0, 0, 8, 16, 16, 16);
    private static final VoxelShape AABB_SOUTH_HALF = Block.box(0, 0, 0, 16, 16, 8);
    private static final VoxelShape AABB_WEST_HALF = Block.box(8, 0, 0, 16, 16, 16);
    private static final VoxelShape AABB_EAST_HALF = Block.box(0, 0, 0, 8, 16, 16);

    private final int drawerCount;
    private final boolean halfDepth;
    private final int storageUnits;

    public final AABB[] slotGeometry;
    public final AABB[] countGeometry;
    public final AABB[] labelGeometry;
    public final AABB[] indGeometry;
    public final AABB[] indBaseGeometry;

    private long ignoreEventTime;

    public BlockDrawers (int drawerCount, boolean halfDepth, int storageUnits, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH));

        this.drawerCount = drawerCount;
        this.halfDepth = halfDepth;
        this.storageUnits = storageUnits;

        slotGeometry = new AABB[drawerCount];
        countGeometry = new AABB[drawerCount];
        labelGeometry = new AABB[drawerCount];
        indGeometry = new AABB[drawerCount];
        indBaseGeometry = new AABB[drawerCount];

        for (int i = 0; i < drawerCount; i++) {
            slotGeometry[i] = new AABB(0, 0, 0, 0, 0, 0);
            countGeometry[i] = new AABB(0, 0, 0, 0, 0, 0);
            labelGeometry[i] = new AABB(0, 0, 0, 0, 0, 0);
            indGeometry[i] = new AABB(0, 0, 0, 0, 0, 0);
            indBaseGeometry[i] = new AABB(0, 0, 0, 0, 0, 0);
        }
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public boolean retrimBlock (Level world, BlockPos pos, ItemStack prototype) {
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

    public int getStorageUnits () {
        return storageUnits;
    }

    public String getNameTypeKey () {
        String type = halfDepth ? "half" : "full";
        return "block." + StorageDrawers.MOD_ID + ".type." + type + "_drawers_" + getDrawerCount();
    }

    @Override
    @NotNull
    public VoxelShape getShape (@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction direction = state.getValue(FACING);
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
    public boolean isPathfindable (@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull PathComputationType type) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement (BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy (@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        BlockEntityDrawers blockEntity = WorldUtils.getBlockEntity(world, pos, BlockEntityDrawers.class);
        if (blockEntity == null)
            return;

        CompoundTag tag = stack.getTagElement("tile");
        if (tag != null) {
            blockEntity.readPortable(tag);
        }

//        if (stack.hasCustomHoverName()) {
//            //    blockEntity.setCustomName(stack.getDisplayName());
//        }

        Item key = null;
        if (entity != null) {
            if (entity.getOffhandItem().getItem() instanceof ItemKey itemKey)
                key = itemKey;
            else if (entity.getOffhandItem().getItem() instanceof ItemKeyring itemKeyring)
                key = itemKeyring.getKey().getItem();
        }

        if (key != null) {
            IDrawerAttributes _attrs = blockEntity.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(new EmptyDrawerAttributes());
            if (_attrs instanceof IDrawerAttributesModifiable attrs) {
                if (key == ModItems.DRAWER_KEY.get()) {
                    attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
                    attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);
                } else if (key == ModItems.QUANTIFY_KEY.get())
                    attrs.setIsShowingQuantity(true);
                else if (key == ModItems.SHROUD_KEY.get())
                    attrs.setIsConcealed(true);
            }
        }
    }

    @Deprecated
    protected int getDrawerSlot (Direction correctSide, @NotNull Vec3 normalizedHit) {
        return getFaceSlot(correctSide, normalizedHit);
    }

    @Deprecated
    protected boolean hitAny (Direction side, Vec3 normalizedHit) {
        return hitWithinArea(side, normalizedHit, .0625f, .9375f);
    }

    @Deprecated
    protected boolean hitTop (@NotNull Vec3 normalizedHit) {
        return hitWithinY(normalizedHit, .5f, 1);
    }

    @Deprecated
    protected boolean hitLeft (Direction side, @NotNull Vec3 normalizedHit) {
        return hitWithinX(side, normalizedHit, 0, .5f);
    }

    @Override
    public Optional<InteractionResult> useSlotInvertible (InteractContext context) {
        ItemStack item = context.player.getItemInHand(InteractionHand.MAIN_HAND);

        // Drawer UI
        if (item.isEmpty()) {
            if (CommonConfig.GENERAL.enableUI.get() && !context.level.isClientSide && context.player.isShiftKeyDown()) {
                openUI(context);
                return Optional.of(InteractionResult.SUCCESS);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<InteractionResult> useSlot (InteractContext context) {
        ItemStack item = context.player.getItemInHand(InteractionHand.MAIN_HAND);

        // Drawer pulling
        if (CommonConfig.GENERAL.enableDetachedDrawers.get() && context.slot >= 0) {
            if (item.getItem() == ModItems.DRAWER_PULLER.get()) {
                this.interactPullDrawer(context);
                return Optional.of(InteractionResult.SUCCESS);
            } else if (item.getItem() instanceof ItemDetachedDrawer) {
                this.interactReturnDrawer(context, item);
                return Optional.of(InteractionResult.SUCCESS);
            }
        }

        // Drawer keys
        if (item.getItem() instanceof ItemKey || item.getItem() instanceof ItemKeyring)
            return Optional.of(InteractionResult.PASS);

        BlockEntityDrawers entity = context.getCheckedEntity(BlockEntityDrawers.class);

        // Drawer upgrades
        if (item.getItem() instanceof ItemUpgrade) {
            if (entity.getGroup().hasMissingDrawers() && CommonConfig.GENERAL.forceDetachedDrawersMaxCapacityCheck.get()) {
                if (!context.level.isClientSide)
                    context.player.displayClientMessage(Component.translatable("message.storagedrawers.missing_slots_upgrade"), true);

                return Optional.of(InteractionResult.PASS);
            }

            if (!entity.upgrades().canAddUpgrade(item)) {
                if (!context.level.isClientSide)
                    context.player.displayClientMessage(Component.translatable("message.storagedrawers.cannot_add_upgrade"), true);

                return Optional.of(InteractionResult.PASS);
            }

            if (!entity.upgrades().addUpgrade(item)) {
                if (!context.level.isClientSide)
                    context.player.displayClientMessage(Component.translatable("message.storagedrawers.max_upgrades"), true);

                return Optional.of(InteractionResult.PASS);
            }

            context.level.sendBlockUpdated(context.pos, context.state, context.state, 3);

            if (!context.player.isCreative()) {
                item.shrink(1);
                if (item.getCount() <= 0)
                    context.player.getInventory().setItem(context.player.getInventory().selected, ItemStack.EMPTY);
            }

            return Optional.of(InteractionResult.SUCCESS);
        }

        return Optional.empty();
    }

    private void openUI(InteractContext context) {
        BlockEntityDrawers blockEntityDrawers = context.getCheckedEntity(BlockEntityDrawers.class);

        NetworkHooks.openScreen((ServerPlayer) context.player, new MenuProvider()
        {
            @Override
            @NotNull
            public Component getDisplayName () {
                ItemStack stack = new ItemStack(BlockDrawers.this);
                return stack.getItem().getName(stack);
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu (int windowId, @NotNull Inventory playerInv, @NotNull Player playerEntity) {
                if (drawerCount == 1)
                    return new ContainerDrawers1(windowId, playerInv, blockEntityDrawers);
                else if (drawerCount == 2)
                    return new ContainerDrawers2(windowId, playerInv, blockEntityDrawers);
                else if (drawerCount == 4)
                    return new ContainerDrawers4(windowId, playerInv, blockEntityDrawers);
                else if (drawerCount == 3 && BlockDrawers.this instanceof BlockCompDrawers)
                    return new ContainerDrawersComp(windowId, playerInv, blockEntityDrawers);
                return null;
            }
        }, extraData -> extraData.writeBlockPos(context.pos));
    }

    @Override
    public InteractionResult putSlot (InteractContext context, boolean altAction) {
        ItemStack item = context.player.getItemInHand(InteractionHand.MAIN_HAND);
        BlockEntityDrawers blockEntityDrawers = context.getCheckedEntity(BlockEntityDrawers.class);
        if (blockEntityDrawers == null)
            return InteractionResult.FAIL;

        blockEntityDrawers.interactPutItemsIntoSlot(context.slot, context.player);

        if (item.isEmpty())
            context.player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult takeSlot (InteractContext context, boolean altAction) {
        BlockEntityDrawers blockEntityDrawers = context.getCheckedEntity(BlockEntityDrawers.class, BlockDrawers.class);
        if (blockEntityDrawers == null)
            return InteractionResult.FAIL;

        IDrawer drawer = blockEntityDrawers.getGroup().getDrawer(context.slot);

        ItemStack item;
        if (altAction)
            item = blockEntityDrawers.takeItemsFromSlot(context.slot, drawer.getStoredItemStackSize());
        else
            item = blockEntityDrawers.takeItemsFromSlot(context.slot, 1);

        if (CommonConfig.GENERAL.debugTrace.get())
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item);

        if (!item.isEmpty()) {
            if (!context.player.getInventory().add(item)) {
                dropItemStack(context.level, context.pos.relative(context.hit.getDirection()), context.player, item);
                context.level.sendBlockUpdated(context.pos, context.state, context.state, Block.UPDATE_ALL);
            } else
                context.level.playSound(null, context.pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                    ((context.level.random.nextFloat() - context.level.random.nextFloat()) * .7f + 1) * 2);
        }

        return InteractionResult.SUCCESS;
    }

    private IDrawerGroup getDrawerGroup (InteractContext context) {
        BlockEntityDrawers entity = context.getCheckedEntity(BlockEntityDrawers.class, BlockDrawers.class);
        return (entity != null) ? entity.getGroup() : null;
    }

    public boolean interactPullDrawer(InteractContext context) {
        IDrawerGroup group = getDrawerGroup(context);
        if (group == null)
            return false;

        IDrawer drawer = group.getDrawer(context.slot);
        if (!drawer.isEnabled() || drawer.isMissing() || !drawer.canDetach())
            return false;

        ItemStack detachedDrawer = pullDrawer(group, context.slot);
        if (!detachedDrawer.isEmpty())
            drawer.setDetached(true);

        giveOrDropItemStack(context, detachedDrawer);
        return true;
    }

    public boolean interactReturnDrawer(InteractContext context, ItemStack detachedDrawer) {
        if (detachedDrawer.isEmpty())
            return false;

        BlockEntityDrawers entity = context.getCheckedEntity(BlockEntityDrawers.class, BlockDrawers.class);
        if (entity == null)
            return false;

        if (!entity.interactReplaceDrawer(context.slot, detachedDrawer))
            return false;

        if (detachedDrawer.getItem() == ModItems.DETACHED_DRAWER.get()) {
            detachedDrawer.setCount(detachedDrawer.getCount() - 1);
            if (detachedDrawer.getCount() > 0)
                return true;
        }

        context.player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        return true;
    }

    private ItemStack pullDrawer(IDrawerGroup group, int slot) {
        IDrawer drawer = group.getDrawer(slot);
        if (drawer.isEmpty())
            return new ItemStack(ModItems.DETACHED_DRAWER.get(), 1);

        DetachedDrawerData data = new DetachedDrawerData(drawer);

        ItemStack stack = new ItemStack(ModItems.DETACHED_DRAWER_FULL.get(), 1);
        stack.setTag(data.serializeNBT());

        return stack;
    }

    private void giveOrDropItemStack(InteractContext context, @NotNull ItemStack item) {
        if (!item.isEmpty()) {
            if (!context.player.getInventory().add(item)) {
                dropItemStack(context.level, context.pos.relative(context.hit.getDirection()), context.player, item);
                context.level.sendBlockUpdated(context.pos, context.state, context.state, Block.UPDATE_ALL);
            }
            else
                context.level.playSound(null, context.pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                    ((context.level.random.nextFloat() - context.level.random.nextFloat()) * .7f + 1) * 2);
        }
    }

    private void dropItemStack (@NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull ItemStack stack) {
        ItemEntity entity = new ItemEntity(world, pos.getX() + .5f, pos.getY() + .3f, pos.getZ() + .5f, stack);
        Vec3 motion = entity.getDeltaMovement();
        entity.push(-motion.x, -motion.y, -motion.z);
        world.addFreshEntity(entity);
    }

    @Override
    @NotNull
    public List<ItemStack> getDrops (@NotNull BlockState state, LootParams.Builder builder) {
        List<ItemStack> items = new ArrayList<>();
        items.add(getMainDrop(state, (BlockEntityDrawers)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)));
        return items;
    }

    protected ItemStack getMainDrop (BlockState state, BlockEntityDrawers tile) {
        ItemStack drop = new ItemStack(this);
        if (tile == null)
            return drop;

        CompoundTag data = drop.getTag();
        if (data == null)
            data = new CompoundTag();

        boolean hasContents = false;
        for (int i = 0; i < tile.getGroup().getDrawerCount(); i++) {
            IDrawer drawer = tile.getGroup().getDrawer(i);
            if (!drawer.isEmpty())
                hasContents = true;
        }
        for (int i = 0; i < tile.upgrades().getSlotCount(); i++) {
            if (!tile.upgrades().getUpgrade(i).isEmpty())
                hasContents = true;
        }

        if (hasContents) {
            CompoundTag tiledata = tile.saveWithoutMetadata();

            data.put("tile", tiledata);
            drop.setTag(data);
        }

        return drop;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource (@NotNull BlockState state) {
        return !CommonConfig.GENERAL.enableAnalogRedstone.get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal (@NotNull BlockState state, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        if (!isSignalSource(state))
            return 0;

        BlockEntityDrawers blockEntity = WorldUtils.getBlockEntity(blockAccess, pos, BlockEntityDrawers.class);
        if (blockEntity == null || !blockEntity.isRedstone())
            return 0;

        return blockEntity.getRedstoneLevel();
    }

    @Override
    public int getDirectSignal (@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull Direction side) {
        return (side == Direction.UP) ? getSignal(state, worldIn, pos, side) : 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState p_51538_, Level p_51539_, BlockPos p_51540_, BlockState p_51541_, boolean p_51542_) {
        if (!p_51538_.is(p_51541_.getBlock())) {
            p_51539_.updateNeighbourForOutputSignal(p_51540_, this);
            super.onRemove(p_51538_, p_51539_, p_51540_, p_51541_, p_51542_);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level blockAccess, BlockPos pos) {
        if (!hasAnalogOutputSignal(state))
            return 0;

        BlockEntityDrawers blockEntity = WorldUtils.getBlockEntity(blockAccess, pos, BlockEntityDrawers.class);
        if (blockEntity == null || !blockEntity.isRedstone())
            return 0;

        return blockEntity.getRedstoneLevel();
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }
}
