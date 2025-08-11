package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

public class ItemUpgradePortability extends ItemUpgrade
{
    public ItemUpgradePortability (Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled () {
        boolean heavy = ModCommonConfig.INSTANCE.DRAWERS.anyHeavyDrawers();
        return heavy && ModCommonConfig.INSTANCE.UPGRADES.portabilityUpgrade.enableUpgrade.get();
    }
}
