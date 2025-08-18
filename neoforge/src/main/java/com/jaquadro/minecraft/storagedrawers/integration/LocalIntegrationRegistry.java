package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.integration.ftb.FTBChunksModule;
import com.jaquadro.minecraft.storagedrawers.integration.ftb.FTBTeamsModule;
import net.neoforged.fml.ModList;

public class LocalIntegrationRegistry
{
    private static LocalIntegrationRegistry instance;

    public static void initialize () {
        IntegrationRegistry reg = instance();
        if (ModList.get().isLoaded("ftbteams") && ModCommonConfig.INSTANCE.INTEGRATION.ftbTeams.enable.get())
            reg.add(new FTBTeamsModule());
        if (ModList.get().isLoaded("ftbchunks") && ModCommonConfig.INSTANCE.INTEGRATION.ftbChunks.enable.get())
            reg.add(new FTBChunksModule());
    }

    private final IntegrationRegistry registry;

    private LocalIntegrationRegistry () {
        registry = new IntegrationRegistry(StorageDrawers.MOD_ID);
    }

    public static boolean isModLoaded (String modid) {
        if (instance == null)
            instance = new LocalIntegrationRegistry();

        return instance.registry.isModLoaded(modid);
    }

    public static IntegrationRegistry instance () {
        if (instance == null)
            instance = new LocalIntegrationRegistry();

        return instance.registry;
    }
}
