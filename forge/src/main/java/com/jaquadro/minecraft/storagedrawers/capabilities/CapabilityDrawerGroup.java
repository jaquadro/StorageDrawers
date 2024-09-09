package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityDrawerGroup
{
    public static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register (RegisterCapabilitiesEvent event) {
        event.register(IDrawerGroup.class);
    }
}
