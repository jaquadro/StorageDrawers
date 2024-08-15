package com.jaquadro.minecraft.storagedrawers.inventory.tooltip;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.item.ItemStack;

public class KeyringTooltip extends BundleTooltip
{
    public KeyringTooltip (NonNullList<ItemStack> items) {
        super(items, items.size());
    }
}
