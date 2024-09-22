package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.texelsaurus.minecraft.chameleon.inventory.content.PositionContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class ContainerDrawers2 extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 80, 23 }, { 80, 49 }
    };

    public ContainerDrawers2 (int windowId, Inventory playerInv, Optional<PositionContent> content) {
        super(ModContainers.DRAWER_CONTAINER_2.get(), windowId, playerInv, content);
    }

    public ContainerDrawers2 (int windowId, Inventory playerInventory, BlockEntityDrawers blockEntityDrawers) {
        super(ModContainers.DRAWER_CONTAINER_2.get(), windowId, playerInventory, blockEntityDrawers);
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
