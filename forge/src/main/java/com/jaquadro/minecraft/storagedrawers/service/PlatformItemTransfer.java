package com.jaquadro.minecraft.storagedrawers.service;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.inventory.ItemTransfer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PlatformItemTransfer implements ItemTransferService
{
    @Override
    public boolean pushItems (IDrawerGroup group, int itemCount, BlockEntity target, Direction side) {
        return ItemTransfer.addItemsFromDrawers(group, itemCount, target, side);
    }

    @Override
    public boolean pullItems (IDrawerGroup group, int itemCount, BlockEntity target, Direction side) {
        return ItemTransfer.takeItemsIntoDrawers(group, itemCount, target, side);
    }
}
