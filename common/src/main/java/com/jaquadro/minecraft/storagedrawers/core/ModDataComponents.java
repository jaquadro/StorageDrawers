package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.components.item.DrawerCountData;
import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModDataComponents
{
    public static final ChameleonRegistry<DataComponentType<?>> COMPONENTS = ChameleonServices.REGISTRY.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<DataComponentType<DrawerCountData>> DRAWER_COUNT =
        COMPONENTS.register("drawer_count", () -> DataComponentType.<DrawerCountData>builder().persistent(DrawerCountData.CODEC).build());
    public static final RegistryEntry<DataComponentType<KeyringContents>> KEYRING_CONTENTS =
        COMPONENTS.register("keyring_content", () -> DataComponentType.<KeyringContents>builder().persistent(KeyringContents.CODEC).build());

    public static void init() {
        COMPONENTS.init();
    }
}
