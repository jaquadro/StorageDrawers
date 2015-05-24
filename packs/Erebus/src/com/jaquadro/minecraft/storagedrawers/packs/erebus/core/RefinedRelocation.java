package com.jaquadro.minecraft.storagedrawers.packs.erebus.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.*;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.block.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.block.BlockSortingDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.item.ItemSortingDrawersPack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RefinedRelocation
{
    public static BlockSortingDrawersPack fullDrawers1;
    public static BlockSortingDrawersPack fullDrawers2;
    public static BlockSortingDrawersPack fullDrawers4;
    public static BlockSortingDrawersPack halfDrawers2;
    public static BlockSortingDrawersPack halfDrawers4;

    public static void init () {
        ConfigManager config = StorageDrawers.config;
        if (!Loader.isModLoaded("RefinedRelocation") || !config.cache.enableRefinedRelocationIntegration)
            return;

        fullDrawers1 = new BlockSortingDrawersPack(ModBlocks.makeName("fullDrawers1"), 1, false);
        fullDrawers2 = new BlockSortingDrawersPack(ModBlocks.makeName("fullDrawers2"), 2, false);
        fullDrawers4 = new BlockSortingDrawersPack(ModBlocks.makeName("fullDrawers4"), 4, false);
        halfDrawers2 = new BlockSortingDrawersPack(ModBlocks.makeName("halfDrawers2"), 2, true);
        halfDrawers4 = new BlockSortingDrawersPack(ModBlocks.makeName("halfDrawers4"), 4, true);

        SortingBlockRegistry.register(ModBlocks.fullDrawers1, fullDrawers1);
        SortingBlockRegistry.register(ModBlocks.fullDrawers2, fullDrawers2);
        SortingBlockRegistry.register(ModBlocks.fullDrawers4, fullDrawers4);
        SortingBlockRegistry.register(ModBlocks.halfDrawers2, halfDrawers2);
        SortingBlockRegistry.register(ModBlocks.halfDrawers4, halfDrawers4);

        if (config.isBlockEnabled("fulldrawers1"))
            GameRegistry.registerBlock(fullDrawers1, ItemSortingDrawersPack.class, "fullDrawersSort1");
        if (config.isBlockEnabled("fulldrawers2"))
            GameRegistry.registerBlock(fullDrawers2, ItemSortingDrawersPack.class, "fullDrawersSort2");
        if (config.isBlockEnabled("fulldrawers4"))
            GameRegistry.registerBlock(fullDrawers4, ItemSortingDrawersPack.class, "fullDrawersSort4");
        if (config.isBlockEnabled("halfdrawers2"))
            GameRegistry.registerBlock(halfDrawers2, ItemSortingDrawersPack.class, "halfDrawersSort2");
        if (config.isBlockEnabled("halfdrawers4"))
            GameRegistry.registerBlock(halfDrawers4, ItemSortingDrawersPack.class, "halfDrawersSort4");
    }

    public static void postInit () {
        ConfigManager config = StorageDrawers.config;
        if (!Loader.isModLoaded("RefinedRelocation") || !config.cache.enableRefinedRelocationIntegration)
            return;

        for (int i = 0; i < BlockDrawersPack.textureNames.length; i++) {
            if (config.isBlockEnabled("fulldrawers1"))
                GameRegistry.addRecipe(new ItemStack(fullDrawers1, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers1, 1, i));
            if (config.isBlockEnabled("fulldrawers2"))
                GameRegistry.addRecipe(new ItemStack(fullDrawers2, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers2, 1, i));
            if (config.isBlockEnabled("halfdrawers2"))
                GameRegistry.addRecipe(new ItemStack(halfDrawers2, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers2, 1, i));
            if (config.isBlockEnabled("fulldrawers4"))
                GameRegistry.addRecipe(new ItemStack(fullDrawers4, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers4, 1, i));
            if (config.isBlockEnabled("halfdrawers4"))
                GameRegistry.addRecipe(new ItemStack(halfDrawers4, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers4, 1, i));
        }
    }
}
