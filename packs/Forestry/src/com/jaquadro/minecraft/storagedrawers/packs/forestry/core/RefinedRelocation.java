package com.jaquadro.minecraft.storagedrawers.packs.forestry.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.*;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.block.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.block.BlockSortingDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.item.ItemSortingDrawersPack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RefinedRelocation
{
    public static BlockSortingDrawersPack fullDrawers1A;
    public static BlockSortingDrawersPack fullDrawers2A;
    public static BlockSortingDrawersPack fullDrawers4A;
    public static BlockSortingDrawersPack halfDrawers2A;
    public static BlockSortingDrawersPack halfDrawers4A;

    public static BlockSortingDrawersPack fullDrawers1B;
    public static BlockSortingDrawersPack fullDrawers2B;
    public static BlockSortingDrawersPack fullDrawers4B;
    public static BlockSortingDrawersPack halfDrawers2B;
    public static BlockSortingDrawersPack halfDrawers4B;

    public static void init () {
        ConfigManager config = StorageDrawers.config;
        if (!Loader.isModLoaded("RefinedRelocation") || !config.cache.enableRefinedRelocationIntegration)
            return;

        fullDrawers1A = new BlockSortingDrawersPack(ModBlocks.makeName("fullDrawers1"), 1, false, 0);
        fullDrawers2A = new BlockSortingDrawersPack(ModBlocks.makeName("fullDrawers2"), 2, false, 0);
        fullDrawers4A = new BlockSortingDrawersPack(ModBlocks.makeName("fullDrawers4"), 4, false, 0);
        halfDrawers2A = new BlockSortingDrawersPack(ModBlocks.makeName("halfDrawers2"), 2, true, 0);
        halfDrawers4A = new BlockSortingDrawersPack(ModBlocks.makeName("halfDrawers4"), 4, true, 0);

        fullDrawers1B = new BlockSortingDrawersPack(ModBlocks.makeName("fullDrawers1"), 1, false, 1);
        fullDrawers2B = new BlockSortingDrawersPack(ModBlocks.makeName("fullDrawers2"), 2, false, 1);
        fullDrawers4B = new BlockSortingDrawersPack(ModBlocks.makeName("fullDrawers4"), 4, false, 1);
        halfDrawers2B = new BlockSortingDrawersPack(ModBlocks.makeName("halfDrawers2"), 2, true, 1);
        halfDrawers4B = new BlockSortingDrawersPack(ModBlocks.makeName("halfDrawers4"), 4, true, 1);

        SortingBlockRegistry.register(ModBlocks.fullDrawers1A, fullDrawers1A);
        SortingBlockRegistry.register(ModBlocks.fullDrawers2A, fullDrawers2A);
        SortingBlockRegistry.register(ModBlocks.fullDrawers4A, fullDrawers4A);
        SortingBlockRegistry.register(ModBlocks.halfDrawers2A, halfDrawers2A);
        SortingBlockRegistry.register(ModBlocks.halfDrawers4A, halfDrawers4A);

        SortingBlockRegistry.register(ModBlocks.fullDrawers1B, fullDrawers1B);
        SortingBlockRegistry.register(ModBlocks.fullDrawers2B, fullDrawers2B);
        SortingBlockRegistry.register(ModBlocks.fullDrawers4B, fullDrawers4B);
        SortingBlockRegistry.register(ModBlocks.halfDrawers2B, halfDrawers2B);
        SortingBlockRegistry.register(ModBlocks.halfDrawers4B, halfDrawers4B);

        if (config.isBlockEnabled("fulldrawers1")) {
            GameRegistry.registerBlock(fullDrawers1A, ItemSortingDrawersPack.class, "fullDrawersSort1A");
            GameRegistry.registerBlock(fullDrawers1B, ItemSortingDrawersPack.class, "fullDrawersSort1B");
        }
        if (config.isBlockEnabled("fulldrawers2")) {
            GameRegistry.registerBlock(fullDrawers2A, ItemSortingDrawersPack.class, "fullDrawersSort2A");
            GameRegistry.registerBlock(fullDrawers2B, ItemSortingDrawersPack.class, "fullDrawersSort2B");
        }
        if (config.isBlockEnabled("fulldrawers4")) {
            GameRegistry.registerBlock(fullDrawers4A, ItemSortingDrawersPack.class, "fullDrawersSort4A");
            GameRegistry.registerBlock(fullDrawers4B, ItemSortingDrawersPack.class, "fullDrawersSort4B");
        }
        if (config.isBlockEnabled("halfdrawers2")) {
            GameRegistry.registerBlock(halfDrawers2A, ItemSortingDrawersPack.class, "halfDrawersSort2A");
            GameRegistry.registerBlock(halfDrawers2B, ItemSortingDrawersPack.class, "halfDrawersSort2B");
        }
        if (config.isBlockEnabled("halfdrawers4")) {
            GameRegistry.registerBlock(halfDrawers4A, ItemSortingDrawersPack.class, "halfDrawersSort4A");
            GameRegistry.registerBlock(halfDrawers4B, ItemSortingDrawersPack.class, "halfDrawersSort4B");
        }
    }

    public static void postInit () {
        ConfigManager config = StorageDrawers.config;
        if (!Loader.isModLoaded("RefinedRelocation") || !config.cache.enableRefinedRelocationIntegration)
            return;

        for (int i = 0; i < BlockDrawersPack.textureNames1.length; i++) {
            if (config.isBlockEnabled("fulldrawers1")) {
                GameRegistry.addRecipe(new ItemStack(fullDrawers1A, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers1A, 1, i));
                GameRegistry.addRecipe(new ItemStack(fullDrawers1B, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers1B, 1, i));
            }
            if (config.isBlockEnabled("fulldrawers2")) {
                GameRegistry.addRecipe(new ItemStack(fullDrawers2A, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers2A, 1, i));
                GameRegistry.addRecipe(new ItemStack(fullDrawers2B, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers2B, 1, i));
            }
            if (config.isBlockEnabled("halfdrawers2")) {
                GameRegistry.addRecipe(new ItemStack(halfDrawers2A, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers2A, 1, i));
                GameRegistry.addRecipe(new ItemStack(halfDrawers2B, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers2B, 1, i));
            }
            if (config.isBlockEnabled("fulldrawers4")) {
                GameRegistry.addRecipe(new ItemStack(fullDrawers4A, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers4A, 1, i));
                GameRegistry.addRecipe(new ItemStack(fullDrawers4B, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers4B, 1, i));
            }
            if (config.isBlockEnabled("halfdrawers4")) {
                GameRegistry.addRecipe(new ItemStack(halfDrawers4A, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers4A, 1, i));
                GameRegistry.addRecipe(new ItemStack(halfDrawers4B, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers4B, 1, i));
            }
        }
    }
}
