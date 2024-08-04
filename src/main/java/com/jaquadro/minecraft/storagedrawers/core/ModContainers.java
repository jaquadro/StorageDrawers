package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.inventory.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModContainers
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS_REGISTER = DeferredRegister.create(Registries.MENU, StorageDrawers.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerDrawers1>> DRAWER_CONTAINER_1 = registerContainer("drawer_container_1", ContainerDrawers1::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ContainerDrawers2>> DRAWER_CONTAINER_2 = registerContainer("drawer_container_2", ContainerDrawers2::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ContainerDrawers4>> DRAWER_CONTAINER_4 = registerContainer("drawer_container_4", ContainerDrawers4::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ContainerDrawersComp>> DRAWER_CONTAINER_COMP = registerContainer("drawer_container_comp", ContainerDrawersComp::new);

    private static <C extends ContainerDrawers> DeferredHolder<MenuType<?>, MenuType<C>> registerContainer(String name, IContainerFactory<C> containerFactory) {
        return CONTAINERS_REGISTER.register(name, () -> IMenuTypeExtension.create(containerFactory));
    }

    public static void register(IEventBus bus) {
        CONTAINERS_REGISTER.register(bus);
    }
}
