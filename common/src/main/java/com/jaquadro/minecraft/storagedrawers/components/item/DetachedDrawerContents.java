package com.jaquadro.minecraft.storagedrawers.components.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class DetachedDrawerContents implements TooltipComponent
{
    public static final DetachedDrawerContents EMPTY = new DetachedDrawerContents(ItemStack.EMPTY, 0, false);
    public static final Codec<DetachedDrawerContents> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(DetachedDrawerContents::getItemPrototype),
            Codec.INT.optionalFieldOf("itemCount", 0).forGetter(DetachedDrawerContents::getItemCount),
            Codec.INT.fieldOf("stackLimit").forGetter(DetachedDrawerContents::getStackLimit),
            Codec.BOOL.fieldOf("heavy").forGetter(DetachedDrawerContents::isHeavy)
        ).apply(instance, DetachedDrawerContents::new)
    );

    private final ItemStack item;
    private final int itemCount;
    private final int stackLimit;
    private final boolean heavy;

    public DetachedDrawerContents (ItemStack item, int itemCount, int stackLimit, boolean heavy) {
        this.item = item.copyWithCount(itemCount);
        this.itemCount = itemCount;
        this.stackLimit = stackLimit;
        this.heavy = heavy;
    }

    public DetachedDrawerContents (ItemStack item, int stackLimit, boolean heavy) {
        this(item, item.getCount(), stackLimit, heavy);
    }

    public ItemStack getItem () {
        return item;
    }

    public ItemStack getItemPrototype () {
        return item == ItemStack.EMPTY ? ItemStack.EMPTY : item.copyWithCount(1);
    }

    public int getItemCount () {
        return itemCount;
    }

    public int getStackLimit () {
        return stackLimit;
    }

    public boolean isHeavy () {
        return heavy;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof DetachedDrawerContents contents) {
            return ItemStack.isSameItemSameComponents(item, contents.getItem())
                && itemCount == contents.getItemCount()
                && stackLimit == contents.getStackLimit()
                && heavy == contents.isHeavy();
        } else
            return false;
    }

    @Override
    public int hashCode () {
        return ItemStack.hashItemAndComponents(getItem()) * 31
            + stackLimit * 31
            + itemCount * 31
            + (heavy ? 1 : 0);
    }

    @Override
    public String toString () {
        return "DetachedDrawerContents [item=" + item + ", count=" + itemCount + ", stackLimit=" + stackLimit + ", heavy=" + heavy + "]";
    }
}
