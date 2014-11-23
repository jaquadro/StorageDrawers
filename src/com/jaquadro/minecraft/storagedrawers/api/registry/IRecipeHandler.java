package com.jaquadro.minecraft.storagedrawers.api.registry;

import net.minecraft.item.crafting.IRecipe;

import java.util.List;

public interface IRecipeHandler
{
    Object[] getInputAsArray (IRecipe recipe);

    List getInputAsList (IRecipe recipe);
}
