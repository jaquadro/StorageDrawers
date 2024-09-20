package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.client.model.BasicDrawerModel;
import com.jaquadro.minecraft.storagedrawers.client.model.ModelLoadPlugin;
import com.jaquadro.minecraft.storagedrawers.client.renderer.BlockEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@Environment(EnvType.CLIENT)
public class StorageDrawersClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient () {
        ModBlockEntities.getBlockEntityTypesWithRenderers().forEach(ro ->
            BlockEntityRenderers.register(ro.get(), BlockEntityDrawersRenderer::new));

        ModelLoadingPlugin.register(new ModelLoadPlugin());

        ModBlocks.getDrawers().forEach(block ->
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped()));
    }
}
