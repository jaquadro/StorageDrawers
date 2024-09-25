package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.inventory.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainers
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS_REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, StorageDrawers.MOD_ID);

    public static final RegistryObject<MenuType<ContainerDrawers1>> DRAWER_CONTAINER_1 = registerContainer("drawer_container_1", ContainerDrawers1::new);
    public static final RegistryObject<MenuType<ContainerDrawers2>> DRAWER_CONTAINER_2 = registerContainer("drawer_container_2", ContainerDrawers2::new);
    public static final RegistryObject<MenuType<ContainerDrawers4>> DRAWER_CONTAINER_4 = registerContainer("drawer_container_4", ContainerDrawers4::new);
    public static final RegistryObject<MenuType<ContainerDrawersComp2>> DRAWER_CONTAINER_COMP_2 = registerContainer("drawer_container_comp_2", ContainerDrawersComp2::new);
    public static final RegistryObject<MenuType<ContainerDrawersComp3>> DRAWER_CONTAINER_COMP_3 = registerContainer("drawer_container_comp_3", ContainerDrawersComp3::new);

    private static <C extends ContainerDrawers> RegistryObject<MenuType<C>> registerContainer(String name, IContainerFactory<C> containerFactory) {
        return CONTAINERS_REGISTER.register(name, () -> IForgeMenuType.create(containerFactory));
    }

    public static void register(IEventBus bus) {
        CONTAINERS_REGISTER.register(bus);
    }
}
