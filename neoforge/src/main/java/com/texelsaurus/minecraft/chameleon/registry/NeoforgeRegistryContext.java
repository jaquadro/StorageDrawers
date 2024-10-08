package com.texelsaurus.minecraft.chameleon.registry;

import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import net.neoforged.bus.api.IEventBus;

public class NeoforgeRegistryContext extends ChameleonInit.InitContext
{
    private final IEventBus eventBus;

    public NeoforgeRegistryContext (IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public IEventBus getEventBus () {
        return eventBus;
    }
}
