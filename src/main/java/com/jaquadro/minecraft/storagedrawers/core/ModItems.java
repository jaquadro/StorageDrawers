package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

public final class ModItems
{
    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, StorageDrawers.MOD_ID);

    public static final RegistryObject<Item>
        OBSIDIAN_STORAGE_UPGRADE = ITEM_REGISTER.register("obsidian_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.OBSIDIAN, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        IRON_STORAGE_UPGRADE = ITEM_REGISTER.register("iron_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.IRON, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        GOLD_STORAGE_UPGRADE = ITEM_REGISTER.register("gold_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.GOLD, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        DIAMOND_STORAGE_UPGRADE = ITEM_REGISTER.register("diamond_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.DIAMOND, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        EMERALD_STORAGE_UPGRADE = ITEM_REGISTER.register("emerald_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.EMERALD, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        ONE_STACK_UPGRADE = ITEM_REGISTER.register("one_stack_upgrade", () -> new ItemUpgrade(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        VOID_UPGRADE = ITEM_REGISTER.register("void_upgrade", () -> new ItemUpgrade(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        CREATIVE_STORAGE_UPGRADE = ITEM_REGISTER.register("creative_storage_upgrade", () -> new ItemUpgrade(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        CREATIVE_VENDING_UPGRADE = ITEM_REGISTER.register("creative_vending_upgrade", () -> new ItemUpgrade(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        CONVERSION_UPGRADE = ITEM_REGISTER.register("conversion_upgrade", () -> new ItemUpgrade(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        REDSTONE_UPGRADE = ITEM_REGISTER.register("redstone_upgrade", () -> new ItemUpgradeRedstone(EnumUpgradeRedstone.COMBINED, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        MIN_REDSTONE_UPGRADE = ITEM_REGISTER.register("min_redstone_upgrade", () -> new ItemUpgradeRedstone(EnumUpgradeRedstone.MIN, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        MAX_REDSTONE_UPGRADE = ITEM_REGISTER.register("max_redstone_upgrade", () -> new ItemUpgradeRedstone(EnumUpgradeRedstone.MAX, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        ILLUMINATION_UPGRADE = ITEM_REGISTER.register("illumination_upgrade", () -> new ItemUpgrade(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        FILL_LEVEL_UPGRADE = ITEM_REGISTER.register("fill_level_upgrade", () -> new ItemUpgrade(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        UPGRADE_TEMPLATE = ITEM_REGISTER.register("upgrade_template", () -> new Item(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS)));

    public static final RegistryObject<ItemKey>
        DRAWER_KEY = ITEM_REGISTER.register("drawer_key", () -> new ItemDrawerKey(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        QUANTIFY_KEY = ITEM_REGISTER.register("quantify_key", () -> new ItemQuantifyKey(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        SHROUD_KEY = ITEM_REGISTER.register("shroud_key", () -> new ItemShroudKey(new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS)));

    public static final RegistryObject<ItemKeyring>
        KEYRING = ITEM_REGISTER.register("keyring", () -> new ItemKeyring(null, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS))),
        KEYRING_DRAWER = ITEM_REGISTER.register("keyring_drawer", () -> new ItemKeyring(DRAWER_KEY, new Item.Properties().stacksTo(1))),
        KEYRING_QUANTIFY = ITEM_REGISTER.register("keyring_quantify", () -> new ItemKeyring(QUANTIFY_KEY, new Item.Properties().stacksTo(1))),
        KEYRING_SHROUD = ITEM_REGISTER.register("keyring_shroud", () -> new ItemKeyring(SHROUD_KEY, new Item.Properties().stacksTo(1)));

    private ModItems() {}

    public static void register(IEventBus bus) {
        for (RegistryObject<Block> ro : ModBlocks.BLOCK_REGISTER.getEntries()) {
            ITEM_REGISTER.register(ro.getId().getPath(), () -> {
                Block block = ro.get();
                if (block instanceof BlockDrawers) {
                    return new ItemDrawers(block, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS));
                } else {
                    return new BlockItem(block, new Item.Properties().tab(ModItemGroup.STORAGE_DRAWERS));
                }
            });
        }
        ITEM_REGISTER.register(bus);
    }

    private static <B extends Item> Stream<B> getItemsOfType(Class<B> itemClass) {
        return ForgeRegistries.ITEMS.getValues().stream().filter(itemClass::isInstance).map(itemClass::cast);
    }

    public static Stream<ItemKey> getKeys() {
        return getItemsOfType(ItemKey.class);
    }

    public static Stream<ItemKeyring> getKeyrings() {
        return getItemsOfType(ItemKeyring.class);
    }
}
