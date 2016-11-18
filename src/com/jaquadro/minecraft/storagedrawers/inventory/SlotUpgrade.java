package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
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
        if (inventory instanceof InventoryUpgrade) {
            ItemStack stack = getStack();
            if (stack.getItem() == ModItems.upgradeStorage) {
                EnumUpgradeStorage upgrade = EnumUpgradeStorage.byMetadata(stack.getMetadata());
                return ((InventoryUpgrade) inventory).canRemoveStorageUpgrade(upgrade.getLevel());
            }

            if (player != null) {
                if (stack.getItem() == ModItems.upgradeCreative && !player.capabilities.isCreativeMode)
                    return false;
            }
        }

        return true;
    }

    public boolean canTakeStack () {
        return canTakeStack(null);
    }
}
