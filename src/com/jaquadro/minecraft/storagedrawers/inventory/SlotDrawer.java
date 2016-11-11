package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class SlotDrawer extends Slot
{
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    private final IDrawerGroup group;
    private final IDrawer drawer;

    public SlotDrawer (IDrawerGroup drawerGroup, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.group = drawerGroup;
        this.drawer = group.getDrawer(index);
    }

    @Override
    public boolean isItemValid (@Nullable ItemStack stack) {
        return stack != null && drawer.canItemBeStored(stack);
    }

    @Nullable
    @Override
    public ItemStack getStack () {
        return drawer.getStoredItemCopy();
    }

    @Override
    public void putStack (@Nullable ItemStack stack) {
        drawer.setStoredItemRedir(stack, stack != null ? stack.stackSize : 0);
    }

    @Override
    public void onSlotChange (ItemStack p_75220_1_, ItemStack p_75220_2_) {

    }

    @Override
    public int getItemStackLimit (ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), drawer.getRemainingCapacity());
    }

    @Override
    public boolean canTakeStack (EntityPlayer playerIn) {
        return false;
    }

    @Override
    public ItemStack decrStackSize (int amount) {
        int withdraw = Math.min(amount, drawer.getStoredItemCount());
        drawer.setStoredItemCount(withdraw);

        ItemStack stack = drawer.getStoredItemCopy();
        stack.stackSize = withdraw;
        return stack;
    }

    public IDrawerGroup getDrawerGroup () {
        return group;
    }

    @Override
    public boolean isSameInventory (Slot other) {
        return other instanceof SlotDrawer && ((SlotDrawer) other).getDrawerGroup() == group;
    }
}
