package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.*;
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
    public static ItemUpgradeCreative upgradeCreative;
    public static ItemShroudKey shroudKey;
    public static ItemPersonalKey personalKey;
    public static ItemTape tape;

    public void init () {
        upgradeTemplate = new Item().setUnlocalizedName(makeName("upgradeTemplate")).setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        upgradeStorage = new ItemUpgradeStorage(makeName("upgradeStorage"));
        upgradeStatus = new ItemUpgradeStatus(makeName("upgradeStatus"));
        upgradeVoid = new ItemUpgradeVoid(makeName("upgradeVoid"));
        drawerKey = new ItemDrawerKey(makeName("drawerKey"));
        upgradeCreative = new ItemUpgradeCreative(makeName("upgradeCreative"));
        shroudKey = new ItemShroudKey(makeName("shroudKey"));
        personalKey = new ItemPersonalKey(makeName("personalKey"));
        tape = new ItemTape(makeName("tape"));

        GameRegistry.registerItem(upgradeTemplate, "upgradeTemplate");

        if (StorageDrawers.config.cache.enableStorageUpgrades)
            GameRegistry.registerItem(upgradeStorage, "upgradeStorage");
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            GameRegistry.registerItem(upgradeStatus, "upgradeStatus");
        if (StorageDrawers.config.cache.enableVoidUpgrades)
            GameRegistry.registerItem(upgradeVoid, "upgradeVoid");
        if (StorageDrawers.config.cache.enableCreativeUpgrades)
            GameRegistry.registerItem(upgradeCreative, "upgradeCreative");
        if (StorageDrawers.config.cache.enableLockUpgrades)
            GameRegistry.registerItem(drawerKey, "drawerKey");
        if (StorageDrawers.config.cache.enableShroudUpgrades)
            GameRegistry.registerItem(shroudKey, "shroudKey");
        if (StorageDrawers.config.cache.enablePersonalUpgrades)
            GameRegistry.registerItem(personalKey, "personalKey");
        if (StorageDrawers.config.cache.enableTape)
            GameRegistry.registerItem(tape, "tape");
    }

    public static String getQualifiedName (Item item) {
        return GameData.getItemRegistry().getNameForObject(item).toString();
    }

    public static String makeName (String name) {
        return StorageDrawers.MOD_ID.toLowerCase() + "." + name;
    }
}
