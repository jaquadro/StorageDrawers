package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class ModItems
{
    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, StorageDrawers.MOD_ID);
    public static final List<RegistryObject<? extends Item>> EXCLUDE_ITEMS_CREATIVE_TAB = new ArrayList<>();

    private static final ResourceKey<CreativeModeTab> MAIN = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(StorageDrawers.MOD_ID, "storagedrawers"));

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
        //CONVERSION_UPGRADE = ITEM_REGISTER.register("conversion_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        REDSTONE_UPGRADE = ITEM_REGISTER.register("redstone_upgrade", () -> new ItemUpgradeRedstone(EnumUpgradeRedstone.COMBINED, new Item.Properties())),
        MIN_REDSTONE_UPGRADE = ITEM_REGISTER.register("min_redstone_upgrade", () -> new ItemUpgradeRedstone(EnumUpgradeRedstone.MIN, new Item.Properties())),
        MAX_REDSTONE_UPGRADE = ITEM_REGISTER.register("max_redstone_upgrade", () -> new ItemUpgradeRedstone(EnumUpgradeRedstone.MAX, new Item.Properties())),
        ILLUMINATION_UPGRADE = ITEM_REGISTER.register("illumination_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        FILL_LEVEL_UPGRADE = ITEM_REGISTER.register("fill_level_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        BALANCE_FILL_UPGRADE = ITEM_REGISTER.register("balance_fill_upgrade", () -> new ItemUpgrade(new Item.Properties())),
        UPGRADE_TEMPLATE = ITEM_REGISTER.register("upgrade_template", () -> new Item(new Item.Properties())),
        DETACHED_DRAWER = ITEM_REGISTER.register("detached_drawer", () -> new ItemDetachedDrawer(new Item.Properties())),
        DETACHED_DRAWER_FULL = ITEM_REGISTER.register("detached_drawer_full", () -> new ItemDetachedDrawer(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<ItemKey>
        DRAWER_KEY = ITEM_REGISTER.register("drawer_key", () -> new ItemDrawerKey(new Item.Properties())),
        QUANTIFY_KEY = ITEM_REGISTER.register("quantify_key", () -> new ItemQuantifyKey(new Item.Properties())),
        SHROUD_KEY = ITEM_REGISTER.register("shroud_key", () -> new ItemShroudKey(new Item.Properties())),
        DRAWER_PULLER = ITEM_REGISTER.register("drawer_puller", () -> new ItemDrawerPuller(new Item.Properties()));

    public static final RegistryObject<ItemKeyring>
        KEYRING = ITEM_REGISTER.register("keyring", () -> new ItemKeyring(null, new Item.Properties().stacksTo(1))),
        KEYRING_DRAWER = ITEM_REGISTER.register("keyring_drawer", () -> new ItemKeyring(DRAWER_KEY, new Item.Properties().stacksTo(1))),
        KEYRING_QUANTIFY = ITEM_REGISTER.register("keyring_quantify", () -> new ItemKeyring(QUANTIFY_KEY, new Item.Properties().stacksTo(1))),
        KEYRING_SHROUD = ITEM_REGISTER.register("keyring_shroud", () -> new ItemKeyring(SHROUD_KEY, new Item.Properties().stacksTo(1))),
        KEYRING_PULLER = ITEM_REGISTER.register("keyring_puller", () -> new ItemKeyring(DRAWER_PULLER, new Item.Properties().stacksTo(1)));

    private ModItems() { }

    public static void register(IEventBus bus) {
        for (RegistryObject<Block> ro : ModBlocks.BLOCK_REGISTER.getEntries()) {
            if (ModBlocks.EXCLUDE_ITEMS.contains(ro.getId().getPath()))
                continue;

            registerBlock(ITEM_REGISTER, ro);
        }

        ITEM_REGISTER.register(bus);
    }

    static void registerBlock(DeferredRegister<Item> register, RegistryObject<? extends Block> blockHolder) {
        if (blockHolder == null)
            return;

        register.register(blockHolder.getId().getPath(), () -> {
            Block block = blockHolder.get();
            if (block instanceof BlockMeta)
                return null;
            if (block instanceof BlockDrawers) {
                return new ItemDrawers(block, new Item.Properties());
            } else if (block instanceof BlockTrim) {
                return new ItemTrim(block, new Item.Properties());
            } else {
                return new BlockItem(block, new Item.Properties());
            }
        });
    }

    public static void creativeModeTabRegister(RegisterEvent event) {
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_DRAWER);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_QUANTIFY);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_SHROUD);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PULLER);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(DETACHED_DRAWER_FULL);

        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            helper.register(MAIN, CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.OAK_FULL_DRAWERS_2.get()))
                .title(Component.translatable("itemGroup.storagedrawers"))
                .displayItems((params, output) -> {
                    ITEM_REGISTER.getEntries().forEach((reg) -> {
                        if (reg == null || !reg.isPresent())
                            return;
                        if (ModItems.EXCLUDE_ITEMS_CREATIVE_TAB.contains(reg))
                            return;
                        output.accept(reg.get().getDefaultInstance());
                    });
                })
                .build());
        });
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
