package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.item.ItemCompDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemBasicDrawers;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks
{
    //public static BlockDrawers fullDrawers1;
    //public static BlockDrawers fullDrawers2;
    //public static BlockDrawers fullDrawers4;
    //public static BlockDrawers halfDrawers2;
    //public static BlockDrawers halfDrawers4;
    public static BlockDrawers basicDrawers;
    public static BlockCompDrawers compDrawers;
    //public static BlockController controller;

    public void init () {
        /*fullDrawers1 = new BlockDrawers("fullDrawers1", 1, false);
        fullDrawers2 = new BlockDrawers("fullDrawers2", 2, false);
        fullDrawers4 = new BlockDrawers("fullDrawers4", 4, false);
        halfDrawers2 = new BlockDrawers("halfDrawers2", 2, true);
        halfDrawers4 = new BlockDrawers("halfDrawers4", 4, true);*/
        basicDrawers = new BlockDrawers("basicDrawers");
        compDrawers = new BlockCompDrawers("compDrawers");
        //controller = new BlockController("controller");

        ConfigManager config = StorageDrawers.config;

        GameRegistry.registerBlock(basicDrawers, ItemBasicDrawers.class, "basicDrawers");
        GameRegistry.registerTileEntity(TileEntityDrawersStandard.class, ModBlocks.getQualifiedName(basicDrawers));

        /*if (config.isBlockEnabled("fulldrawers1")) {
            GameRegistry.registerBlock(fullDrawers1, ItemDrawers.class, "fullDrawers1");
            GameRegistry.registerTileEntity(TileEntityDrawersStandard.class, ModBlocks.getQualifiedName(fullDrawers1));
        }
        if (config.isBlockEnabled("fulldrawers2")) {
            GameRegistry.registerBlock(fullDrawers2, ItemDrawers.class, "fullDrawers2");
            GameRegistry.registerTileEntity(TileEntityDrawersStandard.class, ModBlocks.getQualifiedName(fullDrawers2));
        }
        if (config.isBlockEnabled("fulldrawers4")) {
            GameRegistry.registerBlock(fullDrawers4, ItemDrawers.class, "fullDrawers4");
            GameRegistry.registerTileEntity(TileEntityDrawersStandard.class, ModBlocks.getQualifiedName(fullDrawers4));
        }
        if (config.isBlockEnabled("halfdrawers2")) {
            GameRegistry.registerBlock(halfDrawers2, ItemDrawers.class, "halfDrawers2");
            GameRegistry.registerTileEntity(TileEntityDrawersStandard.class, ModBlocks.getQualifiedName(halfDrawers2));
        }
        if (config.isBlockEnabled("halfdrawers4")) {
            GameRegistry.registerBlock(halfDrawers4, ItemDrawers.class, "halfDrawers4");
            GameRegistry.registerTileEntity(TileEntityDrawersStandard.class, ModBlocks.getQualifiedName(halfDrawers4));
        }*/
        if (config.isBlockEnabled("compdrawers")) {
            GameRegistry.registerBlock(compDrawers, ItemCompDrawers.class, "compDrawers");
            GameRegistry.registerTileEntity(TileEntityDrawersComp.class, ModBlocks.getQualifiedName(compDrawers));
        }
        if (config.isBlockEnabled("controller")) {
            //GameRegistry.registerBlock(controller, ItemController.class, "controller");
            //GameRegistry.registerTileEntity(TileEntityController.class, ModBlocks.getQualifiedName(controller));
        }
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block).toString();
    }
}
