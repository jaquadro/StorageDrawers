package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(StorageDrawers.MOD_ID)
public class ModContainers
{
    public static final ContainerType<ContainerDrawers1> DRAWER_CONTAINER_1 = null;
    public static final ContainerType<ContainerDrawers2> DRAWER_CONTAINER_2 = null;
    public static final ContainerType<ContainerDrawers4> DRAWER_CONTAINER_4 = null;

    @Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {

        @SubscribeEvent
        public static void registerContainers (RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create(ContainerDrawers1::new).setRegistryName("drawer_container_1"));
            event.getRegistry().register(IForgeContainerType.create(ContainerDrawers2::new).setRegistryName("drawer_container_2"));
            event.getRegistry().register(IForgeContainerType.create(ContainerDrawers4::new).setRegistryName("drawer_container_4"));
        }
    }

    public static void registerScreens () {
        ScreenManager.registerFactory(DRAWER_CONTAINER_1, DrawerScreen.Slot1::new);
        ScreenManager.registerFactory(DRAWER_CONTAINER_2, DrawerScreen.Slot2::new);
        ScreenManager.registerFactory(DRAWER_CONTAINER_4, DrawerScreen.Slot4::new);
    }
}
