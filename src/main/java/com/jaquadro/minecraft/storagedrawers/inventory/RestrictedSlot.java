package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RestrictedSlot extends Slot
{
    public RestrictedSlot (Container inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }

    @Override
    public boolean mayPlace (@NotNull ItemStack stack) {
        return container.canPlaceItem(getSlotIndex(), stack);
    }
}
