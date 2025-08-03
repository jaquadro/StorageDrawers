package com.jaquadro.minecraft.storagedrawers.components.item;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record FrameData (ItemStack base, ItemStack side, ItemStack trim, ItemStack front)
{
    public static final FrameData EMPTY = new FrameData();

    public static final Codec<FrameData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ItemStack.OPTIONAL_CODEC.fieldOf("base").forGetter(FrameData::base),
            ItemStack.OPTIONAL_CODEC.fieldOf("side").forGetter(FrameData::side),
            ItemStack.OPTIONAL_CODEC.fieldOf("trim").forGetter(FrameData::trim),
            ItemStack.OPTIONAL_CODEC.fieldOf("front").forGetter(FrameData::front)
        ).apply(instance, FrameData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FrameData> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC,
        FrameData::base,
        ItemStack.OPTIONAL_STREAM_CODEC,
        FrameData::side,
        ItemStack.OPTIONAL_STREAM_CODEC,
        FrameData::trim,
        ItemStack.OPTIONAL_STREAM_CODEC,
        FrameData::front,
        FrameData::new
    );

    public FrameData () {
        this(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    public FrameData (MaterialData data) {
        this(data.getFrameBase(), data.getSide(), data.getTrim(), data.getFront());
    }

    public MaterialData asMaterialData() {
        return new MaterialData(base, side, front, trim);
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof FrameData frame) {
            return ItemStack.isSameItemSameComponents(base, frame.base)
                && ItemStack.isSameItemSameComponents(side, frame.side)
                && ItemStack.isSameItemSameComponents(front, frame.front)
                && ItemStack.isSameItemSameComponents(trim, frame.trim);
        } else
            return false;
    }

    @Override
    public int hashCode () {
        return 31 * ItemStack.hashItemAndComponents(base)
            + 31 * ItemStack.hashItemAndComponents(side)
            + 31 * ItemStack.hashItemAndComponents(trim)
            + 31 * ItemStack.hashItemAndComponents(front);
    }

    @Override
    public String toString () {
        return "FrameData [base=" + base + ", side=" + side + ", trim=" + trim + ", front=" + front + "]";
    }
}
