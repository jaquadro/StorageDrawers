package com.jaquadro.minecraft.storagedrawers.core.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class FallbackShapedOreRecipe extends ShapedOreRecipe
{
    public FallbackShapedOreRecipe (Block result, Object... recipe) {
        super(result, recipe);
    }

    public FallbackShapedOreRecipe (Item result, Object... recipe) {
        super(result, recipe);
    }

    public FallbackShapedOreRecipe (ItemStack result, Object... recipe) {
        super(result, recipe);
    }
}
