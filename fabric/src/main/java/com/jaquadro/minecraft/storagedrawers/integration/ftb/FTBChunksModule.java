package com.jaquadro.minecraft.storagedrawers.integration.ftb;

import com.jaquadro.minecraft.storagedrawers.core.ModSecurity;
import com.jaquadro.minecraft.storagedrawers.integration.IntegrationModule;

public class FTBChunksModule extends IntegrationModule
{
    @Override
    public String getModID () {
        return "ftbchunks";
    }

    @Override
    public void init () throws Throwable {

    }

    @Override
    public void postInit () {
        ModSecurity.registry.registerProvider(new FTBChunksInteractionProvider());
    }
}
