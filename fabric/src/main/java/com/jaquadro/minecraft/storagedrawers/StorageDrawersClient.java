package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.client.gui.ClientDetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.client.gui.ClientKeyringTooltip;
import com.jaquadro.minecraft.storagedrawers.client.model.ModelLoadPlugin;
import com.jaquadro.minecraft.storagedrawers.client.model.PlatformDecoratedModel;
import com.jaquadro.minecraft.storagedrawers.client.renderer.BlockEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.BlockEntityFramingRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerScreen;
import com.jaquadro.minecraft.storagedrawers.inventory.FramingTableScreen;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.DetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.KeyringTooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public class StorageDrawersClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient () {
        ModBlockEntities.DRAWER_TYPES.forEach(ro -> BlockEntityRenderers.register(ro.get(), BlockEntityDrawersRenderer::new));
        BlockEntityRenderers.register(ModBlockEntities.FRAMING_TABLE.get(), BlockEntityFramingRenderer::new);

        // Register model mapper on initialization
        ItemModels.ID_MAPPER.put(
            ModConstants.loc("framed_block"), PlatformDecoratedModel.PlatformDecoratedItemModel.Unbaked.MAP_CODEC
        );

        ModelLoadingPlugin.register(new ModelLoadPlugin());

        ModBlocks.getDrawers().forEach(block ->
            BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT));
        ModBlocks.getFramedBlocks().forEach(block ->
            BlockRenderLayerMap.putBlock((Block)block, ChunkSectionLayer.TRANSLUCENT));

        MenuScreens.register(ModContainers.DRAWER_CONTAINER_1.get(), DrawerScreen.Slot1::new);
        MenuScreens.register(ModContainers.DRAWER_CONTAINER_2.get(), DrawerScreen.Slot2::new);
        MenuScreens.register(ModContainers.DRAWER_CONTAINER_4.get(), DrawerScreen.Slot4::new);
        MenuScreens.register(ModContainers.DRAWER_CONTAINER_COMP_2.get(), DrawerScreen.Compacting2::new);
        MenuScreens.register(ModContainers.DRAWER_CONTAINER_COMP_3.get(), DrawerScreen.Compacting3::new);
        MenuScreens.register(ModContainers.FRAMING_TABLE.get(), FramingTableScreen::new);

        TooltipComponentCallback.EVENT.register((TooltipComponent data) -> {
            if (data instanceof DetachedDrawerTooltip)
                return new ClientDetachedDrawerTooltip(((DetachedDrawerTooltip) data).contents());
            if (data instanceof KeyringTooltip)
                return new ClientKeyringTooltip(((KeyringTooltip) data).contents());
            return null;
        });
    }
}
