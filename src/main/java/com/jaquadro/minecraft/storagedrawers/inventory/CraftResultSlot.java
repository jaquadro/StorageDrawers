package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class CraftResultSlot extends Slot
{
    private final Container inputInventory;
    private final int[] inputSlots;
    private final Player player;
    private int amountCrafted;

    public CraftResultSlot (Player player, Container inputInventory, Container inventory, int[] inputSlots, int slot, int x, int y) {
        super(inventory, slot, x, y);

        this.player = player;
        this.inputSlots = inputSlots;
        this.inputInventory = inputInventory;
    }

    @Override
    public boolean mayPlace (ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull ItemStack remove (int amount) {
        if (hasItem())
            amountCrafted += Math.min(amount, getItem().getCount());

        return super.remove(amount);
    }

    @Override
    public void onTake (@NotNull Player player, @NotNull ItemStack stack) {
        for (int slot : inputSlots) {
            ItemStack itemTarget = inputInventory.getItem(slot);
            if (!itemTarget.isEmpty())
                inputInventory.removeItem(slot, stack.getCount());
        }

        amountCrafted = 0;
    }

    @Override
    protected void onQuickCraft (@NotNull ItemStack stack, int amount) {
        for (int slot : inputSlots) {
            ItemStack itemTarget = inputInventory.getItem(slot);
            if (!itemTarget.isEmpty())
                inputInventory.removeItem(slot, amount);
        }

        amountCrafted += amount;
        super.onQuickCraft(stack, amount);
    }
}
