package com.jaquadro.minecraft.storagedrawers.packs.forestry.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.config.IUserConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlockFactory;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.StorageDrawersPack;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;

public class ModBlocks
{
    public static Block[] fullDrawers1;
    public static Block[] fullDrawers2;
    public static Block[] fullDrawers4;
    public static Block[] halfDrawers2;
    public static Block[] halfDrawers4;
    public static Block[] trim;

    public void init () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
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

            fullDrawers1[i] = factory.createBlock(BlockConfiguration.BasicFull1, resolver);
            fullDrawers2[i] = factory.createBlock(BlockConfiguration.BasicFull2, resolver);
            fullDrawers4[i] = factory.createBlock(BlockConfiguration.BasicFull4, resolver);
            halfDrawers2[i] = factory.createBlock(BlockConfiguration.BasicHalf2, resolver);
            halfDrawers4[i] = factory.createBlock(BlockConfiguration.BasicHalf4, resolver);
            trim[i] = factory.createBlock(BlockConfiguration.Trim, resolver);
        }

        IUserConfig config = api.userConfig();
        IBlockConfig blockConfig = config.blockConfig();

        char[] tail = new char[] { 'A', 'B' };

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull1))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(fullDrawers1[i], "fullDrawers1" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull2))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(fullDrawers2[i], "fullDrawers2" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull4))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(fullDrawers4[i], "fullDrawers4" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf2))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(halfDrawers2[i], "halfDrawers2" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf4))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(halfDrawers4[i], "halfDrawers4" + tail[i]);
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.Trim))) {
            for (int i = 0; i < resolvers.length; i++)
                factory.registerBlock(trim[i], "trim" + tail[i]);
        }

        if (!config.addonConfig().showAddonItemsNEI()) {
            for (int i = 0; i < resolvers.length; i++) {
                factory.hideBlock(getQualifiedName(fullDrawers1[i]));
                factory.hideBlock(getQualifiedName(fullDrawers2[i]));
                factory.hideBlock(getQualifiedName(fullDrawers4[i]));
                factory.hideBlock(getQualifiedName(halfDrawers2[i]));
                factory.hideBlock(getQualifiedName(halfDrawers4[i]));
                factory.hideBlock(getQualifiedName(trim[i]));
            }
        }
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block);
    }
}
