package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootEntryItem extends LootEntry
{
    protected final Item item;
    protected final LootFunction[] functions;

    public LootEntryItem(Item itemIn, int weightIn, int qualityIn, LootFunction[] functionsIn, LootCondition[] conditionsIn, String entryName)
    {
        super(weightIn, qualityIn, conditionsIn, entryName);
        this.item = itemIn;
        this.functions = functionsIn;
    }

    public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context)
    {
        ItemStack itemstack = new ItemStack(this.item);

        for (LootFunction lootfunction : this.functions)
        {
            if (LootConditionManager.testAllConditions(lootfunction.getConditions(), rand, context))
            {
                itemstack = lootfunction.apply(itemstack, rand, context);
            }
        }

        if (itemstack.stackSize > 0)
        {
            if (itemstack.stackSize < this.item.getItemStackLimit(itemstack))
            {
                stacks.add(itemstack);
            }
            else
            {
                int i = itemstack.stackSize;

                while (i > 0)
                {
                    ItemStack itemstack1 = itemstack.copy();
                    itemstack1.stackSize = Math.min(itemstack.getMaxStackSize(), i);
                    i -= itemstack1.stackSize;
                    stacks.add(itemstack1);
                }
            }
        }
    }

    protected void serialize(JsonObject json, JsonSerializationContext context)
    {
        if (this.functions != null && this.functions.length > 0)
        {
            json.add("functions", context.serialize(this.functions));
        }

        ResourceLocation resourcelocation = (ResourceLocation)Item.REGISTRY.getNameForObject(this.item);

        if (resourcelocation == null)
        {
            throw new IllegalArgumentException("Can\'t serialize unknown item " + this.item);
        }
        else
        {
            json.addProperty("name", resourcelocation.toString());
        }
    }

    public static LootEntryItem deserialize(JsonObject object, JsonDeserializationContext deserializationContext, int weightIn, int qualityIn, LootCondition[] conditionsIn)
    {
        String name = net.minecraftforge.common.ForgeHooks.readLootEntryName(object, "item");
        Item item = JsonUtils.getItem(object, "name");
        LootFunction[] alootfunction;

        if (object.has("functions"))
        {
            alootfunction = (LootFunction[])JsonUtils.deserializeClass(object, "functions", deserializationContext, LootFunction[].class);
        }
        else
        {
            alootfunction = new LootFunction[0];
        }

        return new LootEntryItem(item, weightIn, qualityIn, alootfunction, conditionsIn, name);
    }
}