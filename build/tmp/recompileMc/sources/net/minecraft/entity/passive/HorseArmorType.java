package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum HorseArmorType
{
    NONE(0),
    IRON(5, "iron", "meo"),
    GOLD(7, "gold", "goo"),
    DIAMOND(11, "diamond", "dio");

    private final String textureName;
    private final String hash;
    private final int protection;

    private HorseArmorType(int armorStrengthIn)
    {
        this.protection = armorStrengthIn;
        this.textureName = null;
        this.hash = "";
    }

    private HorseArmorType(int armorStrengthIn, String p_i46800_4_, String p_i46800_5_)
    {
        this.protection = armorStrengthIn;
        this.textureName = "textures/entity/horse/armor/horse_armor_" + p_i46800_4_ + ".png";
        this.hash = p_i46800_5_;
    }

    public int getOrdinal()
    {
        return this.ordinal();
    }

    @SideOnly(Side.CLIENT)
    public String getHash()
    {
        return this.hash;
    }

    public int getProtection()
    {
        return this.protection;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public String getTextureName()
    {
        return this.textureName;
    }

    public static HorseArmorType getByOrdinal(int ordinal)
    {
        return values()[ordinal];
    }

    public static HorseArmorType getByItemStack(@Nullable ItemStack stack)
    {
        return stack == null ? NONE : getByItem(stack.getItem());
    }

    public static HorseArmorType getByItem(@Nullable Item itemIn)
    {
        return itemIn == Items.IRON_HORSE_ARMOR ? IRON : (itemIn == Items.GOLDEN_HORSE_ARMOR ? GOLD : (itemIn == Items.DIAMOND_HORSE_ARMOR ? DIAMOND : NONE));
    }

    public static boolean isHorseArmor(@Nullable Item itemIn)
    {
        return getByItem(itemIn) != NONE;
    }
}