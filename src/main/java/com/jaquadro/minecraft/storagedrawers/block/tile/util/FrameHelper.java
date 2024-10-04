package com.jaquadro.minecraft.storagedrawers.block.tile.util;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedSourceBlock;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FrameHelper
{
    public static ItemStack makeFramedItem (IFramedBlock resultBlock, ItemStack source, ItemStack matSide, ItemStack matTrim, ItemStack matFront) {
        if (!(resultBlock instanceof Block))
            return ItemStack.EMPTY;

        if (source.isEmpty())
            return ItemStack.EMPTY;

        if (source.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (!(block instanceof IFramedSourceBlock))
                return ItemStack.EMPTY;
        } else
            return ItemStack.EMPTY;

        MaterialData data = new MaterialData();
        data.setFrameBase(new ItemStack(source.getItem()));
        if (resultBlock.supportsFrameMaterial(FrameMaterial.SIDE))
            data.setSide(matSide);
        if (resultBlock.supportsFrameMaterial(FrameMaterial.TRIM))
            data.setTrim(matTrim);
        if (resultBlock.supportsFrameMaterial(FrameMaterial.FRONT))
            data.setFront(matFront);

        CompoundTag tag = source.getOrCreateTag().copy();
        tag = data.write(tag);

        ItemStack stack = new ItemStack((Block)resultBlock, source.getCount());
        stack.setTag(tag);

        return stack;
    }
}
