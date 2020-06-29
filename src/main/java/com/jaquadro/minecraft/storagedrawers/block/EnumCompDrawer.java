package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGeometry;
import net.minecraft.util.IStringSerializable;

public enum EnumCompDrawer implements IDrawerGeometry, IStringSerializable
{
    OPEN1(0, 1, "open1"),
    OPEN2(1, 2, "open2"),
    OPEN3(2, 3, "open3");

    private static final EnumCompDrawer[] META_LOOKUP;
    private static final EnumCompDrawer[] OPEN_SLOTS_LOOKUP;

    private final int meta;
    private final int openSlots;
    private final String name;

    EnumCompDrawer (int meta, int openSlots, String name) {
        this.meta = meta;
        this.name = name;
        this.openSlots = openSlots;
    }

    @Override
    public boolean isHalfDepth () {
        return false;
    }

    @Override
    public int getDrawerCount () {
        return 3;
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

    public static EnumCompDrawer byOpenSlots (int openSlots) {
        return OPEN_SLOTS_LOOKUP[openSlots];
    }

    @Override
    public String toString () {
        return func_176610_l();
    }

    @Override
    public String func_176610_l () {
        return name;
    }

    static {
        META_LOOKUP = new EnumCompDrawer[values().length];
        for (EnumCompDrawer upgrade : values()) {
            META_LOOKUP[upgrade.getMetadata()] = upgrade;
        }

        OPEN_SLOTS_LOOKUP = new EnumCompDrawer[values().length + 1];
        for (EnumCompDrawer item : values()) {
            OPEN_SLOTS_LOOKUP[item.getOpenSlots()] = item;
        }
    }
}
