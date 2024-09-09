package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ItemTrim extends BlockItem
{
    public ItemTrim (Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName (ItemStack stack) {
        String fallback = null;
        Block block = Block.byItem(stack.getItem());

        if (block instanceof BlockTrim trim) {
            String matKey = trim.getMatKey();
            if (matKey != null) {
                String mat = Component.translatable(trim.getNameMatKey()).getString();
                fallback = Component.translatable(trim.getNameTypeKey(), mat).getString();
            }
        }

        return Component.translatableWithFallback(this.getDescriptionId(stack), fallback);
    }
}
