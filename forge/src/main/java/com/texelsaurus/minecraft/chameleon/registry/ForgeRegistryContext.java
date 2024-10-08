package com.texelsaurus.minecraft.chameleon.registry;

import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import net.minecraftforge.eventbus.api.IEventBus;

public class ForgeRegistryContext extends ChameleonInit.InitContext
{
    private final IEventBus eventBus;

    public ForgeRegistryContext (IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public IEventBus getEventBus () {
        return eventBus;
    }
}
