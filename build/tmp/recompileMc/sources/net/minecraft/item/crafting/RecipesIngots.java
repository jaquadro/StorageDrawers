package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class RecipesIngots
{
    private final Object[][] recipeItems = new Object[][] {{Blocks.GOLD_BLOCK, new ItemStack(Items.GOLD_INGOT, 9)}, {Blocks.IRON_BLOCK, new ItemStack(Items.IRON_INGOT, 9)}, {Blocks.DIAMOND_BLOCK, new ItemStack(Items.DIAMOND, 9)}, {Blocks.EMERALD_BLOCK, new ItemStack(Items.EMERALD, 9)}, {Blocks.LAPIS_BLOCK, new ItemStack(Items.DYE, 9, EnumDyeColor.BLUE.getDyeDamage())}, {Blocks.REDSTONE_BLOCK, new ItemStack(Items.REDSTONE, 9)}, {Blocks.COAL_BLOCK, new ItemStack(Items.COAL, 9, 0)}, {Blocks.HAY_BLOCK, new ItemStack(Items.WHEAT, 9)}, {Blocks.SLIME_BLOCK, new ItemStack(Items.SLIME_BALL, 9)}};

    /**
     * Adds the ingot recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager manager)
    {
        for (Object[] aobject : this.recipeItems)
        {
            Block block = (Block)aobject[0];
            ItemStack itemstack = (ItemStack)aobject[1];
            manager.addRecipe(new ItemStack(block), new Object[] {"###", "###", "###", '#', itemstack});
            manager.addRecipe(itemstack, new Object[] {"#", '#', block});
        }

        manager.addRecipe(new ItemStack(Items.GOLD_INGOT), new Object[] {"###", "###", "###", '#', Items.GOLD_NUGGET});
        manager.addRecipe(new ItemStack(Items.GOLD_NUGGET, 9), new Object[] {"#", '#', Items.GOLD_INGOT});
    }
}