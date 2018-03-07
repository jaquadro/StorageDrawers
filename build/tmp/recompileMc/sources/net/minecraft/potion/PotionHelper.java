package net.minecraft.potion;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

public class PotionHelper
{
    private static final List<PotionHelper.MixPredicate<PotionType>> POTION_TYPE_CONVERSIONS = Lists.<PotionHelper.MixPredicate<PotionType>>newArrayList();
    private static final List<PotionHelper.MixPredicate<Item>> POTION_ITEM_CONVERSIONS = Lists.<PotionHelper.MixPredicate<Item>>newArrayList();
    private static final List<PotionHelper.ItemPredicateInstance> POTION_ITEMS = Lists.<PotionHelper.ItemPredicateInstance>newArrayList();
    private static final Predicate<ItemStack> IS_POTION_ITEM = new Predicate<ItemStack>()
    {
        public boolean apply(@Nullable ItemStack p_apply_1_)
        {
            for (PotionHelper.ItemPredicateInstance potionhelper$itempredicateinstance : PotionHelper.POTION_ITEMS)
            {
                if (potionhelper$itempredicateinstance.apply(p_apply_1_))
                {
                    return true;
                }
            }

            return false;
        }
    };

    public static boolean isReagent(ItemStack stack)
    {
        return isItemConversionReagent(stack) || isTypeConversionReagent(stack);
    }

    protected static boolean isItemConversionReagent(ItemStack stack)
    {
        int i = 0;

        for (int j = POTION_ITEM_CONVERSIONS.size(); i < j; ++i)
        {
            if (((PotionHelper.MixPredicate)POTION_ITEM_CONVERSIONS.get(i)).reagent.apply(stack))
            {
                return true;
            }
        }

        return false;
    }

