package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;

public class ItemUpgradeIllumination extends ItemUpgrade
{
    public ItemUpgradeIllumination (Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled () {
        return CommonConfig.UPGRADES.enableIlluminationUpgrade.get();
    }
}
