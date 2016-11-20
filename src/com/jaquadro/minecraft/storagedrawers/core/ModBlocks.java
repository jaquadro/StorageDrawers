package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.resources.ModelRegistry;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.*;
import com.jaquadro.minecraft.storagedrawers.client.model.*;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityFramingRenderer;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.item.*;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ModBlocks
{
    public static BlockDrawers basicDrawers;
    public static BlockCompDrawers compDrawers;
    public static BlockController controller;
    public static BlockSlave controllerSlave;
    public static BlockTrim trim;
    public static BlockFramingTable framingTable;
    public static BlockDrawersCustom customDrawers;
    public static BlockTrimCustom customTrim;

    public void init () {
        //resolver = new DataResolver(StorageDrawers.MOD_ID);

        basicDrawers = new BlockDrawers("basicdrawers", "basicDrawers");
        compDrawers = new BlockCompDrawers("compdrawers", "compDrawers");
        controller = new BlockController("controller");
        controllerSlave = new BlockSlave("controllerslave", "controllerSlave");
        trim = new BlockTrim("trim", "trim");
        framingTable = new BlockFramingTable("framingtable", "framingTable");
        customDrawers = new BlockDrawersCustom("customdrawers", "customDrawers");
        customTrim = new BlockTrimCustom("customtrim", "customTrim");

        ConfigManager config = StorageDrawers.config;

        GameRegistry.register(basicDrawers);
        GameRegistry.register(new ItemBasicDrawers(basicDrawers).setRegistryName(basicDrawers.getRegistryName()));
        GameRegistry.registerTileEntity(TileEntityDrawersStandard.class, basicDrawers.getRegistryName().toString());

        if (config.isBlockEnabled("compdrawers")) {
            GameRegistry.register(compDrawers);
            GameRegistry.register(new ItemCompDrawers(compDrawers).setRegistryName(compDrawers.getRegistryName()));
            GameRegistry.registerTileEntity(TileEntityDrawersComp.class, compDrawers.getRegistryName().toString());
        }
        if (config.isBlockEnabled("controller")) {
            GameRegistry.register(controller);
            GameRegistry.register(new ItemController(controller).setRegistryName(controller.getRegistryName()));
            GameRegistry.registerTileEntity(TileEntityController.class, controller.getRegistryName().toString());
        }
        if (config.isBlockEnabled("controllerSlave")) {
            GameRegistry.register(controllerSlave);
            GameRegistry.register(new ItemBlock(controllerSlave).setRegistryName(controllerSlave.getRegistryName()));
            GameRegistry.registerTileEntity(TileEntitySlave.class, controllerSlave.getRegistryName().toString());
        }
        if (config.isBlockEnabled("trim")) {
            GameRegistry.register(trim);
            GameRegistry.register(new ItemTrim(trim).setRegistryName(trim.getRegistryName()));
        }

        if (config.cache.enableFramedDrawers) {
            GameRegistry.register(framingTable);
            GameRegistry.register(new ItemFramingTable(framingTable).setRegistryName(framingTable.getRegistryName()));
            GameRegistry.registerTileEntity(TileEntityFramingTable.class, framingTable.getRegistryName().toString());

            GameRegistry.register(customDrawers);
            GameRegistry.register(new ItemCustomDrawers(customDrawers).setRegistryName(customDrawers.getRegistryName()));

            GameRegistry.register(customTrim);
            GameRegistry.register(new ItemCustomTrim(customTrim).setRegistryName(customTrim.getRegistryName()));
            GameRegistry.registerTileEntity(TileEntityTrim.class, trim.getRegistryName().toString());
        }

        StorageDrawers.proxy.registerDrawer(basicDrawers);
        StorageDrawers.proxy.registerDrawer(compDrawers);

        for (String key : new String[] { "drawerBasic" })
            OreDictionary.registerOre(key, new ItemStack(basicDrawers, 1, OreDictionary.WILDCARD_VALUE));

        //resolver.init();
    }

    @SideOnly(Side.CLIENT)
    public void initDynamic () {
        basicDrawers.initDynamic();
        compDrawers.initDynamic();
        customDrawers.initDynamic();
    }

    @SideOnly(Side.CLIENT)
    public void initClient () {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.class, new TileEntityDrawersRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersComp.class, new TileEntityDrawersRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFramingTable.class, new TileEntityFramingRenderer());

        ModelRegistry modelRegistry = Chameleon.instance.modelRegistry;

        modelRegistry.registerModel(new BasicDrawerModel.Register());
        modelRegistry.registerModel(new CompDrawerModel.Register());
        modelRegistry.registerModel(new FramingTableModel.Register());
        modelRegistry.registerModel(new CustomDrawerModel.Register());
        modelRegistry.registerModel(new CustomTrimModel.Register());

        modelRegistry.registerItemVariants(trim);
        modelRegistry.registerItemVariants(controller);
        modelRegistry.registerItemVariants(controllerSlave);
    }
}
