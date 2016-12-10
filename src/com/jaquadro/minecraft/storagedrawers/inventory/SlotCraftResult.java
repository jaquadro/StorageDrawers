package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
    public boolean isItemValid (ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack decrStackSize (int count) {
        if (getHasStack())
            amountCrafted += Math.min(count, getStack().stackSize);

        return super.decrStackSize(count);
    }

    @Override
    protected void onCrafting (ItemStack stack, int count) {
        amountCrafted += count;
        super.onCrafting(stack, count);
    }

    @Override
    protected void onCrafting (ItemStack stack) {
        stack.onCrafting(player.world, player, amountCrafted);
        amountCrafted = 0;
    }

    @Override
    public void onPickupFromSlot (EntityPlayer player, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, inputInventory);
        onCrafting(stack);

        for (int slot : inputSlots) {
            ItemStack itemTarget = inputInventory.getStackInSlot(slot);
            if (itemTarget != null)
                inputInventory.decrStackSize(slot, 1);
        }
    }
}
