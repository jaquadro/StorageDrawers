package com.jaquadro.minecraft.storagedrawers.components.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public record DrawerCountData(int count)
{
    public static final Codec<DrawerCountData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.INT.fieldOf("__storagedrawers_count").forGetter(DrawerCountData::count)
    ).apply(inst, DrawerCountData::new));

    public static final DrawerCountData EMPTY = new DrawerCountData(0);

    @Override
    public boolean equals (Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DrawerCountData that))
            return false;

        return count == that.count;
    }

    @Override
    public int hashCode () {
        return Objects.hashCode(count);
    }

    @Override
    public String toString () {
        return "DrawerCountData [count=" + count + "]";
    }
}
