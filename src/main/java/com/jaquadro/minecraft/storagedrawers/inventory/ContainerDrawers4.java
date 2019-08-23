/*package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerDrawers4 extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 67, 23 }, { 67, 49 }, { 93, 23 }, { 93, 49 }
    };

    public ContainerDrawers4 (InventoryPlayer playerInventory, TileEntityDrawers tileEntity) {
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

*/