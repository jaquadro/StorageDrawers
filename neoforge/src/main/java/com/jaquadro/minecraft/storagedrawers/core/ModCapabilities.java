package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityItemRepository;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModCapabilities
{
    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        CapabilityDrawerGroup.register(event);
        CapabilityItemRepository.register(event);
        CapabilityDrawerAttributes.register(event);
    }
}
