package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot
{
    public SlotUpgrade (IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canTakeStack (EntityPlayer player) {
        return canTakeStack();
    }

    public boolean canTakeStack () {
        if (inventory instanceof InventoryUpgrade) {
            ItemStack stack = getStack();
            if (stack != null && stack.getItem() == ModItems.upgrade)
                return ((InventoryUpgrade) inventory).canRemoveStorageUpgrade(stack.getItemDamage());
        }

        return true;
    }
}
