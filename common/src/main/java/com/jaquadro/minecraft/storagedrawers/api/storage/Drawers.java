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
    }
}
