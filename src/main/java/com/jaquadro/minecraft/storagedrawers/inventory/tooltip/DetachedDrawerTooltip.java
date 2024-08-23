package com.jaquadro.minecraft.storagedrawers.inventory.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class DetachedDrawerTooltip implements TooltipComponent {
    private ItemStack item;

    public DetachedDrawerTooltip (ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem () {
        return item;
    }
}
