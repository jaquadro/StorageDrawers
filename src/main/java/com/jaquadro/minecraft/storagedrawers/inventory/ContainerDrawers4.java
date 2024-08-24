package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ContainerDrawers4 extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 67, 23 }, { 93, 23 }, { 67, 49 }, { 93, 49 }
    };

    public ContainerDrawers4 (int windowId, Inventory playerInv, FriendlyByteBuf data) {
        super(ModContainers.DRAWER_CONTAINER_4.get(), windowId, playerInv, data);
    }

    public ContainerDrawers4 (int windowId, Inventory playerInventory, BlockEntityDrawers blockEntityDrawers) {
        super(ModContainers.DRAWER_CONTAINER_4.get(), windowId, playerInventory, blockEntityDrawers);
    }

    @Override
    public int getStorageSlotX (int slot) {
        return slotCoordinates[slot][0];
    }

    @Override
    public int getStorageSlotY (int slot) {
        return slotCoordinates[slot][1];
    }
}
