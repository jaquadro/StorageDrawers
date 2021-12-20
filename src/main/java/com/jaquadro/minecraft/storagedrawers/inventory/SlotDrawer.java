package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotDrawer extends Slot
{
    private static Container emptyInventory = new EmptyInventory();
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
    public boolean mayPlace (@Nonnull ItemStack stack) {
        return !stack.isEmpty() && drawer.canItemBeStored(stack);
    }

    @Override
    @Nonnull
    public ItemStack getItem () {
        ItemStack stack = ItemStackHelper.encodeItemStack(drawer.getStoredItemPrototype(), drawer.getStoredItemCount());
        container.setLastAccessedItem(stack);
        return stack;
    }

    @Override
    public void set (@Nonnull ItemStack stack) {
        IDrawer target = drawer.setStoredItem(stack);
        stack = ItemStackHelper.decodeItemStack(stack);
        target.setStoredItemCount(stack.getCount());
    }

    @Override
    public void onQuickCraft (@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {

    }

    @Override
    public int getMaxStackSize (@Nonnull ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), drawer.getRemainingCapacity());
    }

    @Override
    public boolean mayPickup (Player playerIn) {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack remove (int amount) {
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

    static class EmptyInventory extends SimpleContainer implements WorldlyContainer {
        public EmptyInventory() {
            super(0);
        }

        public int[] getSlotsForFace(Direction side) {
            return new int[0];
        }

        public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
            return false;
        }

        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return false;
        }
    }
}
