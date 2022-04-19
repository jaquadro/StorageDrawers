package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ClientConfig;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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
        public ResourceLocation getID() {
            return StorageDrawers.rl("drawerprobe");
        }

        @Override
        public void addProbeInfo(ProbeMode probeMode, IProbeInfo probe, Player player, Level world, BlockState blockState, IProbeHitData data) {
            TileEntityDrawers tileEntityDrawers = WorldUtils.getBlockEntity(world, data.getPos(), TileEntityDrawers.class);
            if (tileEntityDrawers != null) {
                DrawerOverlay overlay = new DrawerOverlay();
                for (Component component : overlay.getOverlay(tileEntityDrawers)) {
                    probe.text(component);
                }
            }
        }
    }
}
