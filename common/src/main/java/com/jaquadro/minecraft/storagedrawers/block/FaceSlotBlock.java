package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.config.PlayerConfig;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class FaceSlotBlock extends HorizontalDirectionalBlock implements INetworked, EntityBlock
{
    private long ignoreEventTime;
    private long ignoreEventThresh = 2;

    protected FaceSlotBlock (Properties properties) {
        super(properties);
    }

    public InteractionResult rightAction (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        int slot = getFaceSlot(state, hit);
        InteractContext context = new InteractContext(state, level, pos, player, hit, slot);

        boolean altAction = PlayerConfig.getInvertShift(player) != player.isShiftKeyDown();

        if (!PlayerConfig.getInvertClick(player)) {
            Optional<InteractionResult> useResult = useSlotInvertible(context);
            if (useResult.isPresent())
                return useResult.get();
        }

        Optional<InteractionResult> useResult = useSlot(context);
        if (useResult.isPresent())
            return useResult.get();

        if (slot < 0)
            return InteractionResult.PASS;

        if (PlayerConfig.getInvertClick(player))
            return takeSlot(context, altAction);
        else
            return putSlot(context, altAction);
    }

    public InteractionResult leftAction (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        int slot = getFaceSlot(state, hit);
        InteractContext context = new InteractContext(state, level, pos, player, hit, slot);

        /*if (level.getGameTime() - ignoreEventTime < ignoreEventThresh) {
            ignoreEventTime = level.getGameTime();
            return InteractionResult.FAIL;
        }
        ignoreEventTime = level.getGameTime();*/

        boolean altAction = PlayerConfig.getInvertShift(player) != player.isShiftKeyDown();

        if (PlayerConfig.getInvertClick(player)) {
            Optional<InteractionResult> useResult = useSlotInvertible(context);
            if (useResult.isPresent())
                return useResult.get();
        }

        if (slot < 0)
            return InteractionResult.PASS;

        if (PlayerConfig.getInvertClick(player))
            return putSlot(context, altAction);
        else
            return takeSlot(context, altAction);
    }

    @Override
    public InteractionResult useWithoutItem (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        return rightAction(state, level, pos, player, hit);
    }

    @Override
    public void attack (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockHitResult hit = WorldUtils.rayTraceEyes(level, player, pos);
        if (hit.getType() != HitResult.Type.BLOCK)
            return;
        if (!hit.getBlockPos().equals(pos))
            return;

        leftAction(state, level, pos, player, hit);
    }

    public Optional<InteractionResult> useSlot(InteractContext context) {
        return Optional.of(InteractionResult.PASS);
    }

    public Optional<InteractionResult> useSlotInvertible(InteractContext context) {
        return Optional.of(InteractionResult.PASS);
    }

    public InteractionResult putSlot(InteractContext context, boolean altAction) {
        return InteractionResult.PASS;
    }

    public InteractionResult takeSlot(InteractContext context, boolean altAction) {
        return InteractionResult.PASS;
    }

    public final int getFaceSlot (@NotNull BlockState state, @NotNull BlockHitResult hit) {
        Direction side = hit.getDirection();
        if (state.getValue(FACING) != side)
            return -1;
        return getFaceSlot(hit.getDirection(), normalizeHitVec(hit.getLocation()));
    }

    @NotNull
    private static Vec3 normalizeHitVec (@NotNull Vec3 hit) {
        return new Vec3(
            ((hit.x < 0) ? hit.x - Math.floor(hit.x) : hit.x) % 1,
            ((hit.y < 0) ? hit.y - Math.floor(hit.y) : hit.y) % 1,
            ((hit.z < 0) ? hit.z - Math.floor(hit.z) : hit.z) % 1
        );
    }

    protected int getFaceSlot (Direction correctSide, @NotNull Vec3 normalizedHit) {
        return -1;
    }

    protected boolean hitWithinArea (Direction side, Vec3 normalizedHit, float min, float max) {
        return hitWithinX(side, normalizedHit, min, max) && hitWithinY(normalizedHit, min, max);
    }

    protected boolean hitWithinY (@NotNull Vec3 normalizedHit, float min, float max) {
        return normalizedHit.y > min && normalizedHit.y < max;
    }

    protected boolean hitWithinX (Direction side, @NotNull Vec3 normalizedHit, float min, float max) {
        return switch (side) {
            case NORTH -> normalizedHit.x > (1 - max) && normalizedHit.x < (1 - min);
            case SOUTH -> normalizedHit.x > min && normalizedHit.x < max;
            case WEST -> normalizedHit.z > min && normalizedHit.z < max;
            case EAST -> normalizedHit.z > (1 - max) && normalizedHit.z < (1 - min);
            default -> true;
        };
    }

    public static class InteractContext {
        public BlockState state;
        public Level level;
        public BlockPos pos;
        public Player player;
        public BlockHitResult hit;
        public int slot;

        public InteractContext (BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, int slot) {
            this.state = state;
            this.level = level;
            this.pos = pos;
            this.player = player;
            this.hit = hit;
            this.slot = slot;
        }

        public <BE extends BlockEntity> BE getCheckedEntity(Class<BE> type) {
            if (level.getBlockState(pos) != state)
                return null;

            return WorldUtils.getBlockEntity(level, pos, type);
        }

        public <BE extends BlockEntity, B extends Block> BE getCheckedEntity(Class<BE> type, Class<B> blockType) {
            if (!blockType.isInstance(state.getBlock()))
                return null;

            return getCheckedEntity(type);
        }
    }
}