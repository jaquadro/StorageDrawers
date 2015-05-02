package com.jaquadro.minecraft.storagedrawers.block;

import net.minecraft.util.IStringSerializable;

public enum EnumBasicDrawer implements IStringSerializable
{
    FULL1(0, 1, false, "full1", "full1"),
    FULL2(1, 2, false, "full2", "full2"),
    FULL4(2, 4, false, "full4", "full4"),
    HALF2(3, 2, true, "half2", "half2"),
    HALF4(4, 4, true, "half4", "half4");

    private static final EnumBasicDrawer[] META_LOOKUP;

    private final int meta;
    private final int drawerCount;
    private final boolean halfDepth;
    private final String name;
    private final String unlocalizedName;

    private EnumBasicDrawer (int meta, int drawerCount, boolean halfDepth, String name, String unlocalizedName) {
        this.meta = meta;
        this.name = name;
        this.drawerCount = drawerCount;
        this.halfDepth = halfDepth;
        this.unlocalizedName = unlocalizedName;
    }

    public int getMetadata () {
        return meta;
    }

    public int getDrawerCount () {
        return drawerCount;
    }

    public boolean isHalfDepth () {
        return halfDepth;
    }

    public String getUnlocalizedName () {
        return unlocalizedName;
    }

    public static EnumBasicDrawer byMetadata (int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length)
            meta = 0;
        return META_LOOKUP[meta];
    }

    @Override
    public String toString () {
        return unlocalizedName;
    }

    @Override
    public String getName () {
        return name;
    }

    static {
        META_LOOKUP = new EnumBasicDrawer[values().length];
        for (EnumBasicDrawer upgrade : values()) {
            META_LOOKUP[upgrade.getMetadata()] = upgrade;
        }
    }
}
