package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs
{
    private static final ResourceKey<CreativeModeTab> MAIN = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "storagedrawers"));
    private static final CreativeModeTab MAIN_TAB = FabricItemGroup.builder()
        .icon(() -> new ItemStack(ModBlocks.OAK_FULL_DRAWERS_2.get()))
        .title(Component.translatable("itemGroup.storagedrawers"))
        .build();

    public static void init () {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MAIN, MAIN_TAB);

        ItemGroupEvents.modifyEntriesEvent(MAIN).register(itemGroup -> {
            ModItems.ITEMS.getEntries().forEach((reg) -> {
                if (reg == null)
                    return;
                if (ModItems.EXCLUDE_ITEMS_CREATIVE_TAB.contains(reg))
                    return;
                itemGroup.accept(new ItemStack(reg.get()));
            });
        });
    }

    /*
    public static void init (RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            helper.register(MAIN, CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.OAK_FULL_DRAWERS_2.get()))
                .title(Component.translatable("itemGroup.storagedrawers"))
                .displayItems((params, output) -> {
                    ModItems.ITEMS.getEntries().forEach((reg) -> {
                        if (reg == null)
                            return;
                        if (ModItems.EXCLUDE_ITEMS_CREATIVE_TAB.contains(reg))
                            return;
                        output.accept(new ItemStack(reg.get()));
                    });
                })
                .build());
        });
    }*/
}
