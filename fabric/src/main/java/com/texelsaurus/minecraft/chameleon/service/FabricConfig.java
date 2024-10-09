package com.texelsaurus.minecraft.chameleon.service;

import com.jaquadro.minecraft.storagedrawers.config.ModClientConfig;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.texelsaurus.minecraft.chameleon.config.ConfigSpec;
import com.texelsaurus.minecraft.chameleon.config.ForgeApiConfig;
import com.texelsaurus.minecraft.chameleon.config.StaticConfig;
import net.fabricmc.loader.api.FabricLoader;

public class FabricConfig implements ChameleonConfig
{
    private ChameleonConfig configImpl;

    public FabricConfig () {
        this(null);
    }

    private FabricConfig (ConfigSpec spec) {
        if (FabricLoader.getInstance().isModLoaded("forgeconfigapiport"))
            configImpl = new ForgeApiConfig(spec);
        else
            configImpl = new StaticConfig(spec);
    }

    @Override
    public void init() {
        configImpl.init();
    }

    @Override
    public <T extends ChameleonConfig> T create (ConfigSpec spec) {
        return (T)new FabricConfig(spec);
    }

    @Override
    public <T> ConfigEntry<T> define (String name, T defaultValue) {
        return configImpl.define(name, defaultValue);
    }

    @Override
    public <T extends Comparable<? super T>> ConfigEntry<T> defineInRange (String name, T defaultValue, T min, T max, Class<T> clazz) {
        return configImpl.defineInRange(name, defaultValue, min, max, clazz);
    }

    @Override
    public <T extends Enum<T>> ConfigEntry<T> defineEnum (String name, T defaultValue) {
        return configImpl.defineEnum(name, defaultValue);
    }

    @Override
    public void pushGroup (String name) {
        configImpl.pushGroup(name);
    }

    @Override
    public void popGroup () {
        configImpl.popGroup();
    }
}
