package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemKeyring;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class KeyringRecipe extends ShapedRecipe
{
    public KeyringRecipe(ResourceLocation name) {
        super(name, "", 3, 3, NonNullList.of(Ingredient.EMPTY,
                Ingredient.EMPTY, Ingredient.of(Items.IRON_NUGGET), Ingredient.EMPTY,
                Ingredient.of(Items.IRON_NUGGET),
                Ingredient.of(ModItems.getKeys().map(i -> new ItemStack(i, 1))),
                Ingredient.of(Items.IRON_NUGGET),
                Ingredient.EMPTY, Ingredient.of(Items.IRON_NUGGET), Ingredient.EMPTY),
            new ItemStack(ModItems.KEYRING.get()));
    }

    @Override
    public ItemStack assemble (CraftingContainer inv) {
        ItemStack center = inv.getItem(4);
        if (center.isEmpty() || !(center.getItem() instanceof ItemKey))
            return ItemStack.EMPTY;

        ItemStack result = ItemKeyring.getKeyring(center);
        if (result.isEmpty())
            return ItemStack.EMPTY;

        ItemKeyring.add(result, center);
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer () {
        return StorageDrawers.KEYRING_RECIPE_SERIALIZER.get();
    }
}
