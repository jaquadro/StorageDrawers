package com.jaquadro.minecraft.storagedrawers.util;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.config.CompTierRegistry;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.*;

public class CompactingHelper
{
    private static InventoryLookup lookup1 = new InventoryLookup(1, 1);
    private static InventoryLookup lookup2 = new InventoryLookup(2, 2);
    private static InventoryLookup lookup3 = new InventoryLookup(3, 3);

    private Level world;

    public class Result
    {
        @Nonnull
        private ItemStack stack;
        private int size;

        public Result (ItemStack stack, int size) {
            this.stack = stack;
            this.size = size;
        }

        @Nonnull
        public ItemStack getStack () {
            return stack;
        }

        public int getSize () {
            return size;
        }
    }

    public CompactingHelper (Level world) {
        this.world = world;
    }

    @Nonnull
    public Result findHigherTier (@Nonnull ItemStack stack) {
        boolean debugTrace = CommonConfig.GENERAL.debugTrace.get();
        if (!world.isClientSide && debugTrace)
            StorageDrawers.log.info("Finding ascending candidates for " + stack.toString());

        CompTierRegistry.Record record = StorageDrawers.compRegistry.findHigherTier(stack);
        if (record != null) {
            if (!world.isClientSide && debugTrace)
                StorageDrawers.log.info("Found " + record.upper.toString() + " in registry with conv=" + record.convRate);

            return new Result(record.upper, record.convRate);
        }

        List<ItemStack> candidates = new ArrayList<>();

        int lookupSize = setupLookup(lookup3, stack);
        List<ItemStack> fwdCandidates = findAllMatchingRecipes(lookup3);

        if (fwdCandidates.size() == 0) {
            lookupSize = setupLookup(lookup2, stack);
            fwdCandidates = findAllMatchingRecipes(lookup2);
        }

        if (fwdCandidates.size() > 0) {
            for (ItemStack match : fwdCandidates) {
                setupLookup(lookup1, match);
                List<ItemStack> backCandidates = findAllMatchingRecipes(lookup1);

                for (ItemStack comp : backCandidates) {
                    if (comp.getCount() != lookupSize)
                        continue;

                    // TODO: ItemStackMatcher.areItemsEqual(comp, stack, false)
                    if (!ItemStackMatcher.areItemsEqual(comp, stack))
                        continue;

                    candidates.add(match);
                    if (!world.isClientSide && debugTrace)
                        StorageDrawers.log.info("Found ascending candidate for " + stack.toString() + ": " + match.toString() + " size=" + lookupSize + ", inverse=" + comp.toString());

                    break;
                }
            }
        }

        ItemStack modMatch = findMatchingModCandidate(stack, candidates);
        if (!modMatch.isEmpty())
            return new Result(modMatch, lookupSize);

        if (candidates.size() > 0)
            return new Result(candidates.get(0), lookupSize);

        if (!world.isClientSide && debugTrace)
            StorageDrawers.log.info("No candidates found");

        return new Result(ItemStack.EMPTY, 0);
    }

    @Nonnull
    public Result findLowerTier (@Nonnull ItemStack stack) {
        boolean debugTrace = CommonConfig.GENERAL.debugTrace.get();
        if (!world.isClientSide && debugTrace)
            StorageDrawers.log.info("Finding descending candidates for " + stack.toString());

        CompTierRegistry.Record record = StorageDrawers.compRegistry.findLowerTier(stack);
        if (record != null) {
            if (!world.isClientSide && debugTrace)
                StorageDrawers.log.info("Found " + record.lower.toString() + " in registry with conv=" + record.convRate);

            return new Result(record.lower, record.convRate);
        }

        List<ItemStack> candidates = new ArrayList<>();
        Map<ItemStack, Integer> candidatesRate = new HashMap<>();

        for (Recipe<CraftingContainer> recipe : world.getRecipeManager().byType(RecipeType.CRAFTING).values()) {
            ItemStack output = recipe.getResultItem();
            // TODO: ItemStackOreMatcher.areItemsEqual(stack, output, true)
            if (!ItemStackMatcher.areItemsEqual(stack, output))
                continue;

            @Nonnull ItemStack match = tryMatch(stack, recipe.getIngredients());
            if (!match.isEmpty()) {
                int lookupSize = setupLookup(lookup1, output);
                List<ItemStack> compMatches = findAllMatchingRecipes(lookup1);
                for (ItemStack comp : compMatches) {
                    int recipeSize = recipe.getIngredients().size();
                    // TODO: ItemStackOreMatcher.areItemsEqual(match, comp, true)
                    if (ItemStackMatcher.areItemsEqual(match, comp) && comp.getCount() == recipeSize) {
                        candidates.add(match);
                        candidatesRate.put(match, recipeSize);

                        if (!world.isClientSide && debugTrace)
                            StorageDrawers.log.info("Found descending candidate for " + stack.toString() + ": " + match.toString() + " size=" + recipeSize + ", inverse=" + comp.toString());
                    } else if (!world.isClientSide && debugTrace)
                        StorageDrawers.log.info("Back-check failed for " + match.toString() + " size=" + lookupSize + ", inverse=" + comp.toString());
                }
            }
        }

        ItemStack modMatch = findMatchingModCandidate(stack, candidates);
        if (!modMatch.isEmpty())
            return new Result(modMatch, candidatesRate.get(modMatch));

        if (candidates.size() > 0) {
            ItemStack match = candidates.get(0);
            return new Result(match, candidatesRate.get(match));
        }

        if (!world.isClientSide && debugTrace)
            StorageDrawers.log.info("No candidates found");

        return new Result(ItemStack.EMPTY, 0);
    }

