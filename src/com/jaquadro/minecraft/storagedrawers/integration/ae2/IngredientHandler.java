package com.jaquadro.minecraft.storagedrawers.integration.ae2;

import appeng.api.recipes.IIngredient;
import com.jaquadro.minecraft.storagedrawers.api.registry.IIngredientHandler;
import net.minecraft.item.ItemStack;

public class IngredientHandler implements IIngredientHandler
{
    @Override
    public ItemStack getItemStack (Object object) {
        if (!(object instanceof IIngredient))
            return null;

        try {
            IIngredient ingredient = (IIngredient) object;
            ItemStack[] set = ingredient.getItemStackSet();
            if (set == null || set.length == 0)
                return null;

            return set[0];
        }
        catch (Throwable t) {
            return null;
        }
    }
}
