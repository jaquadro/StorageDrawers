package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.List;

@WailaPlugin(StorageDrawers.MOD_ID)
public class Waila implements IWailaPlugin
{
    @Override
    public void register (IRegistrar registrar) {
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
        public ItemStack getStack (IDataAccessor accessor, IPluginConfig config) {
            return new ItemStack(accessor.getBlock(), 1);
        }

        @Override
        public void appendBody (List<ITextComponent> currenttip, IDataAccessor accessor, IPluginConfig config) {
            TileEntityDrawers tile = (TileEntityDrawers) accessor.getTileEntity();

            DrawerOverlay overlay = new DrawerOverlay();
            overlay.showContent = config.get(new ResourceLocation(StorageDrawers.MOD_ID, "display.content"));
            overlay.showStackLimit = config.get(new ResourceLocation(StorageDrawers.MOD_ID, "display.stacklimit"));
            overlay.showStatus = config.get(new ResourceLocation(StorageDrawers.MOD_ID, "display.status"));

            currenttip.addAll(overlay.getOverlay(tile));
        }
    }
}
