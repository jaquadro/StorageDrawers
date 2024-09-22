package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class Drawers
{
    public static final IDrawer DISABLED = new DisabledDrawer();
    public static final IFractionalDrawer DISABLED_FRACTIONAL = new DisabledFractionalDrawer();

    private static class DisabledDrawer implements IDrawer
    {
        @NotNull
        @Override
        public ItemStack getStoredItemPrototype () {
            return ItemStack.EMPTY;
        }

        @NotNull
        @Override
        public IDrawer setStoredItem (@NotNull ItemStack itemPrototype) {
            return this;
        }

        @Override
        public int getStoredItemCount () {
            return 0;
        }

        @Override
        public void setStoredItemCount (int amount) {

        }

        @Override
        public int getMaxCapacity (@NotNull ItemStack itemPrototype) {
            return 0;
        }

        @Override
        public int getRemainingCapacity () {
            return 0;
        }

        @Override
        public boolean canItemBeStored (@NotNull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            return false;
        }

        @Override
        public boolean canItemBeExtracted (@NotNull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            return false;
        }

        @Override
        public boolean isEmpty () {
            return true;
        }

        @Override
        public boolean isEnabled () {
            return false;
        }

        @Override
        public IDrawer copy () {
            return this;
        }
    }

    private static class DisabledFractionalDrawer extends DisabledDrawer implements IFractionalDrawer
    {
        @Override
        public int getConversionRate () {
            return 0;
        }

        @Override
        public int getStoredItemRemainder () {
            return 0;
        }

        @Override
        public boolean isSmallestUnit () {
            return false;
        }

        @Override
        public IFractionalDrawer copy () {
            return this;
        }
    }

    public static class WrappedDrawer implements IDrawer
    {
        private IDrawer wrapped;

        public WrappedDrawer (IDrawer drawer) {
            wrapped = drawer;
        }

        @NotNull
        @Override
        public ItemStack getStoredItemPrototype () {
            return wrapped.getStoredItemPrototype();
        }

        @NotNull
        @Override
        public IDrawer setStoredItem (@NotNull ItemStack itemPrototype) {
            return wrapped.setStoredItem(itemPrototype);
        }

        @Override
        public int getStoredItemCount () {
            return wrapped.getStoredItemCount();
        }

        @Override
        public void setStoredItemCount (int amount) {
            wrapped.setStoredItemCount(amount);
        }

        @Override
        public int getMaxCapacity (@NotNull ItemStack itemPrototype) {
            return wrapped.getMaxCapacity(itemPrototype);
        }

        @Override
        public int getRemainingCapacity () {
            return wrapped.getRemainingCapacity();
        }

        @Override
        public boolean canItemBeStored (@NotNull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            return wrapped.canItemBeStored(itemPrototype, matchPredicate);
        }

        @Override
        public boolean canItemBeExtracted (@NotNull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            return wrapped.canItemBeExtracted(itemPrototype, matchPredicate);
        }

        @Override
        public boolean isEmpty () {
            return wrapped.isEmpty();
        }

        @Override
        public boolean isEnabled () {
            return wrapped.isEnabled();
        }

        @Override
        public IDrawer copy () {
            return new WrappedDrawer(wrapped);
        }
    }

    public static class WrappedFractionalDrawer extends WrappedDrawer implements IFractionalDrawer
    {
        private IFractionalDrawer wrapped;

        public WrappedFractionalDrawer (IFractionalDrawer drawer) {
            super(drawer);
            wrapped = drawer;
        }

        @Override
        public int getConversionRate () {
            return wrapped.getConversionRate();
        }

        @Override
        public int getStoredItemRemainder () {
            return wrapped.getStoredItemRemainder();
        }

        @Override
        public boolean isSmallestUnit () {
            return wrapped.isSmallestUnit();
        }

        @Override
        public IFractionalDrawer copy () {
            return new WrappedFractionalDrawer(wrapped);
        }
    }
}
