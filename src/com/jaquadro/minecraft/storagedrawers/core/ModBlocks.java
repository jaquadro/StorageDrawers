package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class ModBlocks
{
    public static BlockDrawers drawers;

    public void init () {
        drawers = new BlockDrawers("drawers");

        GameRegistry.registerBlock(drawers, "drawers");

        GameRegistry.registerTileEntity(TileEntityDrawers.class, ModBlocks.getQualifiedName(drawers));
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block);
    }
}
