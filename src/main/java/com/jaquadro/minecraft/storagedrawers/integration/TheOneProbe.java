package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
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
    private static final TheOneProbe INSTANCE = new TheOneProbe();

    public static TheOneProbe getInstance() {
        return INSTANCE;
    }

    @Override
    public Void apply(ITheOneProbe probe) {
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
            if (world.isClientSide() && !ClientConfig.INTEGRATION.enableTheOneProbe.get())
                return;

            BlockEntityDrawers blockEntityDrawers = WorldUtils.getBlockEntity(world, data.getPos(), BlockEntityDrawers.class);
            if (blockEntityDrawers != null) {
                DrawerOverlay overlay = new DrawerOverlay();
                for (Component component : overlay.getOverlay(blockEntityDrawers)) {
                    probe.text(component);
                }
            }
        }
    }
}
