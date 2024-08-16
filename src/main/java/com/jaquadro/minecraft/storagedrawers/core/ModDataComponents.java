package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.components.item.DrawerCountData;
import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModDataComponents
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, StorageDrawers.MOD_ID);

    public static final RegistryObject<DataComponentType<DrawerCountData>> DRAWER_COUNT =
        COMPONENTS.register("drawer_count", () -> DataComponentType.<DrawerCountData>builder().persistent(DrawerCountData.CODEC).build());
    public static final RegistryObject<DataComponentType<KeyringContents>> KEYRING_CONTENTS =
        COMPONENTS.register("keyring_content", () -> DataComponentType.<KeyringContents>builder().persistent(KeyringContents.CODEC).build());
}
