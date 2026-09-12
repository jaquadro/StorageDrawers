package com.jaquadro.minecraft.storagedrawers.service;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

// Helper service to interface with IItemHandler / Fabric Storage
public interface ItemTransferService
{
    boolean pushItems (IDrawerGroup group, int itemCount, BlockEntity target, Direction side);

    boolean pullItems (IDrawerGroup group, int itemCount, BlockEntity target, Direction side);
}
