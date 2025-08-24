package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum EnumUpgradeMagnet implements StringRepresentable
{
    LEVEL1(0, 1, "level1", "level1"),
    LEVEL2(1, 2, "level2", "level2"),
    LEVEL3(2, 3, "level3", "level3");

    private static final EnumUpgradeMagnet[] META_LOOKUP;

    private final int meta;
    private final int level;
    private final String name;
    private final String unlocalizedName;

    EnumUpgradeMagnet (int meta, int level, String name, String unlocalizedName) {
        this.meta = meta;
        this.level = level;
        this.name = name;
        this.unlocalizedName = unlocalizedName;
    }

    public int getMetadata () {
        return meta;
    }

    public int getLevel () {
        return level;
    }

    public String getUnlocalizedName () {
        return unlocalizedName;
    }

    public static EnumUpgradeMagnet byMetadata (int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length)
            meta = 0;
        return META_LOOKUP[meta];
    }

    @Override
    public String toString () {
        return unlocalizedName;
    }

    @Override
    @NotNull
    public String getSerializedName () {
        return name;
    }

    static {
        META_LOOKUP = new EnumUpgradeMagnet[values().length];
        for (EnumUpgradeMagnet upgrade : values()) {
            META_LOOKUP[upgrade.getMetadata()] = upgrade;
        }
    }
}
