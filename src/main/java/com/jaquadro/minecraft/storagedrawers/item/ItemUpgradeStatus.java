package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.item.Item;

public class ItemUpgradeStatus extends ItemUpgrade
{
    private static int statusGroupId;
    static {
        statusGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeStatus level;

    public ItemUpgradeStatus (EnumUpgradeStatus status, Item.Properties properties) {
        this(status, properties, statusGroupId);
    }

    protected ItemUpgradeStatus (EnumUpgradeStatus status, Item.Properties properties, int groupId) {
        super(properties, groupId);

        this.level = status;
    }
}
