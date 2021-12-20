package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.util.StringRepresentable;

public enum EnumUpgradeRedstone implements StringRepresentable
{
    COMBINED(0, "combined", "combined"),
    MAX(1, "max", "max"),
    MIN(2, "min", "min");

    private static final EnumUpgradeRedstone[] META_LOOKUP;

    private final int meta;
    private final String name;
    private final String unlocalizedName;

    EnumUpgradeRedstone (int meta, String name, String unlocalizedName) {
        this.meta = meta;
        this.name = name;
        this.unlocalizedName = unlocalizedName;
    }

    public int getMetadata () {
        return meta;
    }

    public String getUnlocalizedName () {
        return unlocalizedName;
    }

    public static EnumUpgradeRedstone byMetadata (int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length)
            meta = 0;
        return META_LOOKUP[meta];
    }

    @Override
    public String toString () {
        return unlocalizedName;
    }

    @Override
    public String getSerializedName () {
        return name;
    }

    static {
        META_LOOKUP = new EnumUpgradeRedstone[values().length];
        for (EnumUpgradeRedstone upgrade : values()) {
            META_LOOKUP[upgrade.getMetadata()] = upgrade;
        }
    }
}
