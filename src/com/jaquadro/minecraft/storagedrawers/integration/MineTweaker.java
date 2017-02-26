package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.chameleon.integration.IntegrationModule;
import com.jaquadro.minecraft.storagedrawers.integration.minetweaker.Compaction;
import com.jaquadro.minecraft.storagedrawers.integration.minetweaker.OreDictionaryBlacklist;
import com.jaquadro.minecraft.storagedrawers.integration.minetweaker.OreDictionaryWhitelist;
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
        MineTweakerAPI.registerClass(OreDictionaryWhitelist.class);
        MineTweakerAPI.registerClass(Compaction.class);
    }

    @Override
    public void postInit () {

    }
}
