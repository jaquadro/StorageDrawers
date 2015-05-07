package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemTrim extends ItemMultiTexture
{
    public ItemTrim (Block block) {
        super(block, block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
                ItemStack stack = (ItemStack)input;
                if (stack.hasTagCompound() && stack.getTagCompound().hasKey("material")) {
                    String key = stack.getTagCompound().getString("material");
                    return "storageDrawers.material." + key;
                }

                return "";
            }
        });
    }
}
