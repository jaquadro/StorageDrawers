package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeLock;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStatus;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems
{
    public static Item upgradeTemplate;
    public static ItemUpgrade upgrade;
    public static ItemUpgradeStatus upgradeStatus;
    public static ItemUpgradeLock upgradeLock;

    public void init () {
        upgradeTemplate = new Item().setUnlocalizedName("upgradeTemplate").setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        upgrade = new ItemUpgrade("upgrade");
        upgradeStatus = new ItemUpgradeStatus("upgradeStatus");
        upgradeLock = new ItemUpgradeLock("upgradeLock");

        GameRegistry.registerItem(upgradeTemplate, "upgradeTemplate");

        if (StorageDrawers.config.cache.enableStorageUpgrades)
            GameRegistry.registerItem(upgrade, "upgrade");
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            GameRegistry.registerItem(upgradeStatus, "upgradeStatus");
        if (StorageDrawers.config.cache.enableLockUpgrades)
            GameRegistry.registerItem(upgradeLock, "upgradeLock");
    }
}
