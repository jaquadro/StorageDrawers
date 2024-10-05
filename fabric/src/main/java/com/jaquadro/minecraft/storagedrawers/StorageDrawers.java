package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import com.jaquadro.minecraft.storagedrawers.config.CompTierRegistry;
import com.jaquadro.minecraft.storagedrawers.config.ModClientConfig;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.*;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class StorageDrawers implements ModInitializer
{
    public static final Api api = new Api();

    @Override
    public void onInitialize () {
        ModCommonConfig.INSTANCE.context().init();
        ModClientConfig.INSTANCE.context().init();

        ModBlocks.init();
        ModItems.init();
        ModCreativeTabs.init();
        ModBlockEntities.init();
        ModContainers.init();
        ModDataComponents.init();
        ModRecipes.init();

        ModNetworking.INSTANCE.init();
        CommonEvents.init();

        CompTierRegistry.INSTANCE.initialize();
        PlatformCapabilities.initHandlers();

        ModCommonConfig.INSTANCE.setLoaded();
        ModClientConfig.INSTANCE.setLoaded();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerList().getPlayers())
                PlayerEventListener.onPlayerTick(player);
        });
    }
}
