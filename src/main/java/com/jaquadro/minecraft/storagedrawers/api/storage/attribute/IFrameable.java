package com.jaquadro.minecraft.storagedrawers.api.storage.attribute;

import net.minecraft.item.ItemStack;

/**
 * Item Classes which extend this class are allowed to be used in the Framing Table.
 */
public interface IFrameable {
    /**
     * The provided ItemStacks are NOT copies. Unless you want to change the input items in the framing table, DO NOT change the ItemStacks.
     * @param input The input ItemStack of which you can copy relevant NBT from (used for things like copying tile nbt from old framed drawer)
     * @param matSide ItemStack to use as the decoration on `side`
     * @param matTrim ItemStack to use as the decoration on `trim`
     * @param matFront ItemStack to use as the decoration on `front`
     * @return The ItemStack to put in the output slot of the Framing Table
     */
    ItemStack decorate(ItemStack input, ItemStack matSide, ItemStack matTrim, ItemStack matFront);
}
