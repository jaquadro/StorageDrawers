package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.ModRecipes;
import com.jaquadro.minecraft.storagedrawers.item.ItemKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemKeyring;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public class KeyringRecipe extends ShapedRecipe
{
    public KeyringRecipe (CraftingBookCategory cat) {
        super("", cat, new ShapedRecipePattern(3, 3, NonNullList.of(Ingredient.EMPTY,
            Ingredient.EMPTY, Ingredient.of(Items.IRON_NUGGET), Ingredient.EMPTY,
            Ingredient.of(Items.IRON_NUGGET),
                Ingredient.of(ModItems.getKeys().map(i -> new ItemStack(i, 1))),
            Ingredient.of(Items.IRON_NUGGET),
            Ingredient.EMPTY, Ingredient.of(Items.IRON_NUGGET), Ingredient.EMPTY), Optional.empty()),
            new ItemStack(ModItems.KEYRING.get()));
    }

    @Override
    public ItemStack assemble (CraftingInput inv, HolderLookup.Provider registries) {
        ItemStack center = inv.getItem(4);
        if (center.isEmpty() || !(center.getItem() instanceof ItemKey))
            return ItemStack.EMPTY;

        ItemStack result = ItemKeyring.getKeyring(center);
        if (result.isEmpty())
            return ItemStack.EMPTY;

        KeyringContents contents = result.get(ModDataComponents.KEYRING_CONTENTS.get());
        if (contents != null) {
            KeyringContents.Mutable mutable = new KeyringContents.Mutable(contents);
            mutable.tryInsert(center);
            result.set(ModDataComponents.KEYRING_CONTENTS.get(), mutable.toImmutable());
        }

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer () {
        return ModRecipes.KEYRING_RECIPE_SERIALIZER.get();
    }
}
