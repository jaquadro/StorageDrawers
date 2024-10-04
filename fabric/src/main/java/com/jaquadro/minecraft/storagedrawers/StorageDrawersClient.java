package com.jaquadro.minecraft.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.client.gui.ClientDetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.client.gui.ClientKeyringTooltip;
import com.jaquadro.minecraft.storagedrawers.client.model.ModelLoadPlugin;
import com.jaquadro.minecraft.storagedrawers.client.renderer.BlockEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerScreen;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.DetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.KeyringTooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

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

        MenuScreens.register(ModContainers.DRAWER_CONTAINER_1.get(), DrawerScreen.Slot1::new);
        MenuScreens.register(ModContainers.DRAWER_CONTAINER_2.get(), DrawerScreen.Slot2::new);
        MenuScreens.register(ModContainers.DRAWER_CONTAINER_4.get(), DrawerScreen.Slot4::new);
        MenuScreens.register(ModContainers.DRAWER_CONTAINER_COMP_2.get(), DrawerScreen.Compacting2::new);
        MenuScreens.register(ModContainers.DRAWER_CONTAINER_COMP_3.get(), DrawerScreen.Compacting3::new);

        TooltipComponentCallback.EVENT.register((TooltipComponent data) -> {
            if (data instanceof DetachedDrawerTooltip)
                return new ClientDetachedDrawerTooltip(((DetachedDrawerTooltip) data).contents());
            if (data instanceof KeyringTooltip)
                return new ClientKeyringTooltip(((KeyringTooltip) data).contents());
            return null;
        });
    }
}
