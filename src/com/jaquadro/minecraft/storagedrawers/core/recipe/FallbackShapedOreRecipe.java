package com.jaquadro.minecraft.storagedrawers.core.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class FallbackShapedOreRecipe extends ShapedOreRecipe
{
    public FallbackShapedOreRecipe (ResourceLocation group, Block result, Object... recipe) {
        super(group, result, recipe);
    }

    public FallbackShapedOreRecipe (ResourceLocation group, Item result, Object... recipe) {
        super(group, result, recipe);
    }

    public FallbackShapedOreRecipe (ResourceLocation group, ItemStack result, Object... recipe) {
        super(group, result, recipe);
    }
}
