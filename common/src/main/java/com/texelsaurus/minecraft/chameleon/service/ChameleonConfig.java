package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.config.ConfigSpec;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface ChameleonConfig
{
    <T extends ChameleonConfig> T create(ConfigSpec spec);

    void init(String modId, Type type);

    <T> ConfigEntry<T> define(String name, T defaultValue);

    <T extends Comparable<? super T>> ConfigEntry<T> defineInRange(String name, T defaultValue, T min, T max, Class<T> clazz);

    default ConfigEntry<Integer> defineInRange(String name, int defaultValue, int min, int max) {
        return defineInRange(name, defaultValue, min, max, Integer.class);
    }

    <T extends Enum<T>> ConfigEntry<T> defineEnum(String name, T defaultValue);

    <T> ConfigEntry<List<? extends T>> defineList(String name, List<? extends T> defaultList, Predicate<Object> elementValidator);

    void comment(String comment);
    void comment(String... comment);
    void pushGroup(String name);
    void popGroup();

    enum Type {
       COMMON,
       CLIENT,
       SERVER;
    }

    abstract class ConfigEntry<T> implements Supplier<T>, Consumer<T>
    {
        protected String name;
        protected String[] comment;
        protected T defaultValue;
        protected T rangeMin;
        protected T rangeMax;

        public ConfigEntry<T> name(String name) {
            this.name = name;
            return this;
        }

        public ConfigEntry<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public ConfigEntry<T> comment(String... comment) {
            this.comment = comment;
            return this;
        }

        public ConfigEntry<T> range(T min, T max) {
            this.rangeMin = min;
            this.rangeMax = max;
            return this;
        }

        public abstract ConfigEntry<T> build();

        @Override
        public final void accept (T value) {
            set(value);
        }

        public abstract void set(T value);
    }
}
