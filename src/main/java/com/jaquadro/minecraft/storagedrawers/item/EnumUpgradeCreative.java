package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.util.IStringSerializable;

public enum EnumUpgradeCreative implements IStringSerializable
{
    STORAGE(0, "store", "store"),
    VENDING(1, "vend", "vend");

    private static final EnumUpgradeCreative[] META_LOOKUP;

    private final int meta;
    private final String name;
    private final String unlocalizedName;

    EnumUpgradeCreative (int meta, String name, String unlocalizedName) {
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

    public static EnumUpgradeCreative byMetadata (int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length)
            meta = 0;
        return META_LOOKUP[meta];
    }

    @Override
    public String toString () {
        return unlocalizedName;
    }

    @Override
    public String func_176610_l () {
        return name;
    }

    static {
        META_LOOKUP = new EnumUpgradeCreative[values().length];
        for (EnumUpgradeCreative upgrade : values()) {
            META_LOOKUP[upgrade.getMetadata()] = upgrade;
        }
    }
}
