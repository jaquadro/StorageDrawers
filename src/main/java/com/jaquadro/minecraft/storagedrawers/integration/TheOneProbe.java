package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ClientConfig;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.function.Function;

public class TheOneProbe implements Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe probe) {
        if (ClientConfig.INTEGRATION.enableTheOneProbe.get())
            probe.registerProvider(new DrawerProbeProvider());

        return null;
    }

    private static class DrawerProbeProvider implements IProbeInfoProvider {
        @Override
        public String getID() {
            return new ResourceLocation(StorageDrawers.MOD_ID, "drawerprobe").toString();
        }

        @Override
        public void addProbeInfo(ProbeMode probeMode, IProbeInfo probe, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            TileEntity tile = world.getTileEntity(data.getPos());
            if (tile instanceof TileEntityDrawers) {
                TileEntityDrawers drawers = (TileEntityDrawers) tile;

                DrawerOverlay overlay = new DrawerOverlay();
                for (ITextComponent component : overlay.getOverlay(drawers)) {
                    probe.text(component);
                }
            }
        }
    }
}
