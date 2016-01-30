package com.jaquadro.minecraft.storagedrawers.packs.forestry.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.config.IUserConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlockFactory;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.StorageDrawersPack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RefinedRelocation
{
    public static Block fullDrawers1A;
    public static Block fullDrawers2A;
    public static Block fullDrawers4A;
    public static Block halfDrawers2A;
    public static Block halfDrawers4A;

    public static Block fullDrawers1B;
    public static Block fullDrawers2B;
    public static Block fullDrawers4B;
    public static Block halfDrawers2B;
    public static Block halfDrawers4B;

    public static void init () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        if (!Loader.isModLoaded("RefinedRelocation") || !api.userConfig().integrationConfig().isRefinedRelocationEnabled())
            return;

        IPackBlockFactory factory = api.packFactory();
        IPackDataResolver resolver1 = StorageDrawersPack.instance.resolver1;
        IPackDataResolver resolver2 = StorageDrawersPack.instance.resolver2;

        fullDrawers1A = factory.createBlock(BlockConfiguration.SortingFull1, resolver1);
        fullDrawers2A = factory.createBlock(BlockConfiguration.SortingFull2, resolver1);
        fullDrawers4A = factory.createBlock(BlockConfiguration.SortingFull4, resolver1);
        halfDrawers2A = factory.createBlock(BlockConfiguration.SortingHalf2, resolver1);
        halfDrawers4A = factory.createBlock(BlockConfiguration.SortingHalf4, resolver1);

        fullDrawers1B = factory.createBlock(BlockConfiguration.SortingFull1, resolver2);
        fullDrawers2B = factory.createBlock(BlockConfiguration.SortingFull2, resolver2);
        fullDrawers4B = factory.createBlock(BlockConfiguration.SortingFull4, resolver2);
        halfDrawers2B = factory.createBlock(BlockConfiguration.SortingHalf2, resolver2);
        halfDrawers4B = factory.createBlock(BlockConfiguration.SortingHalf4, resolver2);

        factory.bindSortingBlock(ModBlocks.fullDrawers1A, fullDrawers1A);
        factory.bindSortingBlock(ModBlocks.fullDrawers2A, fullDrawers2A);
        factory.bindSortingBlock(ModBlocks.fullDrawers4A, fullDrawers4A);
        factory.bindSortingBlock(ModBlocks.halfDrawers2A, halfDrawers2A);
        factory.bindSortingBlock(ModBlocks.halfDrawers4A, halfDrawers4A);

        factory.bindSortingBlock(ModBlocks.fullDrawers1B, fullDrawers1B);
        factory.bindSortingBlock(ModBlocks.fullDrawers2B, fullDrawers2B);
        factory.bindSortingBlock(ModBlocks.fullDrawers4B, fullDrawers4B);
        factory.bindSortingBlock(ModBlocks.halfDrawers2B, halfDrawers2B);
        factory.bindSortingBlock(ModBlocks.halfDrawers4B, halfDrawers4B);

        IUserConfig config = api.userConfig();
        IBlockConfig blockConfig = config.blockConfig();

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull1))) {
            factory.registerBlock(fullDrawers1A, "fullDrawersSort1A");
            factory.registerBlock(fullDrawers1B, "fullDrawersSort1B");
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull2))) {
            factory.registerBlock(fullDrawers2A, "fullDrawersSort2A");
            factory.registerBlock(fullDrawers2B, "fullDrawersSort2B");
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull4))) {
            factory.registerBlock(fullDrawers4A, "fullDrawersSort4A");
            factory.registerBlock(fullDrawers4B, "fullDrawersSort4B");
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf2))) {
            factory.registerBlock(halfDrawers2A, "halfDrawersSort2A");
            factory.registerBlock(halfDrawers2B, "halfDrawersSort2B");
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf4))) {
            factory.registerBlock(halfDrawers4A, "halfDrawersSort4A");
            factory.registerBlock(halfDrawers4B, "halfDrawersSort4B");
        }

        if (!config.addonConfig().showAddonItemsNEI()) {
            factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers1A));
            factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers2A));
            factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers4A));
            factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers2A));
            factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers4A));

            factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers1B));
            factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers2B));
            factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers4B));
            factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers2B));
            factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers4B));
        }
    }

    public static void postInit () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        if (!Loader.isModLoaded("RefinedRelocation") || !api.userConfig().integrationConfig().isRefinedRelocationEnabled())
            return;

        Block[] fullDrawers1 = new Block[] { fullDrawers1A, fullDrawers1B };
        Block[] fullDrawers2 = new Block[] { fullDrawers2A, fullDrawers2B };
        Block[] fullDrawers4 = new Block[] { fullDrawers4A, fullDrawers4B };
        Block[] halfDrawers2 = new Block[] { halfDrawers2A, halfDrawers2B };
        Block[] halfDrawers4 = new Block[] { halfDrawers4A, halfDrawers4B };

        Block[] mfullDrawers1 = new Block[] { ModBlocks.fullDrawers1A, ModBlocks.fullDrawers1B };
        Block[] mfullDrawers2 = new Block[] { ModBlocks.fullDrawers2A, ModBlocks.fullDrawers2B };
        Block[] mfullDrawers4 = new Block[] { ModBlocks.fullDrawers4A, ModBlocks.fullDrawers4B };
        Block[] mhalfDrawers2 = new Block[] { ModBlocks.halfDrawers2A, ModBlocks.halfDrawers2B };
        Block[] mhalfDrawers4 = new Block[] { ModBlocks.halfDrawers4A, ModBlocks.halfDrawers4B };

        IPackDataResolver[] resolver = new IPackDataResolver[] { StorageDrawersPack.instance.resolver1, StorageDrawersPack.instance.resolver2 };

        IBlockConfig blockConfig = api.userConfig().blockConfig();

        for (int p = 0; p < resolver.length; p++) {
            for (int i = 0; i < 16; i++) {
                if (!resolver[p].isValidMetaValue(i))
                    continue;

                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull1)))
                    GameRegistry.addRecipe(new ItemStack(fullDrawers1[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(mfullDrawers1[p], 1, i));
                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull2)))
                    GameRegistry.addRecipe(new ItemStack(fullDrawers2[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(mfullDrawers2[p], 1, i));
                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull4)))
                    GameRegistry.addRecipe(new ItemStack(halfDrawers2[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(mhalfDrawers2[p], 1, i));
                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf2)))
                    GameRegistry.addRecipe(new ItemStack(fullDrawers4[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(mfullDrawers4[p], 1, i));
                if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf4)))
                    GameRegistry.addRecipe(new ItemStack(halfDrawers4[p], 1, i), "x x", " y ", "x x",
                        'x', Items.gold_nugget, 'y', new ItemStack(mhalfDrawers4[p], 1, i));
            }
        }
    }
}
