package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import com.jaquadro.minecraft.storagedrawers.block.framed.BlockFramedStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.framed.BlockFramedTrim;
import com.jaquadro.minecraft.storagedrawers.block.meta.BlockMeta;
import com.jaquadro.minecraft.storagedrawers.item.*;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ModItems
{
    public static final ChameleonRegistry<Item> ITEMS = ChameleonServices.REGISTRY.create(BuiltInRegistries.ITEM, ModConstants.MOD_ID);

    public static final List<RegistryEntry<? extends Item>> EXCLUDE_ITEMS_CREATIVE_TAB = new ArrayList<>();

    public static final RegistryEntry<? extends Item>
        OBSIDIAN_STORAGE_UPGRADE = register("obsidian_storage_upgrade", (p) -> new ItemUpgradeStorage(EnumUpgradeStorage.OBSIDIAN, p), new Item.Properties()),
        COPPER_STORAGE_UPGRADE = register("copper_storage_upgrade", (p) -> new ItemUpgradeStorage(EnumUpgradeStorage.COPPER, p), new Item.Properties()),
        IRON_STORAGE_UPGRADE = register("iron_storage_upgrade", (p) -> new ItemUpgradeStorage(EnumUpgradeStorage.IRON, p), new Item.Properties()),
        GOLD_STORAGE_UPGRADE = register("gold_storage_upgrade", (p) -> new ItemUpgradeStorage(EnumUpgradeStorage.GOLD, p), new Item.Properties()),
        EMERALD_STORAGE_UPGRADE = register("emerald_storage_upgrade", (p) -> new ItemUpgradeStorage(EnumUpgradeStorage.EMERALD, p), new Item.Properties()),
        DIAMOND_STORAGE_UPGRADE = register("diamond_storage_upgrade", (p) -> new ItemUpgradeStorage(EnumUpgradeStorage.DIAMOND, p), new Item.Properties()),
        NETHERITE_STORAGE_UPGRADE = register("netherite_storage_upgrade", (p) -> new ItemUpgradeStorage(EnumUpgradeStorage.NETHERITE, p), new Item.Properties()),
        ONE_STACK_UPGRADE = register("one_stack_upgrade", (p) -> new ItemUpgradeOneStack(p), new Item.Properties()),
        VOID_UPGRADE = register("void_upgrade", (p) -> new ItemUpgradeVoid(p), new Item.Properties()),
        CREATIVE_STORAGE_UPGRADE = register("creative_storage_upgrade", (p) -> new ItemUpgradeCreative(EnumUpgradeCreative.STORAGE, p), new Item.Properties()),
        CREATIVE_VENDING_UPGRADE = register("creative_vending_upgrade", (p) -> new ItemUpgradeCreative(EnumUpgradeCreative.VENDING, p), new Item.Properties()),
        CONVERSION_UPGRADE = register("conversion_upgrade", (p) -> new ItemUpgradeConversion(p), new Item.Properties()),
        REDSTONE_UPGRADE = register("redstone_upgrade", (p) -> new ItemUpgradeRedstone(EnumUpgradeRedstone.COMBINED, p), new Item.Properties()),
        MIN_REDSTONE_UPGRADE = register("min_redstone_upgrade", (p) -> new ItemUpgradeRedstone(EnumUpgradeRedstone.MIN, p), new Item.Properties()),
        MAX_REDSTONE_UPGRADE = register("max_redstone_upgrade", (p) -> new ItemUpgradeRedstone(EnumUpgradeRedstone.MAX, p), new Item.Properties()),
        ILLUMINATION_UPGRADE = register("illumination_upgrade", (p) -> new ItemUpgradeIllumination(p), new Item.Properties()),
        FILL_LEVEL_UPGRADE = register("fill_level_upgrade", (p) -> new ItemUpgradeFillLevel(p), new Item.Properties()),
        BALANCE_FILL_UPGRADE = register("balance_fill_upgrade", (p) -> new ItemUpgradeBalance(p), new Item.Properties()),
        PORTABILITY_UPGRADE = register("portability_upgrade", (p) -> new ItemUpgradePortability(p), new Item.Properties()),
        HOPPER_UPGRADE = register("hopper_upgrade", (p) -> new ItemUpgradeHopper(p), new Item.Properties()),
        MAGNET_UPGRADE = register("magnet_upgrade", (p) -> new ItemUpgradeMagnet(EnumUpgradeMagnet.LEVEL1, p), new Item.Properties()),
        MAGNET_UPGRADE_2 = register("magnet_upgrade_2", (p) -> new ItemUpgradeMagnet(EnumUpgradeMagnet.LEVEL2, p), new Item.Properties()),
        MAGNET_UPGRADE_3 = register("magnet_upgrade_3", (p) -> new ItemUpgradeMagnet(EnumUpgradeMagnet.LEVEL3, p), new Item.Properties()),
        REMOTE_UPGRADE = register("remote_upgrade", (p) -> new ItemUpgradeRemote(false, false, p), new Item.Properties()),
        REMOTE_UPGRADE_BOUND = register("remote_upgrade_bound", (p) -> new ItemUpgradeRemote(false, true, p), new Item.Properties()),
        REMOTE_GROUP_UPGRADE = register("remote_group_upgrade", (p) -> new ItemUpgradeRemote(true, false, p), new Item.Properties()),
        REMOTE_GROUP_UPGRADE_BOUND = register("remote_group_upgrade_bound", (p) -> new ItemUpgradeRemote(true, true, p), new Item.Properties()),
        UPGRADE_TEMPLATE = register("upgrade_template", (p) -> new Item(p), new Item.Properties()),
        DETACHED_DRAWER = register("detached_drawer", (p) -> new ItemDetachedDrawer(p), new Item.Properties()),
        DETACHED_DRAWER_FULL = register("detached_drawer_full", (p) -> new ItemDetachedDrawer(p), new Item.Properties().stacksTo(1).overrideDescription("item.storagedrawers.detached_drawer"));

    public static final RegistryEntry<? extends ItemKey>
        DRAWER_KEY = register("drawer_key", (p) -> new ItemDrawerKey(p), new Item.Properties()),
        QUANTIFY_KEY = register("quantify_key", (p) -> new ItemQuantifyKey(p), new Item.Properties()),
        SHROUD_KEY = register("shroud_key", (p) -> new ItemShroudKey(p), new Item.Properties()),
        PERSONAL_KEY = register("personal_key", (p) -> new ItemPersonalKey(null, p), new Item.Properties()),
        PERSONAL_KEY_COFH = register("personal_key_cofh", (p) -> new ItemPersonalKey("cofh", p), new Item.Properties()),
        PERSONAL_KEY_FTB = register("personal_key_ftb", (p) -> new ItemPersonalKey("ftb", p), new Item.Properties()),
        PERSONAL_KEY_UNLOCK = register("personal_key_unlock", (p) -> new ItemPersonalKey("unlock", p), new Item.Properties()),
        SUSPEND_KEY = register("suspend_key", (p) -> new ItemSuspendKey(p), new Item.Properties()),
        PRIORITY_KEY = register("priority_key", (p) -> new ItemPriorityKey(0, 1, p), new Item.Properties()),
        PRIORITY_KEY_P1 = register("priority_key_p1", (p) -> new ItemPriorityKey(1, 2, p), new Item.Properties()),
        PRIORITY_KEY_P2 = register("priority_key_p2", (p) -> new ItemPriorityKey(2, -1, p), new Item.Properties()),
        PRIORITY_KEY_N1 = register("priority_key_n1", (p) -> new ItemPriorityKey(-1, -2, p), new Item.Properties()),
        PRIORITY_KEY_N2 = register("priority_key_n2", (p) -> new ItemPriorityKey(-2, 0, p), new Item.Properties()),
        DRAWER_PULLER = register("drawer_puller", (p) -> new ItemDrawerPuller(p), new Item.Properties());

    public static final RegistryEntry<? extends ItemKeyring>
        KEYRING = register("keyring", (p) -> new ItemKeyring(null, p), new Item.Properties().stacksTo(1)),
        KEYRING_DRAWER = register("keyring_drawer", (p) -> new ItemKeyring(DRAWER_KEY, p), new Item.Properties().stacksTo(1)),
        KEYRING_QUANTIFY = register("keyring_quantify", (p) -> new ItemKeyring(QUANTIFY_KEY, p), new Item.Properties().stacksTo(1)),
        KEYRING_SHROUD = register("keyring_shroud", (p) -> new ItemKeyring(SHROUD_KEY, p), new Item.Properties().stacksTo(1)),
        KEYRING_PERSONAL = register("keyring_personal", (p) -> new ItemKeyring(PERSONAL_KEY, p), new Item.Properties().stacksTo(1)),
        KEYRING_PERSONAL_COFH = register("keyring_personal_cofh", (p) -> new ItemKeyring(PERSONAL_KEY_COFH, p), new Item.Properties().stacksTo(1)),
        KEYRING_PERSONAL_FTB = register("keyring_personal_ftb", (p) -> new ItemKeyring(PERSONAL_KEY_FTB, p), new Item.Properties().stacksTo(1)),
        KEYRING_PERSONAL_UNLOCK = register("keyring_personal_unlock", (p) -> new ItemKeyring(PERSONAL_KEY_UNLOCK, p), new Item.Properties().stacksTo(1)),
        KEYRING_SUSPEND = register("keyring_suspend", (p) -> new ItemKeyring(SUSPEND_KEY, p), new Item.Properties().stacksTo(1)),
        KEYRING_PRIORITY = register("keyring_priority", (p) -> new ItemKeyring(PRIORITY_KEY, p), new Item.Properties().stacksTo(1)),
        KEYRING_PRIORITY_P1 = register("keyring_priority_p1", (p) -> new ItemKeyring(PRIORITY_KEY_P1, p), new Item.Properties().stacksTo(1)),
        KEYRING_PRIORITY_P2 = register("keyring_priority_p2", (p) -> new ItemKeyring(PRIORITY_KEY_P2, p), new Item.Properties().stacksTo(1)),
        KEYRING_PRIORITY_N1 = register("keyring_priority_n1", (p) -> new ItemKeyring(PRIORITY_KEY_N1, p), new Item.Properties().stacksTo(1)),
        KEYRING_PRIORITY_N2 = register("keyring_priority_n2", (p) -> new ItemKeyring(PRIORITY_KEY_N2, p), new Item.Properties().stacksTo(1)),
        KEYRING_PULLER = register("keyring_puller", (p) -> new ItemKeyring(DRAWER_PULLER, p), new Item.Properties().stacksTo(1));

    private static <C extends Item> RegistryEntry<C> register (String name, Function<Item.Properties, C> supplier, Item.Properties props) {
        return ITEMS.register(name, () -> supplier.apply(props.setId(modKey(name))));
    }
    
    private ModItems () { }

    public static void init (ChameleonInit.InitContext context) {
        EXCLUDE_ITEMS_CREATIVE_TAB.add(PRIORITY_KEY_N1);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(PRIORITY_KEY_N2);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(PRIORITY_KEY_P1);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(PRIORITY_KEY_P2);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_DRAWER);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_QUANTIFY);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_SHROUD);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PERSONAL);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PERSONAL_COFH);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PERSONAL_FTB);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PERSONAL_UNLOCK);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_SUSPEND);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PRIORITY);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PRIORITY_P1);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PRIORITY_P2);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PRIORITY_N1);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PRIORITY_N2);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(KEYRING_PULLER);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(DETACHED_DRAWER_FULL);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(REMOTE_UPGRADE_BOUND);
        EXCLUDE_ITEMS_CREATIVE_TAB.add(REMOTE_GROUP_UPGRADE_BOUND);

        for (RegistryEntry<Block> ro : ModBlocks.BLOCKS.getEntries()) {
            if (ModBlocks.EXCLUDE_ITEMS.contains(ro.getId().getPath()))
                continue;

            registerBlock(ITEMS, ro);
        }

        ITEMS.init(context);
    }

    static Identifier modLoc (String name) {
        return Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, name);
    }

    static ResourceKey<Item> modKey (String name) {
        return ResourceKey.create(Registries.ITEM, modLoc(name));
    }

    static ResourceKey<Item> modKey (Identifier name) {
        return ResourceKey.create(Registries.ITEM, name);
    }

    static void registerBlock (ChameleonRegistry<Item> register, RegistryEntry<? extends Block> blockHolder) {
        if (blockHolder == null)
            return;

        register.register(blockHolder.getId().getPath(), () -> {
            Block block = blockHolder.get();
            Item.Properties itemProperties = new Item.Properties().useBlockDescriptionPrefix().setId(modKey(blockHolder.getId()));
            
            if (block instanceof BlockMeta)
                return null;
            if (block instanceof BlockFramedStandardDrawers) {
                return new ItemFramedDrawers(block, itemProperties);
            } else if (block instanceof BlockDrawers) {
                return new ItemDrawers(block, itemProperties);
            } else if (block instanceof BlockFramedTrim) {
                return new ItemFramedTrim(block, itemProperties);
            } else if (block instanceof BlockTrim) {
                return new ItemTrim(block, itemProperties);
            } else {
                return new BlockItem(block, itemProperties);
            }
        });
    }

    private static <B extends Item> Stream<B> getItemsOfType (Class<B> itemClass) {
        return BuiltInRegistries.ITEM.stream().filter(itemClass::isInstance).map(itemClass::cast);
    }

    public static Stream<ItemKey> getKeys () {
        return getItemsOfType(ItemKey.class);
    }

    public static Stream<ItemKeyring> getKeyrings () {
        return getItemsOfType(ItemKeyring.class);
    }
}
