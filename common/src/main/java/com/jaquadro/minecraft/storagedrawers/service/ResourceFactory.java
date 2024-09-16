package com.jaquadro.minecraft.storagedrawers.service;

import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersStandard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public interface ResourceFactory
{
    BlockEntityType.BlockEntitySupplier<BlockEntityDrawersStandard> createBlockEntityDrawersStandard (int slotCount);
    BlockEntityType.BlockEntitySupplier<BlockEntityDrawersComp> createBlockEntityDrawersComp (int slotCount);
}
