package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class RecipesFood
{
    /**
     * Adds the food recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager manager)
    {
        manager.addShapelessRecipe(new ItemStack(Items.MUSHROOM_STEW), new Object[] {Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Items.BOWL});
        manager.addRecipe(new ItemStack(Items.COOKIE, 8), new Object[] {"#X#", 'X', new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage()), '#', Items.WHEAT});
        manager.addRecipe(new ItemStack(Items.RABBIT_STEW), new Object[] {" R ", "CPM", " B ", 'R', new ItemStack(Items.COOKED_RABBIT), 'C', Items.CARROT, 'P', Items.BAKED_POTATO, 'M', Blocks.BROWN_MUSHROOM, 'B', Items.BOWL});
        manager.addRecipe(new ItemStack(Items.RABBIT_STEW), new Object[] {" R ", "CPD", " B ", 'R', new ItemStack(Items.COOKED_RABBIT), 'C', Items.CARROT, 'P', Items.BAKED_POTATO, 'D', Blocks.RED_MUSHROOM, 'B', Items.BOWL});
        manager.addRecipe(new ItemStack(Blocks.MELON_BLOCK), new Object[] {"MMM", "MMM", "MMM", 'M', Items.MELON});
        manager.addRecipe(new ItemStack(Items.BEETROOT_SOUP), new Object[] {"OOO", "OOO", " B ", 'O', Items.BEETROOT, 'B', Items.BOWL});
        manager.addRecipe(new ItemStack(Items.MELON_SEEDS), new Object[] {"M", 'M', Items.MELON});
        manager.addRecipe(new ItemStack(Items.PUMPKIN_SEEDS, 4), new Object[] {"M", 'M', Blocks.PUMPKIN});
        manager.addShapelessRecipe(new ItemStack(Items.PUMPKIN_PIE), new Object[] {Blocks.PUMPKIN, Items.SUGAR, Items.EGG});
        manager.addShapelessRecipe(new ItemStack(Items.FERMENTED_SPIDER_EYE), new Object[] {Items.SPIDER_EYE, Blocks.BROWN_MUSHROOM, Items.SUGAR});
        manager.addShapelessRecipe(new ItemStack(Items.BLAZE_POWDER, 2), new Object[] {Items.BLAZE_ROD});
        manager.addShapelessRecipe(new ItemStack(Items.MAGMA_CREAM), new Object[] {Items.BLAZE_POWDER, Items.SLIME_BALL});
    }
}