    protected static boolean isTypeConversionReagent(ItemStack stack)
    {
        int i = 0;

        for (int j = POTION_TYPE_CONVERSIONS.size(); i < j; ++i)
        {
            if (((PotionHelper.MixPredicate)POTION_TYPE_CONVERSIONS.get(i)).reagent.apply(stack))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean hasConversions(ItemStack input, ItemStack reagent)
    {
        return !IS_POTION_ITEM.apply(input) ? false : hasItemConversions(input, reagent) || hasTypeConversions(input, reagent);
    }

    protected static boolean hasItemConversions(ItemStack p_185206_0_, ItemStack p_185206_1_)
    {
        Item item = p_185206_0_.getItem();
        int i = 0;

        for (int j = POTION_ITEM_CONVERSIONS.size(); i < j; ++i)
        {
            PotionHelper.MixPredicate<Item> mixpredicate = (PotionHelper.MixPredicate)POTION_ITEM_CONVERSIONS.get(i);

            if (mixpredicate.input == item && mixpredicate.reagent.apply(p_185206_1_))
            {
                return true;
            }
        }

        return false;
    }

    protected static boolean hasTypeConversions(ItemStack p_185209_0_, ItemStack p_185209_1_)
    {
        PotionType potiontype = PotionUtils.getPotionFromItem(p_185209_0_);
        int i = 0;

        for (int j = POTION_TYPE_CONVERSIONS.size(); i < j; ++i)
        {
            PotionHelper.MixPredicate<PotionType> mixpredicate = (PotionHelper.MixPredicate)POTION_TYPE_CONVERSIONS.get(i);

            if (mixpredicate.input == potiontype && mixpredicate.reagent.apply(p_185209_1_))
            {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static ItemStack doReaction(ItemStack reagent, @Nullable ItemStack potionIn)
    {
        if (potionIn != null)
        {
            PotionType potiontype = PotionUtils.getPotionFromItem(potionIn);
            Item item = potionIn.getItem();
            int i = 0;

            for (int j = POTION_ITEM_CONVERSIONS.size(); i < j; ++i)
            {
                PotionHelper.MixPredicate<Item> mixpredicate = (PotionHelper.MixPredicate)POTION_ITEM_CONVERSIONS.get(i);

                if (mixpredicate.input == item && mixpredicate.reagent.apply(reagent))
                {
                    return PotionUtils.addPotionToItemStack(new ItemStack((Item)mixpredicate.output), potiontype);
                }
            }

            i = 0;

            for (int k = POTION_TYPE_CONVERSIONS.size(); i < k; ++i)
            {
                PotionHelper.MixPredicate<PotionType> mixpredicate1 = (PotionHelper.MixPredicate)POTION_TYPE_CONVERSIONS.get(i);

                if (mixpredicate1.input == potiontype && mixpredicate1.reagent.apply(reagent))
                {
                    return PotionUtils.addPotionToItemStack(new ItemStack(item), (PotionType)mixpredicate1.output);
                }
            }
        }

        return potionIn;
    }

    public static void init()
    {
        Predicate<ItemStack> predicate = new PotionHelper.ItemPredicateInstance(Items.NETHER_WART);
        Predicate<ItemStack> predicate1 = new PotionHelper.ItemPredicateInstance(Items.GOLDEN_CARROT);
        Predicate<ItemStack> predicate2 = new PotionHelper.ItemPredicateInstance(Items.REDSTONE);
        Predicate<ItemStack> predicate3 = new PotionHelper.ItemPredicateInstance(Items.FERMENTED_SPIDER_EYE);
        Predicate<ItemStack> predicate4 = new PotionHelper.ItemPredicateInstance(Items.RABBIT_FOOT);
        Predicate<ItemStack> predicate5 = new PotionHelper.ItemPredicateInstance(Items.GLOWSTONE_DUST);
        Predicate<ItemStack> predicate6 = new PotionHelper.ItemPredicateInstance(Items.MAGMA_CREAM);
        Predicate<ItemStack> predicate7 = new PotionHelper.ItemPredicateInstance(Items.SUGAR);
        Predicate<ItemStack> predicate8 = new PotionHelper.ItemPredicateInstance(Items.FISH, ItemFishFood.FishType.PUFFERFISH.getMetadata());
        Predicate<ItemStack> predicate9 = new PotionHelper.ItemPredicateInstance(Items.SPECKLED_MELON);
        Predicate<ItemStack> predicate10 = new PotionHelper.ItemPredicateInstance(Items.SPIDER_EYE);
        Predicate<ItemStack> predicate11 = new PotionHelper.ItemPredicateInstance(Items.GHAST_TEAR);
        Predicate<ItemStack> predicate12 = new PotionHelper.ItemPredicateInstance(Items.BLAZE_POWDER);
        registerPotionItem(new PotionHelper.ItemPredicateInstance(Items.POTIONITEM));
        registerPotionItem(new PotionHelper.ItemPredicateInstance(Items.SPLASH_POTION));
        registerPotionItem(new PotionHelper.ItemPredicateInstance(Items.LINGERING_POTION));
        registerPotionItemConversion(Items.POTIONITEM, new PotionHelper.ItemPredicateInstance(Items.GUNPOWDER), Items.SPLASH_POTION);
        registerPotionItemConversion(Items.SPLASH_POTION, new PotionHelper.ItemPredicateInstance(Items.DRAGON_BREATH), Items.LINGERING_POTION);
        registerPotionTypeConversion(PotionTypes.WATER, predicate9, PotionTypes.MUNDANE);
        registerPotionTypeConversion(PotionTypes.WATER, predicate11, PotionTypes.MUNDANE);
        registerPotionTypeConversion(PotionTypes.WATER, predicate4, PotionTypes.MUNDANE);
        registerPotionTypeConversion(PotionTypes.WATER, predicate12, PotionTypes.MUNDANE);
        registerPotionTypeConversion(PotionTypes.WATER, predicate10, PotionTypes.MUNDANE);
        registerPotionTypeConversion(PotionTypes.WATER, predicate7, PotionTypes.MUNDANE);
        registerPotionTypeConversion(PotionTypes.WATER, predicate6, PotionTypes.MUNDANE);
        registerPotionTypeConversion(PotionTypes.WATER, predicate5, PotionTypes.THICK);
        registerPotionTypeConversion(PotionTypes.WATER, predicate2, PotionTypes.MUNDANE);
        registerPotionTypeConversion(PotionTypes.WATER, predicate, PotionTypes.AWKWARD);
        registerPotionTypeConversion(PotionTypes.AWKWARD, predicate1, PotionTypes.NIGHT_VISION);
        registerPotionTypeConversion(PotionTypes.NIGHT_VISION, predicate2, PotionTypes.LONG_NIGHT_VISION);
        registerPotionTypeConversion(PotionTypes.NIGHT_VISION, predicate3, PotionTypes.INVISIBILITY);
        registerPotionTypeConversion(PotionTypes.LONG_NIGHT_VISION, predicate3, PotionTypes.LONG_INVISIBILITY);
        registerPotionTypeConversion(PotionTypes.INVISIBILITY, predicate2, PotionTypes.LONG_INVISIBILITY);
        registerPotionTypeConversion(PotionTypes.AWKWARD, predicate6, PotionTypes.FIRE_RESISTANCE);
        registerPotionTypeConversion(PotionTypes.FIRE_RESISTANCE, predicate2, PotionTypes.LONG_FIRE_RESISTANCE);
        registerPotionTypeConversion(PotionTypes.AWKWARD, predicate4, PotionTypes.LEAPING);
        registerPotionTypeConversion(PotionTypes.LEAPING, predicate2, PotionTypes.LONG_LEAPING);
        registerPotionTypeConversion(PotionTypes.LEAPING, predicate5, PotionTypes.STRONG_LEAPING);
        registerPotionTypeConversion(PotionTypes.LEAPING, predicate3, PotionTypes.SLOWNESS);
        registerPotionTypeConversion(PotionTypes.LONG_LEAPING, predicate3, PotionTypes.LONG_SLOWNESS);
        registerPotionTypeConversion(PotionTypes.SLOWNESS, predicate2, PotionTypes.LONG_SLOWNESS);
        registerPotionTypeConversion(PotionTypes.SWIFTNESS, predicate3, PotionTypes.SLOWNESS);
        registerPotionTypeConversion(PotionTypes.LONG_SWIFTNESS, predicate3, PotionTypes.LONG_SLOWNESS);
        registerPotionTypeConversion(PotionTypes.AWKWARD, predicate7, PotionTypes.SWIFTNESS);
        registerPotionTypeConversion(PotionTypes.SWIFTNESS, predicate2, PotionTypes.LONG_SWIFTNESS);
        registerPotionTypeConversion(PotionTypes.SWIFTNESS, predicate5, PotionTypes.STRONG_SWIFTNESS);
        registerPotionTypeConversion(PotionTypes.AWKWARD, predicate8, PotionTypes.WATER_BREATHING);
        registerPotionTypeConversion(PotionTypes.WATER_BREATHING, predicate2, PotionTypes.LONG_WATER_BREATHING);
        registerPotionTypeConversion(PotionTypes.AWKWARD, predicate9, PotionTypes.HEALING);
        registerPotionTypeConversion(PotionTypes.HEALING, predicate5, PotionTypes.STRONG_HEALING);
        registerPotionTypeConversion(PotionTypes.HEALING, predicate3, PotionTypes.HARMING);
        registerPotionTypeConversion(PotionTypes.STRONG_HEALING, predicate3, PotionTypes.STRONG_HARMING);
        registerPotionTypeConversion(PotionTypes.HARMING, predicate5, PotionTypes.STRONG_HARMING);
        registerPotionTypeConversion(PotionTypes.POISON, predicate3, PotionTypes.HARMING);
        registerPotionTypeConversion(PotionTypes.LONG_POISON, predicate3, PotionTypes.HARMING);
        registerPotionTypeConversion(PotionTypes.STRONG_POISON, predicate3, PotionTypes.STRONG_HARMING);
        registerPotionTypeConversion(PotionTypes.AWKWARD, predicate10, PotionTypes.POISON);
        registerPotionTypeConversion(PotionTypes.POISON, predicate2, PotionTypes.LONG_POISON);
        registerPotionTypeConversion(PotionTypes.POISON, predicate5, PotionTypes.STRONG_POISON);
        registerPotionTypeConversion(PotionTypes.AWKWARD, predicate11, PotionTypes.REGENERATION);
        registerPotionTypeConversion(PotionTypes.REGENERATION, predicate2, PotionTypes.LONG_REGENERATION);
        registerPotionTypeConversion(PotionTypes.REGENERATION, predicate5, PotionTypes.STRONG_REGENERATION);
        registerPotionTypeConversion(PotionTypes.AWKWARD, predicate12, PotionTypes.STRENGTH);
        registerPotionTypeConversion(PotionTypes.STRENGTH, predicate2, PotionTypes.LONG_STRENGTH);
        registerPotionTypeConversion(PotionTypes.STRENGTH, predicate5, PotionTypes.STRONG_STRENGTH);
        registerPotionTypeConversion(PotionTypes.WATER, predicate3, PotionTypes.WEAKNESS);
        registerPotionTypeConversion(PotionTypes.WEAKNESS, predicate2, PotionTypes.LONG_WEAKNESS);
    }

    /**
     * Registers a conversion from one potion item to another, with the given reagent. For example, normal potions
     * become splash potions using gunpowder.
     */
    public static void registerPotionItemConversion(ItemPotion p_185201_0_, PotionHelper.ItemPredicateInstance p_185201_1_, ItemPotion p_185201_2_)
    {
        POTION_ITEM_CONVERSIONS.add(new PotionHelper.MixPredicate(p_185201_0_, p_185201_1_, p_185201_2_));
    }

    /**
     * Registers an itempredicate that identifies a potion item, for example Items.potionItem, or Items.lingering_potion
     */
    public static void registerPotionItem(PotionHelper.ItemPredicateInstance p_185202_0_)
    {
        POTION_ITEMS.add(p_185202_0_);
    }

    /**
     * Registers a conversion from one PotionType to another PotionType, with the given reagent
     */
    public static void registerPotionTypeConversion(PotionType input, Predicate<ItemStack> reagentPredicate, PotionType output)
    {
        POTION_TYPE_CONVERSIONS.add(new PotionHelper.MixPredicate(input, reagentPredicate, output));
    }

    public static class ItemPredicateInstance implements Predicate<ItemStack>
        {
            private final Item item;
            private final int meta;

            public ItemPredicateInstance(Item itemIn)
            {
                this(itemIn, -1);
            }

            public ItemPredicateInstance(Item itemIn, int metaIn)
            {
                this.item = itemIn;
                this.meta = metaIn;
            }

            public boolean apply(@Nullable ItemStack p_apply_1_)
            {
                return p_apply_1_ != null && p_apply_1_.getItem() == this.item && (this.meta == -1 || this.meta == p_apply_1_.getMetadata());
            }
        }

    public static class MixPredicate<T>
        {
            final T input;
            final Predicate<ItemStack> reagent;
            final T output;

            public MixPredicate(T inputIn, Predicate<ItemStack> reagentIn, T outputIn)
            {
                this.input = inputIn;
                this.reagent = reagentIn;
                this.output = outputIn;
            }
        }
}