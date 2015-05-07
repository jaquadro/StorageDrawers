package com.jaquadro.minecraft.storagedrawers.integration;

public class MineTweaker extends IntegrationModule
{
    @Override
    public String getModID () {
        return "MineTweaker3";
    }

    @Override
    public void init () throws Throwable {
        //MineTweakerAPI.registerClass(OreDictionaryBlacklist.class);
        //MineTweakerAPI.registerClass(OreDictionaryWhitelist.class);
    }

    @Override
    public void postInit () {

    }
}
