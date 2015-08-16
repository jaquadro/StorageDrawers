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
        register(new ItemStack(Blocks.clay), new ItemStack(Items.clay_ball), 4);
        register(new ItemStack(Blocks.snow), new ItemStack(Items.snowball), 4);
        register(new ItemStack(Blocks.glowstone), new ItemStack(Items.glowstone_dust), 4);
        register(new ItemStack(Blocks.brick_block), new ItemStack(Items.brick), 4);
        register(new ItemStack(Blocks.nether_brick), new ItemStack(Items.netherbrick), 4);
        register(new ItemStack(Blocks.quartz_block), new ItemStack(Items.quartz), 4);
        register(new ItemStack(Blocks.melon_block), new ItemStack(Items.melon), 9);

        if (!Loader.isModLoaded("ExtraUtilities"))
            register(new ItemStack(Blocks.sandstone), new ItemStack(Blocks.sand), 4);
    }

    public boolean register (ItemStack upper, ItemStack lower, int convRate) {
        if (upper == null || lower == null)
            return false;
        if (convRate != 4 && convRate != 9)
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
