package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

public class ItemUpgradeRedstone extends ItemUpgrade
{
    private static final int redstoneGroupId;
    static {
        redstoneGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeRedstone type;

    public ItemUpgradeRedstone (EnumUpgradeRedstone type, Properties properties) {
        this(type, properties, redstoneGroupId);
    }

    protected ItemUpgradeRedstone (EnumUpgradeRedstone type, Properties properties, int groupId) {
        super(properties, groupId);

        this.type = type;
    }

    @Override
    public boolean isEnabled () {
        if (!ModCommonConfig.INSTANCE.UPGRADES.redstoneUpgrade.enableUpgrade.get())
            return false;

        if (type == EnumUpgradeRedstone.MAX)
            return ModCommonConfig.INSTANCE.UPGRADES.redstoneUpgrade.enableMax.get();
        else if (type == EnumUpgradeRedstone.MIN)
            return ModCommonConfig.INSTANCE.UPGRADES.redstoneUpgrade.enableMin.get();

        return true;
    }
}