package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.integration.minetweaker.OreDictionaryBlacklist;
import minetweaker.MineTweakerAPI;

public class MineTweaker extends IntegrationModule
{
    @Override
    public String getModID () {
        return "MineTweaker3";
    }

    @Override
    public void init () throws Throwable {
        MineTweakerAPI.registerClass(OreDictionaryBlacklist.class);
    }

    @Override
    public void postInit () {

    }
}
