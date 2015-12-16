package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.item.ItemCompDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemController;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemTrim;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModBlocks
{
    public static BlockDrawers fullDrawers1;
    public static BlockDrawers fullDrawers2;
    public static BlockDrawers fullDrawers4;
    public static BlockDrawers halfDrawers2;
    public static BlockDrawers halfDrawers4;
    public static BlockCompDrawers compDrawers;
    public static BlockController controller;
    public static BlockTrim trim;
    public static BlockSlave controllerSlave;

    public static BlockDrawersCustom fullCustom1;
    public static BlockDrawersCustom fullCustom2;

    public void init () {
        fullDrawers1 = new BlockDrawers("fullDrawers1", 1, false);
        fullDrawers2 = new BlockDrawers("fullDrawers2", 2, false);
        fullDrawers4 = new BlockDrawers("fullDrawers4", 4, false);
        halfDrawers2 = new BlockDrawers("halfDrawers2", 2, true);
        halfDrawers4 = new BlockDrawers("halfDrawers4", 4, true);
        compDrawers = new BlockCompDrawers("compDrawers");
        controller = new BlockController("drawerController");
        trim = new BlockTrim("trim");
        controllerSlave = new BlockSlave("controllerSlave");

        fullCustom1 = new BlockDrawersCustom("fullCustom1", 1, false);
        fullCustom2 = new BlockDrawersCustom("fullCustom2", 2, false);

        ConfigManager config = StorageDrawers.config;

        if (config.isBlockEnabled("fulldrawers1"))
            GameRegistry.registerBlock(fullDrawers1, ItemDrawers.class, "fullDrawers1");
        if (config.isBlockEnabled("fulldrawers2"))
            GameRegistry.registerBlock(fullDrawers2, ItemDrawers.class, "fullDrawers2");
        if (config.isBlockEnabled("fulldrawers4"))
            GameRegistry.registerBlock(fullDrawers4, ItemDrawers.class, "fullDrawers4");
        if (config.isBlockEnabled("halfdrawers2"))
            GameRegistry.registerBlock(halfDrawers2, ItemDrawers.class, "halfDrawers2");
        if (config.isBlockEnabled("halfdrawers4"))
            GameRegistry.registerBlock(halfDrawers4, ItemDrawers.class, "halfDrawers4");
        if (config.isBlockEnabled("compdrawers"))
            GameRegistry.registerBlock(compDrawers, ItemCompDrawers.class, "compDrawers");
        if (config.isBlockEnabled("controller"))
            GameRegistry.registerBlock(controller, ItemController.class, "controller");
        if (config.isBlockEnabled("controllerSlave"))
            GameRegistry.registerBlock(controllerSlave, "controllerSlave");
        if (config.isBlockEnabled("trim"))
            GameRegistry.registerBlock(trim, ItemTrim.class, "trim");

        GameRegistry.registerBlock(fullCustom1, ItemDrawers.class, "fullCustom1");
        GameRegistry.registerBlock(fullCustom2, ItemDrawers.class, "fullCustom2");

        GameRegistry.registerTileEntityWithAlternatives(TileEntityDrawersStandard.class, getQualifiedName("tileDrawersStandard"),
            getQualifiedName(fullDrawers1), getQualifiedName(fullDrawers2), getQualifiedName(fullDrawers4),
            getQualifiedName(halfDrawers2), getQualifiedName(halfDrawers4));

        GameRegistry.registerTileEntityWithAlternatives(TileEntityDrawersComp.class, getQualifiedName("tileDrawersComp"),
            getQualifiedName(compDrawers));

        GameRegistry.registerTileEntityWithAlternatives(TileEntityController.class, getQualifiedName("tileController"),
            getQualifiedName(controller));

        GameRegistry.registerTileEntityWithAlternatives(TileEntitySlave.class, getQualifiedName("tileControllerSlave"),
            getQualifiedName(controllerSlave));

        StorageDrawers.proxy.registerDrawer(fullDrawers1);
        StorageDrawers.proxy.registerDrawer(fullDrawers2);
        StorageDrawers.proxy.registerDrawer(fullDrawers4);
        StorageDrawers.proxy.registerDrawer(halfDrawers2);
        StorageDrawers.proxy.registerDrawer(halfDrawers4);
        StorageDrawers.proxy.registerDrawer(compDrawers);

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

        for (String key : new String[] { "drawerBasic" })
            OreDictionary.registerOre(key, new ItemStack(fullCustom1, 1, OreDictionary.WILDCARD_VALUE));
    }

    public static String getQualifiedName (String name) {
        return StorageDrawers.MOD_ID + ":" + name;
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block);
    }
}
