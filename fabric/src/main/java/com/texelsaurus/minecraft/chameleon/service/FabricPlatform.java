package com.texelsaurus.minecraft.chameleon.service;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatform implements ChameleonPlatform
{
    @Override
    public boolean isPhysicalClient () {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
