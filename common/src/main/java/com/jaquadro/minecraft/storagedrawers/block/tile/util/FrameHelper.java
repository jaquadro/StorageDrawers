package com.jaquadro.minecraft.storagedrawers.block.tile.util;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedSourceBlock;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.components.item.FrameData;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
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
        data.setFrameBase(new ItemStack(source.getItem(), 1));
        if (resultBlock.supportsFrameMaterial(FrameMaterial.SIDE))
            data.setSide(matSide.copyWithCount(1));
        if (resultBlock.supportsFrameMaterial(FrameMaterial.TRIM))
            data.setTrim(matTrim.copyWithCount(1));
        if (resultBlock.supportsFrameMaterial(FrameMaterial.FRONT))
            data.setFront(matFront.copyWithCount(1));

        ItemStack stack = source.transmuteCopy(((Block) resultBlock).asItem());
        stack.set(ModDataComponents.FRAME_DATA.get(), new FrameData(data));

        return stack;
    }
}