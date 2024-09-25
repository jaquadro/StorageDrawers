package com.jaquadro.minecraft.storagedrawers.inventory.tooltip;

import com.jaquadro.minecraft.storagedrawers.components.item.DetachedDrawerContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record DetachedDrawerTooltip(DetachedDrawerContents contents) implements TooltipComponent {
}
