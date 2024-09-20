package com.texelsaurus.minecraft.chameleon.service;

import net.minecraftforge.fml.loading.FMLEnvironment;

public class ForgePlatform implements ChameleonPlatform
{
    @Override
    public boolean isPhysicalClient () {
        return FMLEnvironment.dist.isClient();
    }
}
