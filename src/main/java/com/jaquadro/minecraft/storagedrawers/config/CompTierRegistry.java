package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CompTierRegistry
{
    public static class Record {
        @NotNull
        public final ItemStack upper;
        @NotNull
        public final ItemStack lower;
        public final int convRate;

        public Record (@NotNull ItemStack upper, @NotNull ItemStack lower, int convRate) {
            this.upper = upper;
            this.lower = lower;
            this.convRate = convRate;
        }
    }

    private final List<Record> records = new ArrayList<>();
    private List<String> pendingRules = new ArrayList<>();
    private boolean initialized;

    public CompTierRegistry () { }

    public void initialize () {
        initialized = true;

        if (CommonConfig.GENERAL.enableExtraCompactingRules.get()) {
            register(new ItemStack(Blocks.CLAY), new ItemStack(Items.CLAY_BALL), 4);
            register(new ItemStack(Blocks.SNOW_BLOCK), new ItemStack(Items.SNOWBALL), 4);
            register(new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST), 4);
            register(new ItemStack(Blocks.BRICKS), new ItemStack(Items.BRICK), 4);
            register(new ItemStack(Blocks.NETHER_BRICKS), new ItemStack(Items.NETHER_BRICK), 4);
            register(new ItemStack(Blocks.NETHER_WART_BLOCK), new ItemStack(Items.NETHER_WART), 9);
            register(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Items.QUARTZ), 4);
            register(new ItemStack(Blocks.MELON), new ItemStack(Items.MELON_SLICE), 9);

            if (!ModList.get().isLoaded("extrautilities")) {
                register(new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.SAND), 4);
                register(new ItemStack(Blocks.RED_SANDSTONE), new ItemStack(Blocks.RED_SAND, 1), 4);
            }
        }

        CommonConfig.onLoad(() -> CommonConfig.GENERAL.compRules.get().forEach(this::register));

        for (String rule : pendingRules) {
            register(rule);
        }

        pendingRules = null;
    }

    public boolean register (@NotNull ItemStack upper, @NotNull ItemStack lower, int convRate) {
        if (upper.isEmpty() || lower.isEmpty())
            return false;

        unregisterUpperTarget(upper);
        unregisterLowerTarget(lower);

        Record r = new Record(upper.copy(), lower.copy(), convRate);
        r.upper.setCount(1);
        r.lower.setCount(1);

        records.add(r);

        StorageDrawers.log.info("New compacting rule " + convRate + " " + lower.getItem().toString() + " = 1 " + upper.getItem().toString());

        return true;
    }

    public static boolean validateRuleSyntax (String rule) {
        String[] parts = rule.split("\\s*,\\s*");
        if (parts.length != 3)
            return false;

        ResourceLocation upperResource = ResourceLocation.tryParse(parts[0]);
        ResourceLocation lowerResource = ResourceLocation.tryParse(parts[1]);
        if (upperResource == null || lowerResource == null)
            return false;

        try {
            int conv = Integer.parseInt(parts[2]);
            return conv >= 1;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public void register (List<String> rules) {
        rules.forEach(this::register);
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
        Item upperItem = BuiltInRegistries.ITEM.get(upperResource);

        ResourceLocation lowerResource = new ResourceLocation(parts[1]);
        Item lowerItem = BuiltInRegistries.ITEM.get(lowerResource);

        try {
            int conv = Integer.parseInt(parts[2]);
            return register(new ItemStack(upperItem), new ItemStack(lowerItem), conv);
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean unregisterUpperTarget (@NotNull ItemStack stack) {
        for (Record r : records) {
            if (ItemStack.matches(stack, r.upper)) {
                records.remove(r);
                return true;
            }
        }

        return false;
    }

    public boolean unregisterLowerTarget (@NotNull ItemStack stack) {
        for (Record r : records) {
            if (ItemStack.matches(stack, r.lower)) {
                records.remove(r);
                return true;
            }
        }

        return false;
    }

    public Record findHigherTier (@NotNull ItemStack stack) {
        if (stack.isEmpty())
            return null;

        for (Record r : records) {
            if (ItemStack.isSameItemSameComponents(stack, r.lower))
                return r;
        }

        return null;
    }

    public Record findLowerTier (@NotNull ItemStack stack) {
        if (stack.isEmpty())
            return null;

        for (Record r : records) {
            if (ItemStack.isSameItemSameComponents(stack, r.upper))
                return r;
        }

        return null;
    }
}
