package com.jaquadro.minecraft.storagedrawers.api.framing;

import net.minecraft.world.item.ItemStack;

public interface IFramedSourceBlock
{
    ItemStack makeFramedItem (ItemStack source, ItemStack matSide, ItemStack matTrim, ItemStack matFront);
}