    private List<ItemStack> findAllMatchingRecipes (CraftingContainer crafting) {
        List<ItemStack> candidates = new ArrayList<>();

        for (CraftingRecipe recipe : world.getRecipeManager().getRecipesFor(RecipeType.CRAFTING, crafting, world)) {
            if (recipe.matches(crafting, world)) {
                ItemStack result = recipe.assemble(crafting);
                if (!result.isEmpty())
                    candidates.add(result);
            }
        }

        return candidates;
    }

    @Nonnull
    private ItemStack findMatchingModCandidate (@Nonnull ItemStack reference, List<ItemStack> candidates) {
        ResourceLocation referenceName = reference.getItem().getRegistryName();
        if (referenceName != null) {
            for (ItemStack candidate : candidates) {
                ResourceLocation matchName = candidate.getItem().getRegistryName();
                if (matchName != null) {
                    if (referenceName.getNamespace().equals(matchName.getPath()))
                        return candidate;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    private ItemStack tryMatch (@Nonnull ItemStack stack, NonNullList<Ingredient> ingredients) {
        if (ingredients.size() != 9 && ingredients.size() != 4)
            return ItemStack.EMPTY;

        Ingredient refIngredient = ingredients.get(0);
        ItemStack[] refMatchingStacks = refIngredient.getItems();
        if (refMatchingStacks.length == 0)
            return ItemStack.EMPTY;

        for (int i = 1, n = ingredients.size(); i < n; i++) {
            Ingredient ingredient = ingredients.get(i);
            @Nonnull ItemStack match = ItemStack.EMPTY;

            for (ItemStack ingItemMatch : refMatchingStacks) {
                if (ingredient.test(ingItemMatch)) {
                    match = ingItemMatch;
                    break;
                }
            }

            if (match.isEmpty())
                return ItemStack.EMPTY;
        }

        ItemStack match = findMatchingModCandidate(stack, Arrays.asList(refMatchingStacks));
        if (match.isEmpty())
            match = refMatchingStacks[0];

        return match;
    }

    private int setupLookup (InventoryLookup inv, @Nonnull ItemStack stack) {
        for (int i = 0, n = inv.getContainerSize(); i < n; i++)
            inv.setItem(i, stack);

        return inv.getContainerSize();
    }

    private static class InventoryLookup extends CraftingContainer
    {
        private ItemStack[] stackList;

        public InventoryLookup (int width, int height) {
            super(null, width, height);

            stackList = new ItemStack[width * height];
            for (int i = 0; i < stackList.length; i++)
                stackList[i] = ItemStack.EMPTY;
        }

        @Override
        public int getContainerSize ()
        {
            return this.stackList.length;
        }

        @Override
        @Nonnull
        public ItemStack getItem (int slot)
        {
            return slot >= this.getContainerSize() ? ItemStack.EMPTY : this.stackList[slot];
        }

        @Override
        @Nonnull
        public ItemStack removeItemNoUpdate (int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        @Nonnull
        public ItemStack removeItem (int slot, int count) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem (int slot, @Nonnull ItemStack stack) {
            stackList[slot] = stack;
        }
    }
}
