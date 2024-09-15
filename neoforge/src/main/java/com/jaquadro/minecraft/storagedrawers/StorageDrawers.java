package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import com.jaquadro.minecraft.storagedrawers.config.*;
import com.jaquadro.minecraft.storagedrawers.core.*;
import com.jaquadro.minecraft.storagedrawers.core.recipe.AddUpgradeRecipe;
import com.jaquadro.minecraft.storagedrawers.core.recipe.KeyringRecipe;
import com.texelsaurus.minecraft.chameleon.service.NeoforgeConfig;
import com.texelsaurus.minecraft.chameleon.service.NeoforgeNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StorageDrawers.MOD_ID)
public class StorageDrawers
{
    public static final String MOD_ID = "storagedrawers";
    public static final Api api = new Api();
    public static Logger log = LogManager.getLogger();
    //public static ConfigManager config;
    //public static CompTierRegistry compRegistry;
    //public static OreDictRegistry oreDictRegistry;

    //public static RenderRegistry renderRegistry;
    //public static WailaRegistry wailaRegistry;
    //public static SecurityRegistry securityRegistry;

    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AddUpgradeRecipe>> UPGRADE_RECIPE_SERIALIZER = RECIPES.register("add_upgrade", () -> new SimpleCraftingRecipeSerializer<>(AddUpgradeRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<KeyringRecipe>> KEYRING_RECIPE_SERIALIZER = RECIPES.register("keyring", () -> new SimpleCraftingRecipeSerializer<>(KeyringRecipe::new));

    public StorageDrawers (ModContainer modContainer, IEventBus modEventBus) {
        ModCommonConfig.INSTANCE.context().init();
        ModClientConfig.INSTANCE.context().init();
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ((NeoforgeConfig)ModCommonConfig.INSTANCE.context()).neoSpec);
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, ((NeoforgeConfig)ModClientConfig.INSTANCE.context()).neoSpec);

        //modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.spec);
        //modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.spec);

        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModContainers.init();
        ModDataComponents.COMPONENTS.init();

        modEventBus.addListener(this::setup);
        //modEventBus.addListener(MessageHandler::register);
        //modEventBus.addListener(this::onModQueueEvent);
        modEventBus.addListener(this::onModConfigEvent);
        modEventBus.addListener(ModItems::creativeModeTabRegister);
        modEventBus.addListener(PlatformCapabilities::register);

        NeoforgeNetworking.init(MOD_ID, modEventBus, ModNetworking.INSTANCE);

        RECIPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    private void setup (final FMLCommonSetupEvent event) {
        //compRegistry = new CompTierRegistry();
        CompTierRegistry.INSTANCE.initialize();

        //oreDictRegistry = new OreDictRegistry();
        //renderRegistry = new RenderRegistry();
        //wailaRegistry = new WailaRegistry();
        //securityRegistry = new SecurityRegistry();

        //proxy.registerRenderers();

        //LocalIntegrationRegistry.instance().init();
        //compRegistry.initialize();

        //LocalIntegrationRegistry.instance().postInit();
    }

    @SuppressWarnings("Convert2MethodRef")  // otherwise the class loader gets upset if TheOneProbe is not loaded
    //private void onModQueueEvent(final InterModEnqueueEvent event) {
    //    InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> new TheOneProbe());
    //}
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
