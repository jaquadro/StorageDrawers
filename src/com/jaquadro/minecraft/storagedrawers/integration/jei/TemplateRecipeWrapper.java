package com.jaquadro.minecraft.storagedrawers.integration.jei;
/*
import com.jaquadro.minecraft.storagedrawers.core.recipe.TemplateRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TemplateRecipeWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper
{
    private final TemplateRecipe recipe;
    private final IJeiHelpers helpers;

    public TemplateRecipeWrapper (TemplateRecipe recipe, IJeiHelpers helpers) {
        this.recipe = recipe;
        this.helpers = helpers;
    }

    @Override
    public int getWidth () {
        return 3;
    }

    @Override
    public int getHeight () {
        return 3;
    }

    @Override
    public void getIngredients (IIngredients ingredients) {
        IStackHelper stackHelper = helpers.getStackHelper();

        List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(Arrays.asList(recipe.getInput()));
        ingredients.setInputLists(ItemStack.class, inputs);

        ItemStack recipeOutput = recipe.getRecipeOutput();
        if (recipeOutput != null)
            ingredients.setOutput(ItemStack.class, recipeOutput);
    }
}
*/