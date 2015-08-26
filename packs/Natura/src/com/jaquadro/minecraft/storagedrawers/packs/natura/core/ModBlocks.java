package com.jaquadro.minecraft.storagedrawers.packs.natura.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.config.IUserConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlockFactory;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.packs.natura.StorageDrawersPack;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import java.util.Map;

public class ModBlocks
{
    public static Block fullDrawers1;
    public static Block fullDrawers2;
    public static Block fullDrawers4;
    public static Block halfDrawers2;
    public static Block halfDrawers4;
    public static Block trim;

    public void init () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        IPackBlockFactory factory = api.packFactory();
        IPackDataResolver resolver = StorageDrawersPack.instance.resolver;

        fullDrawers1 = factory.createBlock(BlockConfiguration.BasicFull1, resolver);
        fullDrawers2 = factory.createBlock(BlockConfiguration.BasicFull2, resolver);
        fullDrawers4 = factory.createBlock(BlockConfiguration.BasicFull4, resolver);
        halfDrawers2 = factory.createBlock(BlockConfiguration.BasicHalf2, resolver);
        halfDrawers4 = factory.createBlock(BlockConfiguration.BasicHalf4, resolver);
        trim = factory.createBlock(BlockConfiguration.Trim, resolver);

        IUserConfig config = api.userConfig();
        IBlockConfig blockConfig = config.blockConfig();

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull1)))
            factory.registerBlock(fullDrawers1, "fullDrawers1");
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull2)))
            factory.registerBlock(fullDrawers2, "fullDrawers2");
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull4)))
            factory.registerBlock(fullDrawers4, "fullDrawers4");
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf2)))
            factory.registerBlock(halfDrawers2, "halfDrawers2");
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf4)))
            factory.registerBlock(halfDrawers4, "halfDrawers4");
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.Trim)))
            factory.registerBlock(trim, "trim");

        if (!config.addonConfig().showAddonItemsNEI()) {
            factory.hideBlock(getQualifiedName(fullDrawers1));
            factory.hideBlock(getQualifiedName(fullDrawers2));
            factory.hideBlock(getQualifiedName(fullDrawers4));
            factory.hideBlock(getQualifiedName(halfDrawers2));
            factory.hideBlock(getQualifiedName(halfDrawers4));
            factory.hideBlock(getQualifiedName(trim));
        }

        addAlternativeTileEntityMappings(TileEntityDrawersStandard.class, getQualifiedName(fullDrawers1),
            getQualifiedName(fullDrawers2), getQualifiedName(fullDrawers4),
            getQualifiedName(halfDrawers2), getQualifiedName(halfDrawers4));
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block);
    }

    public static void addAlternativeTileEntityMappings (Class<? extends TileEntity> tileEntityClass, String... alternatives) {
        Map<String, Class<?>> teMappings = ObfuscationReflectionHelper.getPrivateValue(TileEntity.class, null, "field_" + "145855_i", "nameToClassMap");
        for (String s : alternatives) {
            if (!teMappings.containsKey(s)) {
                teMappings.put(s, tileEntityClass);
            }
        }
    }
}
