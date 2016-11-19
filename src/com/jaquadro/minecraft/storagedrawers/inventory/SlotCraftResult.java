package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

public class SlotCraftResult extends Slot
{
    private final IInventory inputInventory;
    private final int[] inputSlots;
    private EntityPlayer player;
    private int amountCrafted;

    public SlotCraftResult (EntityPlayer player, IInventory inputInventory, IInventory inventory, int[] inputSlots, int slot, int x, int y) {
        super(inventory, slot, x, y);

        this.player = player;
        this.inputSlots = inputSlots;
        this.inputInventory = inputInventory;
    }

    @Override
    public boolean isItemValid (@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize (int count) {
        if (getHasStack())
            amountCrafted += Math.min(count, getStack().func_190916_E());

        return super.decrStackSize(count);
    }

    @Override
    protected void onCrafting (@Nonnull ItemStack stack, int count) {
        amountCrafted += count;
        super.onCrafting(stack, count);
    }

    @Override
    protected void onCrafting (@Nonnull ItemStack stack) {
        stack.onCrafting(player.getEntityWorld(), player, amountCrafted);
        amountCrafted = 0;
    }

    @Override
    @Nonnull
    public ItemStack func_190901_a (EntityPlayer player, @Nonnull ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, inputInventory);
        onCrafting(stack);

        for (int slot : inputSlots) {
            ItemStack itemTarget = inputInventory.getStackInSlot(slot);
            if (!itemTarget.func_190926_b())
                inputInventory.decrStackSize(slot, 1);
        }

        return stack;
    }
}
