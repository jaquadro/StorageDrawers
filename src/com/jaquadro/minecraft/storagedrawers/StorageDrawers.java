package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.core.*;
import com.jaquadro.minecraft.storagedrawers.network.BlockClickMessage;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = StorageDrawers.MOD_ID, name = StorageDrawers.MOD_NAME, version = StorageDrawers.MOD_VERSION, dependencies = "after:waila;")
public class StorageDrawers
{
    public static final String MOD_ID = "StorageDrawers";
    public static final String MOD_NAME = "Storage Drawers";
    public static final String MOD_VERSION = "1.0.0";
    static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawers.";

    public static final ModBlocks blocks = new ModBlocks();
    public static final ModItems items = new ModItems();
    public static final ModRecipes recipes = new ModRecipes();

    public static SimpleNetworkWrapper network;

    @Mod.Instance(MOD_ID)
    public static StorageDrawers instance;

    @SidedProxy(clientSide = SOURCE_PATH + "core.ClientProxy", serverSide = SOURCE_PATH + "core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        network.registerMessage(BlockClickMessage.Handler.class, BlockClickMessage.class, 0, Side.SERVER);

        blocks.init();
        items.init();

        if (Loader.isModLoaded("Waila")) {
            FMLInterModComms.sendMessage("Waila", "register", SOURCE_PATH + "compat.WailaProvider.registerProvider");
        }
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {
        proxy.registerRenderers();

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
    }

    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        recipes.init();
    }
}
