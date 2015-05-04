package com.jaquadro.minecraft.storagedrawers.block;

import net.minecraft.util.IStringSerializable;

public enum EnumCompDrawer implements IStringSerializable
{
    OPEN1(0, 1, "open1"),
    OPEN2(1, 2, "open2"),
    OPEN3(2, 3, "open3");

    private static final EnumCompDrawer[] META_LOOKUP;

    private final int meta;
    private final int openSlots;
    private final String name;

    private EnumCompDrawer (int meta, int openSlots, String name) {
        this.meta = meta;
        this.name = name;
        this.openSlots = openSlots;
    }

    public int getMetadata () {
        return meta;
    }

    public int getOpenSlots () {
        return openSlots;
    }

    public static EnumCompDrawer byMetadata (int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length)
            meta = 0;
        return META_LOOKUP[meta];
    }

    @Override
    public String toString () {
        return getName();
    }

    @Override
    public String getName () {
        return name;
    }

    static {
        META_LOOKUP = new EnumCompDrawer[values().length];
        for (EnumCompDrawer upgrade : values()) {
            META_LOOKUP[upgrade.getMetadata()] = upgrade;
        }
    }
}
