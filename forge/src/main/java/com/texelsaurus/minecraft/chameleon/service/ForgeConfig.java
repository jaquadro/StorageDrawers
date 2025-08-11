package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.config.ConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ForgeConfig implements ChameleonConfig
{
    private final ConfigSpec localSpec;
    private final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public ForgeConfigSpec forgeSpec;

    public ForgeConfig () {
        localSpec = null;
    }

    private ForgeConfig (ConfigSpec spec) {
        localSpec = spec;
    }

    @Override
    public void init(String modId, ChameleonConfig.Type type) {
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
    public <T> ConfigEntry<List<? extends T>> defineList (String name, List<? extends T> defaultList, Predicate<Object> elementValidator) {
        return new ForgeConfigEntryList<T>(BUILDER).validator(elementValidator).name(name).defaultValue(defaultList);
    }

    @Override
    public void pushGroup (String name) {
        BUILDER.push(name);
    }

    @Override
    public void popGroup () {
        BUILDER.pop();
    }

    @Override
    public void comment (String comment) {
        BUILDER.comment(" " + comment);
    }

    @Override
    public void comment (String... comment) {
        BUILDER.comment(Arrays.stream(comment).map(s -> " " + s).toArray(String[]::new));
    }

    public class ForgeConfigEntry<T> extends ConfigEntry<T>
    {
        ForgeConfigSpec.Builder builder;
        ForgeConfigSpec.ConfigValue<T> value;


        public ForgeConfigEntry(ForgeConfigSpec.Builder builder) {
            this.builder = builder;
        }

        @Override
        public ConfigEntry<T> build () {
            if (comment != null)
                builder.comment(Arrays.stream(comment).map(s -> " " + s).toArray(String[]::new));

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

    public class ForgeConfigEntryList<T> extends ForgeConfigEntry<List<? extends T>>
    {
        Predicate<Object> validator;
        public ForgeConfigEntryList (ForgeConfigSpec.Builder builder) {
            super(builder);
            validator = x -> true;
        }
        public ForgeConfigEntryList<T> validator(Predicate<Object> validtor) {
            if (validtor != null)
                this.validator = validtor;
            return this;
        }
        @Override
        protected ForgeConfigSpec.ConfigValue<List<? extends T>> define () {
            return builder.defineList(name, defaultValue, validator);
        }
    }
}
