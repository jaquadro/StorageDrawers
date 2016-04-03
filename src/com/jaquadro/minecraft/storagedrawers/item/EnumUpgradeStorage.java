package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.util.IStringSerializable;

public enum EnumUpgradeStorage implements IStringSerializable
{
    OBSIDIAN(0, 2, "obsidian", "obsidian"),
    IRON(1, 3, "iron", "iron"),
    GOLD(2, 4, "gold", "gold"),
    DIAMOND(3, 5, "diamond", "diamond"),
    EMERALD(4, 6, "emerald", "emerald");

    private static final EnumUpgradeStorage[] META_LOOKUP;
    private static final EnumUpgradeStorage[] LEVEL_LOOKUP;

    private final int meta;
    private final int level;
    private final String name;
    private final String unlocalizedName;

    private EnumUpgradeStorage (int meta, int level, String name, String unlocalizedName) {
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

    public static EnumUpgradeStorage byMetadata (int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length)
            meta = 0;
        return META_LOOKUP[meta];
    }

    public static EnumUpgradeStorage byLevel (int level) {
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
        META_LOOKUP = new EnumUpgradeStorage[values().length];
        for (EnumUpgradeStorage upgrade : values()) {
            META_LOOKUP[upgrade.getMetadata()] = upgrade;
        }

        int maxLevel = 0;
        for (EnumUpgradeStorage upgrade : values()) {
            if (upgrade.getLevel() > maxLevel)
                maxLevel = upgrade.getLevel();
        }

        LEVEL_LOOKUP = new EnumUpgradeStorage[maxLevel + 1];
        for (EnumUpgradeStorage upgrade : values()) {
            LEVEL_LOOKUP[upgrade.getLevel()] = upgrade;
        }
    }
}
