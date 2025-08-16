package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

public class ItemUpgradeCreative extends ItemUpgrade
{
    private static final int creativeGroupId;
    static {
        creativeGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeCreative type;

    public ItemUpgradeCreative (EnumUpgradeCreative type, Properties properties) {
        this(type, properties, creativeGroupId);
    }

    protected ItemUpgradeCreative (EnumUpgradeCreative type, Properties properties, int groupId) {
        super(properties, groupId);

        this.type = type;
    }

    @Override
    public boolean isEnabled () {
        if (type == EnumUpgradeCreative.STORAGE)
            return ModCommonConfig.INSTANCE.UPGRADES.creativeStorageUpgrade.enableUpgrade.get();
        else if (type == EnumUpgradeCreative.VENDING)
            return ModCommonConfig.INSTANCE.UPGRADES.creativeVendingUpgrade.enableUpgrade.get();

        return true;
    }
}