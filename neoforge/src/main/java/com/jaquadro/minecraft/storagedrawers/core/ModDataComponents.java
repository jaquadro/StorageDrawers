package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.components.item.DrawerCountData;
import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(StorageDrawers.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DrawerCountData>> DRAWER_COUNT =
        COMPONENTS.register("drawer_count", () -> DataComponentType.<DrawerCountData>builder().persistent(DrawerCountData.CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KeyringContents>> KEYRING_CONTENTS =
        COMPONENTS.register("keyring_content", () -> DataComponentType.<KeyringContents>builder().persistent(KeyringContents.CODEC).build());
}
