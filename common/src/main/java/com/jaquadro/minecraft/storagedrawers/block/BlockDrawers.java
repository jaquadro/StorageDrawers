package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.api.config.IDrawerConfig;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.DetachedDrawerData;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.capabilities.Capabilities;
import com.jaquadro.minecraft.storagedrawers.components.item.DetachedDrawerContents;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.ModSecurity;
import com.jaquadro.minecraft.storagedrawers.item.*;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import com.texelsaurus.minecraft.chameleon.inventory.ContentMenuProvider;
import com.texelsaurus.minecraft.chameleon.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class BlockDrawers extends FaceSlotBlock implements INetworked, EntityBlock
{
    public enum GeometryType {
        Slot,
        Count,
        Label,
        Indicator,
        IndicatorBase
    }

    private static final VoxelShape AABB_NORTH_FULL = Shapes.join(Shapes.block(), Block.box(1, 1, 0, 15, 15, 1), BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_SOUTH_FULL = Shapes.join(Shapes.block(), Block.box(1, 1, 15, 15, 15, 16), BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_WEST_FULL = Shapes.join(Shapes.block(), Block.box(0, 1, 1, 1, 15, 15), BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_EAST_FULL = Shapes.join(Shapes.block(), Block.box(15, 1, 1, 16, 15, 15), BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_NORTH_HALF = Block.box(0, 0, 8, 16, 16, 16);
    private static final VoxelShape AABB_SOUTH_HALF = Block.box(0, 0, 0, 16, 16, 8);
    private static final VoxelShape AABB_WEST_HALF = Block.box(8, 0, 0, 16, 16, 16);
    private static final VoxelShape AABB_EAST_HALF = Block.box(0, 0, 0, 8, 16, 16);
    private static final VoxelShape HOPPER_INSIDE = Block.box(2, 12, 2, 14, 16, 14);
    private static final VoxelShape AABB_NORTH_HOPPER = Shapes.join(AABB_NORTH_FULL, HOPPER_INSIDE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_SOUTH_HOPPER = Shapes.join(AABB_SOUTH_FULL, HOPPER_INSIDE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_WEST_HOPPER = Shapes.join(AABB_WEST_FULL, HOPPER_INSIDE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape AABB_EAST_HOPPER = Shapes.join(AABB_EAST_FULL, HOPPER_INSIDE, BooleanOp.ONLY_FIRST);

    private static final Map<UUID, Long> lastLeftClick = new HashMap<>();

    private final int drawerCount;
    private final boolean halfDepth;
    private final IDrawerConfig drawerConfig;

    // Deprecated
    private int storageUnits;

    public final AABB[] slotGeometry;
    public final AABB[] countGeometry;
    public final AABB[] labelGeometry;
    public final AABB[] indGeometry;
    public final AABB[] indBaseGeometry;

    private long ignoreEventTime;

    public BlockDrawers (int drawerCount, boolean halfDepth, IDrawerConfig drawerConfig, Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH));

        this.drawerCount = drawerCount;
        this.halfDepth = halfDepth;
        this.drawerConfig = drawerConfig;
        this.storageUnits = 0;

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

    @Deprecated
    public BlockDrawers (int drawerCount, boolean halfDepth, int storageUnits, Properties properties) {
        this(drawerCount, halfDepth, null, properties);

        this.storageUnits = storageUnits;
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

    public boolean repartitionBlock (Level world, BlockPos pos, ItemStack prototype) {
        return false;
    }

    // TODO: ABSTRACT?  Still need BlockState?
    public int getDrawerCount () {
        return drawerCount;
    }

    public boolean isHalfDepth () {
        return halfDepth;
    }

    public int getStorageUnits () {
        return drawerConfig != null ? drawerConfig.getUnitsPerSlot() : storageUnits;
    }

    public String getNameTypeKey () {
        String type = halfDepth ? "half" : "full";
        return "block." + ModConstants.MOD_ID + ".type." + type + "_drawers_" + getDrawerCount();
    }

    @Override
    @NotNull
    public VoxelShape getShape (@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction direction = state.getValue(FACING);
        BlockEntityDrawers blockEntity = WorldUtils.getBlockEntity(worldIn, pos, BlockEntityDrawers.class);
        if (blockEntity != null && blockEntity.getDrawerAttributes().isHopper()) {
            return switch (direction) {
                case EAST -> AABB_EAST_HOPPER;
                case WEST -> AABB_WEST_HOPPER;
                case SOUTH -> AABB_SOUTH_HOPPER;
                default -> AABB_NORTH_HOPPER;
            };
        }

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
    public boolean isPathfindable (@NotNull BlockState state, @NotNull PathComputationType type) {
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

        TypedEntityData<BlockEntityType<?>> blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null) {
            ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), blockEntityData.copyTagWithoutId());
            blockEntity.readPortable(input);
        } else {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), customData.copyTag());
                blockEntity.readPortable(input);
            }
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

        boolean keyEnabled = true;
        if (key instanceof ItemKey itemKey)
            keyEnabled = itemKey.isEnabled();

        IDrawerAttributes _attrs = blockEntity.getCapability(Capabilities.DRAWER_ATTRIBUTES);
        if (_attrs instanceof IDrawerAttributesModifiable attrs) {
            if (key != null && keyEnabled) {
                if (key == ModItems.DRAWER_KEY.get()) {
                    attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
                    attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);
                } else if (key == ModItems.QUANTIFY_KEY.get())
                    attrs.setIsShowingQuantity(true);
                else if (key == ModItems.SHROUD_KEY.get())
                    attrs.setIsConcealed(true);
                else if (key == ModItems.SUSPEND_KEY.get())
                    attrs.setIsSuspended(true);
            }

            if (ModCommonConfig.INSTANCE.TOOLS.quantifyKey.showDefault.get())
                attrs.setIsShowingQuantity(true);
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
            if (!context.level.isClientSide() && context.player.isShiftKeyDown()) {
                openUI(context);
                return Optional.of(InteractionResult.SUCCESS);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<InteractionResult> useSlot (InteractContext context) {
        ItemStack item = context.player.getItemInHand(InteractionHand.MAIN_HAND);

        ItemStack keyItem = null;
        if (item.getItem() instanceof ItemKeyring keyring)
            keyItem = keyring.getKey();

        BlockEntityDrawers blockEntity = context.getCheckedEntity(BlockEntityDrawers.class);
        if (!SecurityManager.hasAccess(context.player, blockEntity)) {
            // Admin key unlocking
            if (item.getItem() instanceof ItemPersonalKey || (keyItem != null && keyItem.getItem() instanceof ItemPersonalKey)) {
                if (keyItem != null)
                    item = keyItem;

                ItemPersonalKey pk = (ItemPersonalKey) item.getItem();
                if (Objects.equals(pk.getSecurityProviderKey(), "unlock")) {
                    blockEntity.setOwner(null);
                    blockEntity.setSecurityProvider(null);
                }
            }
            return Optional.of(InteractionResult.SUCCESS);
        }

        if (context.level.isClientSide())
            return Optional.of(InteractionResult.SUCCESS);

        // Drawer pulling
        if (ModCommonConfig.INSTANCE.DRAWERS.detached.enable.get() && context.slot >= 0) {
            if (item.getItem() == ModItems.DRAWER_PULLER.get() || (keyItem != null && keyItem.getItem() == ModItems.DRAWER_PULLER.get())) {
                this.interactPullDrawer(context);
                return Optional.of(InteractionResult.SUCCESS);
            } else if (item.getItem() instanceof ItemDetachedDrawer) {
                this.interactReturnDrawer(context, item);
                return Optional.of(InteractionResult.SUCCESS);
            }
        }

        // Re-trimming
        if (Block.byItem(item.getItem()) instanceof BlockTrim && context.slot >= 0 && context.player.isShiftKeyDown()) {
            if (!retrimBlock(context.level, context.pos, item))
                return Optional.of(InteractionResult.PASS);

            if (!context.player.isCreative()) {
                item.shrink(1);
                if (item.getCount() <= 0)
                    context.player.getInventory().setItem(context.player.getInventory().getSelectedSlot(), ItemStack.EMPTY);
                context.level.playSound(null, context.pos, SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, .2f,
                    ((context.level.getRandom().nextFloat() - context.level.getRandom().nextFloat()) * .7f + 1) * 2);
            }

            return Optional.of(InteractionResult.SUCCESS);
        }

        // Re-partitioning
        if (Block.byItem(item.getItem()) instanceof BlockDrawers && context.slot >= 0 && context.player.isShiftKeyDown()) {
            if (!repartitionBlock(context.level, context.pos, item))
                return Optional.of(InteractionResult.PASS);

            if (!context.player.isCreative()) {
                item.shrink(1);
                if (item.getCount() <= 0)
                    context.player.getInventory().setItem(context.player.getInventory().getSelectedSlot(), ItemStack.EMPTY);
                context.level.playSound(null, context.pos, SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, .2f,
                    ((context.level.getRandom().nextFloat() - context.level.getRandom().nextFloat()) * .7f + 1) * 2);
            }

            return Optional.of(InteractionResult.SUCCESS);
        }

        // Personal Key
        if (item.getItem() instanceof ItemPersonalKey || (keyItem != null && keyItem.getItem() instanceof ItemPersonalKey)) {
            if (!ModCommonConfig.INSTANCE.TOOLS.personalKey.enable.get())
                return Optional.of(InteractionResult.PASS);

            if (keyItem != null)
                item = keyItem;

            String securityKey = ((ItemPersonalKey) item.getItem()).getSecurityProviderKey();
            ISecurityProvider provider = ModSecurity.registry.getProvider(securityKey);

            if (Objects.equals(securityKey, "unlock")) {
                blockEntity.setOwner(null);
                blockEntity.setSecurityProvider(null);
            }
            else if (blockEntity.getOwner() == null) {
                blockEntity.setOwner(context.player.getUUID());
                blockEntity.setSecurityProvider(provider);
            }
            else if (SecurityManager.hasOwnership(context.player.getGameProfile(), blockEntity)) {
                blockEntity.setOwner(null);
                blockEntity.setSecurityProvider(null);
            }
            else
                return Optional.of(InteractionResult.PASS);
            return Optional.of(InteractionResult.SUCCESS);
        }

        // Other Drawer keys
        if (item.getItem() instanceof ItemKey || item.getItem() instanceof ItemKeyring)
            return Optional.of(InteractionResult.PASS);

        BlockEntityDrawers entity = context.getCheckedEntity(BlockEntityDrawers.class);

        // Drawer upgrades
        if (item.getItem() instanceof ItemUpgrade && !context.player.isShiftKeyDown()) {
            if (entity.getGroup().hasMissingDrawers() && ModCommonConfig.INSTANCE.DRAWERS.detached.forceMaxCapacityCheck.get()) {
                if (!context.level.isClientSide())
                    context.player.sendOverlayMessage(Component.translatable("message.storagedrawers.missing_slots_upgrade"));

                return Optional.of(InteractionResult.PASS);
            }

            // Copy remote binding if remote upgrade already present
            if (item.getItem() instanceof ItemUpgradeRemote remote && entity.upgrades().hasRemoteUpgrade()) {
                if (remote.isBound()) {
                    entity.upgrades().updateRemoteUpgradeBinding(item);
                    BlockPos pos = ItemUpgradeRemote.getBoundPosition(item);
                    if (pos != null)
                        context.player.sendOverlayMessage(Component.translatable("message.storagedrawers.updated_remote_binding", pos.getX(), pos.getY(), pos.getZ()));

                    return Optional.of(InteractionResult.SUCCESS);
                }
            }

            if (!entity.upgrades().canAddUpgrade(item)) {
                if (!context.level.isClientSide())
                    context.player.sendOverlayMessage(Component.translatable("message.storagedrawers.cannot_add_upgrade"));

                return Optional.of(InteractionResult.PASS);
            }

            if (!entity.upgrades().addUpgrade(item)) {
                if (!context.level.isClientSide())
                    context.player.sendOverlayMessage(Component.translatable("message.storagedrawers.max_upgrades"));

                return Optional.of(InteractionResult.PASS);
            }

            context.level.sendBlockUpdated(context.pos, context.state, context.state, 3);

            if (!context.player.isCreative()) {
                item.shrink(1);
                if (item.getCount() <= 0)
                    context.player.getInventory().setItem(context.player.getInventory().getSelectedSlot(), ItemStack.EMPTY);
            }

            return Optional.of(InteractionResult.SUCCESS);
        }

        return Optional.empty();
    }

    @Override
    public InteractionResult putSlot (InteractContext context, boolean altAction) {
        ItemStack item = context.player.getItemInHand(InteractionHand.MAIN_HAND);
        BlockEntityDrawers blockEntityDrawers = context.getCheckedEntity(BlockEntityDrawers.class);
        if (blockEntityDrawers == null)
            return InteractionResult.FAIL;

        if (!SecurityManager.hasAccess(context.player, blockEntityDrawers))
            return InteractionResult.PASS;

        if (context.level.isClientSide())
            return InteractionResult.SUCCESS;

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

        if (!SecurityManager.hasAccess(context.player, blockEntityDrawers))
            return InteractionResult.PASS;

        if (context.level.isClientSide())
            return InteractionResult.SUCCESS;

        IDrawer drawer = blockEntityDrawers.getGroup().getDrawer(context.slot);

        ItemStack item;
        if (altAction)
            item = blockEntityDrawers.takeItemsFromSlot(context.slot, drawer.getStoredItemStackSize(), context.player);
        else
            item = blockEntityDrawers.takeItemsFromSlot(context.slot, 1, context.player);

        if (!item.isEmpty()) {
            if (!context.player.getInventory().add(item)) {
                dropItemStack(context.level, context.pos.relative(context.hit.getDirection()), context.player, item);
                context.level.sendBlockUpdated(context.pos, context.state, context.state, Block.UPDATE_ALL);
            } else
                context.level.playSound(null, context.pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                    ((context.level.getRandom().nextFloat() - context.level.getRandom().nextFloat()) * .7f + 1) * 2);
        }

        return InteractionResult.SUCCESS;
    }

    public boolean interactPullDrawer(InteractContext context) {
        BlockEntityDrawers entity = context.getCheckedEntity(BlockEntityDrawers.class, BlockDrawers.class);
        if (entity == null)
            return false;

        IDrawer drawer = entity.getGroup().getDrawer(context.slot);
        if (!drawer.isEnabled() || drawer.isMissing() || !drawer.canDetach())
            return false;

        ItemStack detachedDrawer = pullDrawer(context.level, entity, context.slot);
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

        if (!entity.interactReplaceDrawer(context.slot, detachedDrawer, context.player))
            return false;

        if (detachedDrawer.getItem() == ModItems.DETACHED_DRAWER.get()) {
            detachedDrawer.setCount(detachedDrawer.getCount() - 1);
            if (detachedDrawer.getCount() > 0)
                return true;
        }

        context.player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        return true;
    }

    private ItemStack pullDrawer(Level level, BlockEntityDrawers group, int slot) {
        int cap = group.getEffectiveDrawerCapacity() * group.upgrades().getStorageMultiplier();
        Item baseItem = ModItems.DETACHED_DRAWER_FULL.get();

        IDrawer drawer = group.getDrawer(slot);
        DetachedDrawerData data = new DetachedDrawerData(drawer, cap);
        if (drawer.isEmpty())
            baseItem = ModItems.DETACHED_DRAWER.get();

        if (ModCommonConfig.INSTANCE.DRAWERS.detached.heavyDrawers.get() && !group.upgrades().hasPortabilityUpgrade())
            data.setIsHeavy(true);

        // TODO: Move away from CUSTOM_DATA
        ItemStack stack = new ItemStack(baseItem, 1);

        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        data.serializeNBT(output);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(output.buildResult()));

        ItemStack savedItem = data.getStoredItemPrototype().copyWithCount(data.getStoredItemCount());
        DetachedDrawerContents contents = new DetachedDrawerContents(savedItem, cap, data.isHeavy());
        stack.set(ModDataComponents.DETACHED_DRAWER_CONTENTS.get(), contents);
        if (!drawer.isEmpty())
            stack.set(DataComponents.MAX_STACK_SIZE, 1);

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
                    ((context.level.getRandom().nextFloat() - context.level.getRandom().nextFloat()) * .7f + 1) * 2);
        }
    }

    private void openUI(InteractContext context) {
        if (!ModCommonConfig.INSTANCE.GENERAL.enableUI.get())
            return;

        MenuProvider provider = context.state.getMenuProvider(context.level, context.pos);
        if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
            ModServices.log.info("Open BlockDrawers UI " + context.pos);

        if (provider instanceof ContentMenuProvider<?> menu)
            menu.openMenu((ServerPlayer) context.player);
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider (BlockState blockState, Level level, BlockPos blockPos) {
        BlockEntityDrawers blockEntityDrawers = WorldUtils.getBlockEntity(level, blockPos, BlockEntityDrawers.class);
        if (blockEntityDrawers == null)
            return null;

        return new BlockEntityDrawers.ContentProvider(blockEntityDrawers);
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

        if (ModCommonConfig.INSTANCE.DRAWERS.storage.dropMode.get() == ModCommonConfig.DropMode.KEEP) {
            boolean hasUpgradeContents = false;
            boolean hasItemContents = false;
            for (int i = 0; i < tile.getGroup().getDrawerCount(); i++) {
                IDrawer drawer = tile.getGroup().getDrawer(i);
                if (!drawer.isEmpty() || drawer.isMissing())
                    hasItemContents = true;
            }
            for (int i = 0; i < tile.upgrades().getSlotCount(); i++) {
                if (!tile.upgrades().getUpgrade(i).isEmpty())
                    hasUpgradeContents = true;
            }

            if (hasItemContents || hasUpgradeContents) {
                TagValueOutput tagOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, tile.getLevel().registryAccess());
                tile.saveWithId(tagOutput);
                drop.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(tile.getType(), tagOutput.buildResult()));
                if (hasItemContents)
                    drop.set(DataComponents.MAX_STACK_SIZE, 1);
            }
        }

        if (tile.hasCustomName())
            drop.set(DataComponents.CUSTOM_NAME, tile.getCustomName());

        return drop;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource (@NotNull BlockState state) {
        return !ModCommonConfig.INSTANCE.UPGRADES.redstoneUpgrade.analogOutput.get();
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

    @Override
    protected void affectNeighborsAfterRemoval (BlockState state, ServerLevel level, BlockPos pos, boolean $$3) {
        if (!level.getBlockState(pos).is(state.getBlock()))
            level.updateNeighbourForOutputSignal(pos, this);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level blockAccess, BlockPos pos, Direction dir) {
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

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean flag) {
        BlockEntityDrawers blockEntity = WorldUtils.getBlockEntity(level, pos, BlockEntityDrawers.class);
        if (blockEntity != null) {
            IDrawerAttributes attr = blockEntity.getDrawerAttributes();
            if (attr.isHopper() && !attr.isSuspended())
                blockEntity.entityInside(level, pos, state, entity);
        }
    }

    @Override
    protected VoxelShape getInteractionShape (BlockState state, BlockGetter blockGetter, BlockPos pos) {
        BlockEntityDrawers blockEntity = WorldUtils.getBlockEntity(blockGetter, pos, BlockEntityDrawers.class);
        if (blockEntity != null && blockEntity.getDrawerAttributes().isHopper())
            return HOPPER_INSIDE;

        return super.getInteractionShape(state, blockGetter, pos);
    }

    @Override
    protected void tick (@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
            ModServices.log.info("BlockDrawers [{}] tick", pos);

        if (world.isClientSide())
            return;

        BlockEntityDrawers blockEntity = WorldUtils.getBlockEntity(world, pos, BlockEntityDrawers.class);
        if (blockEntity == null)
            return;

        // Tick hopper
        IDrawerAttributes attribs = blockEntity.getDrawerAttributes();
        if (attribs.isHopper() || attribs.isMagnet()) {
            UpgradeData upgrades = blockEntity.upgrades();
            int tickTime = 20;

            if (attribs.isMagnet()) {
                int idleRate = upgrades.getMagnetIdleRate();
                tickTime = blockEntity.pushItemsTick(world, pos, state)
                    ? upgrades.getMagnetActiveRate()
                    : rand.nextInt(idleRate, idleRate + 5);
            }

            world.scheduleTick(pos, state.getBlock(), tickTime);
        }

        // Only validates if validation is scheduled on entity
        blockEntity.validateBoundController();
    }
}
