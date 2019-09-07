package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerDrawersComp extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 80, 23 }, { 67, 49 }, { 93, 49 }
    };

    public ContainerDrawersComp (int windowId, PlayerInventory playerInventory, PacketBuffer packet) {
        super(ModContainers.DRAWER_CONTAINER_COMP, windowId, playerInventory, packet);
    }

    public ContainerDrawersComp (int windowId, PlayerInventory playerInventory, TileEntityDrawers tile) {
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
