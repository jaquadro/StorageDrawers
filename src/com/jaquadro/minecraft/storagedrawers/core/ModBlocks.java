package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.*;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.item.*;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModBlocks
{
    public static DataResolver resolver;

    public static BlockDrawers fullDrawers1;
    public static BlockDrawers fullDrawers2;
    public static BlockDrawers fullDrawers4;
    public static BlockDrawers halfDrawers2;
    public static BlockDrawers halfDrawers4;
    public static BlockCompDrawers compDrawers;
    public static BlockController controller;
    public static BlockTrim trim;
    public static BlockSlave controllerSlave;
    public static BlockFramingTable framingTable;

    public static BlockDrawersCustom fullCustom1;
    public static BlockDrawersCustom fullCustom2;
    public static BlockDrawersCustom fullCustom4;
    public static BlockDrawersCustom halfCustom2;
    public static BlockDrawersCustom halfCustom4;

    public void init () {
        resolver = new DataResolver(StorageDrawers.MOD_ID);

        fullDrawers1 = new BlockDrawers("fullDrawers1", 1, false);
        fullDrawers2 = new BlockDrawers("fullDrawers2", 2, false);
        fullDrawers4 = new BlockDrawers("fullDrawers4", 4, false);
        halfDrawers2 = new BlockDrawers("halfDrawers2", 2, true);
        halfDrawers4 = new BlockDrawers("halfDrawers4", 4, true);
        compDrawers = new BlockCompDrawers("compDrawers");
        controller = new BlockController("drawerController");
        trim = new BlockTrim("trim");
        controllerSlave = new BlockSlave("controllerSlave");
        framingTable = new BlockFramingTable("framingTable");

        fullCustom1 = new BlockDrawersCustom("fullCustom1", 1, false);
        fullCustom2 = new BlockDrawersCustom("fullCustom2", 2, false);
        fullCustom4 = new BlockDrawersCustom("fullCustom4", 4, false);
        halfCustom2 = new BlockDrawersCustom("halfCustom2", 2, true);
        halfCustom4 = new BlockDrawersCustom("halfCustom4", 4, true);

        ConfigManager config = StorageDrawers.config;

        if (config.isBlockEnabled("fulldrawers1"))
            GameRegistry.registerBlock(fullDrawers1, ItemBasicDrawers.class, "fullDrawers1");
        if (config.isBlockEnabled("fulldrawers2"))
            GameRegistry.registerBlock(fullDrawers2, ItemBasicDrawers.class, "fullDrawers2");
        if (config.isBlockEnabled("fulldrawers4"))
            GameRegistry.registerBlock(fullDrawers4, ItemBasicDrawers.class, "fullDrawers4");
        if (config.isBlockEnabled("halfdrawers2"))
            GameRegistry.registerBlock(halfDrawers2, ItemBasicDrawers.class, "halfDrawers2");
        if (config.isBlockEnabled("halfdrawers4"))
            GameRegistry.registerBlock(halfDrawers4, ItemBasicDrawers.class, "halfDrawers4");
        if (config.isBlockEnabled("compdrawers"))
            GameRegistry.registerBlock(compDrawers, ItemCompDrawers.class, "compDrawers");
        if (config.isBlockEnabled("controller"))
            GameRegistry.registerBlock(controller, ItemController.class, "controller");
        if (config.isBlockEnabled("controllerSlave"))
            GameRegistry.registerBlock(controllerSlave, "controllerSlave");
        if (config.isBlockEnabled("trim"))
            GameRegistry.registerBlock(trim, ItemTrim.class, "trim");

        if (config.cache.enableFramedDrawers) {
            GameRegistry.registerBlock(framingTable, ItemFramingTable.class, "framingTable");

            if (config.isBlockEnabled("fulldrawers1"))
                GameRegistry.registerBlock(fullCustom1, ItemCustomDrawers.class, "fullCustom1");
            if (config.isBlockEnabled("fulldrawers2"))
                GameRegistry.registerBlock(fullCustom2, ItemCustomDrawers.class, "fullCustom2");
            if (config.isBlockEnabled("fulldrawers4"))
                GameRegistry.registerBlock(fullCustom4, ItemCustomDrawers.class, "fullCustom4");
            if (config.isBlockEnabled("halfdrawers2"))
                GameRegistry.registerBlock(halfCustom2, ItemCustomDrawers.class, "halfCustom2");
            if (config.isBlockEnabled("halfdrawers4"))
                GameRegistry.registerBlock(halfCustom4, ItemCustomDrawers.class, "halfCustom4");
        }

        GameRegistry.registerTileEntityWithAlternatives(TileEntityDrawersStandard.class, getQualifiedName("tileDrawersStandard"),
            getQualifiedName(fullDrawers1), getQualifiedName(fullDrawers2), getQualifiedName(fullDrawers4),
            getQualifiedName(halfDrawers2), getQualifiedName(halfDrawers4));

        GameRegistry.registerTileEntityWithAlternatives(TileEntityDrawersComp.class, getQualifiedName("tileDrawersComp"),
            getQualifiedName(compDrawers));

        GameRegistry.registerTileEntityWithAlternatives(TileEntityController.class, getQualifiedName("tileController"),
            getQualifiedName(controller));

        GameRegistry.registerTileEntityWithAlternatives(TileEntitySlave.class, getQualifiedName("tileControllerSlave"),
            getQualifiedName(controllerSlave));

        GameRegistry.registerTileEntity(TileEntityFramingTable.class, getQualifiedName("framingTable"));

        StorageDrawers.proxy.registerDrawer(fullDrawers1);
        StorageDrawers.proxy.registerDrawer(fullDrawers2);
        StorageDrawers.proxy.registerDrawer(fullDrawers4);
        StorageDrawers.proxy.registerDrawer(halfDrawers2);
        StorageDrawers.proxy.registerDrawer(halfDrawers4);
        StorageDrawers.proxy.registerDrawer(compDrawers);

        StorageDrawers.proxy.registerDrawer(fullCustom1);
        StorageDrawers.proxy.registerDrawer(fullCustom2);
        StorageDrawers.proxy.registerDrawer(fullCustom4);
        StorageDrawers.proxy.registerDrawer(halfCustom2);
        StorageDrawers.proxy.registerDrawer(halfCustom4);

        for (String key : new String[] { "drawerBasic" })
            OreDictionary.registerOre(key, new ItemStack(fullDrawers1, 1, OreDictionary.WILDCARD_VALUE));
        for (String key : new String[] { "drawerBasic" })
            OreDictionary.registerOre(key, new ItemStack(fullDrawers2, 1, OreDictionary.WILDCARD_VALUE));
        for (String key : new String[] { "drawerBasic" })
            OreDictionary.registerOre(key, new ItemStack(fullDrawers4, 1, OreDictionary.WILDCARD_VALUE));
        for (String key : new String[] { "drawerBasic" })
            OreDictionary.registerOre(key, new ItemStack(halfDrawers2, 1, OreDictionary.WILDCARD_VALUE));
        for (String key : new String[] { "drawerBasic" })
            OreDictionary.registerOre(key, new ItemStack(halfDrawers4, 1, OreDictionary.WILDCARD_VALUE));

        resolver.init();
    }

    public static String getQualifiedName (String name) {
        return StorageDrawers.MOD_ID + ":" + name;
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block);
    }
}
