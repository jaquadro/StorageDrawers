package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.*;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(StorageDrawers.MOD_ID)
public class ModItems
{
    public static final Item
        OBSIDIAN_STORAGE_UPGRADE = null,
        IRON_STORAGE_UPGRADE = null,
        GOLD_STORAGE_UPGRADE = null,
        DIAMOND_STORAGE_UPGRADE = null,
        EMERALD_STORAGE_UPGRADE = null,
        ONE_STACK_UPGRADE = null,
        VOID_UPGRADE = null,
        CREATIVE_STORAGE_UPGRADE = null,
        CREATIVE_VENDING_UPGRADE = null,
        CONVERSION_UPGRADE = null,
        REDSTONE_UPGRADE = null,
        MIN_REDSTONE_UPGRADE = null,
        MAX_REDSTONE_UPGRADE = null,
        ILLUMINATION_UPGRADE = null,
        FILL_LEVEL_UPGRADE = null,
        UPGRADE_TEMPLATE = null,
        DRAWER_KEY = null,
        QUANTIFY_KEY = null,
        SHROUD_KEY = null;

    /*@ObjectHolder(StorageDrawers.MOD_ID + ":upgrade_template")
    public static Item upgradeTemplate;
    @ObjectHolder(StorageDrawers.MOD_ID + ":upgrade_storage")
    public static ItemUpgradeStorage upgradeStorage;
    @ObjectHolder(StorageDrawers.MOD_ID + ":upgrade_one_stack")
    public static ItemUpgrade upgradeOneStack;
    @ObjectHolder(StorageDrawers.MOD_ID + ":upgrade_status")
    public static ItemUpgradeStatus upgradeStatus;
    @ObjectHolder(StorageDrawers.MOD_ID + ":drawer_key")
    public static ItemDrawerKey drawerKey;
    @ObjectHolder(StorageDrawers.MOD_ID + ":upgrade_void")
    public static ItemUpgrade upgradeVoid;
    @ObjectHolder(StorageDrawers.MOD_ID + ":upgrade_conversion")
    public static ItemUpgrade upgradeConversion;
    @ObjectHolder(StorageDrawers.MOD_ID + ":upgrade_creative")
    public static ItemUpgradeCreative upgradeCreative;
    @ObjectHolder(StorageDrawers.MOD_ID + ":upgrade_redstone")
    public static ItemUpgradeRedstone upgradeRedstone;
    @ObjectHolder(StorageDrawers.MOD_ID + ":shroud_key")
    public static ItemShroudKey shroudKey;
    @ObjectHolder(StorageDrawers.MOD_ID + ":personal_key")
    public static ItemPersonalKey personalKey;
    @ObjectHolder(StorageDrawers.MOD_ID + ":quantify_key")
    public static ItemQuantifyKey quantifyKey;
    @ObjectHolder(StorageDrawers.MOD_ID + ":tape")
    public static ItemTape tape;*/

