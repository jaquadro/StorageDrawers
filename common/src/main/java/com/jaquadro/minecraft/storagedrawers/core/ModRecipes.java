package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.core.recipe.*;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipes
{
    private static final ChameleonRegistry<RecipeSerializer<?>> RECIPES = ChameleonServices.REGISTRY.create(BuiltInRegistries.RECIPE_SERIALIZER, ModConstants.MOD_ID);

    public static final RegistryEntry<RecipeSerializer<AddUpgradeRecipe>> UPGRADE_RECIPE_SERIALIZER = RECIPES.register("add_upgrade", () -> new RecipeSerializer<>(AddUpgradeRecipe.MAP_CODEC, AddUpgradeRecipe.STREAM_CODEC));
    public static final RegistryEntry<RecipeSerializer<KeyringRecipe>> KEYRING_RECIPE_SERIALIZER = RECIPES.register("keyring", () -> new RecipeSerializer<>(KeyringRecipe.MAP_CODEC, KeyringRecipe.STREAM_CODEC));
    public static final RegistryEntry<RecipeSerializer<RemoteGroupUpgradeRecipe>> REMOTE_GROUP_UPGRADE_SERIALIZER = RECIPES.register("remote_group_upgrade", () -> new RecipeSerializer<>(RemoteGroupUpgradeRecipe.MAP_CODEC, RemoteGroupUpgradeRecipe.STREAM_CODEC));
    public static final RegistryEntry<RecipeSerializer<UpgradeDetachedDrawerRecipe>> DETACHED_UPGRADE_RECIPE_SERIALIZER = RECIPES.register("add_detached_upgrade", () -> new RecipeSerializer<>(UpgradeDetachedDrawerRecipe.MAP_CODEC, UpgradeDetachedDrawerRecipe.STREAM_CODEC));
    public static final RegistryEntry<RecipeSerializer<PersonalKeyRecipe>> PERSONAL_KEY_RECIPE_SERIALIZER = RECIPES.register("personal_key_cycle", () -> new RecipeSerializer<>(PersonalKeyRecipe.MAP_CODEC, PersonalKeyRecipe.STREAM_CODEC));

    public static void init (ChameleonInit.InitContext context) {
        RECIPES.init(context);
    }
}
