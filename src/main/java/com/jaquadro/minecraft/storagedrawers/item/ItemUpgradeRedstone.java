package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import net.minecraft.world.item.Item;

public class ItemUpgradeRedstone extends ItemUpgrade
{
    private static final int redstoneGroupId;
    static {
        redstoneGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeRedstone type;

    public ItemUpgradeRedstone (EnumUpgradeRedstone type, Item.Properties properties) {
        this(type, properties, redstoneGroupId);
    }

    protected ItemUpgradeRedstone (EnumUpgradeRedstone type, Item.Properties properties, int groupId) {
        super(properties, groupId);

        this.type = type;
    }

    @Override
    public boolean isEnabled () {
        return CommonConfig.UPGRADES.enableRedstoneUpgrade.get();
    }
}