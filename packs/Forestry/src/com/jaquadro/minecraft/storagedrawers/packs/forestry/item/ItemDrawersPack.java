package com.jaquadro.minecraft.storagedrawers.packs.forestry.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.block.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.core.ModBlocks;
import net.minecraft.block.Block;

public class ItemDrawersPack extends ItemDrawers
{
    public ItemDrawersPack (Block block) {
        super(block, getTextureGroupForBlock(block));
    }

    protected ItemDrawersPack (Block block, String[] names) {
        super(block, names);
    }

    private static String[] getTextureGroupForBlock (Block block) {
        if (block == ModBlocks.fullDrawers1A ||
            block == ModBlocks.fullDrawers2A ||
            block == ModBlocks.fullDrawers4A ||
            block == ModBlocks.halfDrawers2A ||
            block == ModBlocks.halfDrawers4A)
            return BlockDrawersPack.textureNames1;
        else
            return BlockDrawersPack.textureNames2;
    }

    protected int getCapacityForBlock (Block block) {
        ConfigManager config = StorageDrawers.config;
        int count = 0;

        if (block == ModBlocks.fullDrawers1A || block == ModBlocks.fullDrawers1B)
            count = config.getBlockBaseStorage("fulldrawers1");
        else if (block == ModBlocks.fullDrawers2A || block == ModBlocks.fullDrawers2B)
            count = config.getBlockBaseStorage("fulldrawers2");
        else if (block == ModBlocks.fullDrawers4A || block == ModBlocks.fullDrawers4B)
            count = config.getBlockBaseStorage("fulldrawers4");
        else if (block == ModBlocks.halfDrawers2A || block == ModBlocks.halfDrawers2B)
            count = config.getBlockBaseStorage("halfdrawers2");
        else if (block == ModBlocks.halfDrawers4A || block == ModBlocks.halfDrawers4B)
            count = config.getBlockBaseStorage("halfdrawers4");

        return count;
    }
}
