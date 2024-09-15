package com.jaquadro.minecraft.storagedrawers.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockKeyButton  extends FaceAttachedHorizontalDirectionalBlock
{
    public static final MapCodec<BlockKeyButton> CODEC = RecordCodecBuilder.mapCodec((inst) -> inst.group(
        KeyType.CODEC.fieldOf("key_type").forGetter(block -> block.keyType),
        propertiesCodec()
    ).apply(inst, BlockKeyButton::new));

    public static final BooleanProperty POWERED;
    protected static final VoxelShape CEILING_AABB_X;
    protected static final VoxelShape CEILING_AABB_Z;
    protected static final VoxelShape FLOOR_AABB_X;
    protected static final VoxelShape FLOOR_AABB_Z;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape PRESSED_CEILING_AABB_X;
    protected static final VoxelShape PRESSED_CEILING_AABB_Z;
    protected static final VoxelShape PRESSED_FLOOR_AABB_X;
    protected static final VoxelShape PRESSED_FLOOR_AABB_Z;
    protected static final VoxelShape PRESSED_NORTH_AABB;
    protected static final VoxelShape PRESSED_SOUTH_AABB;
    protected static final VoxelShape PRESSED_WEST_AABB;
    protected static final VoxelShape PRESSED_EAST_AABB;

    private final KeyType keyType;

    public BlockKeyButton (KeyType keyType, BlockBehaviour.Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(POWERED, false)
            .setValue(FACE, AttachFace.WALL));
        this.keyType = keyType;
    }

    public MapCodec<BlockKeyButton> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collision) {
        Direction dir = state.getValue(FACING);
        boolean powered = state.getValue(POWERED);

        switch (state.getValue(FACE)) {
            case FLOOR:
                if (dir.getAxis() == Direction.Axis.X)
                    return powered ? PRESSED_FLOOR_AABB_X : FLOOR_AABB_X;
                return powered ? PRESSED_FLOOR_AABB_Z : FLOOR_AABB_Z;
            case WALL:
                return switch (dir) {
                    case EAST -> powered ? PRESSED_EAST_AABB : EAST_AABB;
                    case WEST -> powered ? PRESSED_WEST_AABB : WEST_AABB;
                    case SOUTH -> powered ? PRESSED_SOUTH_AABB : SOUTH_AABB;
                    case NORTH, UP, DOWN -> powered ? PRESSED_NORTH_AABB : NORTH_AABB;
                };
            case CEILING:
            default:
                if (dir.getAxis() == Direction.Axis.X)
                    return powered ? PRESSED_CEILING_AABB_X : CEILING_AABB_X;
                else
                    return powered ? PRESSED_CEILING_AABB_Z : CEILING_AABB_Z;
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(POWERED)) {
            return InteractionResult.CONSUME;
        } else {
            this.press(state, level, pos);
            this.playSound(player, level, pos, true);
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);

            BlockPos targetPos = pos.offset(state.getValue(FACING).getOpposite().getNormal());
            Block target = level.getBlockState(targetPos).getBlock();
            if (target instanceof BlockController controller)
                controller.toggle(level, targetPos, player, keyType);
            else if (target instanceof BlockControllerIO io)
                io.toggle(level, targetPos, player, keyType);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    public void press(BlockState state, Level level, BlockPos pos) {
        level.setBlock(pos, state.setValue(POWERED, true), 3);
        this.updateNeighbours(state, level, pos);
        level.scheduleTick(pos, this, 10);
    }

    protected void playSound(@Nullable Player player, LevelAccessor level, BlockPos pos, boolean clickOn) {
        level.playSound(clickOn ? player : null, pos, this.getSound(clickOn), SoundSource.BLOCKS);
    }

    protected SoundEvent getSound(boolean clickOn) {
        return clickOn ? BlockSetType.OAK.buttonClickOn() : BlockSetType.OAK.buttonClickOff();
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED))
            this.checkPressed(state, level, pos);
    }

    protected void checkPressed(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, false), 3);
            this.updateNeighbours(state, level, pos);
            this.playSound(null, level, pos, false);
            level.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
        }
    }

    private void updateNeighbours(BlockState state, Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, FACE);
    }

    static {
        POWERED = BlockStateProperties.POWERED;
        CEILING_AABB_X = Block.box(3.0, 14.0, 3.0, 13.0, 16.0, 13.0);
        CEILING_AABB_Z = Block.box(3.0, 14.0, 3.0, 13.0, 16.0, 13.0);
        FLOOR_AABB_X = Block.box(3.0, 0.0, 3.0, 13.0, 2.0, 13.0);
        FLOOR_AABB_Z = Block.box(3.0, 0.0, 3.0, 13.0, 2.0, 13.0);
        NORTH_AABB = Block.box(3.0, 3.0, 14.0, 13.0, 13.0, 16.0);
        SOUTH_AABB = Block.box(3.0, 3.0, 0.0, 13.0, 13.0, 2.0);
        WEST_AABB = Block.box(14.0, 3.0, 3.0, 16.0, 13.0, 13.0);
        EAST_AABB = Block.box(0.0, 3.0, 3.0, 2.0, 13.0, 13.0);
        PRESSED_CEILING_AABB_X = Block.box(3.0, 15.0, 3.0, 13.0, 16.0, 13.0);
        PRESSED_CEILING_AABB_Z = Block.box(3.0, 15.0, 3.0, 13.0, 16.0, 13.0);
        PRESSED_FLOOR_AABB_X = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
        PRESSED_FLOOR_AABB_Z = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
        PRESSED_NORTH_AABB = Block.box(3.0, 3.0, 15.0, 13.0, 13.0, 16.0);
        PRESSED_SOUTH_AABB = Block.box(3.0, 3.0, 0.0, 13.0, 13.0, 1.0);
        PRESSED_WEST_AABB = Block.box(15.0, 3.0, 3.0, 16.0, 13.0, 13.0);
        PRESSED_EAST_AABB = Block.box(0.0, 3.0, 3.0, 1.0, 13.0, 13.0);
    }
}
