package com.texelsaurus.minecraft.chameleon;

import com.texelsaurus.minecraft.chameleon.service.*;

import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class ChameleonServices
{
    public static final ChameleonRegistries REGISTRY = load(ChameleonRegistries.class);
    public static final ChameleonNetworking NETWORK = load(ChameleonNetworking.class);
    public static final ChameleonConfig CONFIG = load(ChameleonConfig.class);
    public static final ChameleonCapabilities CAPABILITY = load(ChameleonCapabilities.class);
    public static final ChameleonContainer CONTAINER = load(ChameleonContainer.class);
    public static final ChameleonPlatform PLATFORM = load(ChameleonPlatform.class);

    private static <T> T load(Class<T> clazz) {
        var providers = ServiceLoader.load(clazz, clazz.getClassLoader()).stream().toList();
        if (providers.size() != 1) {
            throw new IllegalStateException("Found " + providers.size() + " providers for " + clazz.getName() + ": " +
                providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(", ", "[", "]")));
        }

        final T service = providers.get(0).get();
        return service;
    }
}
