package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerDrawers1 extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 80, 36 }
    };

    public ContainerDrawers1 (int windowId, PlayerInventory playerInv, PacketBuffer data) {
        super(windowId, playerInv, data);
    }

    public ContainerDrawers1 (int windowId, PlayerInventory playerInventory, TileEntityDrawers tileEntity) {
        super(windowId, playerInventory, tileEntity);
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
