package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotDrawer extends Slot
{
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    private ContainerDrawers container;
    private final IDrawerGroup group;
    private final IDrawer drawer;

    public SlotDrawer (ContainerDrawers container, IDrawerGroup drawerGroup, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.container = container;
        this.group = drawerGroup;
        this.drawer = group.getDrawer(index);
    }

    @Override
    public boolean isItemValid (@Nonnull ItemStack stack) {
        return !stack.isEmpty() && drawer.canItemBeStored(stack);
    }

    @Override
    @Nonnull
    public ItemStack getStack () {
        ItemStack stack = ItemStackHelper.encodeItemStack(drawer.getStoredItemPrototype(), drawer.getStoredItemCount());
        container.setLastAccessedItem(stack);
        return stack;
    }

    @Override
    public void putStack (@Nonnull ItemStack stack) {
        drawer.setStoredItem(stack);
        if (!ItemStackHelper.isStackEncoded(stack))
            drawer.setStoredItemCount(stack.getCount());
    }

    @Override
    public void onSlotChange (@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {

    }

    @Override
    public int getItemStackLimit (@Nonnull ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), drawer.getRemainingCapacity());
    }

    @Override
    public boolean canTakeStack (EntityPlayer playerIn) {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize (int amount) {
        int withdraw = Math.min(amount, drawer.getStoredItemCount());
        drawer.setStoredItemCount(withdraw);

        ItemStack stack = drawer.getStoredItemPrototype().copy();
        stack.setCount(drawer.getStoredItemCount() - withdraw);
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
