package com.jaquadro.minecraft.storagedrawers.integration.ae2;

import com.jaquadro.minecraft.storagedrawers.api.registry.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;

import java.lang.reflect.Method;
import java.util.List;

public class ShapelessRecipeHandler implements IRecipeHandler
{
    private Class classShapelessRecipe;
    private Method methodGetInput;

    private boolean valid = true;

    public ShapelessRecipeHandler () {
        try {
            classShapelessRecipe = Class.forName("appeng.recipes.game.ShapelessRecipe");
            methodGetInput = classShapelessRecipe.getMethod("getInput");

            if (!List.class.isAssignableFrom(methodGetInput.getReturnType()))
                valid = false;
        }
        catch (Throwable t) {
            valid = false;
        }
    }

    public Class getRecipeClass () {
        return classShapelessRecipe;
    }

    public boolean isValid () {
        return valid;
    }

    @Override
    public Object[] getInputAsArray (IRecipe recipe) {
        return null;
    }

    @Override
    public List getInputAsList (IRecipe recipe) {
        try {
            return (List) methodGetInput.invoke(recipe);
        }
        catch (Throwable t) {
            return null;
        }
    }
}
