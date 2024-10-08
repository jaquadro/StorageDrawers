package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import com.jaquadro.minecraft.storagedrawers.config.*;
import com.jaquadro.minecraft.storagedrawers.core.*;
import com.jaquadro.minecraft.storagedrawers.network.PlayerBoolConfigMessage;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.registry.ForgeRegistryContext;
import com.texelsaurus.minecraft.chameleon.service.ForgeConfig;
import com.texelsaurus.minecraft.chameleon.service.ForgeNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

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
        ForgeRegistryContext context = new ForgeRegistryContext(bus);

        ModBlocks.init(context);
        ModItems.init(context);
        ModBlockEntities.init(context);
        ModContainers.init(context);
        ModDataComponents.init(context);
        ModRecipes.init(context);

        bus.addListener(this::setup);
        // bus.addListener(this::onModQueueEvent);
        bus.addListener(this::onModConfigEvent);
        bus.addListener(ModCreativeTabs::init);
        bus.addListener(PlatformCapabilities::register);

        ForgeNetworking.init(ModNetworking.INSTANCE, context);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PlayerEventListener());
    }

    private void setup (final FMLCommonSetupEvent event) {
        CompTierRegistry.INSTANCE.initialize();
        PlatformCapabilities.initHandlers();

        var map = BuiltInRegistries.DATA_COMPONENT_TYPE.asHolderIdMap();
        for (int i = 0; i < map.size(); i++)
            ModServices.log.info(i + "=" + map.byId(i).get());

        var menumap = BuiltInRegistries.MENU.asHolderIdMap();
        for (int i = 0; i < menumap.size(); i++)
            ModServices.log.info(i + "=" + menumap.byId(i).getRegisteredName());

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

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() || !(event.getEntity() instanceof Player))
            return;

        if (Minecraft.getInstance().player == null)
            return;

        UUID playerId = Minecraft.getInstance().player.getUUID();
        if (event.getEntity().getUUID() == playerId) {
            ChameleonServices.NETWORK.sendToServer(new PlayerBoolConfigMessage(playerId.toString(), "invertShift", ModClientConfig.INSTANCE.GENERAL.invertShift.get()));
            ChameleonServices.NETWORK.sendToServer(new PlayerBoolConfigMessage(playerId.toString(), "invertClick", ModClientConfig.INSTANCE.GENERAL.invertClick.get()));
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
