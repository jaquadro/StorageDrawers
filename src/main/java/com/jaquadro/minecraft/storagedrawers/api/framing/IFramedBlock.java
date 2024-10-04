package com.jaquadro.minecraft.storagedrawers.api.framing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

public interface IFramedBlock
{
    IFramedBlockEntity getFramedBlockEntity(@NotNull Level world, @NotNull BlockPos pos);

    boolean supportsFrameMaterial (FrameMaterial material);
}
