package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import com.jaquadro.minecraft.storagedrawers.config.*;
import com.jaquadro.minecraft.storagedrawers.core.*;
import com.texelsaurus.minecraft.chameleon.service.ForgeConfig;
import com.texelsaurus.minecraft.chameleon.service.ForgeNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StorageDrawers.MOD_ID)
public class StorageDrawers
{
    public static final String MOD_ID = "storagedrawers";
    public static final Api api = new Api();
    public static Logger log = LogManager.getLogger();
    //public static ConfigManager config;

    //public static OreDictRegistry oreDictRegistry;

    //public static RenderRegistry renderRegistry;
    //public static WailaRegistry wailaRegistry;
    //public static SecurityRegistry securityRegistry;

    public StorageDrawers () {
        ModCommonConfig.INSTANCE.context().init();
        ModClientConfig.INSTANCE.context().init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ((ForgeConfig)ModCommonConfig.INSTANCE.context()).forgeSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ((ForgeConfig)ModClientConfig.INSTANCE.context()).forgeSpec);

        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.spec);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.spec);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModContainers.init();
        ModDataComponents.init();
        ModRecipes.init();

        bus.addListener(this::setup);
        // bus.addListener(this::onModQueueEvent);
        bus.addListener(this::onModConfigEvent);
        bus.addListener(ModCreativeTabs::init);
        bus.addListener(PlatformCapabilities::register);

        ForgeNetworking.init(ModNetworking.INSTANCE);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup (final FMLCommonSetupEvent event) {
        CompTierRegistry.INSTANCE.initialize();
        PlatformCapabilities.initHandlers();

        //oreDictRegistry = new OreDictRegistry();
        //renderRegistry = new RenderRegistry();
        //wailaRegistry = new WailaRegistry();
        //securityRegistry = new SecurityRegistry();

        //proxy.registerRenderers();

        //LocalIntegrationRegistry.instance().init();
        //compRegistry.initialize();

        //LocalIntegrationRegistry.instance().postInit();
    }

    /*
    @SuppressWarnings("Convert2MethodRef")  // otherwise the class loader gets upset if TheOneProbe is not loaded
    private void onModQueueEvent(final InterModEnqueueEvent event) {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> new TheOneProbe());
    }
    */

    private void onModConfigEvent(final ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON)
            ModCommonConfig.INSTANCE.setLoaded();
        if (event.getConfig().getType() == ModConfig.Type.CLIENT)
            ModClientConfig.INSTANCE.setLoaded();
    }

    @SubscribeEvent
    public void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        //ConfigManager.serverPlayerConfigSettings.remove(event.player.getUniqueID());
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
