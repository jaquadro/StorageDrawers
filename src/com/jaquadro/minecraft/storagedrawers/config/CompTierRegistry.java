package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

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
    }

    public void register (ItemStack upper, ItemStack lower, int convRate) {
        Record r = new Record();
        r.upper = upper;
        r.lower = lower;
        r.convRate = convRate;

        records.add(r);
    }

    public Record findHigherTier (ItemStack stack) {
        if (stack == null || stack.getItem() == null)
            return null;

        for (Record r : records) {
            if (stack.isItemEqual(r.lower) && ItemStack.areItemStackTagsEqual(stack, r.lower))
                return r;
        }

        return null;
    }

    public Record findLowerTier (ItemStack stack) {
        if (stack == null || stack.getItem() == null)
            return null;

        for (Record r : records) {
            if (stack.isItemEqual(r.upper) && ItemStack.areItemStackTagsEqual(stack, r.upper))
                return r;
        }

        return null;
    }
}
