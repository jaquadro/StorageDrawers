package com.jaquadro.minecraft.storagedrawers.item.pack;

import com.jaquadro.minecraft.storagedrawers.item.ItemTrim;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockTrimPack;
import net.minecraft.block.Block;

public class ItemTrimPack extends ItemTrim
{
    public ItemTrimPack (Block block) {
        super(block, getUnlocalizedNames(block));
    }

    protected ItemTrimPack (Block block, String[] unlocalizedNames) {
        super(block, unlocalizedNames);
    }

    private static String[] getUnlocalizedNames (Block block) {
        if (block instanceof BlockTrimPack)
            return ((BlockTrimPack) block).getUnlocalizedNames();
        else
            return new String[16];
    }
}
