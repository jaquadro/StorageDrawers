package com.jaquadro.minecraft.storagedrawers.integration.ae2;

import com.jaquadro.minecraft.storagedrawers.api.registry.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;

import java.lang.reflect.Method;
import java.util.List;

public class ShapedRecipeHandler // implements IRecipeHandler
{
    /*private Class classShapedRecipe;
    private Method methodGetInput;

    private boolean valid = true;

    public ShapedRecipeHandler () {
        try {
            classShapedRecipe = Class.forName("appeng.recipes.game.ShapedRecipe");
            methodGetInput = classShapedRecipe.getMethod("getInput");

            if (!methodGetInput.getReturnType().isArray())
                valid = false;
        }
        catch (Throwable t) {
            valid = false;
        }
    }

    public Class getRecipeClass () {
        return classShapedRecipe;
    }

    public boolean isValid () {
        return valid;
    }

    @Override
    public Object[] getInputAsArray (IRecipe recipe) {
        try {
            return (Object[]) methodGetInput.invoke(recipe);
        }
        catch (Throwable t) {
            return null;
        }
    }

    @Override
    public List getInputAsList (IRecipe recipe) {
        return null;
    }*/
}
