package com.texelsaurus.minecraft.chameleon;

import com.texelsaurus.minecraft.chameleon.service.*;

import java.util.ServiceLoader;

public final class ChameleonServices
{
    public static final ChameleonRegistries REGISTRY = load(ChameleonRegistries.class);
    public static final ChameleonNetworking NETWORK = load(ChameleonNetworking.class);
    public static final ChameleonConfig CONFIG = load(ChameleonConfig.class);
    public static final ChameleonCapabilities CAPABILITY = load(ChameleonCapabilities.class);
    public static final ChameleonContainer CONTAINER = load(ChameleonContainer.class);

    private static <T> T load(Class<T> clazz) {
        final T service = ServiceLoader.load(clazz).findFirst().orElseThrow();
        return service;
    }
}
