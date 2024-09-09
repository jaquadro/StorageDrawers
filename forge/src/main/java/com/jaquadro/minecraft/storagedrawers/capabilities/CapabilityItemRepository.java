package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityItemRepository
{
    public static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register (RegisterCapabilitiesEvent event) {
        event.register(IItemRepository.class);
    }
}
