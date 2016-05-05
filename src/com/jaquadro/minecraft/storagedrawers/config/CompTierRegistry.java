package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.List;

public class CompTierRegistry
{
    public class Record {
        public ItemStack upper;
        public ItemStack lower;
        public int convRate;
    }

    private List<Record> records = new ArrayList<Record>();

    public CompTierRegistry () {
        register(new ItemStack(Blocks.CLAY), new ItemStack(Items.CLAY_BALL), 4);
        register(new ItemStack(Blocks.SNOW), new ItemStack(Items.SNOWBALL), 4);
        register(new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST), 4);
        register(new ItemStack(Blocks.BRICK_BLOCK), new ItemStack(Items.BRICK), 4);
        register(new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Items.NETHERBRICK), 4);
        register(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Items.QUARTZ), 4);
        register(new ItemStack(Blocks.MELON_BLOCK), new ItemStack(Items.MELON), 9);

        if (!Loader.isModLoaded("ExtraUtilities"))
            register(new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.SAND), 4);
    }

    public boolean register (ItemStack upper, ItemStack lower, int convRate) {
        if (upper == null || lower == null)
            return false;

        unregisterUpperTarget(upper);
        unregisterLowerTarget(lower);

        Record r = new Record();
        r.upper = upper.copy();
        r.lower = lower.copy();
        r.convRate = convRate;

        r.upper.stackSize = 1;
        r.lower.stackSize = 1;

        records.add(r);

        return true;
    }

    public boolean unregisterUpperTarget (ItemStack stack) {
        for (Record r : records) {
            if (ItemStack.areItemStacksEqual(stack, r.upper)) {
                records.remove(r);
                return true;
            }
        }

        return false;
    }

    public boolean unregisterLowerTarget (ItemStack stack) {
        for (Record r : records) {
            if (ItemStack.areItemStacksEqual(stack, r.lower)) {
                records.remove(r);
                return true;
            }
        }

        return false;
    }

    public Record findHigherTier (ItemStack stack) {
        if (stack == null || stack.getItem() == null)
            return null;

        for (int i = 0, n = records.size(); i < n; i++) {
            Record r = records.get(i);
            if (stack.isItemEqual(r.lower) && ItemStack.areItemStackTagsEqual(stack, r.lower))
                return r;
        }

        return null;
    }

    public Record findLowerTier (ItemStack stack) {
        if (stack == null || stack.getItem() == null)
            return null;

        for (int i = 0, n = records.size(); i < n; i++) {
            Record r = records.get(i);
            if (stack.isItemEqual(r.upper) && ItemStack.areItemStackTagsEqual(stack, r.upper))
                return r;
        }

        return null;
    }
}
