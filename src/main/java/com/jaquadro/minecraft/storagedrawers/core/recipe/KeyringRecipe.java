package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemKeyring;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public class KeyringRecipe extends ShapedRecipe
{
    public KeyringRecipe(CraftingBookCategory cat) {
        super("", cat, new ShapedRecipePattern(3, 3, NonNullList.of(Ingredient.EMPTY,
            Ingredient.EMPTY, Ingredient.of(Items.IRON_NUGGET), Ingredient.EMPTY,
            Ingredient.of(Items.IRON_NUGGET),
                Ingredient.of(ModItems.getKeys().map(i -> new ItemStack(i, 1))),
            Ingredient.of(Items.IRON_NUGGET),
            Ingredient.EMPTY, Ingredient.of(Items.IRON_NUGGET), Ingredient.EMPTY), Optional.empty()),
            new ItemStack(ModItems.KEYRING.get()));
    }

    @Override
    public boolean matches (CraftingContainer p_44176_, Level p_44177_) {
        return super.matches(p_44176_, p_44177_);
    }

    @Override
    public ItemStack assemble (CraftingContainer inv, RegistryAccess registries) {
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
