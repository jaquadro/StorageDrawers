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
    public static Block fullDrawers1A;
    public static Block fullDrawers2A;
    public static Block fullDrawers4A;
    public static Block halfDrawers2A;
    public static Block halfDrawers4A;
    public static Block trimA;

    public static Block fullDrawers1B;
    public static Block fullDrawers2B;
    public static Block fullDrawers4B;
    public static Block halfDrawers2B;
    public static Block halfDrawers4B;
    public static Block trimB;

    public void init () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        IPackBlockFactory factory = api.packFactory();
        IPackDataResolver resolver1 = StorageDrawersPack.instance.resolver1;
        IPackDataResolver resolver2 = StorageDrawersPack.instance.resolver2;

        fullDrawers1A = factory.createBlock(BlockConfiguration.BasicFull1, resolver1);
        fullDrawers2A = factory.createBlock(BlockConfiguration.BasicFull2, resolver1);
        fullDrawers4A = factory.createBlock(BlockConfiguration.BasicFull4, resolver1);
        halfDrawers2A = factory.createBlock(BlockConfiguration.BasicHalf2, resolver1);
        halfDrawers4A = factory.createBlock(BlockConfiguration.BasicHalf4, resolver1);
        trimA = factory.createBlock(BlockConfiguration.Trim, resolver1);

        fullDrawers1B = factory.createBlock(BlockConfiguration.BasicFull1, resolver2);
        fullDrawers2B = factory.createBlock(BlockConfiguration.BasicFull2, resolver2);
        fullDrawers4B = factory.createBlock(BlockConfiguration.BasicFull4, resolver2);
        halfDrawers2B = factory.createBlock(BlockConfiguration.BasicHalf2, resolver2);
        halfDrawers4B = factory.createBlock(BlockConfiguration.BasicHalf4, resolver2);
        trimB = factory.createBlock(BlockConfiguration.Trim, resolver2);

        IUserConfig config = api.userConfig();
        IBlockConfig blockConfig = config.blockConfig();

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull1))) {
            factory.registerBlock(fullDrawers1A, "fullDrawers1A");
            factory.registerBlock(fullDrawers1B, "fullDrawers1B");
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull2))) {
            factory.registerBlock(fullDrawers2A, "fullDrawers2A");
            factory.registerBlock(fullDrawers2B, "fullDrawers2B");
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull4))) {
            factory.registerBlock(fullDrawers4A, "fullDrawers4A");
            factory.registerBlock(fullDrawers4B, "fullDrawers4B");
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf2))) {
            factory.registerBlock(halfDrawers2A, "halfDrawers2A");
            factory.registerBlock(halfDrawers2B, "halfDrawers2B");
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf4))) {
            factory.registerBlock(halfDrawers4A, "halfDrawers4A");
            factory.registerBlock(halfDrawers4B, "halfDrawers4B");
        }
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.Trim))) {
            factory.registerBlock(trimA, "trimA");
            factory.registerBlock(trimB, "trimB");
        }

        if (!config.addonConfig().showAddonItemsNEI()) {
            factory.hideBlock(getQualifiedName(fullDrawers1A));
            factory.hideBlock(getQualifiedName(fullDrawers2A));
            factory.hideBlock(getQualifiedName(fullDrawers4A));
            factory.hideBlock(getQualifiedName(halfDrawers2A));
            factory.hideBlock(getQualifiedName(halfDrawers4A));
            factory.hideBlock(getQualifiedName(trimA));

            factory.hideBlock(getQualifiedName(fullDrawers1B));
            factory.hideBlock(getQualifiedName(fullDrawers2B));
            factory.hideBlock(getQualifiedName(fullDrawers4B));
            factory.hideBlock(getQualifiedName(halfDrawers2B));
            factory.hideBlock(getQualifiedName(halfDrawers4B));
            factory.hideBlock(getQualifiedName(trimB));
        }
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block);
    }
}
