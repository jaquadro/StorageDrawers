package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType
{
    ALL,
    ARMOR,
    ARMOR_FEET,
    ARMOR_LEGS,
    ARMOR_CHEST,
    ARMOR_HEAD,
    WEAPON,
    DIGGER,
    FISHING_ROD,
    BREAKABLE,
    BOW;

    /**
     * Return true if the item passed can be enchanted by a enchantment of this type.
     */
    public boolean canEnchantItem(Item itemIn)
    {
        if (this == ALL)
        {
            return true;
        }
        else if (this == BREAKABLE && itemIn.isDamageable())
        {
            return true;
        }
        else if (itemIn instanceof ItemArmor)
        {
            if (this == ARMOR)
            {
                return true;
            }
            else
            {
                ItemArmor itemarmor = (ItemArmor)itemIn;
                return itemarmor.armorType == EntityEquipmentSlot.HEAD ? this == ARMOR_HEAD : (itemarmor.armorType == EntityEquipmentSlot.LEGS ? this == ARMOR_LEGS : (itemarmor.armorType == EntityEquipmentSlot.CHEST ? this == ARMOR_CHEST : (itemarmor.armorType == EntityEquipmentSlot.FEET ? this == ARMOR_FEET : false)));
            }
        }
        else
        {
            return itemIn instanceof ItemSword ? this == WEAPON : (itemIn instanceof ItemTool ? this == DIGGER : (itemIn instanceof ItemBow ? this == BOW : (itemIn instanceof ItemFishingRod ? this == FISHING_ROD : false)));
        }
    }
}