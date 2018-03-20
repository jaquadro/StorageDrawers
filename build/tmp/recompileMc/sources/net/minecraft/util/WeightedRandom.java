package net.minecraft.util;

import java.util.List;
import java.util.Random;

public class WeightedRandom
{
    /**
     * Returns the total weight of all items in a collection.
     */
    public static int getTotalWeight(List <? extends WeightedRandom.Item > collection)
    {
        int i = 0;
        int j = 0;

        for (int k = collection.size(); j < k; ++j)
        {
            WeightedRandom.Item weightedrandom$item = (WeightedRandom.Item)collection.get(j);
            i += weightedrandom$item.itemWeight;
        }

        return i;
    }

    /**
     * Returns a random choice from the input items, with a total weight value.
     */
    public static <T extends WeightedRandom.Item> T getRandomItem(Random random, List<T> collection, int totalWeight)
    {
        if (totalWeight <= 0)
        {
            throw new IllegalArgumentException();
        }
        else
        {
            int i = random.nextInt(totalWeight);
            return getRandomItem(collection, i);
        }
    }

    public static <T extends WeightedRandom.Item> T getRandomItem(List<T> collection, int weight)
    {
        int i = 0;

        for (int j = collection.size(); i < j; ++i)
        {
            T t = collection.get(i);
            weight -= t.itemWeight;

            if (weight < 0)
            {
                return t;
            }
        }

        return (T)null;
    }

    /**
     * Returns a random choice from the input items.
     */
    public static <T extends WeightedRandom.Item> T getRandomItem(Random random, List<T> collection)
    {
        return getRandomItem(random, collection, getTotalWeight(collection));
    }

    public static class Item
        {
            /** The Weight is how often the item is chosen(higher number is higher chance(lower is lower)) */
            public int itemWeight;

            public Item(int itemWeightIn)
            {
                this.itemWeight = itemWeightIn;
            }
        }
}