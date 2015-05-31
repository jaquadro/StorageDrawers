package com.jaquadro.minecraft.storagedrawers.packs.erebus.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemTrim;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.block.BlockDrawersPack;
import net.minecraft.block.Block;

public class ItemTrimPack extends ItemTrim
{
    public ItemTrimPack (Block block) {
        super(block, BlockDrawersPack.textureNames);
    }
}
