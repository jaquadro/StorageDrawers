package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.config.*;
import com.jaquadro.minecraft.storagedrawers.core.*;
import com.jaquadro.minecraft.storagedrawers.core.handlers.EventHandler;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import com.jaquadro.minecraft.storagedrawers.integration.LocalIntegrationRegistry;
import com.jaquadro.minecraft.storagedrawers.network.BoolConfigUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.security.SecurityRegistry;

import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

@Mod(modid = StorageDrawers.MOD_ID, name = StorageDrawers.MOD_NAME, version = StorageDrawers.MOD_VERSION,
    dependencies = "required-after:Forge@[12.17.0.1908,);required-after:Chameleon;after:waila;",
    guiFactory = StorageDrawers.SOURCE_PATH + "core.ModGuiFactory",
    acceptedMinecraftVersions = "[1.9,1.11)")
public class StorageDrawers
{
    public static final String MOD_ID = "StorageDrawers";
    public static final String MOD_NAME = "Storage Drawers";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawers.";

    public static final Api api = new Api();
    
    public static Logger logger;

    public static final ModBlocks blocks = new ModBlocks();
    public static final ModItems items = new ModItems();
    public static final ModRecipes recipes = new ModRecipes();

    public static SimpleNetworkWrapper network;
    public static ConfigManager config;
    public static CompTierRegistry compRegistry;
    public static OreDictRegistry oreDictRegistry;
    public static HashMap<Item , Integer> itemTracer = new HashMap<Item, Integer>();
    public static List<BlockPos> blocklock = new LinkedList<BlockPos>();

    public static RecipeHandlerRegistry recipeHandlerRegistry;
    public static RenderRegistry renderRegistry;
    public static WailaRegistry wailaRegistry;
    //public static BlockRegistry blockRegistry;
    public static SecurityRegistry securityRegistry;

    @Mod.Instance(MOD_ID)
    public static StorageDrawers instance;

    @SidedProxy(clientSide = SOURCE_PATH + "core.ClientProxy", serverSide = SOURCE_PATH + "core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        config = new ConfigManager(new File(event.getModConfigurationDirectory(), MOD_ID + ".cfg"));
        
        logger = event.getModLog();
        
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        network.registerMessage(BoolConfigUpdateMessage.Handler.class, BoolConfigUpdateMessage.class, 0, Side.SERVER);

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
        //blockRegistry = new BlockRegistry();
        securityRegistry = new SecurityRegistry();

        blocks.init();
        items.init();

        proxy.initDynamic();
        proxy.initClient();
        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(proxy);
        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        //MinecraftForge.EVENT_BUS.register(DrawerModelBakeEventHandler.instance);

        LocalIntegrationRegistry.instance().init();
        compRegistry.initialize();
    }

    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        recipes.init();

        LocalIntegrationRegistry.instance().postInit();
        //StorageDrawersApi.instance().packFactory().registerResolver(ModBlocks.resolver);
    }

    @SubscribeEvent
    public void onConfigChanged (ConfigChangedEvent.OnConfigChangedEvent event) {
        boolean preShiftValue = config.cache.invertShift;
        if (event.getModID().equals(MOD_ID))
            config.syncConfig();
        if (event.isWorldRunning() && preShiftValue != config.cache.invertShift) {
            StorageDrawers.network.sendToServer(new BoolConfigUpdateMessage(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID().toString(), "invertShift", config.cache.invertShift));
        }
    }

    @SubscribeEvent
    public void onPlayerDisconnect(PlayerLoggedOutEvent event) {
        ConfigManager.serverPlayerConfigSettings.remove(event.player.getUniqueID());
    }
}
