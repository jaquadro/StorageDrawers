package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.util.IStringSerializable;

public enum EnumUpgradeStatus implements IStringSerializable
{
    LEVEL1(0, 1, "level1", "level1"),
    LEVEL2(1, 2, "level2", "level2");

    private static final EnumUpgradeStatus[] META_LOOKUP;
    private static final EnumUpgradeStatus[] LEVEL_LOOKUP;

    private final int meta;
    private final int level;
    private final String name;
    private final String unlocalizedName;

    EnumUpgradeStatus (int meta, int level, String name, String unlocalizedName) {
        this.meta = meta;
        this.name = name;
        this.level = level;
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

    public static EnumUpgradeStatus byMetadata (int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length)
            meta = 0;
        return META_LOOKUP[meta];
    }

    public static EnumUpgradeStatus byLevel (int level) {
        if (level < 0 || level >= LEVEL_LOOKUP.length)
            level = 0;
        return LEVEL_LOOKUP[level];
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
        META_LOOKUP = new EnumUpgradeStatus[values().length];
        for (EnumUpgradeStatus upgrade : values()) {
            META_LOOKUP[upgrade.getMetadata()] = upgrade;
        }

        int maxLevel = 0;
        for (EnumUpgradeStatus upgrade : values()) {
            if (upgrade.getLevel() > maxLevel)
                maxLevel = upgrade.getLevel();
        }

        LEVEL_LOOKUP = new EnumUpgradeStatus[maxLevel + 1];
        for (EnumUpgradeStatus upgrade : values()) {
            LEVEL_LOOKUP[upgrade.getLevel()] = upgrade;
        }
    }
}
