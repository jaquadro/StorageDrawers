package com.dynious.refinedrelocation.api.filter;

import net.minecraft.item.ItemStack;

public interface IFilter
{
    public boolean passesFilter(ItemStack itemStack);
}
