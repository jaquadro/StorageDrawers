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
    public static ItemUpgradeCreative upgradeCreative;
    public static ItemShroudKey shroudKey;
    public static ItemPersonalKey personalKey;
    public static ItemTape tape;

    public void init () {
        upgradeTemplate = new Item().setUnlocalizedName(makeName("upgradeTemplate")).setTextureName(StorageDrawers.MOD_ID + ":upgrade_template").setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        upgrade = new ItemUpgrade(makeName("upgrade"));
        upgradeStatus = new ItemUpgradeStatus(makeName("upgradeStatus"));
        upgradeLock = new ItemUpgradeLock(makeName("upgradeLock"));
        upgradeVoid = new ItemUpgradeVoid(makeName("upgradeVoid"));
        upgradeCreative = new ItemUpgradeCreative(makeName("upgradeCreative"));
        shroudKey = new ItemShroudKey(makeName("shroudKey"));
        personalKey = new ItemPersonalKey(makeName("personalKey"));
        tape = new ItemTape(makeName("tape"));

        GameRegistry.registerItem(upgradeTemplate, "upgradeTemplate");

        if (StorageDrawers.config.cache.enableStorageUpgrades)
            GameRegistry.registerItem(upgrade, "upgrade");
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            GameRegistry.registerItem(upgradeStatus, "upgradeStatus");
        if (StorageDrawers.config.cache.enableVoidUpgrades)
            GameRegistry.registerItem(upgradeVoid, "upgradeVoid");
        if (StorageDrawers.config.cache.enableCreativeUpgrades)
            GameRegistry.registerItem(upgradeCreative, "upgradeCreative");
        if (StorageDrawers.config.cache.enableLockUpgrades)
            GameRegistry.registerItem(upgradeLock, "upgradeLock");
        if (StorageDrawers.config.cache.enableShroudUpgrades)
            GameRegistry.registerItem(shroudKey, "shroudKey");
        if (StorageDrawers.config.cache.enablePersonalUpgrades)
            GameRegistry.registerItem(personalKey, "personalKey");
        if (StorageDrawers.config.cache.enableTape)
            GameRegistry.registerItem(tape, "tape");
    }

    public static String makeName (String name) {
        return StorageDrawers.MOD_ID.toLowerCase() + "." + name;
    }
}
