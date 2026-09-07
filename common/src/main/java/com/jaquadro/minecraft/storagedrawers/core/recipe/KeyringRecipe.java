package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.ModRecipes;
import com.jaquadro.minecraft.storagedrawers.item.ItemKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemKeyring;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Map;

public class KeyringRecipe extends CustomRecipe
{
    public static final MapCodec<KeyringRecipe> MAP_CODEC = MapCodec.unit(KeyringRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, KeyringRecipe> STREAM_CODEC = StreamCodec.of((buf, recipe) -> {}, buf -> new KeyringRecipe());

    private static final ShapedRecipePattern PATTERN = ShapedRecipePattern.of(Map.of(
            'X', Ingredient.of(Items.IRON_NUGGET),
            '#', Ingredient.of(ModItems.getKeys())),
        " X ", "X#X", " X ");

    public KeyringRecipe () {
        super();
    }

    @Override
    public boolean matches (CraftingInput inv, Level level) {
        return PATTERN.matches(inv);
    }

    @Override
    public ItemStack assemble (CraftingInput inv) {
        ItemStack center = inv.getItem(4);
        if (center.isEmpty() || !(center.getItem() instanceof ItemKey))
            return ItemStack.EMPTY;

        ItemStack result = ItemKeyring.getKeyring(center);
        if (result.isEmpty())
            return ItemStack.EMPTY;

        KeyringContents contents = result.get(ModDataComponents.KEYRING_CONTENTS.get());
        if (contents == null)
            contents = new KeyringContents(new ArrayList<>());

        KeyringContents.Mutable mutable = new KeyringContents.Mutable(contents);
        mutable.tryInsert(center.copy());
        result.set(ModDataComponents.KEYRING_CONTENTS.get(), mutable.toImmutable());

        return result;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer () {
        return ModRecipes.KEYRING_RECIPE_SERIALIZER.get();
    }
}
