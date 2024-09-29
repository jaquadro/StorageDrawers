package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;

public class ItemUpgradeVoid extends ItemUpgrade
{
    public ItemUpgradeVoid (Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled () {
        return CommonConfig.UPGRADES.enableVoidUpgrade.get();
    }
}
