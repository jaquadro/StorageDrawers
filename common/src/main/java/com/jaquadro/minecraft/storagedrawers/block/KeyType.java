package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;
import java.util.stream.Stream;

public record KeyType (String name)
{
    private static final Map<String, KeyType> TYPES = new Object2ObjectArrayMap<>();
    public static final Codec<KeyType> CODEC;

    public static final KeyType DRAWER;
    public static final KeyType CONCEALMENT;
    public static final KeyType PERSONAL;
    public static final KeyType QUANTIFY;

    public static KeyType register(KeyType type) {
        TYPES.put(type.name, type);
        return type;
    }

    public static Stream<KeyType> values() {
        return TYPES.values().stream();
    }

    public String name() {
        return this.name;
    }

    public boolean isEnabled () {
        boolean keyEnabled = false;
        if (this == KeyType.DRAWER)
            keyEnabled = ModCommonConfig.INSTANCE.TOOLS.drawerKey.enable.get();
        else if (this == KeyType.QUANTIFY)
            keyEnabled = ModCommonConfig.INSTANCE.TOOLS.quantifyKey.enable.get();
        else if (this == KeyType.CONCEALMENT)
            keyEnabled = ModCommonConfig.INSTANCE.TOOLS.concealmentKey.enable.get();
        else if (this == KeyType.PERSONAL)
            keyEnabled = ModCommonConfig.INSTANCE.TOOLS.personalKey.enable.get();

        return keyEnabled;
    }

    static {
        CODEC = Codec.stringResolver(KeyType::name, TYPES::get);
        DRAWER = register(new KeyType("drawer"));
        CONCEALMENT = register(new KeyType("concealment"));
        PERSONAL = register(new KeyType("personal"));
        QUANTIFY = register(new KeyType("quantify"));
    }
}
