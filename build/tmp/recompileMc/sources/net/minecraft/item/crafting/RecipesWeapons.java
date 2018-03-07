package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesWeapons
{
    private final String[][] recipePatterns = new String[][] {{"X", "X", "#"}};
    private final Object[][] recipeItems = new Object[][] {{Blocks.PLANKS, Blocks.COBBLESTONE, Items.IRON_INGOT, Items.DIAMOND, Items.GOLD_INGOT}, {Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.GOLDEN_SWORD}};

    /**
     * Adds the weapon recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager manager)
    {
        for (int i = 0; i < this.recipeItems[0].length; ++i)
        {
            Object object = this.recipeItems[0][i];

            for (int j = 0; j < this.recipeItems.length - 1; ++j)
            {
                Item item = (Item)this.recipeItems[j + 1][i];
                manager.addRecipe(new ItemStack(item), new Object[] {this.recipePatterns[j], '#', Items.STICK, 'X', object});
            }
        }

        manager.addRecipe(new ItemStack(Items.BOW, 1), new Object[] {" #X", "# X", " #X", 'X', Items.STRING, '#', Items.STICK});
        manager.addRecipe(new ItemStack(Items.ARROW, 4), new Object[] {"X", "#", "Y", 'Y', Items.FEATHER, 'X', Items.FLINT, '#', Items.STICK});
        manager.addRecipe(new ItemStack(Items.SPECTRAL_ARROW, 2), new Object[] {" # ", "#X#", " # ", 'X', Items.ARROW, '#', Items.GLOWSTONE_DUST});
    }
}