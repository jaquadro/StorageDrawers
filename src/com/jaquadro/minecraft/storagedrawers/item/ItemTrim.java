package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.item.ItemMultiTexture;

public class ItemTrim extends ItemMultiTexture
{
    public ItemTrim (Block block) {
        super(block, block, BlockWood.field_150096_a);
    }
}
