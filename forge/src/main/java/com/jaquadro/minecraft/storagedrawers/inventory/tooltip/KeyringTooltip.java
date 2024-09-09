package com.jaquadro.minecraft.storagedrawers.inventory.tooltip;

import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record KeyringTooltip(KeyringContents contents) implements TooltipComponent
{
}