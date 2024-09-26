package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.inventory.*;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.inventory.content.PositionContent;
import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

public class ModContainers
{
    public static final ChameleonRegistry<MenuType<?>> CONTAINERS = ChameleonServices.REGISTRY.create(BuiltInRegistries.MENU, ModConstants.MOD_ID);

    public static final RegistryEntry<MenuType<ContainerDrawers1>> DRAWER_CONTAINER_1 =
        CONTAINERS.register("drawer_container_1", ChameleonServices.CONTAINER.getContainerSupplier(ContainerDrawers1::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<ContainerDrawers2>> DRAWER_CONTAINER_2 =
        CONTAINERS.register("drawer_container_2", ChameleonServices.CONTAINER.getContainerSupplier(ContainerDrawers2::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<ContainerDrawers4>> DRAWER_CONTAINER_4 =
        CONTAINERS.register("drawer_container_4", ChameleonServices.CONTAINER.getContainerSupplier(ContainerDrawers4::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<ContainerDrawersComp3>> DRAWER_CONTAINER_COMP_2 =
        CONTAINERS.register("drawer_container_comp_2", ChameleonServices.CONTAINER.getContainerSupplier(ContainerDrawersComp3::new, PositionContent.SERIALIZER));
    public static final RegistryEntry<MenuType<ContainerDrawersComp3>> DRAWER_CONTAINER_COMP_3 =
        CONTAINERS.register("drawer_container_comp_3", ChameleonServices.CONTAINER.getContainerSupplier(ContainerDrawersComp3::new, PositionContent.SERIALIZER));

    public static void init() {
        CONTAINERS.init();
    }
}
