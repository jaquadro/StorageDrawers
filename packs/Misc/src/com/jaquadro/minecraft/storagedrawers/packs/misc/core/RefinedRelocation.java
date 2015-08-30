package com.jaquadro.minecraft.storagedrawers.packs.misc.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.config.IUserConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlockFactory;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.misc.StorageDrawersPack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RefinedRelocation
{
    public static Block[] fullDrawers1;
    public static Block[] fullDrawers2;
    public static Block[] fullDrawers4;
    public static Block[] halfDrawers2;
    public static Block[] halfDrawers4;

    public static void init () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        if (!Loader.isModLoaded("RefinedRelocation") || !api.userConfig().integrationConfig().isRefinedRelocationEnabled())
            return;

        IPackBlockFactory factory = api.packFactory();
        IPackDataResolver[] resolvers = StorageDrawersPack.instance.resolvers;

        fullDrawers1 = new Block[resolvers.length];
        fullDrawers2 = new Block[resolvers.length];
        fullDrawers4 = new Block[resolvers.length];
        halfDrawers2 = new Block[resolvers.length];
        halfDrawers4 = new Block[resolvers.length];

        for (int i = 0; i < resolvers.length; i++) {
            IPackDataResolver resolver = resolvers[i];

            fullDrawers1[i] = factory.createBlock(BlockConfiguration.SortingFull1, resolver);
            fullDrawers2[i] = factory.createBlock(BlockConfiguration.SortingFull2, resolver);
            fullDrawers4[i] = factory.createBlock(BlockConfiguration.SortingFull4, resolver);
            halfDrawers2[i] = factory.createBlock(BlockConfiguration.SortingHalf2, resolver);
            halfDrawers4[i] = factory.createBlock(BlockConfiguration.SortingHalf4, resolver);

            factory.bindSortingBlock(ModBlocks.fullDrawers1[i], fullDrawers1[i]);
            factory.bindSortingBlock(ModBlocks.fullDrawers2[i], fullDrawers2[i]);
            factory.bindSortingBlock(ModBlocks.fullDrawers4[i], fullDrawers4[i]);
            factory.bindSortingBlock(ModBlocks.halfDrawers2[i], halfDrawers2[i]);
            factory.bindSortingBlock(ModBlocks.halfDrawers4[i], halfDrawers4[i]);
        }

        IUserConfig config = api.userConfig();
        IBlockConfig blockConfig = config.blockConfig();

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull1))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(fullDrawers1[i], "fullDrawersSort1_" + i);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull2))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(fullDrawers2[i], "fullDrawersSort2_" + i);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull4))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(fullDrawers4[i], "fullDrawersSort4_" + i);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf2))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(halfDrawers2[i], "halfDrawersSort2_" + i);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf4))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(halfDrawers4[i], "halfDrawersSort4_" + i);
        }

        if (!config.addonConfig().showAddonItemsNEI()) {
            for (int i = 0; i < resolvers.length; i++) {
                factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers1[i]));
                factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers2[i]));
                factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers4[i]));
                factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers2[i]));
                factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers4[i]));
            }
        }
    }

    public static void postInit () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        if (!Loader.isModLoaded("RefinedRelocation") || !api.userConfig().integrationConfig().isRefinedRelocationEnabled())
            return;

        IPackDataResolver[] resolvers = StorageDrawersPack.instance.resolvers;
        IBlockConfig blockConfig = api.userConfig().blockConfig();

        for (int p = 0; p < resolvers.length; p++) {
            IPackDataResolver resolver = resolvers[p];
            for (int i = 0; i < 16; i++) {
                if (!resolver.isValidMetaValue(i))
                    continue;

                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull1)))
                    GameRegistry.addRecipe(new ItemStack(fullDrawers1[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers1[p], 1, i));
                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull2)))
                    GameRegistry.addRecipe(new ItemStack(fullDrawers2[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers2[p], 1, i));
                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull4)))
                    GameRegistry.addRecipe(new ItemStack(halfDrawers2[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers2[p], 1, i));
                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf2)))
                    GameRegistry.addRecipe(new ItemStack(fullDrawers4[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.fullDrawers4[p], 1, i));
                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf4)))
                    GameRegistry.addRecipe(new ItemStack(halfDrawers4[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(ModBlocks.halfDrawers4[p], 1, i));
            }
        }
    }
}
