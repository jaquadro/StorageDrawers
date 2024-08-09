package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.meta.BlockMeta;
import com.jaquadro.minecraft.storagedrawers.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ModItems
{
    public static final DeferredRegister.Items ITEM_REGISTER = DeferredRegister.createItems(StorageDrawers.MOD_ID);

    private static final ResourceKey<CreativeModeTab> MAIN = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(StorageDrawers.MOD_ID, "storagedrawers"));

    public static final DeferredItem<? extends Item>
        OBSIDIAN_STORAGE_UPGRADE = ITEM_REGISTER.register("obsidian_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.OBSIDIAN, new Item.Properties())),
        IRON_STORAGE_UPGRADE = ITEM_REGISTER.register("iron_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.IRON, new Item.Properties())),
        GOLD_STORAGE_UPGRADE = ITEM_REGISTER.register("gold_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.GOLD, new Item.Properties())),
        DIAMOND_STORAGE_UPGRADE = ITEM_REGISTER.register("diamond_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.DIAMOND, new Item.Properties())),
        EMERALD_STORAGE_UPGRADE = ITEM_REGISTER.register("emerald_storage_upgrade", () -> new ItemUpgradeStorage(EnumUpgradeStorage.EMERALD, new Item.Properties())),
        ONE_STACK_UPGRADE = ITEM_REGISTER.register("one_stack_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        VOID_UPGRADE = ITEM_REGISTER.register("void_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        CREATIVE_STORAGE_UPGRADE = ITEM_REGISTER.register("creative_storage_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        CREATIVE_VENDING_UPGRADE = ITEM_REGISTER.register("creative_vending_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        //CONVERSION_UPGRADE = ITEM_REGISTER.register("conversion_upgrade", () -> new ItemUpgrade(new Item.Properties())),
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
        for (DeferredHolder<Block, ? extends Block> ro : ModBlocks.BLOCK_REGISTER.getEntries()) {
            if (ModBlocks.EXCLUDE_ITEMS.contains(ro.getId().getPath()))
                continue;

            ITEM_REGISTER.register(ro.getId().getPath(), () -> {
                Block block = ro.get();
                if (block instanceof BlockMeta)
                    return null;
                if (block instanceof BlockDrawers) {
                    return new ItemDrawers(block, new Item.Properties());
                } else {
                    return new BlockItem(block, new Item.Properties());
                }
            });
        }
        ITEM_REGISTER.register(bus);
    }

    public static void creativeModeTabRegister(RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            helper.register(MAIN, CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.OAK_FULL_DRAWERS_2.get()))
                .title(Component.translatable("storagedrawers"))
                .displayItems((params, output) -> {
                    ITEM_REGISTER.getEntries().forEach((reg) -> {
                        output.accept(new ItemStack(reg.get()));
                    });
                })
                .build());
        });
    }
}
