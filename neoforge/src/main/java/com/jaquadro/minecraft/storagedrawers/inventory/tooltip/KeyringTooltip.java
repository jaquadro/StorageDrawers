package com.jaquadro.minecraft.storagedrawers.inventory.tooltip;

import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record KeyringTooltip(KeyringContents contents) implements TooltipComponent {
}
