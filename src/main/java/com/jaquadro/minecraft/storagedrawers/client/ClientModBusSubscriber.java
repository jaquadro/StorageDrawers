package com.jaquadro.minecraft.storagedrawers.client;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.BlockEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModBusSubscriber
{
    @SubscribeEvent
    public static void clientSetup(RegisterMenuScreensEvent event) {
        event.register(ModContainers.DRAWER_CONTAINER_1.get(), DrawerScreen.Slot1::new);
        event.register(ModContainers.DRAWER_CONTAINER_2.get(), DrawerScreen.Slot2::new);
        event.register(ModContainers.DRAWER_CONTAINER_4.get(), DrawerScreen.Slot4::new);
        event.register(ModContainers.DRAWER_CONTAINER_COMP.get(), DrawerScreen.Compacting::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(RegisterRenderers event) {
        ModBlockEntities.getBlockEntityTypesWithRenderers().forEach(ro -> event.registerBlockEntityRenderer(ro.get(), BlockEntityDrawersRenderer::new));
    }
}
