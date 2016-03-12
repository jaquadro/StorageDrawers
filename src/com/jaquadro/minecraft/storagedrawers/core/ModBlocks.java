package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.*;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.item.ItemCompDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemBasicDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemController;
import com.jaquadro.minecraft.storagedrawers.item.ItemTrim;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class ModBlocks
{
    public static BlockDrawers basicDrawers;
    public static BlockCompDrawers compDrawers;
    public static BlockController controller;
    public static BlockSlave controllerSlave;
    public static BlockTrim trim;

    public void init () {
        //resolver = new DataResolver(StorageDrawers.MOD_ID);

        basicDrawers = new BlockDrawers("basicDrawers");
        compDrawers = new BlockCompDrawers("compDrawers");
        controller = new BlockController("controller");
        controllerSlave = new BlockSlave("controllerSlave");
        trim = new BlockTrim("trim");

        ConfigManager config = StorageDrawers.config;

        GameRegistry.registerBlock(basicDrawers, ItemBasicDrawers.class, "basicDrawers");
        GameRegistry.registerTileEntity(TileEntityDrawersStandard.class, ModBlocks.getQualifiedName(basicDrawers));

        if (config.isBlockEnabled("compdrawers")) {
            GameRegistry.registerBlock(compDrawers, ItemCompDrawers.class, "compDrawers");
            GameRegistry.registerTileEntity(TileEntityDrawersComp.class, ModBlocks.getQualifiedName(compDrawers));
        }
        if (config.isBlockEnabled("controller")) {
            GameRegistry.registerBlock(controller, ItemController.class, "controller");
            GameRegistry.registerTileEntity(TileEntityController.class, ModBlocks.getQualifiedName(controller));
        }
        if (config.isBlockEnabled("controllerSlave")) {
            GameRegistry.registerBlock(controllerSlave, "controllerSlave");
            GameRegistry.registerTileEntity(TileEntitySlave.class, ModBlocks.getQualifiedName(controllerSlave));
        }
        if (config.isBlockEnabled("trim")) {
            GameRegistry.registerBlock(trim, ItemTrim.class, "trim");
        }

        StorageDrawers.proxy.registerDrawer(basicDrawers);
        StorageDrawers.proxy.registerDrawer(compDrawers);

        for (String key : new String[] { "drawerBasic" })
            OreDictionary.registerOre(key, new ItemStack(basicDrawers, 1, OreDictionary.WILDCARD_VALUE));

        //resolver.init();
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block).toString();
    }
}
