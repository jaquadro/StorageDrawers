package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems
{
    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, StorageDrawers.MOD_ID);

    private static CreativeModeTab MAIN;

    public static final RegistryObject<Item>
        OBSIDIAN_STORAGE_UPGRADE = ITEM_REGISTER.register("obsidian_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.OBSIDIAN, new Item.Properties())),
        IRON_STORAGE_UPGRADE = ITEM_REGISTER.register("iron_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.IRON, new Item.Properties())),
        GOLD_STORAGE_UPGRADE = ITEM_REGISTER.register("gold_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.GOLD, new Item.Properties())),
        DIAMOND_STORAGE_UPGRADE = ITEM_REGISTER.register("diamond_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.DIAMOND, new Item.Properties())),
        EMERALD_STORAGE_UPGRADE = ITEM_REGISTER.register("emerald_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.EMERALD, new Item.Properties())),
        ONE_STACK_UPGRADE = ITEM_REGISTER.register("one_stack_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        VOID_UPGRADE = ITEM_REGISTER.register("void_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        CREATIVE_STORAGE_UPGRADE = ITEM_REGISTER.register("creative_storage_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        CREATIVE_VENDING_UPGRADE = ITEM_REGISTER.register("creative_vending_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        CONVERSION_UPGRADE = ITEM_REGISTER.register("conversion_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        REDSTONE_UPGRADE = ITEM_REGISTER.register("redstone_upgrade", () -> new ItemUpgradeRedstone(EnumUpgradeRedstone.COMBINED, new Item.Properties())),
        MIN_REDSTONE_UPGRADE = ITEM_REGISTER.register("min_redstone_upgrade", () -> new ItemUpgradeRedstone(EnumUpgradeRedstone.MIN, new Item.Properties())),
        MAX_REDSTONE_UPGRADE = ITEM_REGISTER.register("max_redstone_upgrade", () -> new ItemUpgradeRedstone(EnumUpgradeRedstone.MAX, new Item.Properties())),
        ILLUMINATION_UPGRADE = ITEM_REGISTER.register("illumination_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        FILL_LEVEL_UPGRADE = ITEM_REGISTER.register("fill_level_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        UPGRADE_TEMPLATE = ITEM_REGISTER.register("upgrade_template", () -> new Item(new Item.Properties())),
        DRAWER_KEY = ITEM_REGISTER.register("drawer_key", () -> new ItemDrawerKey(new Item.Properties())),
        QUANTIFY_KEY = ITEM_REGISTER.register("quantify_key", () -> new ItemQuantifyKey(new Item.Properties())),
        SHROUD_KEY = ITEM_REGISTER.register("shroud_key", () -> new ItemShroudKey(new Item.Properties()));

    private ModItems() {}

    public static void register(IEventBus bus) {
        for (RegistryObject<Block> ro : ModBlocks.BLOCK_REGISTER.getEntries()) {
            ITEM_REGISTER.register(ro.getId().getPath(), () -> {
                Block block = ro.get();
                if (block instanceof BlockDrawers) {
                    return new ItemDrawers(block, new Item.Properties());
                } else {
                    return new BlockItem(block, new Item.Properties());
                }
            });
        }
        ITEM_REGISTER.register(bus);
    }

    public static void creativeModeTabRegister(CreativeModeTabEvent.Register event) {
        MAIN = event.registerCreativeModeTab(new ResourceLocation(StorageDrawers.MOD_ID, "storagedrawers"), builder -> builder
            .icon(() -> new ItemStack(ModBlocks.OAK_FULL_DRAWERS_2.get()))
            .title(Component.translatable("storagedrawers"))
            .displayItems((params, output) -> {
                ITEM_REGISTER.getEntries().forEach((reg) -> {
                    output.accept(new ItemStack(reg.get()));
                });
            }));
    }
}
