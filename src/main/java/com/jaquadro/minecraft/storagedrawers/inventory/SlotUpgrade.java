package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
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

        if (inventory instanceof InventoryUpgrade)
            return ((InventoryUpgrade) inventory).canAddUpgrade(stack);

        return false;
    }

    @Override
    public boolean canTakeStack (PlayerEntity player) {
        if (inventory instanceof InventoryUpgrade) {
            ItemStack stack = getStack();
            if (stack.getItem() instanceof ItemUpgradeStorage) {
                EnumUpgradeStorage upgrade = ((ItemUpgradeStorage)stack.getItem()).level;
                return ((InventoryUpgrade) inventory).canRemoveStorageUpgrade(getSlotIndex());
            }

            if (player != null && !player.isCreative()) {
                if (stack.getItem() == ModItems.CREATIVE_STORAGE_UPGRADE || stack.getItem() == ModItems.CREATIVE_VENDING_UPGRADE)
                    return false;
            }
        }

        return true;
    }

    public boolean canTakeStack () {
        return canTakeStack(null);
    }
}
