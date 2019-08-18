package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.chameleon.util.ItemResourceLocation;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CompTierRegistry
{
    public class Record {
        @Nonnull
        public final ItemStack upper;
        @Nonnull
        public final ItemStack lower;
        public final int convRate;

        public Record (@Nonnull ItemStack upper, @Nonnull ItemStack lower, int convRate) {
            this.upper = upper;
            this.lower = lower;
            this.convRate = convRate;
        }
    }

    private List<Record> records = new ArrayList<Record>();
    private List<String> pendingRules = new ArrayList<String>();
    private boolean initialized;

    public CompTierRegistry () { }

    public void initialize () {
        initialized = true;

        if (StorageDrawers.config.cache.registerExtraCompRules) {
            register(new ItemStack(Blocks.CLAY), new ItemStack(Items.CLAY_BALL), 4);
            register(new ItemStack(Blocks.SNOW), new ItemStack(Items.SNOWBALL), 4);
            register(new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST), 4);
            register(new ItemStack(Blocks.BRICK_BLOCK), new ItemStack(Items.BRICK), 4);
            register(new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Items.NETHERBRICK), 4);
            register(new ItemStack(Blocks.NETHER_WART_BLOCK), new ItemStack(Items.NETHER_WART), 9);
            register(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Items.QUARTZ), 4);
            register(new ItemStack(Blocks.MELON_BLOCK), new ItemStack(Items.MELON), 9);

            if (!Loader.isModLoaded("ExtraUtilities")) {
                register(new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.SAND), 4);
                register(new ItemStack(Blocks.RED_SANDSTONE), new ItemStack(Blocks.SAND, 1, 1), 4);
            }
        }

        if (StorageDrawers.config.cache.compRules != null) {
            for (String rule : StorageDrawers.config.cache.compRules)
                register(rule);
        }

        for (String rule : pendingRules) {
            register(rule);
        }

        pendingRules = null;
    }

    public boolean register (@Nonnull ItemStack upper, @Nonnull ItemStack lower, int convRate) {
        if (upper.isEmpty() || lower.isEmpty())
            return false;

        unregisterUpperTarget(upper);
        unregisterLowerTarget(lower);

        Record r = new Record(upper.copy(), lower.copy(), convRate);
        r.upper.setCount(1);
        r.lower.setCount(1);

        records.add(r);

        return true;
    }

    public boolean register (String rule) {
        if (!initialized) {
            pendingRules.add(rule);
            return true;
        }

        String[] parts = rule.split("\\s*,\\s*");
        if (parts.length != 3)
            return false;

        ItemResourceLocation upperResource = new ItemResourceLocation(parts[0]);
        ItemStack upperItem = upperResource.getItemStack();

        ItemResourceLocation lowerResource = new ItemResourceLocation(parts[1]);
        ItemStack lowerItem = lowerResource.getItemStack();

        if (upperItem.isEmpty() || lowerItem.isEmpty())
            return false;

        if (upperItem.getMetadata() == OreDictionary.WILDCARD_VALUE)
            upperItem = new ItemStack(upperItem.getItem(), 1, 0);
        if (lowerItem.getMetadata() == OreDictionary.WILDCARD_VALUE)
            lowerItem = new ItemStack(lowerItem.getItem(), 1, 0);

        try {
            int conv = Integer.parseInt(parts[2]);
            return register(upperItem, lowerItem, conv);
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean unregisterUpperTarget (@Nonnull ItemStack stack) {
        for (Record r : records) {
            if (ItemStack.areItemStacksEqual(stack, r.upper)) {
                records.remove(r);
                return true;
            }
        }

        return false;
    }

    public boolean unregisterLowerTarget (@Nonnull ItemStack stack) {
        for (Record r : records) {
            if (ItemStack.areItemStacksEqual(stack, r.lower)) {
                records.remove(r);
                return true;
            }
        }

        return false;
    }

    public Record findHigherTier (@Nonnull ItemStack stack) {
        if (stack.isEmpty())
            return null;

        for (Record r : records) {
            if (stack.isItemEqual(r.lower) && ItemStack.areItemStackTagsEqual(stack, r.lower))
                return r;
        }

        return null;
    }

    public Record findLowerTier (@Nonnull ItemStack stack) {
        if (stack.isEmpty())
            return null;

        for (Record r : records) {
            if (stack.isItemEqual(r.upper) && ItemStack.areItemStackTagsEqual(stack, r.upper))
                return r;
        }

        return null;
    }
}
