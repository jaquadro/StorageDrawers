package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.ClientConfig;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawersComp;
import com.jaquadro.minecraft.storagedrawers.item.ItemKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemKeyring;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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

public abstract class BlockDrawers extends HorizontalDirectionalBlock implements INetworked, EntityBlock
{

    // TODO: TE.getModelData()
    //public static final IUnlistedProperty<DrawerStateModelData> STATE_MODEL = UnlistedModelData.create(DrawerStateModelData.class);

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

    public int getStorageUnits() {
        return storageUnits;
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

        if (entity != null && entity.getOffhandItem().getItem() == ModItems.DRAWER_KEY.get()) {
            IDrawerAttributes _attrs = blockEntity.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(new EmptyDrawerAttributes());
            if (_attrs instanceof IDrawerAttributesModifiable attrs) {
                attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
                attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);
            }
        }
    }

    @Override
    @NotNull
    public InteractionResult use (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack item = player.getItemInHand(hand);
        if (hand == InteractionHand.OFF_HAND)
            return InteractionResult.PASS;

        if (level.isClientSide && Util.getMillis() == ignoreEventTime) {
            ignoreEventTime = 0;
            return InteractionResult.PASS;
        }

        BlockEntityDrawers blockEntityDrawers = WorldUtils.getBlockEntity(level, pos, BlockEntityDrawers.class);
        if (blockEntityDrawers == null)
            return InteractionResult.FAIL;

        //if (!SecurityManager.hasAccess(player.getGameProfile(), tileDrawers))
        //    return false;

        if (CommonConfig.GENERAL.debugTrace.get()) {
            StorageDrawers.log.info("BlockDrawers.onBlockActivated");
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item.toString());
        }


        if (!item.isEmpty()) {
            if (item.getItem() instanceof ItemKey || item.getItem() instanceof ItemKeyring)
                return InteractionResult.PASS;

            if (item.getItem() instanceof ItemUpgrade) {
                if (!blockEntityDrawers.upgrades().canAddUpgrade(item)) {
                    if (!level.isClientSide)
                        player.displayClientMessage(new TranslatableComponent("message.storagedrawers.cannot_add_upgrade"), true);

                    return InteractionResult.PASS;
                }

                if (!blockEntityDrawers.upgrades().addUpgrade(item)) {
                    if (!level.isClientSide)
                        player.displayClientMessage(new TranslatableComponent("message.storagedrawers.max_upgrades"), true);

                    return InteractionResult.PASS;
                }

                level.sendBlockUpdated(pos, state, state, 3);

                if (!player.isCreative()) {
                    item.shrink(1);
                    if (item.getCount() <= 0)
                        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
                }

                return InteractionResult.SUCCESS;
            }
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

            if (CommonConfig.GENERAL.enableUI.get() && !level.isClientSide) {
                NetworkHooks.openGui((ServerPlayer)player, new MenuProvider()
                {
                    @Override
                    @NotNull
                    public Component getDisplayName () {
                        return new TranslatableComponent(getDescriptionId());
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
                }, extraData -> extraData.writeBlockPos(pos));
                return InteractionResult.SUCCESS;
            }
        }
        //if (tileDrawers.isSealed())
        //    return false;

        int slot = getDrawerSlot(state, hit);
        if (slot < 0)
            return InteractionResult.PASS;

        blockEntityDrawers.interactPutItemsIntoSlot(slot, player);

        if (item.isEmpty())
            player.setItemInHand(hand, ItemStack.EMPTY);

        return InteractionResult.SUCCESS;
    }

    protected final int getDrawerSlot (@NotNull BlockState state, @NotNull BlockHitResult hit) {
        Direction side = hit.getDirection();
        if (state.getValue(FACING) != side)
            return -1;
        return getDrawerSlot(hit.getDirection(), normalizeHitVec(hit.getLocation()));
    }

    @NotNull
    private static Vec3 normalizeHitVec (@NotNull Vec3 hit) {
        return new Vec3(
            ((hit.x < 0) ? hit.x - Math.floor(hit.x) : hit.x) % 1,
            ((hit.y < 0) ? hit.y - Math.floor(hit.y) : hit.y) % 1,
            ((hit.z < 0) ? hit.z - Math.floor(hit.z) : hit.z) % 1
        );
    }

    protected int getDrawerSlot (Direction correctSide, @NotNull Vec3 normalizedHit) {
        return -1;
    }

    protected boolean hitAny(Direction side, Vec3 normalizedHit) {
        if (side == Direction.NORTH || side == Direction.SOUTH) {
            return .0625 < normalizedHit.x && normalizedHit.x < .9375 && .0625 < normalizedHit.y && normalizedHit.y < .9375;
        }
        else if (side == Direction.EAST || side == Direction.WEST) {
            return .0625 < normalizedHit.z && normalizedHit.z < .9375 && .0625 < normalizedHit.y && normalizedHit.y < .9375;
        }
        return false;
    }

    protected boolean hitTop (@NotNull Vec3 normalizedHit) {
        return normalizedHit.y > .5;
    }

    protected boolean hitLeft (Direction side, @NotNull Vec3 normalizedHit) {
        return switch (side) {
            case NORTH -> normalizedHit.x > .5;
            case SOUTH -> normalizedHit.x < .5;
            case WEST -> normalizedHit.z < .5;
            case EAST -> normalizedHit.z > .5;
            default -> true;
        };
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player playerIn) {
        interactTakeItems(state, worldIn, pos, playerIn);
    }

    public boolean interactTakeItems(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player) {
        if (CommonConfig.GENERAL.debugTrace.get())
            StorageDrawers.log.info("onBlockClicked");

        if (!(state.getBlock() instanceof BlockDrawers))
            return false;

        BlockEntityDrawers blockEntityDrawers = WorldUtils.getBlockEntity(level, blockPos, BlockEntityDrawers.class);
        if (blockEntityDrawers == null)
            return false;

        BlockHitResult hit = WorldUtils.rayTraceEyes(level, player, blockPos);
        if (hit.getType() != HitResult.Type.BLOCK)
            return false;
        if (!hit.getBlockPos().equals(blockPos))
            return false;

        if (level.getBlockState(blockPos) != state)
            return false;

        int slot = getDrawerSlot(state, hit);
        if (slot < 0)
            return false;

        IDrawer drawer = blockEntityDrawers.getDrawer(slot);

        ItemStack item;
        boolean invertShift = ClientConfig.GENERAL.invertShift.get();

        if (player.isShiftKeyDown() != invertShift)
            item = blockEntityDrawers.takeItemsFromSlot(slot, drawer.getStoredItemStackSize());
        else
            item = blockEntityDrawers.takeItemsFromSlot(slot, 1);

        if (CommonConfig.GENERAL.debugTrace.get())
            StorageDrawers.log.info((item.isEmpty()) ? "  null item" : "  " + item);

        if (!item.isEmpty()) {
            if (!player.getInventory().add(item)) {
                dropItemStack(level, blockPos.relative(hit.getDirection()), player, item);
                level.sendBlockUpdated(blockPos, state, state, Block.UPDATE_ALL);
            }
            else
                level.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, ((level.random.nextFloat() - level.random.nextFloat()) * .7f + 1) * 2);
        }
        return true;
    }

    private void dropItemStack (@NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull ItemStack stack) {
        ItemEntity entity = new ItemEntity(world, pos.getX() + .5f, pos.getY() + .3f, pos.getZ() + .5f, stack);
        Vec3 motion = entity.getDeltaMovement();
        entity.push(-motion.x, -motion.y, -motion.z);
        world.addFreshEntity(entity);
    }

    @Override
    @NotNull
    public List<ItemStack> getDrops (@NotNull BlockState state, LootContext.Builder builder) {
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
        return true;
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
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }
}
