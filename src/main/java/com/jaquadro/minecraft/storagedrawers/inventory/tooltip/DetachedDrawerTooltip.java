package com.jaquadro.minecraft.storagedrawers.inventory.tooltip;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class DetachedDrawerTooltip implements TooltipComponent {
    private final IDrawer drawer;
    private final ItemStack item;
    private final int stackLimit;

    public DetachedDrawerTooltip (IDrawer drawer, ItemStack item, int stackLimit) {
        this.drawer = drawer;
        this.item = item;
        this.stackLimit = stackLimit;
    }

    public IDrawer getDrawer () {
        return drawer;
    }

    public ItemStack getItem () {
        return item;
    }

    public int getStackLimit () {
        return stackLimit;
    }
}
