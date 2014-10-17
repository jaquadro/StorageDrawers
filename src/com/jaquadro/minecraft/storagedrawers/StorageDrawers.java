package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.config.CompTierRegistry;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.config.OreDictRegistry;
import com.jaquadro.minecraft.storagedrawers.core.*;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import com.jaquadro.minecraft.storagedrawers.network.BlockClickMessage;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@Mod(modid = StorageDrawers.MOD_ID, name = StorageDrawers.MOD_NAME, version = StorageDrawers.MOD_VERSION, dependencies = "after:waila;", guiFactory = StorageDrawers.SOURCE_PATH + "core.ModGuiFactory")
public class StorageDrawers
{
    public static final String MOD_ID = "StorageDrawers";
    public static final String MOD_NAME = "Storage Drawers";
    public static final String MOD_VERSION = "1.1.6";
    static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawers.";

    public static final ModBlocks blocks = new ModBlocks();
    public static final ModItems items = new ModItems();
    public static final ModRecipes recipes = new ModRecipes();
    public static final ModIntegration integration = new ModIntegration();

    public static SimpleNetworkWrapper network;
    public static ConfigManager config;
    public static CompTierRegistry compRegistry;
    public static OreDictRegistry oreDictRegistry;

    @Mod.Instance(MOD_ID)
    public static StorageDrawers instance;

    @SidedProxy(clientSide = SOURCE_PATH + "core.ClientProxy", serverSide = SOURCE_PATH + "core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        config = new ConfigManager(new File(event.getModConfigurationDirectory(), MOD_ID + ".cfg"));

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        network.registerMessage(BlockClickMessage.Handler.class, BlockClickMessage.class, 0, Side.SERVER);

        compRegistry = new CompTierRegistry();
        oreDictRegistry = new OreDictRegistry();

        blocks.init();
        items.init();

        if (Loader.isModLoaded("Waila")) {
            FMLInterModComms.sendMessage("Waila", "register", SOURCE_PATH + "compat.WailaProvider.registerProvider");
        }
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {
        proxy.registerRenderers();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        FMLCommonHandler.instance().bus().register(instance);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        integration.init();
    }

    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        recipes.init();
        integration.postInit();
    }

    @SubscribeEvent
    public void onConfigChanged (ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(MOD_ID))
            config.syncConfig();
    }
}
