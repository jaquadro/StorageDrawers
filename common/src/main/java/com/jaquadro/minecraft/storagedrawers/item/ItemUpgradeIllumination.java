package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

public class ItemUpgradeIllumination extends ItemUpgrade
{
    public ItemUpgradeIllumination (Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled () {
        return ModCommonConfig.INSTANCE.UPGRADES.enableIlluminationUpgrade.get();
    }
}