package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;

public class ContainerDrawers2 extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 80, 23 }, { 80, 49 }
    };

    public ContainerDrawers2 (int windowId, Inventory playerInv, FriendlyByteBuf data) {
        super(ModContainers.DRAWER_CONTAINER_2, windowId, playerInv, data);
    }

    public ContainerDrawers2 (int windowId, Inventory playerInventory, TileEntityDrawers tileEntity) {
        super(ModContainers.DRAWER_CONTAINER_2, windowId, playerInventory, tileEntity);
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
