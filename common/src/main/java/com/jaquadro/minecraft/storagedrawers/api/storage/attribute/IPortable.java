package com.jaquadro.minecraft.storagedrawers.api.storage.attribute;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IPortable
{
    /**
     * Gets whether an item stack is considered "heavy", lacking a necessary portability attribute
     *
     * @param stack
     * @return
     */
    boolean isHeavy (HolderLookup.Provider provider, ItemStack stack);
}
