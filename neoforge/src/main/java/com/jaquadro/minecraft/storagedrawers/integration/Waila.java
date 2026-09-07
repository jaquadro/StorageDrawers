package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ModClientConfig;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.impl.ui.ItemStackElement;

@WailaPlugin(StorageDrawers.MOD_ID)
public class Waila implements IWailaPlugin
{
    @Override
    public void registerClient (IWailaClientRegistration registration) {
        if (!ModCommonConfig.INSTANCE.INTEGRATION.waila.enable.get()
            || !ModClientConfig.INSTANCE.INTEGRATION.enableWaila.get())
            return;

        registration.addConfig(StorageDrawers.rl("display.content"), true);
        registration.addConfig(StorageDrawers.rl("display.stacklimit"), true);
        registration.addConfig(StorageDrawers.rl("display.status"), true);

        WailaDrawer provider = new WailaDrawer();
        registration.registerBlockComponent(provider, BlockDrawers.class);
    }

    public static class WailaDrawer implements IBlockComponentProvider
    {
        @Override
        public @Nullable Element getIcon (BlockAccessor accessor, IPluginConfig config, Element currentIcon) {
            return ItemStackElement.of(new ItemStack(accessor.getBlock()));
        }

        @Override
        public void appendTooltip (ITooltip currenttip, BlockAccessor accessor, IPluginConfig config) {
            BlockEntityDrawers blockEntityDrawers = (BlockEntityDrawers) accessor.getBlockEntity();

            DrawerOverlay overlay = new DrawerOverlay();
            overlay.showContent = config.get(StorageDrawers.rl("display.content"));
            overlay.showStackLimit = config.get(StorageDrawers.rl("display.stacklimit"));
            overlay.showStatus = config.get(StorageDrawers.rl("display.status"));

            currenttip.addAll(overlay.getOverlay(blockEntityDrawers));
        }

        @Override
        public Identifier getUid () {
            return Identifier.fromNamespaceAndPath(StorageDrawers.MOD_ID, "main");
        }
    }
}
