package com.texelsaurus.minecraft.chameleon.config;

import com.texelsaurus.minecraft.chameleon.config.ConfigSpec;
import com.texelsaurus.minecraft.chameleon.service.ChameleonConfig;

// Incomplete implementation -- this is just a proxy to default config values

public class StaticConfig implements ChameleonConfig
{
    private final ConfigSpec localSpec;

    public StaticConfig () {
        localSpec = null;
    }

    public StaticConfig (ConfigSpec spec) {
        localSpec = spec;
    }

    @Override
    public <T extends ChameleonConfig> T create (ConfigSpec spec) {
        return (T)new StaticConfig(spec);
    }

    @Override
    public void init () {
        if (localSpec != null) {
            localSpec.init();
            localSpec.setLoaded();
        }
    }

    @Override
    public <T> ConfigEntry<T> define (String name, T defaultValue) {
        return new FabricConfigEntry<T>().name(name).defaultValue(defaultValue);
    }

    @Override
    public <T extends Comparable<? super T>> ConfigEntry<T> defineInRange (String name, T defaultValue, T min, T max, Class<T> clazz) {
        return new FabricConfigEntry<T>().name(name).defaultValue(defaultValue);
    }

    @Override
    public <T extends Enum<T>> ConfigEntry<T> defineEnum (String name, T defaultValue) {
        return new FabricConfigEntry<T>().name(name).defaultValue(defaultValue);
    }

    @Override
    public void pushGroup (String name) {

    }

    @Override
    public void popGroup () {

    }

    public static class FabricConfigEntry<T> extends ConfigEntry<T>
    {
        public FabricConfigEntry () { }

        @Override
        public ConfigEntry<T> build () {
            return this;
        }

        @Override
        public void set (T value) {

        }

        @Override
        public T get () {
            return defaultValue;
        }
    }
}