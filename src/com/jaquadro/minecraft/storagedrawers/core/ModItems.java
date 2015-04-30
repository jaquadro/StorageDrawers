package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeLock;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStatus;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems
{
    public static Item upgradeTemplate;
    public static ItemUpgradeStorage upgradeStorage;
    public static ItemUpgradeStatus upgradeStatus;
    public static ItemUpgradeLock upgradeLock;

    public void init () {
        upgradeTemplate = new Item().setUnlocalizedName("upgradeTemplate").setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        upgradeStorage = new ItemUpgradeStorage("upgradeStorage");
        upgradeStatus = new ItemUpgradeStatus("upgradeStatus");
        upgradeLock = new ItemUpgradeLock("upgradeLock");

        GameRegistry.registerItem(upgradeTemplate, "upgradeTemplate");

        if (StorageDrawers.config.cache.enableStorageUpgrades)
            GameRegistry.registerItem(upgradeStorage, "upgradeStorage");
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            GameRegistry.registerItem(upgradeStatus, "upgradeStatus");
        if (StorageDrawers.config.cache.enableLockUpgrades)
            GameRegistry.registerItem(upgradeLock, "upgradeLock");
    }

    public static String getQualifiedName (Item item) {
        return GameData.getItemRegistry().getNameForObject(item).toString();
    }
}
