package com.jaquadro.minecraft.storagedrawers.packs.forestry.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.block.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.item.ItemDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.item.ItemTrimPack;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.StorageDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.block.BlockTrimPack;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks
{
    public static BlockDrawers fullDrawers1A;
    public static BlockDrawers fullDrawers2A;
    public static BlockDrawers fullDrawers4A;
    public static BlockDrawers halfDrawers2A;
    public static BlockDrawers halfDrawers4A;
    public static BlockTrim trimA;

    public static BlockDrawers fullDrawers1B;
    public static BlockDrawers fullDrawers2B;
    public static BlockDrawers fullDrawers4B;
    public static BlockDrawers halfDrawers2B;
    public static BlockDrawers halfDrawers4B;
    public static BlockTrim trimB;

    public void init () {
        fullDrawers1A = new BlockDrawersPack(makeName("fullDrawers1"), 1, false, 0);
        fullDrawers2A = new BlockDrawersPack(makeName("fullDrawers2"), 2, false, 0);
        fullDrawers4A = new BlockDrawersPack(makeName("fullDrawers4"), 4, false, 0);
        halfDrawers2A = new BlockDrawersPack(makeName("halfDrawers2"), 2, true, 0);
        halfDrawers4A = new BlockDrawersPack(makeName("halfDrawers4"), 4, true, 0);
        trimA = new BlockTrimPack(makeName("trim"), 0);

        fullDrawers1B = new BlockDrawersPack(makeName("fullDrawers1"), 1, false, 1);
        fullDrawers2B = new BlockDrawersPack(makeName("fullDrawers2"), 2, false, 1);
        fullDrawers4B = new BlockDrawersPack(makeName("fullDrawers4"), 4, false, 1);
        halfDrawers2B = new BlockDrawersPack(makeName("halfDrawers2"), 2, true, 1);
        halfDrawers4B = new BlockDrawersPack(makeName("halfDrawers4"), 4, true, 1);
        trimB = new BlockTrimPack(makeName("trim"), 1);

        ConfigManager config = StorageDrawers.config;

        if (config.isBlockEnabled("fulldrawers1")) {
            GameRegistry.registerBlock(fullDrawers1A, ItemDrawersPack.class, "fullDrawers1A");
            GameRegistry.registerBlock(fullDrawers1B, ItemDrawersPack.class, "fullDrawers1B");
        }
        if (config.isBlockEnabled("fulldrawers2")) {
            GameRegistry.registerBlock(fullDrawers2A, ItemDrawersPack.class, "fullDrawers2A");
            GameRegistry.registerBlock(fullDrawers2B, ItemDrawersPack.class, "fullDrawers2B");
        }
        if (config.isBlockEnabled("fulldrawers4")) {
            GameRegistry.registerBlock(fullDrawers4A, ItemDrawersPack.class, "fullDrawers4A");
            GameRegistry.registerBlock(fullDrawers4B, ItemDrawersPack.class, "fullDrawers4B");
        }
        if (config.isBlockEnabled("halfdrawers2")) {
            GameRegistry.registerBlock(halfDrawers2A, ItemDrawersPack.class, "halfDrawers2A");
            GameRegistry.registerBlock(halfDrawers2B, ItemDrawersPack.class, "halfDrawers2B");
        }
        if (config.isBlockEnabled("halfdrawers4")) {
            GameRegistry.registerBlock(halfDrawers4A, ItemDrawersPack.class, "halfDrawers4A");
            GameRegistry.registerBlock(halfDrawers4B, ItemDrawersPack.class, "halfDrawers4B");
        }
        if (config.isBlockEnabled("trim")) {
            GameRegistry.registerBlock(trimA, ItemTrimPack.class, "trimA");
            GameRegistry.registerBlock(trimB, ItemTrimPack.class, "trimB");
        }
    }

    public static String makeName (String name) {
        return StorageDrawersPack.MOD_ID.toLowerCase() + "." + name;
    }
}
