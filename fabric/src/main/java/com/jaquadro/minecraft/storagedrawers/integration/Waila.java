package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ModClientConfig;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.impl.ui.ItemStackElement;

@WailaPlugin(ModConstants.MOD_ID)
public class Waila implements IWailaPlugin
{
    @Override
    public void registerClient (IWailaClientRegistration registration) {
        if (!ModCommonConfig.INSTANCE.INTEGRATION.waila.enable.get()
            || !ModClientConfig.INSTANCE.INTEGRATION.enableWaila.get())
            return;

        registration.addConfig(ModConstants.loc("display.content"), true);
        registration.addConfig(ModConstants.loc("display.stacklimit"), true);
        registration.addConfig(ModConstants.loc("display.status"), true);

        WailaDrawer provider = new WailaDrawer();
        registration.registerBlockComponent(provider, BlockDrawers.class);
    }

    public static class WailaDrawer implements IBlockComponentProvider
    {
        @Override
        @NotNull
        public IElement getIcon (BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
            return ItemStackElement.of(new ItemStack(accessor.getBlock()));
        }

        @Override
        public void appendTooltip (ITooltip currenttip, BlockAccessor accessor, IPluginConfig config) {
            BlockEntityDrawers blockEntityDrawers = (BlockEntityDrawers) accessor.getBlockEntity();

            DrawerOverlay overlay = new DrawerOverlay();
            overlay.showContent = config.get(ModConstants.loc("display.content"));
            overlay.showStackLimit = config.get(ModConstants.loc("display.stacklimit"));
            overlay.showStatus = config.get(ModConstants.loc("display.status"));

            currenttip.addAll(overlay.getOverlay(blockEntityDrawers));
        }

        @Override
        public ResourceLocation getUid () {
            return new ResourceLocation(ModConstants.MOD_ID, "main");
        }
    }
}
