package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.texelsaurus.minecraft.chameleon.inventory.content.PositionContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class ContainerDrawersComp extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 80, 23 }, { 67, 49 }, { 93, 49 }
    };

    public ContainerDrawersComp (int windowId, Inventory playerInventory, Optional<PositionContent> content) {
        super(ModContainers.DRAWER_CONTAINER_COMP.get(), windowId, playerInventory, content);
    }

    public ContainerDrawersComp (int windowId, Inventory playerInventory, BlockEntityDrawers tile) {
        super(ModContainers.DRAWER_CONTAINER_COMP.get(), windowId, playerInventory, tile);
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
