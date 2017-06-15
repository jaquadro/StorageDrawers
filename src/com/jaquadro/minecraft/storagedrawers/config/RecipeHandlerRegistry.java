package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.api.registry.IIngredientHandler;
import com.jaquadro.minecraft.storagedrawers.api.registry.IRecipeHandler;
import com.jaquadro.minecraft.storagedrawers.api.registry.IRecipeHandlerRegistry;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeHandlerRegistry implements IRecipeHandlerRegistry
{
    private Map<Class, IRecipeHandler> recipeRegistry = new HashMap<>();
    private Map<Class, IIngredientHandler> ingredientRegistry = new HashMap<>();

    public RecipeHandlerRegistry () {
        registerRecipeHandler(ShapedOreRecipe.class, new ShapedOreRecipeHandler());
        registerRecipeHandler(ShapedRecipes.class, new ShapedRecipeHandler());
        registerRecipeHandler(ShapelessOreRecipe.class, new ShapelessOreRecipeHandler());
        registerRecipeHandler(ShapelessRecipes.class, new ShapelessRecipeHandler());
    }

    @Override
    public void registerRecipeHandler (Class clazz, IRecipeHandler handler) {
        if (!recipeRegistry.containsKey(clazz))
            recipeRegistry.put(clazz, handler);
    }

    @Override
    public void registerIngredientHandler (Class clazz, IIngredientHandler handler) {
        if (!ingredientRegistry.containsKey(clazz))
            ingredientRegistry.put(clazz, handler);
    }

    @Override
    public IRecipeHandler getRecipeHandler (Class clazz) {
        while (clazz != null) {
            if (recipeRegistry.containsKey(clazz))
                return recipeRegistry.get(clazz);

            clazz = clazz.getSuperclass();
        }

        return null;
    }

    @Override
    public IIngredientHandler getIngredientHandler (Class clazz) {
        while (clazz != null) {
            if (ingredientRegistry.containsKey(clazz))
                return ingredientRegistry.get(clazz);

            for (Class inter : clazz.getInterfaces()) {
                if (ingredientRegistry.containsKey(inter))
                    return ingredientRegistry.get(inter);
            }

            clazz = clazz.getSuperclass();
        }

        return null;
    }

    private static class ShapedOreRecipeHandler implements IRecipeHandler
    {
        @Override
        public Object[] getInputAsArray (IRecipe recipe) {
            return recipe.getIngredients().toArray();
        }

        @Override
        public List getInputAsList (IRecipe recipe) {
            return null;
        }
    }

    private static class ShapedRecipeHandler implements IRecipeHandler
    {
        @Override
        public Object[] getInputAsArray (IRecipe recipe) {
            return ((ShapedRecipes) recipe).recipeItems.toArray();
        }

        @Override
        public List getInputAsList (IRecipe recipe) {
            return null;
        }
    }

    private static class ShapelessOreRecipeHandler implements IRecipeHandler
    {
        @Override
        public Object[] getInputAsArray (IRecipe recipe) {
            return null;
        }

        @Override
        public List getInputAsList (IRecipe recipe) {
            return recipe.getIngredients();
        }
    }

    private static class ShapelessRecipeHandler implements IRecipeHandler
    {
        @Override
        public Object[] getInputAsArray (IRecipe recipe) {
            return null;
        }

        @Override
        public List getInputAsList (IRecipe recipe) {
            return ((ShapelessRecipes) recipe).recipeItems;
        }
    }
}
