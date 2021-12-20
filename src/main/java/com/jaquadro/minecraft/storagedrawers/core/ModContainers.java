package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.inventory.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(StorageDrawers.MOD_ID)
public class ModContainers
{
    public static final MenuType<ContainerDrawers1> DRAWER_CONTAINER_1 = null;
    public static final MenuType<ContainerDrawers2> DRAWER_CONTAINER_2 = null;
    public static final MenuType<ContainerDrawers4> DRAWER_CONTAINER_4 = null;
    public static final MenuType<ContainerDrawersComp> DRAWER_CONTAINER_COMP = null;

    @Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {

        @SubscribeEvent
        public static void registerContainers (RegistryEvent.Register<MenuType<?>> event) {
            event.getRegistry().register(IForgeMenuType.create(ContainerDrawers1::new).setRegistryName("drawer_container_1"));
            event.getRegistry().register(IForgeMenuType.create(ContainerDrawers2::new).setRegistryName("drawer_container_2"));
            event.getRegistry().register(IForgeMenuType.create(ContainerDrawers4::new).setRegistryName("drawer_container_4"));
            event.getRegistry().register(IForgeMenuType.create(ContainerDrawersComp::new).setRegistryName("drawer_container_comp"));
        }
    }

    public static void registerScreens () {
        MenuScreens.register(DRAWER_CONTAINER_1, DrawerScreen.Slot1::new);
        MenuScreens.register(DRAWER_CONTAINER_2, DrawerScreen.Slot2::new);
        MenuScreens.register(DRAWER_CONTAINER_4, DrawerScreen.Slot4::new);
        MenuScreens.register(DRAWER_CONTAINER_COMP, DrawerScreen.Compacting::new);
    }
}
