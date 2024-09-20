package com.texelsaurus.minecraft.chameleon.service;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemHandler;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import net.minecraft.resources.ResourceLocation;

public class FabricCapabilities implements ChameleonCapabilities
{
    @Override
    public <T, C> ChameleonCapability<T> create (ResourceLocation location, Class<T> clazz, Class<C> context) {
        if (clazz == IDrawerAttributes.class)
            return (ChameleonCapability<T>) PlatformCapabilities.DRAWER_ATTRIBUTES;
        if (clazz == IDrawerGroup.class)
            return (ChameleonCapability<T>) PlatformCapabilities.DRAWER_GROUP;
        if (clazz == IItemRepository.class)
            return (ChameleonCapability<T>) PlatformCapabilities.ITEM_REPOSITORY;
        if (clazz == IItemHandler.class)
            return (ChameleonCapability<T>) PlatformCapabilities.ITEM_HANDLER;
        return null;
    }
}
