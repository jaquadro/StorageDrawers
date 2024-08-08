package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.chameleon.integration.IntegrationModule;
import com.jaquadro.minecraft.storagedrawers.integration.crafttweaker.Compaction;
import com.jaquadro.minecraft.storagedrawers.integration.crafttweaker.OreDictionaryBlacklist;
import com.jaquadro.minecraft.storagedrawers.integration.crafttweaker.OreDictionaryWhitelist;
import crafttweaker.CraftTweakerAPI;

public class CraftTweaker extends IntegrationModule
{
    @Override
    public String getModID () {
        return "crafttweaker";
    }

    @Override
    public void init () throws Throwable {
        // CraftTweakerAPI.registerClass(OreDictionaryBlacklist.class);
        // CraftTweakerAPI.registerClass(OreDictionaryWhitelist.class);
        // CraftTweakerAPI.registerClass(Compaction.class);
    }

    @Override
    public void postInit () {

    }
}
