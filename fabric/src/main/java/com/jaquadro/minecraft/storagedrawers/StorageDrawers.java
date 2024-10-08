package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import com.jaquadro.minecraft.storagedrawers.config.CompTierRegistry;
import com.jaquadro.minecraft.storagedrawers.config.ModClientConfig;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.*;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import com.texelsaurus.minecraft.chameleon.service.FabricConfig;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.neoforged.fml.config.ModConfig;

public class StorageDrawers implements ModInitializer
{
    public static final Api api = new Api();

    @Override
    public void onInitialize () {
        ModCommonConfig.INSTANCE.context().init();
        ModClientConfig.INSTANCE.context().init();

        // Provided by Forge Config API Port
        NeoForgeModConfigEvents.loading(ModConstants.MOD_ID).register(this::onModConfigEvent);
        NeoForgeConfigRegistry.INSTANCE.register(ModConstants.MOD_ID, ModConfig.Type.COMMON, ((FabricConfig)ModCommonConfig.INSTANCE.context()).neoSpec);
        NeoForgeConfigRegistry.INSTANCE.register(ModConstants.MOD_ID, ModConfig.Type.CLIENT, ((FabricConfig)ModClientConfig.INSTANCE.context()).neoSpec);

        ChameleonInit.InitContext context = new ChameleonInit.InitContext();

        ModBlocks.init(context);
        ModItems.init(context);
        ModCreativeTabs.init(context);
        ModBlockEntities.init(context);
        ModContainers.init(context);
        ModDataComponents.init(context);
        ModRecipes.init(context);

        ModNetworking.INSTANCE.init(context);
        CommonEvents.init();

        PlatformCapabilities.initHandlers();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerList().getPlayers())
                PlayerEventListener.onPlayerTick(player);
        });
    }

    private void onModConfigEvent(final ModConfig config) {
        if (config.getType() == ModConfig.Type.COMMON) {
            ModCommonConfig.INSTANCE.setLoaded();
            CompTierRegistry.INSTANCE.initialize();
        }
        if (config.getType() == ModConfig.Type.CLIENT)
            ModClientConfig.INSTANCE.setLoaded();
    }
}
