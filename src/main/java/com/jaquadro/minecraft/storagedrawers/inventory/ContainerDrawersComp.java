package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;

public class ContainerDrawersComp extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 80, 23 }, { 67, 49 }, { 93, 49 }
    };

    public ContainerDrawersComp (int windowId, Inventory playerInventory, FriendlyByteBuf packet) {
        super(ModContainers.DRAWER_CONTAINER_COMP, windowId, playerInventory, packet);
    }

    public ContainerDrawersComp (int windowId, Inventory playerInventory, TileEntityDrawers tile) {
        super(ModContainers.DRAWER_CONTAINER_COMP, windowId, playerInventory, tile);
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
