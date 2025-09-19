package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

public class ItemUpgradePush extends ItemUpgrade
{
    private static final int upgradeGroupId;
    static {
        upgradeGroupId = ItemUpgrade.getNextGroupId();
    }

    public final int level;

    public ItemUpgradePush (int level, Properties properties) {
        this(level, properties, upgradeGroupId);
    }

    protected ItemUpgradePush (int level, Properties properties, int groupId) {
        super(properties, groupId);

        setAllowMultiple(false);
        this.level = level;
    }

    @Override
    public boolean isEnabled () {
        return getConfig().enableUpgrade.get();
    }

    public int getActiveSpeed () {
        return getConfig().activeSpeed.get();
    }

    public int getIdleSpeed () {
        return getConfig().idleSpeed.get();
    }

    public int getItemRate () {
        return getConfig().itemRate.get();
    }

    private ModCommonConfig.Upgrades.ItemTransferUpgrade getConfig () {
        return switch (level) {
            case 1 -> ModCommonConfig.INSTANCE.UPGRADES.pushUpgrade.tier1;
            case 2 -> ModCommonConfig.INSTANCE.UPGRADES.pushUpgrade.tier2;
            case 3 -> ModCommonConfig.INSTANCE.UPGRADES.pushUpgrade.tier3;
            default -> null;
        };
    }
}
