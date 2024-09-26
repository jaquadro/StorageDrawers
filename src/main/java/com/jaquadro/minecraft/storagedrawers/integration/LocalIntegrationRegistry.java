package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.integration.cofh.CoFHModule;
import net.minecraftforge.fml.ModList;

public class LocalIntegrationRegistry
{
    private static LocalIntegrationRegistry instance;

    static {
        IntegrationRegistry reg = instance();
        if (ModList.get().isLoaded("cofh_core") && CommonConfig.INTEGRATION.enableCoFHIntegration.get())
            reg.add(new CoFHModule());

        //if (Loader.isModLoaded("Thaumcraft") && StorageDrawers.config.cache.enableThaumcraftIntegration)
        //    reg.add(new Thaumcraft());
        //if (Loader.isModLoaded("appliedenergistics2") && StorageDrawers.config.cache.enableAE2Integration)
        //    reg.add(new AppliedEnergistics());
        //if (Loader.isModLoaded("crafttweaker") && StorageDrawers.config.cache.enableMineTweakerIntegration)
        //    reg.add(new MineTweaker());
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
