package com.jaquadro.minecraft.storagedrawers.components.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DrawerCountData(int count)
{
    public static final Codec<DrawerCountData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.INT.fieldOf("__storagedrawers_count").forGetter(DrawerCountData::count)
    ).apply(inst, DrawerCountData::new));

    public static final DrawerCountData EMPTY = new DrawerCountData(0);
}
