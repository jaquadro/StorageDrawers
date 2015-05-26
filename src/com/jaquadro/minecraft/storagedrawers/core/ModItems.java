package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ModItems
{
    public static Item upgradeTemplate;
    public static ItemUpgrade upgrade;
    public static ItemUpgradeStatus upgradeStatus;
    public static ItemUpgradeLock upgradeLock;
    public static ItemUpgradeVoid upgradeVoid;
    public static ItemShroudKey shroudKey;

    public void init () {
        upgradeTemplate = new Item().setUnlocalizedName("upgradeTemplate").setTextureName(StorageDrawers.MOD_ID + ":upgrade_template").setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        upgrade = new ItemUpgrade("upgrade");
        upgradeStatus = new ItemUpgradeStatus("upgradeStatus");
        upgradeLock = new ItemUpgradeLock("upgradeLock");
        upgradeVoid = new ItemUpgradeVoid("upgradeVoid");
        shroudKey = new ItemShroudKey("shroudKey");

        GameRegistry.registerItem(upgradeTemplate, "upgradeTemplate");

        if (StorageDrawers.config.cache.enableStorageUpgrades)
            GameRegistry.registerItem(upgrade, "upgrade");
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            GameRegistry.registerItem(upgradeStatus, "upgradeStatus");
        if (StorageDrawers.config.cache.enableVoidUpgrades)
            GameRegistry.registerItem(upgradeVoid, "upgradeVoid");
        if (StorageDrawers.config.cache.enableLockUpgrades)
            GameRegistry.registerItem(upgradeLock, "upgradeLock");
        if (StorageDrawers.config.cache.enableShroudUpgrades)
            GameRegistry.registerItem(shroudKey, "shroudKey");
    }
}
