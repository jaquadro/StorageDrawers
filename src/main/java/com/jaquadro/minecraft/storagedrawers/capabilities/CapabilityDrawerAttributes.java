package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityDrawerAttributes
{
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register (RegisterCapabilitiesEvent event) {
        event.register(IDrawerAttributes.class);
    }
}
