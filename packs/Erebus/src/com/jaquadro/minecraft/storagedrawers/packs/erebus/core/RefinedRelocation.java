package com.jaquadro.minecraft.storagedrawers.packs.erebus.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.config.IUserConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlockFactory;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.StorageDrawersPack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RefinedRelocation
{
    public static Block fullDrawers1;
    public static Block fullDrawers2;
    public static Block fullDrawers4;
    public static Block halfDrawers2;
    public static Block halfDrawers4;

    public static void init () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        if (!Loader.isModLoaded("RefinedRelocation") || !api.userConfig().integrationConfig().isRefinedRelocationEnabled())
            return;

        IPackBlockFactory factory = api.packFactory();
        IPackDataResolver resolver = StorageDrawersPack.instance.resolver;

        fullDrawers1 = factory.createBlock(BlockConfiguration.SortingFull1, resolver);
        fullDrawers2 = factory.createBlock(BlockConfiguration.SortingFull2, resolver);
        fullDrawers4 = factory.createBlock(BlockConfiguration.SortingFull4, resolver);
        halfDrawers2 = factory.createBlock(BlockConfiguration.SortingHalf2, resolver);
        halfDrawers4 = factory.createBlock(BlockConfiguration.SortingHalf4, resolver);

        factory.bindSortingBlock(ModBlocks.fullDrawers1, fullDrawers1);
        factory.bindSortingBlock(ModBlocks.fullDrawers2, fullDrawers2);
        factory.bindSortingBlock(ModBlocks.fullDrawers4, fullDrawers4);
        factory.bindSortingBlock(ModBlocks.halfDrawers2, halfDrawers2);
        factory.bindSortingBlock(ModBlocks.halfDrawers4, halfDrawers4);

        IUserConfig config = api.userConfig();
        IBlockConfig blockConfig = config.blockConfig();

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull1)))
            factory.registerBlock(fullDrawers1, "fullDrawersSort1");
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull2)))
            factory.registerBlock(fullDrawers2, "fullDrawersSort2");
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull4)))
            factory.registerBlock(fullDrawers4, "fullDrawersSort4");
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf2)))
            factory.registerBlock(halfDrawers2, "halfDrawersSort2");
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf4)))
            factory.registerBlock(halfDrawers4, "halfDrawersSort4");

        if (!config.addonConfig().showAddonItemsNEI()) {
            factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers1));
            factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers2));
            factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers4));
            factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers2));
            factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers4));
        }
    }

    public static void postInit () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        if (!Loader.isModLoaded("RefinedRelocation") || !api.userConfig().integrationConfig().isRefinedRelocationEnabled())
            return;

        IPackDataResolver resolver = StorageDrawersPack.instance.resolver;
        IBlockConfig blockConfig = api.userConfig().blockConfig();

        for (int i = 0; i < 16; i++) {
            if (!resolver.isValidMetaValue(i))
                continue;

            if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull1)))
                GameRegistry.addRecipe(new ItemStack(fullDrawers1, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers1, 1, i));
            if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull2)))
                GameRegistry.addRecipe(new ItemStack(fullDrawers2, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers2, 1, i));
            if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull4)))
                GameRegistry.addRecipe(new ItemStack(halfDrawers2, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers2, 1, i));
            if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf2)))
                GameRegistry.addRecipe(new ItemStack(fullDrawers4, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers4, 1, i));
            if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf4)))
                GameRegistry.addRecipe(new ItemStack(halfDrawers4, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers4, 1, i));
        }
    }
}
