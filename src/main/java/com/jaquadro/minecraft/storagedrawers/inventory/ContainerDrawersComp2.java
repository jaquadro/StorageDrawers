package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ContainerDrawersComp2 extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 80, 23 }, { 80, 49 }
    };

    public ContainerDrawersComp2 (int windowId, Inventory playerInventory, FriendlyByteBuf packet) {
        super(ModContainers.DRAWER_CONTAINER_COMP_2.get(), windowId, playerInventory, packet);
    }

    public ContainerDrawersComp2 (int windowId, Inventory playerInventory, BlockEntityDrawers tile) {
        super(ModContainers.DRAWER_CONTAINER_COMP_2.get(), windowId, playerInventory, tile);
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
