package com.jaquadro.minecraft.storagedrawers.packs.natura.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.jaquadro.minecraft.storagedrawers.packs.natura.block.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.natura.core.ModBlocks;
import net.minecraft.block.Block;

public class ItemDrawersPack extends ItemDrawers
{
    public ItemDrawersPack (Block block) {
        super(block, BlockDrawersPack.textureNames);
    }

    protected int getCapacityForBlock (Block block) {
        ConfigManager config = StorageDrawers.config;
        int count = 0;

        if (block == ModBlocks.fullDrawers1)
            count = config.getBlockBaseStorage("fulldrawers1");
        else if (block == ModBlocks.fullDrawers2)
            count = config.getBlockBaseStorage("fulldrawers2");
        else if (block == ModBlocks.fullDrawers4)
            count = config.getBlockBaseStorage("fulldrawers4");
        else if (block == ModBlocks.halfDrawers2)
            count = config.getBlockBaseStorage("halfdrawers2");
        else if (block == ModBlocks.halfDrawers4)
            count = config.getBlockBaseStorage("halfdrawers4");

        return count;
    }
}
