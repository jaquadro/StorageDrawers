package com.jaquadro.minecraft.storagedrawers.block;

import net.minecraft.util.StringRepresentable;

public enum EnumFramingTablePart implements StringRepresentable
{
    LEFT("left"),
    RIGHT("right");

    private final String name;

    EnumFramingTablePart (String name) {
        this.name = name;
    }

    @Override
    public String toString () {
        return name;
    }

    @Override
    public String getSerializedName () {
        return name;
    }
}
