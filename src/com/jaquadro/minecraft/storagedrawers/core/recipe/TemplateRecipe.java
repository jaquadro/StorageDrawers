package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.List;

public class TemplateRecipe implements IRecipe
{
    private final Object[] input;

    public TemplateRecipe () {
        List<ItemStack> stick = OreDictionary.getOres("stickWood");
        List<ItemStack> drawer = OreDictionary.getOres("drawerBasic");

        input = new Object[] {
            stick, stick, stick,
            stick, drawer, stick,
            stick, stick, stick
        };
    }

    @Override
    public boolean matches (InventoryCrafting inventory, World world) {
        return !getCraftingResult(inventory).isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult (InventoryCrafting inventory) {
        List<ItemStack> sticks = OreDictionary.getOres("stickWood");
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row == 1 && col == 1)
                    continue;

                ItemStack stack = inventory.getStackInRowAndColumn(col, row);
                if (stack.isEmpty())
                    return ItemStack.EMPTY;

                boolean match = false;
                for (ItemStack comp : sticks) {
                    if (comp != null && comp.isItemEqual(stack))
                        match = true;
                }

                if (!match)
                    return ItemStack.EMPTY;
            }
        }

        ItemStack center = inventory.getStackInRowAndColumn(1, 1);
        if (center.isEmpty() || !(center.getItem() instanceof ItemDrawers))
            return ItemStack.EMPTY;

        if (center.getTagCompound() != null && center.getTagCompound().hasKey("tile"))
            return ItemStack.EMPTY;

        return getRecipeOutput();
    }

    @Override
    public int getRecipeSize () {
        return 9;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput () {
        return new ItemStack(ModItems.upgradeTemplate, 2);
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRemainingItems (InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

    public Object[] getInput () {
        return input;
    }
}
