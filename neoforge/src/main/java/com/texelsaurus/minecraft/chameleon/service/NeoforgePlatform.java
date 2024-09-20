package com.texelsaurus.minecraft.chameleon.service;

import net.neoforged.fml.loading.FMLEnvironment;

public class NeoforgePlatform implements ChameleonPlatform
{
    @Override
    public boolean isPhysicalClient () {
        return FMLEnvironment.dist.isClient();
    }
}
