package com.texelsaurus.minecraft.chameleon.service;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemHandler;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import com.texelsaurus.minecraft.chameleon.capabilities.ForgeCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ForgeCapabilities implements ChameleonCapabilities
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
