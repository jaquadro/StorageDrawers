package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static mcjty.theoneprobe.api.TextStyleClass.MODNAME;

public class TOP {
    public static void registerProviders() {
        ITheOneProbe TOP = TheOneProbe.theOneProbeImp;
        TOP.registerBlockDisplayOverride(new DrawerLogoProvider());
    }
    public static class DrawerLogoProvider implements IBlockDisplayOverride {
        @Override
        public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer entityPlayer, World world, IBlockState blockState, IProbeHitData probeHitData) {
            // Returns false if no override is needed
            Block block = blockState.getBlock();
            if (!(block instanceof BlockDrawers))
                return false;

            ItemStack pickBlock = ((BlockDrawers) block).getWailaTOPBlock(world, probeHitData.getPos(), blockState);

            if (Tools.show(mode, Config.getRealConfig().getShowModName()))
                probeInfo.horizontal()
                        .item(pickBlock)
                        .vertical()
                        .itemLabel(pickBlock)
                        .text(MODNAME + Tools.getModName(block));
            else
                probeInfo.horizontal(probeInfo.defaultLayoutStyle()
                                .alignment(ElementAlignment.ALIGN_CENTER))
                        .item(pickBlock);

            return true;
        }
    }
}