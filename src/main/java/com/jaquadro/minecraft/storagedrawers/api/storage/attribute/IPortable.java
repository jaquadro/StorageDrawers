package com.jaquadro.minecraft.storagedrawers.api.storage.attribute;

import net.minecraft.world.item.ItemStack;

public interface IPortable
{
    /**
     * Gets whether an item stack is considered "heavy", lacking a necessary portability attribute
     *
     * @param stack
     * @return
     */
    boolean isHeavy (ItemStack stack);
}
