package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotDrawer extends Slot
{
    private static IInventory emptyInventory = new EmptyInventory();
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
        IDrawer target = drawer.setStoredItem(stack);
        stack = ItemStackHelper.decodeItemStack(stack);
        target.setStoredItemCount(stack.getCount());
    }

    @Override
    public void onSlotChange (@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {

    }

    @Override
    public int getItemStackLimit (@Nonnull ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), drawer.getRemainingCapacity());
    }

    @Override
    public boolean canTakeStack (PlayerEntity playerIn) {
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

    static class EmptyInventory extends Inventory implements ISidedInventory {
        public EmptyInventory() {
            super(0);
        }

        public int[] getSlotsForFace(Direction side) {
            return new int[0];
        }

        public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
            return false;
        }

        public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
            return false;
        }
    }
}
