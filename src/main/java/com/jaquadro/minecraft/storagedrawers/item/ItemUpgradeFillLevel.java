package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;

public class ItemUpgradeFillLevel extends ItemUpgrade
{
    public ItemUpgradeFillLevel (Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled () {
        return CommonConfig.UPGRADES.enableFillLevelUpgrade.get();
    }
}
