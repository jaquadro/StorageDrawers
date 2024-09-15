package com.texelsaurus.minecraft.chameleon;

import com.texelsaurus.minecraft.chameleon.service.ChameleonCapabilities;
import com.texelsaurus.minecraft.chameleon.service.ChameleonConfig;
import com.texelsaurus.minecraft.chameleon.service.ChameleonNetworking;
import com.texelsaurus.minecraft.chameleon.service.ChameleonRegistries;

import java.util.ServiceLoader;

public final class ChameleonServices
{
    public static final ChameleonRegistries REGISTRY = load(ChameleonRegistries.class);
    public static final ChameleonNetworking NETWORK = load(ChameleonNetworking.class);
    public static final ChameleonConfig CONFIG = load(ChameleonConfig.class);
    public static final ChameleonCapabilities CAPABILITY = load(ChameleonCapabilities.class);

    private static <T> T load(Class<T> clazz) {
        final T service = ServiceLoader.load(clazz).findFirst().orElseThrow();
        return service;
    }
}
