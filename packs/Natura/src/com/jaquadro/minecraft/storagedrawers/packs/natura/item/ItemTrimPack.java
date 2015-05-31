package com.jaquadro.minecraft.storagedrawers.packs.natura.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemTrim;
import com.jaquadro.minecraft.storagedrawers.packs.natura.block.BlockDrawersPack;
import net.minecraft.block.Block;

public class ItemTrimPack extends ItemTrim
{
    public ItemTrimPack (Block block) {
        super(block, BlockDrawersPack.textureNames);
    }
}
