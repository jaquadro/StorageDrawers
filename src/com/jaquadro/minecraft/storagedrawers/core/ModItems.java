package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.resources.ModelRegistry;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.*;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems
{
    public static Item upgradeTemplate;
    public static ItemUpgradeStorage upgradeStorage;
    public static ItemUpgradeStatus upgradeStatus;
    public static ItemDrawerKey drawerKey;
    public static ItemUpgradeVoid upgradeVoid;
    public static ItemUpgradeCreative upgradeCreative;
    public static ItemUpgradeRedstone upgradeRedstone;
    public static ItemShroudKey shroudKey;
    public static ItemPersonalKey personalKey;
    public static ItemTape tape;

    public void init () {
        upgradeTemplate = new Item().setUnlocalizedName(makeName("upgradeTemplate")).setRegistryName(StorageDrawers.MOD_ID, "upgradeTemplate").setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        upgradeStorage = new ItemUpgradeStorage("upgradeStorage", makeName("upgradeStorage"));
        upgradeStatus = new ItemUpgradeStatus("upgradeStatus", makeName("upgradeStatus"));
        upgradeVoid = new ItemUpgradeVoid("upgradeVoid", makeName("upgradeVoid"));
        drawerKey = new ItemDrawerKey("drawerKey", makeName("drawerKey"));
        upgradeCreative = new ItemUpgradeCreative("upgradeCreative", makeName("upgradeCreative"));
        upgradeRedstone = new ItemUpgradeRedstone("upgradeRedstone", makeName("upgradeRedstone"));
        shroudKey = new ItemShroudKey("shroudKey", makeName("shroudKey"));
        personalKey = new ItemPersonalKey("personalKey", makeName("personalKey"));
        tape = new ItemTape("tape", makeName("tape"));

        GameRegistry.register(upgradeTemplate);

        if (StorageDrawers.config.cache.enableStorageUpgrades)
            GameRegistry.register(upgradeStorage);
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            GameRegistry.register(upgradeStatus);
        if (StorageDrawers.config.cache.enableVoidUpgrades)
            GameRegistry.register(upgradeVoid);
        if (StorageDrawers.config.cache.enableCreativeUpgrades)
            GameRegistry.register(upgradeCreative);
        if (StorageDrawers.config.cache.enableRedstoneUpgrades)
            GameRegistry.register(upgradeRedstone);
        if (StorageDrawers.config.cache.enableLockUpgrades)
            GameRegistry.register(drawerKey);
        if (StorageDrawers.config.cache.enableShroudUpgrades)
            GameRegistry.register(shroudKey);
        if (StorageDrawers.config.cache.enablePersonalUpgrades)
            GameRegistry.register(personalKey);
        if (StorageDrawers.config.cache.enableTape)
            GameRegistry.register(tape);
    }

    @SideOnly(Side.CLIENT)
    public void initClient () {
        ModelRegistry modelRegistry = Chameleon.instance.modelRegistry;

        modelRegistry.registerItemVariants(upgradeTemplate);
        modelRegistry.registerItemVariants(upgradeVoid);
        modelRegistry.registerItemVariants(tape);
        modelRegistry.registerItemVariants(drawerKey);
        modelRegistry.registerItemVariants(shroudKey);
        modelRegistry.registerItemVariants(personalKey);
        modelRegistry.registerItemVariants(upgradeStorage);
        modelRegistry.registerItemVariants(upgradeStatus);
        modelRegistry.registerItemVariants(upgradeCreative);
        modelRegistry.registerItemVariants(upgradeRedstone);
    }

    public static String makeName (String name) {
        return StorageDrawers.MOD_ID.toLowerCase() + "." + name;
    }
}
