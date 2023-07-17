package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotUpgrade extends Slot
{
    public SlotUpgrade (IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (inventory instanceof InventoryUpgrade inventoryUpgrade) {
            if (getStack().isEmpty())
                return inventoryUpgrade.canAddUpgrade(stack);
            else
                return inventoryUpgrade.canSwapUpgrade(getSlotIndex(), stack);
        }

        return false;
    }

    @Override
    public boolean canTakeStack (EntityPlayer player) {
        if (inventory instanceof InventoryUpgrade inventoryUpgrade) {
            ItemStack stack = getStack();
            if (stack.getItem() == ModItems.upgradeStorage) {
                EnumUpgradeStorage upgrade = EnumUpgradeStorage.byMetadata(stack.getMetadata());
                return inventoryUpgrade.canRemoveStorageUpgrade(getSlotIndex());
            }

            if (player != null) {
                return stack.getItem() != ModItems.upgradeCreative || player.capabilities.isCreativeMode;
            }
        }

        return true;
    }

    public boolean canTakeStack () {
        return canTakeStack(null);
    }
}
