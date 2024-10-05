package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

public class ItemUpgradeVoid extends ItemUpgrade
{
    public ItemUpgradeVoid (Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled () {
        return ModCommonConfig.INSTANCE.UPGRADES.enableVoidUpgrade.get();
    }
}