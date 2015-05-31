package com.jaquadro.minecraft.storagedrawers.packs.bop.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemTrim;
import com.jaquadro.minecraft.storagedrawers.packs.bop.block.BlockDrawersPack;
import net.minecraft.block.Block;

public class ItemTrimPack extends ItemTrim
{
    public ItemTrimPack (Block block) {
        super(block, BlockDrawersPack.textureNames);
    }
}
