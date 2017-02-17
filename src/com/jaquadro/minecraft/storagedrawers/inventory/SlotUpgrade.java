package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
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
    public boolean isItemValid(ItemStack stack) {
        if (stack == null)
            return false;

        if (inventory instanceof InventoryUpgrade) {
            if (!((InventoryUpgrade)inventory).canAddUpgrade(stack))
                return false;

            if (stack.getItem() == ModItems.upgradeOneStack)
                return ((InventoryUpgrade) inventory).canAddOneStackUpgrade();

            return true;
        }

        return false;
    }

    @Override
    public boolean canTakeStack (EntityPlayer player) {
        if (inventory instanceof InventoryUpgrade) {
            ItemStack stack = getStack();
            if (stack != null && stack.getItem() == ModItems.upgradeStorage) {
                EnumUpgradeStorage upgrade = EnumUpgradeStorage.byMetadata(stack.getMetadata());
                return ((InventoryUpgrade) inventory).canRemoveStorageUpgrade(upgrade.getLevel());
            }

            if (player != null) {
                if (stack != null && stack.getItem() == ModItems.upgradeCreative && !player.capabilities.isCreativeMode)
                    return false;
            }
        }

        return true;
    }

    public boolean canTakeStack () {
        return canTakeStack(null);
    }
}
