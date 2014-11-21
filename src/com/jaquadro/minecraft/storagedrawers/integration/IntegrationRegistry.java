package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class IntegrationRegistry
{
    private static IntegrationRegistry instance;

    private List<IntegrationModule> registry;

    static {
        IntegrationRegistry reg = instance();
        if (Loader.isModLoaded("appliedenergistics2"))
            reg.add(new AppliedEnergistics());
        if (Loader.isModLoaded("Waila"))
            reg.add(new Waila());
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
