package com.jaquadro.minecraft.storagedrawers.components.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class DetachedDrawerContents implements TooltipComponent
{
    public static final DetachedDrawerContents EMPTY = new DetachedDrawerContents(ItemStack.EMPTY, 0);
    public static final Codec<DetachedDrawerContents> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(DetachedDrawerContents::getItem),
            Codec.INT.fieldOf("stackLimit").forGetter(DetachedDrawerContents::getStackLimit)
        ).apply(instance, DetachedDrawerContents::new)
    );

    private final ItemStack item;
    private final int stackLimit;

    public DetachedDrawerContents (ItemStack item, int stackLimit) {
        this.item = item;
        this.stackLimit = stackLimit;
    }

    public ItemStack getItem () {
        return item;
    }

    public int getStackLimit () {
        return stackLimit;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof DetachedDrawerContents contents) {
            return ItemStack.isSameItemSameComponents(item, contents.getItem())
                && stackLimit == contents.getStackLimit();
        } else
            return false;
    }

    @Override
    public int hashCode () {
        return ItemStack.hashItemAndComponents(getItem()) * 31 + stackLimit;
    }

    @Override
    public String toString () {
        return "DetachedDrawerContents [item=" + item + ", stackLimit=" + stackLimit + "]";
    }
}
