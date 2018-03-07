package net.minecraft.item;

import net.minecraft.block.material.MapColor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

public enum EnumDyeColor implements IStringSerializable
{
    WHITE(0, 15, "white", "white", MapColor.SNOW, TextFormatting.WHITE),
    ORANGE(1, 14, "orange", "orange", MapColor.ADOBE, TextFormatting.GOLD),
    MAGENTA(2, 13, "magenta", "magenta", MapColor.MAGENTA, TextFormatting.AQUA),
    LIGHT_BLUE(3, 12, "light_blue", "lightBlue", MapColor.LIGHT_BLUE, TextFormatting.BLUE),
    YELLOW(4, 11, "yellow", "yellow", MapColor.YELLOW, TextFormatting.YELLOW),
    LIME(5, 10, "lime", "lime", MapColor.LIME, TextFormatting.GREEN),
    PINK(6, 9, "pink", "pink", MapColor.PINK, TextFormatting.LIGHT_PURPLE),
    GRAY(7, 8, "gray", "gray", MapColor.GRAY, TextFormatting.DARK_GRAY),
    SILVER(8, 7, "silver", "silver", MapColor.SILVER, TextFormatting.GRAY),
    CYAN(9, 6, "cyan", "cyan", MapColor.CYAN, TextFormatting.DARK_AQUA),
    PURPLE(10, 5, "purple", "purple", MapColor.PURPLE, TextFormatting.DARK_PURPLE),
    BLUE(11, 4, "blue", "blue", MapColor.BLUE, TextFormatting.DARK_BLUE),
    BROWN(12, 3, "brown", "brown", MapColor.BROWN, TextFormatting.GOLD),
    GREEN(13, 2, "green", "green", MapColor.GREEN, TextFormatting.DARK_GREEN),
    RED(14, 1, "red", "red", MapColor.RED, TextFormatting.DARK_RED),
    BLACK(15, 0, "black", "black", MapColor.BLACK, TextFormatting.BLACK);

    private static final EnumDyeColor[] META_LOOKUP = new EnumDyeColor[values().length];
    private static final EnumDyeColor[] DYE_DMG_LOOKUP = new EnumDyeColor[values().length];
    private final int meta;
    private final int dyeDamage;
    private final String name;
    private final String unlocalizedName;
    private final MapColor mapColor;
    private final TextFormatting chatColor;

    private EnumDyeColor(int meta, int dyeDamage, String name, String unlocalizedName, MapColor mapColorIn, TextFormatting chatColor)
    {
        this.meta = meta;
        this.dyeDamage = dyeDamage;
        this.name = name;
        this.unlocalizedName = unlocalizedName;
        this.mapColor = mapColorIn;
        this.chatColor = chatColor;
    }

    public int getMetadata()
    {
        return this.meta;
    }

    public int getDyeDamage()
    {
        return this.dyeDamage;
    }

    public String getUnlocalizedName()
    {
        return this.unlocalizedName;
    }

    public MapColor getMapColor()
    {
        return this.mapColor;
    }

    public static EnumDyeColor byDyeDamage(int damage)
    {
        if (damage < 0 || damage >= DYE_DMG_LOOKUP.length)
        {
            damage = 0;
        }

        return DYE_DMG_LOOKUP[damage];
    }

    public static EnumDyeColor byMetadata(int meta)
    {
        if (meta < 0 || meta >= META_LOOKUP.length)
        {
            meta = 0;
        }

        return META_LOOKUP[meta];
    }

    public String toString()
    {
        return this.unlocalizedName;
    }

    public String getName()
    {
        return this.name;
    }

    static
    {
        for (EnumDyeColor enumdyecolor : values())
        {
            META_LOOKUP[enumdyecolor.getMetadata()] = enumdyecolor;
            DYE_DMG_LOOKUP[enumdyecolor.getDyeDamage()] = enumdyecolor;
        }
    }
}