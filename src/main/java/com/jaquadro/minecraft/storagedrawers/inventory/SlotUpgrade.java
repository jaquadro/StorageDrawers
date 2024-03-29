package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotUpgrade extends Slot
{
    public SlotUpgrade (Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (container instanceof InventoryUpgrade)
            return ((InventoryUpgrade) container).canAddUpgrade(stack);

        return false;
    }

    @Override
    public boolean mayPickup (@NotNull Player player) {
        return canTakeStack(player);
    }

    public boolean canTakeStack () {
        return canTakeStack(null);
    }

    public boolean canTakeStack (@Nullable Player player) {
        if (container instanceof InventoryUpgrade) {
            ItemStack stack = getItem();
            if (stack.getItem() instanceof ItemUpgradeStorage) {
                return ((InventoryUpgrade) container).canRemoveStorageUpgrade(getSlotIndex());
            }

            if (player != null && !player.isCreative()) {
                return stack.getItem() != ModItems.CREATIVE_STORAGE_UPGRADE.get() && stack.getItem() != ModItems.CREATIVE_VENDING_UPGRADE.get();
            }
        }

        return true;
    }
}
