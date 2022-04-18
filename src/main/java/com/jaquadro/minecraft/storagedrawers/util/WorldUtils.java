package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class WorldUtils {
    private WorldUtils() {}

    public static BlockHitResult rayTraceEyes(Level level, Player player, BlockPos blockPos) {
        Vec3 eyePos = player.getEyePosition(1);
        Vec3 lookVector = player.getViewVector(1);
        Vec3 endPos = eyePos.add(lookVector.scale(eyePos.distanceTo(Vec3.atCenterOf(blockPos)) + 1));
        ClipContext context = new ClipContext(eyePos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        return level.clip(context);
    }

    @Nullable
    public static <BE extends BlockEntity> BE getBlockEntity(BlockGetter level, BlockPos blockPos, Class<BE> blockEntityClass) {
        if (level instanceof Level && !((Level) level).isLoaded(blockPos))
            return null;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        return blockEntityClass.isInstance(blockEntity) ? blockEntityClass.cast(blockEntity) : null;
    }
}
