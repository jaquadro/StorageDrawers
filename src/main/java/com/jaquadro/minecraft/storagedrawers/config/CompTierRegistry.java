package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

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

        if (CommonConfig.GENERAL.enableExtraCompactingRules.get()) {
            register(new ItemStack(Blocks.CLAY), new ItemStack(Items.CLAY_BALL), 4);
            register(new ItemStack(Blocks.SNOW), new ItemStack(Items.SNOWBALL), 4);
            register(new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST), 4);
            register(new ItemStack(Blocks.BRICKS), new ItemStack(Items.BRICK), 4);
            register(new ItemStack(Blocks.NETHER_BRICKS), new ItemStack(Items.NETHER_BRICK), 4);
            register(new ItemStack(Blocks.NETHER_WART_BLOCK), new ItemStack(Items.NETHER_WART), 9);
            register(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Items.QUARTZ), 4);
            register(new ItemStack(Blocks.MELON), new ItemStack(Items.MELON), 9);

            if (!ModList.get().isLoaded("extrautilities")) {
                register(new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.SAND), 4);
                register(new ItemStack(Blocks.RED_SANDSTONE), new ItemStack(Blocks.RED_SAND, 1), 4);
            }
        }

        // TODO: Configurable compacting rules
        //if (StorageDrawers.config.cache.compRules != null) {
        //    for (String rule : StorageDrawers.config.cache.compRules)
        //        register(rule);
        //}

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

        ResourceLocation upperResource = new ResourceLocation(parts[0]);
        Item upperItem = ForgeRegistries.ITEMS.getValue(upperResource);

        ResourceLocation lowerResource = new ResourceLocation(parts[1]);
        Item lowerItem = ForgeRegistries.ITEMS.getValue(lowerResource);

        if (upperItem == null || lowerItem == null)
            return false;

        try {
            int conv = Integer.parseInt(parts[2]);
            return register(new ItemStack(upperItem), new ItemStack(lowerItem), conv);
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
