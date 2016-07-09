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
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameData;
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

        basicDrawers = new BlockDrawers("basicDrawers");
        compDrawers = new BlockCompDrawers("compDrawers");
        controller = new BlockController("controller");
        controllerSlave = new BlockSlave("controllerSlave");
        trim = new BlockTrim("trim");
        framingTable = new BlockFramingTable("framingTable");
        customDrawers = new BlockDrawersCustom("customDrawers");
        customTrim = new BlockTrimCustom("customTrim");

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
        if (config.isBlockEnabled("trim"))
            GameRegistry.registerBlock(trim, ItemTrim.class, "trim");

        if (config.cache.enableFramedDrawers) {
            GameRegistry.registerBlock(framingTable, ItemFramingTable.class, "framingTable");
            GameRegistry.registerTileEntity(TileEntityFramingTable.class, ModBlocks.getQualifiedName(framingTable));

            GameRegistry.registerBlock(customDrawers, ItemCustomDrawers.class, "customDrawers");

            GameRegistry.registerBlock(customTrim, ItemCustomTrim.class, "customTrim");
            GameRegistry.registerTileEntity(TileEntityTrim.class, ModBlocks.getQualifiedName(trim));
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

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block).toString();
    }
}
