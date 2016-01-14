package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.config.*;
import com.jaquadro.minecraft.storagedrawers.core.*;
import com.jaquadro.minecraft.storagedrawers.core.api.BlockRegistry;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import com.jaquadro.minecraft.storagedrawers.integration.IntegrationRegistry;
import com.jaquadro.minecraft.storagedrawers.network.BlockClickMessage;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
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
    public static final String MOD_VERSION = "@VERSION@";
    public static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawers.";

    public static final Api api = new Api();

    public static final ModBlocks blocks = new ModBlocks();
    public static final ModItems items = new ModItems();
    public static final ModRecipes recipes = new ModRecipes();

    public static SimpleNetworkWrapper network;
    public static ConfigManager config;
    public static CompTierRegistry compRegistry;
    public static OreDictRegistry oreDictRegistry;

    public static RecipeHandlerRegistry recipeHandlerRegistry;
    public static RenderRegistry renderRegistry;
    public static WailaRegistry wailaRegistry;
    public static BlockRegistry blockRegistry;

    @Mod.Instance(MOD_ID)
    public static StorageDrawers instance;

    @SidedProxy(clientSide = SOURCE_PATH + "core.ClientProxy", serverSide = SOURCE_PATH + "core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        config = new ConfigManager(new File(event.getModConfigurationDirectory(), MOD_ID + ".cfg"));

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        network.registerMessage(BlockClickMessage.Handler.class, BlockClickMessage.class, 0, Side.SERVER);

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            network.registerMessage(CountUpdateMessage.Handler.class, CountUpdateMessage.class, 1, Side.CLIENT);
        }
        else {
            network.registerMessage(CountUpdateMessage.HandlerStub.class, CountUpdateMessage.class, 1, Side.CLIENT);
        }

        compRegistry = new CompTierRegistry();
        oreDictRegistry = new OreDictRegistry();
        recipeHandlerRegistry = new RecipeHandlerRegistry();
        renderRegistry = new RenderRegistry();
        wailaRegistry = new WailaRegistry();
        blockRegistry = new BlockRegistry();

        blocks.init();
        items.init();
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {
        proxy.registerRenderers();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        FMLCommonHandler.instance().bus().register(instance);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        IntegrationRegistry.instance().init();
    }

    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        recipes.init();

        IntegrationRegistry.instance().postInit();

        StorageDrawersApi.instance().packFactory().registerResolver(ModBlocks.resolver);
    }

    @SubscribeEvent
    public void onConfigChanged (ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(MOD_ID))
            config.syncConfig();
    }
}
