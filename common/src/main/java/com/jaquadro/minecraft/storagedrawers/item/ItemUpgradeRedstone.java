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
        return ModCommonConfig.INSTANCE.UPGRADES.enableRedstoneUpgrade.get();
    }
}