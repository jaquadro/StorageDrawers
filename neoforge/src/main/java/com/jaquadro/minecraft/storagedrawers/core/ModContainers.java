package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.inventory.*;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;

public class ModContainers
{
    public static final ChameleonRegistry<MenuType<?>> CONTAINERS = ChameleonServices.REGISTRY.create(BuiltInRegistries.MENU, ModConstants.MOD_ID);

    //public static final DeferredRegister<MenuType<?>> CONTAINERS_REGISTER = DeferredRegister.create(Registries.MENU, StorageDrawers.MOD_ID);

    public static final RegistryEntry<MenuType<ContainerDrawers1>> DRAWER_CONTAINER_1 = registerContainer("drawer_container_1", ContainerDrawers1::new);
    public static final RegistryEntry<MenuType<ContainerDrawers2>> DRAWER_CONTAINER_2 = registerContainer("drawer_container_2", ContainerDrawers2::new);
    public static final RegistryEntry<MenuType<ContainerDrawers4>> DRAWER_CONTAINER_4 = registerContainer("drawer_container_4", ContainerDrawers4::new);
    public static final RegistryEntry<MenuType<ContainerDrawersComp>> DRAWER_CONTAINER_COMP = registerContainer("drawer_container_comp", ContainerDrawersComp::new);

    private static <C extends ContainerDrawers> RegistryEntry<MenuType<C>> registerContainer(String name, IContainerFactory<C> containerFactory) {
        return CONTAINERS.register(name, () -> IMenuTypeExtension.create(containerFactory));
    }

    public static void init() {
        CONTAINERS.init();
    }
}
