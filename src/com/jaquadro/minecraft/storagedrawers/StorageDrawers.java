package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.core.CommonProxy;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = StorageDrawers.MOD_ID, name = StorageDrawers.MOD_NAME, version = StorageDrawers.MOD_VERSION)
public class StorageDrawers
{
    public static final String MOD_ID = "StorageDrawers";
    public static final String MOD_NAME = "Storage Drawers";
    public static final String MOD_VERSION = "1.0.0";
    static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawers.";

    public static final ModBlocks blocks = new ModBlocks();

    @Mod.Instance(MOD_ID)
    public static StorageDrawers instance;

    @SidedProxy(clientSide = SOURCE_PATH + "core.ClientProxy", serverSide = SOURCE_PATH + "core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        blocks.init();
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {
        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {

    }
}
