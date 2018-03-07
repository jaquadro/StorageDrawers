package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class EnchantWithLevels extends LootFunction
{
    private final RandomValueRange randomLevel;
    private final boolean isTreasure;

    public EnchantWithLevels(LootCondition[] conditionsIn, RandomValueRange randomRange, boolean p_i46627_3_)
    {
        super(conditionsIn);
        this.randomLevel = randomRange;
        this.isTreasure = p_i46627_3_;
    }

    public ItemStack apply(ItemStack stack, Random rand, LootContext context)
    {
        EnchantmentHelper.addRandomEnchantment(rand, stack, this.randomLevel.generateInt(rand), this.isTreasure);
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<EnchantWithLevels>
        {
            public Serializer()
            {
                super(new ResourceLocation("enchant_with_levels"), EnchantWithLevels.class);
            }

            public void serialize(JsonObject object, EnchantWithLevels functionClazz, JsonSerializationContext serializationContext)
            {
                object.add("levels", serializationContext.serialize(functionClazz.randomLevel));
                object.addProperty("treasure", Boolean.valueOf(functionClazz.isTreasure));
            }

            public EnchantWithLevels deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn)
            {
                RandomValueRange randomvaluerange = (RandomValueRange)JsonUtils.deserializeClass(object, "levels", deserializationContext, RandomValueRange.class);
                boolean flag = JsonUtils.getBoolean(object, "treasure", false);
                return new EnchantWithLevels(conditionsIn, randomvaluerange, flag);
            }
        }
}