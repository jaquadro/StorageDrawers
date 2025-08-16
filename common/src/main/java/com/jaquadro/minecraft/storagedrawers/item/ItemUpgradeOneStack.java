package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

public class ItemUpgradeOneStack extends ItemUpgrade
{
    public ItemUpgradeOneStack (Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled () {
        return ModCommonConfig.INSTANCE.UPGRADES.oneStackUpgrade.enableUpgrade.get();
    }
}