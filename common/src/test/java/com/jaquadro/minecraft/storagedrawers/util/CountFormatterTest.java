package com.jaquadro.minecraft.storagedrawers.util;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.world.item.ItemStack;
import org.junit.Test;

import java.util.Locale;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;

public class CountFormatterTest {
    private static IDrawer drawer(int count, boolean empty) {
        return new TestDrawer(count, empty);
    }

    private record TestDrawer(int count, boolean empty) implements IDrawer {
        @Override public int getStoredItemCount() { return count; }
        @Override public boolean isEmpty() { return empty; }
        @Override public ItemStack getStoredItemPrototype() { throw new AssertionError(); }
        @Override public IDrawer setStoredItem(ItemStack stack) { throw new AssertionError(); }
        @Override public void setStoredItemCount(int count) { throw new AssertionError(); }
        @Override public int getMaxCapacity(ItemStack stack) { throw new AssertionError(); }
        @Override public int getRemainingCapacity() { throw new AssertionError(); }
        @Override public boolean canItemBeStored(ItemStack stack, Predicate<ItemStack> predicate) { throw new AssertionError(); }
        @Override public boolean canItemBeExtracted(ItemStack stack, Predicate<ItemStack> predicate) { throw new AssertionError(); }
    }

    @Test
    public void preservesLabelsAtRoundingAndSuffixBoundaries() {
        Locale original = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.US);
            int[] counts = {Integer.MIN_VALUE, -1, 0, 9999, 10000, 12345, 99949, 99950, 99999,
                100000, 999999, 1000000, 99999999, 100000000, 999999999, 1000000000, Integer.MAX_VALUE};
            String[] labels = {"-2147483648", "-1", "0", "9999", "10.0K", "12.3K", "99.9K", "99.9K", "100.0K",
                "100K", "1000K", "1.0M", "100.0M", "100M", "1000M", "1.0G", "2.1G"};
            for (int repeat = 0; repeat < 2; repeat++) {
                for (int i = 0; i < counts.length; i++)
                    assertEquals("count " + counts[i], labels[i], CountFormatter.formatApprox(null, drawer(counts[i], false)));
            }
            assertEquals("", CountFormatter.formatApprox(null, null));
            assertEquals("", CountFormatter.formatApprox(null, drawer(12345, true)));
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, original);
        }
    }

    @Test
    public void refreshesCachedLabelsWhenOnlyFormatLocaleChanges() {
        Locale original = Locale.getDefault(Locale.Category.FORMAT);
        try {
            for (Locale locale : new Locale[]{Locale.US, Locale.GERMANY, Locale.US}) {
                Locale.setDefault(Locale.Category.FORMAT, locale);
                String expected = locale.equals(Locale.GERMANY) ? "12,3K" : "12.3K";
                assertEquals(expected, CountFormatter.formatApprox(null, drawer(12345, false)));
                assertEquals(expected, CountFormatter.formatApprox(null, drawer(12345, false)));
            }
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, original);
        }
    }

    @Test
    public void fillingAndClearingTheCachePreservesTheOriginalFloatFormatting() {
        Locale original = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.GERMANY);
            for (int pass = 0; pass < 2; pass++) {
                for (int count = 10000; count < 12500; count++)
                    assertEquals(String.format("%.1fK", count / 1000f),
                        CountFormatter.formatApprox(null, drawer(count, false)));
            }
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, original);
        }
    }
}
