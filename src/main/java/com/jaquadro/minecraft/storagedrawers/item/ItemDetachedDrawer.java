package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.DetachedDrawerData;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.DetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.KeyringTooltip;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ItemDetachedDrawer extends Item
{
    public ItemDetachedDrawer (Properties properties) {
        super(properties);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage (ItemStack stack) {
        DetachedDrawerData data = new DetachedDrawerData(stack.getOrCreateTag());
        ItemStack innerStack = data.getStoredItemPrototype().copy();
        innerStack.setCount(data.getStoredItemCount());
        return Optional.of(new DetachedDrawerTooltip(innerStack));
    }
}
