package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import com.jaquadro.minecraft.storagedrawers.config.*;
import com.jaquadro.minecraft.storagedrawers.core.*;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import com.texelsaurus.minecraft.chameleon.service.ChameleonConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class StorageDrawers implements ModInitializer
{
    public static final Api api = new Api();

    @Override
    public void onInitialize () {
        ModCommonConfig.INSTANCE.context().init(ModConstants.MOD_ID, ChameleonConfig.Type.COMMON);
        ModClientConfig.INSTANCE.context().init(ModConstants.MOD_ID, ChameleonConfig.Type.CLIENT);

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

        CompTierRegistry.INSTANCE.initialize();
        StorageBlacklist.INSTANCE.initialize();
        MaterialBlacklist.INSTANCE.initialize();
        ConversionRegistry.INSTANCE.initialize();
    }
}
