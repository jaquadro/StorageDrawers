/*package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotRestricted extends Slot
{
    public SlotRestricted (IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }

    @Override
    public boolean isItemValid (@Nonnull ItemStack stack) {
        return inventory.isItemValidForSlot(getSlotIndex(), stack);
    }
}
*/