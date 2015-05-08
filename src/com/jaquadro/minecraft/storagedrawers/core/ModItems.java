package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawerKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStatus;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeVoid;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems
{
    public static Item upgradeTemplate;
    public static ItemUpgradeStorage upgradeStorage;
    public static ItemUpgradeStatus upgradeStatus;
    public static ItemDrawerKey drawerKey;
    public static ItemUpgradeVoid upgradeVoid;

    public void init () {
        upgradeTemplate = new Item().setUnlocalizedName("upgradeTemplate").setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        upgradeStorage = new ItemUpgradeStorage("upgradeStorage");
        upgradeStatus = new ItemUpgradeStatus("upgradeStatus");
        upgradeVoid = new ItemUpgradeVoid("upgradeVoid");
        drawerKey = new ItemDrawerKey("drawerKey");

        GameRegistry.registerItem(upgradeTemplate, "upgradeTemplate");

        if (StorageDrawers.config.cache.enableStorageUpgrades)
            GameRegistry.registerItem(upgradeStorage, "upgradeStorage");
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            GameRegistry.registerItem(upgradeStatus, "upgradeStatus");
        if (StorageDrawers.config.cache.enableVoidUpgrades)
            GameRegistry.registerItem(upgradeVoid, "upgradeVoid");
        if (StorageDrawers.config.cache.enableLockUpgrades)
            GameRegistry.registerItem(drawerKey, "drawerKey");
    }

    public static String getQualifiedName (Item item) {
        return GameData.getItemRegistry().getNameForObject(item).toString();
    }
}
