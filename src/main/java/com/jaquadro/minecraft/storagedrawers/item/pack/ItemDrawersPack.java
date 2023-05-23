package com.jaquadro.minecraft.storagedrawers.item.pack;
/*
import com.google.common.base.Function;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.item.ItemBasicDrawers;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemDrawersPack extends ItemBasicDrawers
{
    public ItemDrawersPack (final Block block) {
        super(block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
                ItemStack stack = (ItemStack)input;
                String[] unlocalizedNames = getUnlocalizedNames(block);
                return unlocalizedNames[stack.getMetadata()];
            }
        });
    }

    protected ItemDrawersPack (Block block, final String[] unlocalizedNames) {
        super(block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
                ItemStack stack = (ItemStack)input;
                return unlocalizedNames[stack.getMetadata()];
            }
        });
    }

    private static String[] getUnlocalizedNames (Block block) {
        if (block instanceof BlockDrawersPack)
            return ((BlockDrawersPack) block).getUnlocalizedNames();
        else
            return new String[16];
    }
}
*/
