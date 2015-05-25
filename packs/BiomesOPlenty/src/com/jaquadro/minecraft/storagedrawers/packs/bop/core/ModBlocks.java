package com.jaquadro.minecraft.storagedrawers.packs.bop.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.packs.bop.block.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.bop.item.ItemDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.bop.item.ItemTrimPack;
import com.jaquadro.minecraft.storagedrawers.packs.bop.StorageDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.bop.block.BlockTrimPack;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks
{
    public static BlockDrawers fullDrawers1;
    public static BlockDrawers fullDrawers2;
    public static BlockDrawers fullDrawers4;
    public static BlockDrawers halfDrawers2;
    public static BlockDrawers halfDrawers4;
    public static BlockTrim trim;

    public void init () {
        fullDrawers1 = new BlockDrawersPack(makeName("fullDrawers1"), 1, false);
        fullDrawers2 = new BlockDrawersPack(makeName("fullDrawers2"), 2, false);
        fullDrawers4 = new BlockDrawersPack(makeName("fullDrawers4"), 4, false);
        halfDrawers2 = new BlockDrawersPack(makeName("halfDrawers2"), 2, true);
        halfDrawers4 = new BlockDrawersPack(makeName("halfDrawers4"), 4, true);
        trim = new BlockTrimPack(makeName("trim"));

        ConfigManager config = StorageDrawers.config;

        if (config.isBlockEnabled("fulldrawers1"))
            GameRegistry.registerBlock(fullDrawers1, ItemDrawersPack.class, "fullDrawers1");
        if (config.isBlockEnabled("fulldrawers2"))
            GameRegistry.registerBlock(fullDrawers2, ItemDrawersPack.class, "fullDrawers2");
        if (config.isBlockEnabled("fulldrawers4"))
            GameRegistry.registerBlock(fullDrawers4, ItemDrawersPack.class, "fullDrawers4");
        if (config.isBlockEnabled("halfdrawers2"))
            GameRegistry.registerBlock(halfDrawers2, ItemDrawersPack.class, "halfDrawers2");
        if (config.isBlockEnabled("halfdrawers4"))
            GameRegistry.registerBlock(halfDrawers4, ItemDrawersPack.class, "halfDrawers4");
        if (config.isBlockEnabled("trim"))
            GameRegistry.registerBlock(trim, ItemTrimPack.class, "trim");
    }

    public static String makeName (String name) {
        return StorageDrawersPack.MOD_ID.toLowerCase() + "." + name;
    }
}
