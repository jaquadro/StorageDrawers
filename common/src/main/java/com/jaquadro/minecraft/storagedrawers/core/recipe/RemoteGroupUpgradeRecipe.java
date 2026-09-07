package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.ModRecipes;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Map;

public class RemoteGroupUpgradeRecipe extends CustomRecipe
{
    public static final MapCodec<RemoteGroupUpgradeRecipe> MAP_CODEC = MapCodec.unit(RemoteGroupUpgradeRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoteGroupUpgradeRecipe> STREAM_CODEC = StreamCodec.of((buf, recipe) -> {}, buf -> new RemoteGroupUpgradeRecipe());

    private static final ShapedRecipePattern PATTERN = ShapedRecipePattern.of(Map.of(
            'X', Ingredient.of(Items.ENDER_PEARL),
            '#', Ingredient.of(ModItems.REMOTE_UPGRADE_BOUND.get())),
        "X#X");

    public RemoteGroupUpgradeRecipe () {
        super();
    }

    @Override
    public boolean matches (CraftingInput inv, Level level) {
        return PATTERN.matches(inv);
    }

    @Override
    public ItemStack assemble (CraftingInput inv) {
        ItemStack center = inv.getItem(1);
        if (center == ItemStack.EMPTY)
            center = inv.getItem(4);
        if (center == ItemStack.EMPTY)
            center = inv.getItem(7);

        if (center.isEmpty() || center.getItem() != ModItems.REMOTE_UPGRADE_BOUND.get())
            return ItemStack.EMPTY;

        ItemStack result = new ItemStack(ModItems.REMOTE_GROUP_UPGRADE_BOUND.get());
        result.set(ModDataComponents.CONTROLLER_BINDING.get(), center.get(ModDataComponents.CONTROLLER_BINDING.get()));

        return result;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer () {
        return ModRecipes.REMOTE_GROUP_UPGRADE_SERIALIZER.get();
    }
}
