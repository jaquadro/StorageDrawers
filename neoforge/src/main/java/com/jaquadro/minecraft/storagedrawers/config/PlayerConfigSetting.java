package com.jaquadro.minecraft.storagedrawers.config;

import java.util.UUID;

public class PlayerConfigSetting<T extends Comparable<T>> {
    public final String key;
    public final T value;
    public final UUID uuid;

    public PlayerConfigSetting(String key, T value, UUID uuid) {
        this.key = key;
        this.value = value;
        this.uuid = uuid;
    }

    public T getValue() {
        return this.value;
    }
}
