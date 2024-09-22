package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.texelsaurus.minecraft.chameleon.inventory.content.PositionContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class ContainerDrawers4 extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 67, 23 }, { 93, 23 }, { 67, 49 }, { 93, 49 }
    };

    public ContainerDrawers4 (int windowId, Inventory playerInv, Optional<PositionContent> content) {
        super(ModContainers.DRAWER_CONTAINER_4.get(), windowId, playerInv, content);
    }

    public ContainerDrawers4 (int windowId, Inventory playerInventory, BlockEntityDrawers blockEntityDrawers) {
        super(ModContainers.DRAWER_CONTAINER_4.get(), windowId, playerInventory, blockEntityDrawers);
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
