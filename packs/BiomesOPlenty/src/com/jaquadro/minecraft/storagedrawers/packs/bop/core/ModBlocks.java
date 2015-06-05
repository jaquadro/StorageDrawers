package com.jaquadro.minecraft.storagedrawers.packs.bop.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlockFactory;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.integration.notenoughitems.NEIStorageDrawersConfig;
import com.jaquadro.minecraft.storagedrawers.packs.bop.StorageDrawersPack;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;

public class ModBlocks
{
    public static Block fullDrawers1;
    public static Block fullDrawers2;
    public static Block fullDrawers4;
    public static Block halfDrawers2;
    public static Block halfDrawers4;
    public static Block trim;

    public void init () {
        IPackBlockFactory factory = StorageDrawersApi.instance().packFactory();
        IPackDataResolver resolver = StorageDrawersPack.instance.resolver;

        fullDrawers1 = factory.createBlock(BlockConfiguration.BasicFull1, resolver);
        fullDrawers2 = factory.createBlock(BlockConfiguration.BasicFull2, resolver);
        fullDrawers4 = factory.createBlock(BlockConfiguration.BasicFull4, resolver);
        halfDrawers2 = factory.createBlock(BlockConfiguration.BasicHalf2, resolver);
        halfDrawers4 = factory.createBlock(BlockConfiguration.BasicHalf4, resolver);
        trim = factory.createBlock(BlockConfiguration.Trim, resolver);

        ConfigManager config = StorageDrawers.config;

        if (config.isBlockEnabled("fulldrawers1"))
            factory.registerBlock(fullDrawers1, "fullDrawers1");
        if (config.isBlockEnabled("fulldrawers2"))
            factory.registerBlock(fullDrawers2, "fullDrawers2");
        if (config.isBlockEnabled("fulldrawers4"))
            factory.registerBlock(fullDrawers4, "fullDrawers4");
        if (config.isBlockEnabled("halfdrawers2"))
            factory.registerBlock(halfDrawers2, "halfDrawers2");
        if (config.isBlockEnabled("halfdrawers4"))
            factory.registerBlock(halfDrawers4, "halfDrawers4");
        if (config.isBlockEnabled("trim"))
            factory.registerBlock(trim, "trim");

        if (!config.cache.addonShowNEI) {
            NEIStorageDrawersConfig.hideBlock(getQualifiedName(fullDrawers1));
            NEIStorageDrawersConfig.hideBlock(getQualifiedName(fullDrawers2));
            NEIStorageDrawersConfig.hideBlock(getQualifiedName(fullDrawers4));
            NEIStorageDrawersConfig.hideBlock(getQualifiedName(halfDrawers2));
            NEIStorageDrawersConfig.hideBlock(getQualifiedName(halfDrawers4));
            NEIStorageDrawersConfig.hideBlock(getQualifiedName(trim));
        }
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block);
    }

    public static String makeName (String name) {
        return StorageDrawersPack.MOD_ID.toLowerCase() + "." + name;
    }
}
