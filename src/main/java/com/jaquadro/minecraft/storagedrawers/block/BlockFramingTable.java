package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawersComp3;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockFramingTable extends HorizontalDirectionalBlock implements EntityBlock
{
    public static final EnumProperty<EnumFramingTablePart> PART = EnumProperty.create("part", EnumFramingTablePart.class);

    protected static final VoxelShape TABLE_TOP = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape TABLE_BOTTOM = Block.box(1.0D, 0.0D, 1.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape TABLE_SHAPE = Shapes.or(TABLE_TOP, TABLE_BOTTOM);

    public BlockFramingTable (BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(getStateDefinition().any().setValue(PART, EnumFramingTablePart.RIGHT));
    }

    public static Direction getTableDirection (BlockGetter getter, BlockPos pos) {
        BlockState state = getter.getBlockState(pos);
        return state.getBlock() instanceof BlockFramingTable ? state.getValue(FACING) : null;
    }

    private static Direction getNeighborDirection (EnumFramingTablePart part, Direction direction) {
        return part == EnumFramingTablePart.LEFT ? direction.getClockWise() : direction.getCounterClockWise();
    }

    @Override
    public VoxelShape getShape (BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return TABLE_SHAPE;
    }

    @Override
    public void playerWillDestroy (Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            EnumFramingTablePart part = state.getValue(PART);
            if (part == EnumFramingTablePart.RIGHT) {
                BlockPos pos2 = pos.relative(getNeighborDirection(part, state.getValue(FACING)));
                BlockState state2 = level.getBlockState(pos2);
                if (state2.is(this) && state2.getValue(PART) == EnumFramingTablePart.LEFT) {
                    level.setBlock(pos2, Blocks.AIR.defaultBlockState(), 35);
                }
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement (BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        Direction dirLeft = direction.getCounterClockWise();
        BlockPos pos = context.getClickedPos();
        BlockPos pos2 = pos.relative(dirLeft);

        return context.getLevel().getBlockState(pos2).canBeReplaced(context)
            && context.getLevel().getWorldBorder().isWithinBounds(pos2)
            ? defaultBlockState().setValue(FACING, direction) : null;
    }

    public static Direction getConnectedDirection (BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(PART) == EnumFramingTablePart.RIGHT ? direction.getOpposite() : direction;
    }

    public static DoubleBlockCombiner.BlockType getBlockType (BlockState state) {
        EnumFramingTablePart part = state.getValue(PART);
        return part == EnumFramingTablePart.RIGHT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity (BlockPos pos, BlockState state) {
        return new BlockEntityFramingTable(ModBlockEntities.FRAMING_TABLE.get(), pos, state);
    }

    @Override
    public void setPlacedBy (Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (!level.isClientSide) {
            Direction dirLeft = state.getValue(FACING).getCounterClockWise();
            BlockPos pos2 = pos.relative(dirLeft);
            level.setBlock(pos2, state.setValue(PART, EnumFramingTablePart.LEFT), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    public InteractionResult use (@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        if (state.getValue(PART) != EnumFramingTablePart.RIGHT)
            pos = pos.relative(getNeighborDirection(state.getValue(PART), state.getValue(FACING)));

        openUI(level, pos, player);
        return InteractionResult.CONSUME;
    }

    private void openUI(Level level, BlockPos pos, Player player) {
        BlockEntityFramingTable blockEntity = WorldUtils.getBlockEntity(level, pos, BlockEntityFramingTable.class);
        NetworkHooks.openScreen((ServerPlayer) player, blockEntity, extraData -> extraData.writeBlockPos(pos));
    }
}
