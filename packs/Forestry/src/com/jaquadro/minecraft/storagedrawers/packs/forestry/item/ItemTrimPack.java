package com.jaquadro.minecraft.storagedrawers.packs.forestry.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemTrim;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.block.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.core.ModBlocks;
import net.minecraft.block.Block;

public class ItemTrimPack extends ItemTrim
{
    public ItemTrimPack (Block block) {
        super(block, getTextureGroupForBlock(block));
    }

    private static String[] getTextureGroupForBlock (Block block) {
        if (block == ModBlocks.trimA)
            return BlockDrawersPack.textureNames1;
        else
            return BlockDrawersPack.textureNames2;
    }
}
