/*package com.jaquadro.minecraft.storagedrawers.integration.jei;

import com.jaquadro.minecraft.storagedrawers.core.recipe.TemplateRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

// TODO: Deprecated API
public class TemplateRecipeHandler implements IRecipeHandler<TemplateRecipe>
{
    private final TemplateRecipeWrapper wrapper;

    public TemplateRecipeHandler (TemplateRecipeWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public Class getRecipeClass () {
        return TemplateRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid (TemplateRecipe recipe) {
        return VanillaRecipeCategoryUid.CRAFTING;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper (TemplateRecipe recipe) {
        return wrapper;
    }

    @Override
    public boolean isRecipeValid (TemplateRecipe recipe) {
        return recipe.getRecipeOutput() != null;
    }
}
*/