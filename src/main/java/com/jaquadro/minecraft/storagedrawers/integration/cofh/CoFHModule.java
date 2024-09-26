package com.jaquadro.minecraft.storagedrawers.integration.cofh;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.IntegrationModule;

public class CoFHModule extends IntegrationModule
{
    @Override
    public String getModID () {
        return "cofh_core";
    }

    @Override
    public void init () throws Throwable {

    }

    @Override
    public void postInit () {
        StorageDrawers.securityRegistry.registerProvider(new CoFHSecurityProvider());
    }
}
