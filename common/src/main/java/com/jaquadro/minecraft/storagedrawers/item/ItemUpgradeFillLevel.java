package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

public class ItemUpgradeFillLevel extends ItemUpgrade
{
    public ItemUpgradeFillLevel (Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled () {
        return ModCommonConfig.INSTANCE.UPGRADES.enableFillLevelUpgrade.get();
    }
}