    @Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerItems (RegistryEvent.Register<Item> event) {
            register(event, "obsidian_storage_upgrade", new ItemUpgradeStorage(EnumUpgradeStorage.OBSIDIAN, new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "iron_storage_upgrade", new ItemUpgradeStorage(EnumUpgradeStorage.IRON, new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "gold_storage_upgrade", new ItemUpgradeStorage(EnumUpgradeStorage.GOLD, new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "diamond_storage_upgrade", new ItemUpgradeStorage(EnumUpgradeStorage.DIAMOND, new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "emerald_storage_upgrade", new ItemUpgradeStorage(EnumUpgradeStorage.EMERALD, new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "one_stack_upgrade", new ItemUpgrade(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "void_upgrade", new ItemUpgrade(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "creative_storage_upgrade", new ItemUpgrade(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "creative_vending_upgrade", new ItemUpgrade(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "conversion_upgrade", new ItemUpgrade(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "redstone_upgrade", new ItemUpgradeRedstone(EnumUpgradeRedstone.COMBINED, new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "min_redstone_upgrade", new ItemUpgradeRedstone(EnumUpgradeRedstone.MIN, new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "max_redstone_upgrade", new ItemUpgradeRedstone(EnumUpgradeRedstone.MAX, new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "illumination_upgrade", new ItemUpgrade(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "fill_level_upgrade", new ItemUpgrade(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "upgrade_template", new Item(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "drawer_key", new ItemDrawerKey(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "quantify_key", new ItemQuantifyKey(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));
            register(event, "shroud_key", new ItemShroudKey(new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS)));

            //IForgeRegistry<Item> itemRegistry = event.getRegistry();
            //ConfigManager config = StorageDrawers.config;

            /*itemRegistry.register(new Item().setUnlocalizedName(makeName("upgradeTemplate")).setRegistryName("upgrade_template").setCreativeTab(ModCreativeTabs.tabStorageDrawers));

            if (config.cache.enableStorageUpgrades) {
                itemRegistry.register(new ItemUpgradeStorage("upgrade_storage", makeName("upgradeStorage")));
                itemRegistry.register(new ItemUpgrade("upgrade_one_stack", makeName("upgradeOneStack")));
            }

            if (StorageDrawers.config.cache.enableIndicatorUpgrades)
                itemRegistry.register(new ItemUpgradeStatus("upgrade_status", makeName("upgradeStatus")));
            if (StorageDrawers.config.cache.enableVoidUpgrades)
                itemRegistry.register(new ItemUpgrade("upgrade_void", makeName("upgradeVoid")));
            if (StorageDrawers.config.cache.enableItemConversion)
                itemRegistry.register(new ItemUpgrade("upgrade_conversion", makeName("upgradeConversion")));
            if (StorageDrawers.config.cache.enableCreativeUpgrades)
                itemRegistry.register(new ItemUpgradeCreative("upgrade_creative", makeName("upgradeCreative")));
            if (StorageDrawers.config.cache.enableRedstoneUpgrades)
                itemRegistry.register(new ItemUpgradeRedstone("upgrade_redstone", makeName("upgradeRedstone")));
            if (StorageDrawers.config.cache.enableLockUpgrades)
                itemRegistry.register(new ItemDrawerKey("drawer_key", makeName("drawerKey")));
            if (StorageDrawers.config.cache.enableShroudUpgrades)
                itemRegistry.register(new ItemShroudKey("shroud_key", makeName("shroudKey")));
            if (StorageDrawers.config.cache.enablePersonalUpgrades)
                itemRegistry.register(new ItemPersonalKey("personal_key", makeName("personalKey")));
            if (StorageDrawers.config.cache.enableQuantifiableUpgrades)
                itemRegistry.register(new ItemQuantifyKey("quantify_key", makeName("quantifyKey")));
            if (StorageDrawers.config.cache.enableTape)
                itemRegistry.register(new ItemTape("tape", makeName("tape")));*/
        }

        private static void register(RegistryEvent.Register<Item> event, String key, Item item) {
            item.setRegistryName(new ResourceLocation(StorageDrawers.MOD_ID, key));
            event.getRegistry().register(item);
        }

        /*@SubscribeEvent
        public static void registerModels (ModelRegistryEvent event) {
            ModelRegistry modelRegistry = Chameleon.instance.modelRegistry;

            modelRegistry.registerItemVariants(upgradeTemplate);
            modelRegistry.registerItemVariants(upgradeVoid);
            modelRegistry.registerItemVariants(upgradeConversion);
            modelRegistry.registerItemVariants(tape);
            modelRegistry.registerItemVariants(drawerKey);
            modelRegistry.registerItemVariants(shroudKey);
            modelRegistry.registerItemVariants(personalKey);
            modelRegistry.registerItemVariants(quantifyKey);
            modelRegistry.registerItemVariants(upgradeStorage);
            modelRegistry.registerItemVariants(upgradeOneStack);
            modelRegistry.registerItemVariants(upgradeStatus);
            modelRegistry.registerItemVariants(upgradeCreative);
            modelRegistry.registerItemVariants(upgradeRedstone);
        }*/
    }

    /*public static String makeName (String name) {
        return StorageDrawers.MOD_ID.toLowerCase() + "." + name;
    }*/
}
