package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class IntegrationRegistry
{
    private static IntegrationRegistry instance;

    private List<IntegrationModule> registry;

    static {
        IntegrationRegistry reg = instance();
        /*if (Loader.isModLoaded("appliedenergistics2") && StorageDrawers.config.cache.enableAE2Integration)
            reg.add(new AppliedEnergistics());
        if (Loader.isModLoaded("Waila") && StorageDrawers.config.cache.enableWailaIntegration)
            reg.add(new Waila());
        if (Loader.isModLoaded("Thaumcraft") && StorageDrawers.config.cache.enableThaumcraftIntegration)
            reg.add(new Thaumcraft());
        if (Loader.isModLoaded("MineTweaker3") && StorageDrawers.config.cache.enableMineTweakerIntegration)
            reg.add(new MineTweaker());
        if (Loader.isModLoaded("RefinedRelocation") && StorageDrawers.config.cache.enableRefinedRelocationIntegration)
            reg.add(new RefinedRelocation());*/
    }

    private IntegrationRegistry () {
        registry = new ArrayList<IntegrationModule>();
    }

    public static IntegrationRegistry instance () {
        if (instance == null)
            instance = new IntegrationRegistry();

        return instance;
    }

    public void add (IntegrationModule module) {
        if (module.versionCheck())
            registry.add(module);
    }

    public void init () {
        for (int i = 0; i < registry.size(); i++) {
            IntegrationModule module = registry.get(i);
            if (module.getModID() != null && !Loader.isModLoaded(module.getModID())) {
                registry.remove(i--);
                continue;
            }

            try {
                module.init();
            }
            catch (Throwable t) {
                registry.remove(i--);
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Could not load integration module: " + module.getClass().getName());
            }
        }
    }

    public void postInit () {
        for (IntegrationModule module : registry)
            module.postInit();
    }
}
