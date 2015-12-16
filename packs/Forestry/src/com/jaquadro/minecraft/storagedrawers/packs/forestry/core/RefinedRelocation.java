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
import net.minecraft.block.Block;

public class RefinedRelocation
{
    public static Block[] fullDrawers1;
    public static Block[] fullDrawers2;
    public static Block[] fullDrawers4;
    public static Block[] halfDrawers2;
    public static Block[] halfDrawers4;
    public static Block[] trim;

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
        trim = new Block[resolvers.length];

        for (int i = 0; i < resolvers.length; i++) {
            IPackDataResolver resolver = resolvers[i];

            fullDrawers1[i] = factory.createBlock(BlockConfiguration.SortingFull1, resolver);
            fullDrawers2[i] = factory.createBlock(BlockConfiguration.SortingFull2, resolver);
            fullDrawers4[i] = factory.createBlock(BlockConfiguration.SortingFull4, resolver);
            halfDrawers2[i] = factory.createBlock(BlockConfiguration.SortingHalf2, resolver);
            halfDrawers4[i] = factory.createBlock(BlockConfiguration.SortingHalf4, resolver);
            trim[i] = factory.createBlock(BlockConfiguration.TrimSorting, resolver);

            factory.bindSortingBlock(ModBlocks.fullDrawers1[i], fullDrawers1[i]);
            factory.bindSortingBlock(ModBlocks.fullDrawers2[i], fullDrawers2[i]);
            factory.bindSortingBlock(ModBlocks.fullDrawers4[i], fullDrawers4[i]);
            factory.bindSortingBlock(ModBlocks.halfDrawers2[i], halfDrawers2[i]);
            factory.bindSortingBlock(ModBlocks.halfDrawers4[i], halfDrawers4[i]);
            factory.bindSortingBlock(ModBlocks.trim[i], trim[i]);
        }

        IUserConfig config = api.userConfig();
        IBlockConfig blockConfig = config.blockConfig();

        char[] tail = new char[] { 'A', 'B' };

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull1))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(fullDrawers1[i], "fullDrawersSort1" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull2))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(fullDrawers2[i], "fullDrawersSort2" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingFull4))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(fullDrawers4[i], "fullDrawersSort4" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf2))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(halfDrawers2[i], "halfDrawersSort2" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf4))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(halfDrawers4[i], "halfDrawersSort4" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.TrimSorting))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(trim[i], "trimSort" + tail[i]);
        }

        if (!config.addonConfig().showAddonItemsNEI()) {
            for (int i = 0; i < resolvers.length; i++) {
                factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers1[i]));
                factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers2[i]));
                factory.hideBlock(ModBlocks.getQualifiedName(fullDrawers4[i]));
                factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers2[i]));
                factory.hideBlock(ModBlocks.getQualifiedName(halfDrawers4[i]));
                factory.hideBlock(ModBlocks.getQualifiedName(trim[i]));
            }
        }
    }
}
