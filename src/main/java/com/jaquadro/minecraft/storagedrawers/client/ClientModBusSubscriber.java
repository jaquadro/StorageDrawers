package com.jaquadro.minecraft.storagedrawers.client;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.gui.ClientDetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.client.renderer.BlockEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerScreen;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.DetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.KeyringTooltip;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModBusSubscriber {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            /*
            I'd like to do this registration in bulk as well, but I'm hesitant to put
            client-only code in common classes like ModContainers (memorizing screen types).
            BERs at least all have the same renderer.
            */
            MenuScreens.register(ModContainers.DRAWER_CONTAINER_1.get(), DrawerScreen.Slot1::new);
            MenuScreens.register(ModContainers.DRAWER_CONTAINER_2.get(), DrawerScreen.Slot2::new);
            MenuScreens.register(ModContainers.DRAWER_CONTAINER_4.get(), DrawerScreen.Slot4::new);
            MenuScreens.register(ModContainers.DRAWER_CONTAINER_COMP.get(), DrawerScreen.Compacting::new);
        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(RegisterRenderers event) {
        ModBlockEntities.getBlockEntityTypesWithRenderers().forEach(ro -> event.registerBlockEntityRenderer(ro.get(), BlockEntityDrawersRenderer::new));
    }

    @SubscribeEvent
    public static void registerClientTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(KeyringTooltip.class, ClientBundleTooltip::new);
        event.register(DetachedDrawerTooltip.class, ClientDetachedDrawerTooltip::new);
    }
}
