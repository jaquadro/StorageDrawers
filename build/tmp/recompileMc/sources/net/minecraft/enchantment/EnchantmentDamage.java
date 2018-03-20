package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class EnchantmentDamage extends Enchantment
{
    /** Holds the name to be translated of each protection type. */
    private static final String[] PROTECTION_NAME = new String[] {"all", "undead", "arthropods"};
    /** Holds the base factor of enchantability needed to be able to use the enchant. */
    private static final int[] BASE_ENCHANTABILITY = new int[] {1, 5, 5};
    /** Holds how much each level increased the enchantability factor to be able to use this enchant. */
    private static final int[] LEVEL_ENCHANTABILITY = new int[] {11, 8, 8};
    /**
     * Used on the formula of base enchantability, this is the 'window' factor of values to be able to use thing
     * enchant.
     */
    private static final int[] THRESHOLD_ENCHANTABILITY = new int[] {20, 20, 20};
    /** Defines the type of damage of the enchantment, 0 = all, 1 = undead, 3 = arthropods */
    public final int damageType;

    public EnchantmentDamage(Enchantment.Rarity rarityIn, int damageTypeIn, EntityEquipmentSlot... slots)
    {
        super(rarityIn, EnumEnchantmentType.WEAPON, slots);
        this.damageType = damageTypeIn;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel)
    {
        return BASE_ENCHANTABILITY[this.damageType] + (enchantmentLevel - 1) * LEVEL_ENCHANTABILITY[this.damageType];
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + THRESHOLD_ENCHANTABILITY[this.damageType];
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 5;
    }

    /**
     * Calculates the additional damage that will be dealt by an item with this enchantment. This alternative to
     * calcModifierDamage is sensitive to the targets EnumCreatureAttribute.
     */
    public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
    {
        return this.damageType == 0 ? 1.0F + (float)Math.max(0, level - 1) * 0.5F : (this.damageType == 1 && creatureType == EnumCreatureAttribute.UNDEAD ? (float)level * 2.5F : (this.damageType == 2 && creatureType == EnumCreatureAttribute.ARTHROPOD ? (float)level * 2.5F : 0.0F));
    }

    /**
     * Return the name of key in translation table of this enchantment.
     */
    public String getName()
    {
        return "enchantment.damage." + PROTECTION_NAME[this.damageType];
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    public boolean canApplyTogether(Enchantment ench)
    {
        return !(ench instanceof EnchantmentDamage);
    }

    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     */
    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() instanceof ItemAxe ? true : super.canApply(stack);
    }

    /**
     * Called whenever a mob is damaged with an item that has this enchantment on it.
     */
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level)
    {
        if (target instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)target;

            if (this.damageType == 2 && entitylivingbase.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD)
            {
                int i = 20 + user.getRNG().nextInt(10 * level);
                entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, i, 3));
            }
        }
    }
}