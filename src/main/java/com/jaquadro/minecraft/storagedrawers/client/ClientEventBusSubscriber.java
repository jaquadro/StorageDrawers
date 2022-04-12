package com.jaquadro.minecraft.storagedrawers.client;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModBlocks.getDrawers().forEach(blockDrawers -> ItemBlockRenderTypes.setRenderLayer(blockDrawers, RenderType.cutoutMipped()));
    }

    @SubscribeEvent
    public static void registerEntityRenderers(RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.STANDARD_DRAWERS_1.get(), TileEntityDrawersRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.STANDARD_DRAWERS_2.get(), TileEntityDrawersRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.STANDARD_DRAWERS_4.get(), TileEntityDrawersRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FRACTIONAL_DRAWERS_3.get(), TileEntityDrawersRenderer::new);
    }
}
