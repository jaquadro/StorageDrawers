package com.jaquadro.minecraft.storagedrawers.service;

import com.texelsaurus.minecraft.chameleon.config.ConfigSpec;
import com.texelsaurus.minecraft.chameleon.service.ChameleonConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class ForgeConfig implements ChameleonConfig
{
    private final ConfigSpec localSpec;
    private final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public ForgeConfigSpec forgeSpec;

    public ForgeConfig() {
        localSpec = null;
    }

    public ForgeConfig(ConfigSpec spec) {
        localSpec = spec;
    }

    @Override
    public void init() {
        localSpec.init();
        forgeSpec = BUILDER.build();
    }

    @Override
    public <T extends ChameleonConfig> T create (ConfigSpec spec) {
        return (T)new ForgeConfig(spec);
    }

    @Override
    public <T> ConfigEntry<T> define (String name, T defaultValue) {
        return new ForgeConfigEntry<T>(BUILDER).name(name).defaultValue(defaultValue);
    }

    @Override
    public <T extends Comparable<? super T>> ConfigEntry<T> defineInRange (String name, T defaultValue, T min, T max, Class<T> clazz) {
        return new ForgeConfigEntryRange<T>(BUILDER, clazz).name(name).defaultValue(defaultValue).range(min, max);
    }

    @Override
    public <T extends Enum<T>> ConfigEntry<T> defineEnum (String name, T defaultValue) {
        return new ForgeConfigEntryEnum<T>(BUILDER).name(name).defaultValue(defaultValue);
    }

    @Override
    public void pushGroup (String name) {
        BUILDER.push(name);
    }

    @Override
    public void popGroup () {
        BUILDER.pop();
    }

    public class ForgeConfigEntry<T> extends ChameleonConfig.ConfigEntry<T>
    {
        ForgeConfigSpec.Builder builder;
        ForgeConfigSpec.ConfigValue<T> value;


        public ForgeConfigEntry(ForgeConfigSpec.Builder builder) {
            this.builder = builder;
        }

        @Override
        public ChameleonConfig.ConfigEntry<T> build () {
            if (comment != null)
                builder.comment(comment);

            value = define();
            return this;
        }

        @Override
        public T get () {
            if (value == null)
                return defaultValue;

            return value.get();
        }

        @Override
        public void set (T t) {
            if (value != null)
                value.set(t);
        }

        protected ForgeConfigSpec.ConfigValue<T> define() {
            return builder.define(name, defaultValue);
        }
    }

    public class ForgeConfigEntryRange<T extends Comparable<? super T>> extends ForgeConfigEntry<T>
    {
        Class<T> clazz;

        public ForgeConfigEntryRange (ForgeConfigSpec.Builder builder, Class<T> clazz) {
            super(builder);
            this.clazz = clazz;
        }

        @Override
        protected ForgeConfigSpec.ConfigValue<T> define () {
            return builder.defineInRange(name, defaultValue, rangeMin, rangeMax, clazz);
        }
    }

    public class ForgeConfigEntryEnum<T extends Enum<T>> extends ForgeConfigEntry<T>
    {
        public ForgeConfigEntryEnum (ForgeConfigSpec.Builder builder) {
            super(builder);
        }

        @Override
        protected ForgeConfigSpec.ConfigValue<T> define () {
            return builder.defineEnum(name, defaultValue);
        }
    }
}
