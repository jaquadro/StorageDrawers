package com.jaquadro.minecraft.storagedrawers.integration.notenoughitems;

import codechicken.nei.recipe.ShapedRecipeHandler;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class UpgradeItemRecipeHandler extends ShapedRecipeHandler
{
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("item"))
            loadCraftingRecipes((ItemStack) results[0]);
    }

    @Override
    public void loadCraftingRecipes (ItemStack result) {
        ItemStack itemTemplate = new ItemStack(ModItems.upgradeTemplate);
        List<ItemStack> sticks = OreDictionary.getOres("stickWood");
        List<ItemStack> drawers = OreDictionary.getOres("drawerBasic");

        if (result.isItemEqual(itemTemplate)) {
            arecipes.add(new CachedShapedRecipe(3, 3, new Object[] {
                sticks, sticks, sticks,
                sticks, drawers, sticks,
                sticks, sticks, sticks
            }, result));
        }
    }
}
