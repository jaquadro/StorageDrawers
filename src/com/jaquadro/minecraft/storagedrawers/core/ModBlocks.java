package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class ModBlocks
{
    public static BlockDrawers fullDrawers2;
    public static BlockDrawers fullDrawers4;
    public static BlockDrawers halfDrawers2;
    public static BlockDrawers halfDrawers4;

    public void init () {
        fullDrawers2 = new BlockDrawers("fullDrawers2", 2, false);
        fullDrawers4 = new BlockDrawers("fullDrawers4", 4, false);
        halfDrawers2 = new BlockDrawers("halfDrawers2", 2, true);
        halfDrawers4 = new BlockDrawers("halfDrawers4", 4, true);

        GameRegistry.registerBlock(fullDrawers2, ItemDrawers.class, "fullDrawers2");
        GameRegistry.registerBlock(fullDrawers4, ItemDrawers.class, "fullDrawers4");
        GameRegistry.registerBlock(halfDrawers2, ItemDrawers.class, "halfDrawers2");
        GameRegistry.registerBlock(halfDrawers4, ItemDrawers.class, "halfDrawers4");

        GameRegistry.registerTileEntity(TileEntityDrawers.class, ModBlocks.getQualifiedName(fullDrawers2));
        GameRegistry.registerTileEntity(TileEntityDrawers.class, ModBlocks.getQualifiedName(fullDrawers4));
        GameRegistry.registerTileEntity(TileEntityDrawers.class, ModBlocks.getQualifiedName(halfDrawers2));
        GameRegistry.registerTileEntity(TileEntityDrawers.class, ModBlocks.getQualifiedName(halfDrawers4));
    }

    public static String getQualifiedName (Block block) {
        return GameData.getBlockRegistry().getNameForObject(block);
    }
}
