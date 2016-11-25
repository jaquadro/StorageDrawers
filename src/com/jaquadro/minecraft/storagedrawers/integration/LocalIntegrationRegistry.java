package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.chameleon.integration.IntegrationRegistry;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraftforge.fml.common.Loader;

public class LocalIntegrationRegistry
{
    private static LocalIntegrationRegistry instance;

    static {
        IntegrationRegistry reg = instance();
        //if (Loader.isModLoaded("Waila") && StorageDrawers.config.cache.enableWailaIntegration)
        //    reg.add(new Waila());
        //if (Loader.isModLoaded("Thaumcraft") && StorageDrawers.config.cache.enableThaumcraftIntegration)
        //    reg.add(new Thaumcraft());
        //if (Loader.isModLoaded("appliedenergistics2") && StorageDrawers.config.cache.enableAE2Integration)
        //    reg.add(new AppliedEnergistics());
        if (Loader.isModLoaded("MineTweaker3") && StorageDrawers.config.cache.enableMineTweakerIntegration)
            reg.add(new MineTweaker());
    }

    private IntegrationRegistry registry;

    private LocalIntegrationRegistry () {
        registry = new IntegrationRegistry(StorageDrawers.MOD_ID);
    }

    public static IntegrationRegistry instance () {
        if (instance == null)
            instance = new LocalIntegrationRegistry();

        return instance.registry;
    }
}
