package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotDrawer extends Slot
{
    private static final Container emptyInventory = new EmptyInventory();
    private final ContainerDrawers container;
    private final IDrawerGroup group;
    private final IDrawer drawer;

    public SlotDrawer (ContainerDrawers container, IDrawerGroup drawerGroup, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.container = container;
        this.group = drawerGroup;
        this.drawer = group.getDrawer(index);
    }

    @Override
    public boolean mayPlace (@NotNull ItemStack stack) {
        return !stack.isEmpty() && drawer.canItemBeStored(stack);
    }

    @Override
    @NotNull
    public ItemStack getItem () {
        ItemStack stack = ItemStackHelper.encodeItemStack(drawer.getStoredItemPrototype(), drawer.getStoredItemCount());
        container.setLastAccessedItem(stack);
        return stack;
    }

    @Override
    public void set (@NotNull ItemStack stack) {
        IDrawer target = drawer.setStoredItem(stack);
        stack = ItemStackHelper.decodeItemStack(stack);
        target.setStoredItemCount(stack.getCount());
    }

    @Override
    public void onQuickCraft (@NotNull ItemStack p_75220_1_, @NotNull ItemStack p_75220_2_) {

    }

    @Override
    public int getMaxStackSize (@NotNull ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), drawer.getRemainingCapacity());
    }

    @Override
    public boolean mayPickup (@NotNull Player playerIn) {
        return false;
    }

    @Override
    @NotNull
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

    public IDrawer getDrawer () {
        return drawer;
    }

    // TODO: Forge extension
    // @Override
    public boolean isSameInventory (@NotNull Slot other) {
        return other instanceof SlotDrawer && ((SlotDrawer) other).getDrawerGroup() == group;
    }

    static class EmptyInventory extends SimpleContainer implements WorldlyContainer {
        public EmptyInventory() {
            super(0);
        }

        public int[] getSlotsForFace(@NotNull Direction side) {
            return new int[0];
        }

        public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, @Nullable Direction direction) {
            return false;
        }

        public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
            return false;
        }
    }
}
