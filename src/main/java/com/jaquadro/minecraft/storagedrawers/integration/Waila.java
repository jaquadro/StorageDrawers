package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ClientConfig;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.impl.ui.ItemStackElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

@WailaPlugin(StorageDrawers.MOD_ID)
public class Waila implements IWailaPlugin
{
    @Override
    public void register (IRegistrar registrar) {
        if (!ClientConfig.INTEGRATION.enableWaila.get())
            return;

        registerProvider(registrar);
    }

    private void registerProvider(IRegistrar registrar) {
        WailaDrawer provider = new WailaDrawer();

        registrar.addConfig(new ResourceLocation(StorageDrawers.MOD_ID, "display.content"), true);
        registrar.addConfig(new ResourceLocation(StorageDrawers.MOD_ID, "display.stacklimit"), true);
        registrar.addConfig(new ResourceLocation(StorageDrawers.MOD_ID, "display.status"), true);
        registrar.registerComponentProvider(provider, TooltipPosition.BODY, BlockDrawers.class);
    }

    public static class WailaDrawer implements IComponentProvider
    {
        @Override
        @Nonnull
        public IElement getIcon (BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
            return ItemStackElement.of(new ItemStack(accessor.getBlock()));
        }

        @Override
        public void appendTooltip (ITooltip currenttip, BlockAccessor accessor, IPluginConfig config) {
            TileEntityDrawers tile = (TileEntityDrawers) accessor.getBlockEntity();

            DrawerOverlay overlay = new DrawerOverlay();
            overlay.showContent = config.get(new ResourceLocation(StorageDrawers.MOD_ID, "display.content"));
            overlay.showStackLimit = config.get(new ResourceLocation(StorageDrawers.MOD_ID, "display.stacklimit"));
            overlay.showStatus = config.get(new ResourceLocation(StorageDrawers.MOD_ID, "display.status"));

            currenttip.addAll(overlay.getOverlay(tile));
        }
    }
}
