package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerDrawersComp extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 80, 23 }, { 67, 49 }, { 93, 49 }
    };

    public ContainerDrawersComp (InventoryPlayer playerInventory, TileEntityDrawers tileEntity) {
        super(playerInventory, tileEntity);
    }

    @Override
    protected int getStorageSlotX (int slot) {
        return slotCoordinates[slot][0];
    }

    @Override
    protected int getStorageSlotY (int slot) {
        return slotCoordinates[slot][1];
    }
}
