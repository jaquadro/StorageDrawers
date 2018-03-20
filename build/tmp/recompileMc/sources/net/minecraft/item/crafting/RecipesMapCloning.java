package net.minecraft.item.crafting;

import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesMapCloning implements IRecipe
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inv.getSizeInventory(); ++j)
        {
            ItemStack itemstack1 = inv.getStackInSlot(j);

            if (itemstack1 != null)
            {
                if (itemstack1.getItem() == Items.FILLED_MAP)
                {
                    if (itemstack != null)
                    {
                        return false;
                    }

                    itemstack = itemstack1;
                }
                else
                {
                    if (itemstack1.getItem() != Items.MAP)
                    {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return itemstack != null && i > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Nullable
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inv.getSizeInventory(); ++j)
        {
            ItemStack itemstack1 = inv.getStackInSlot(j);

            if (itemstack1 != null)
            {
                if (itemstack1.getItem() == Items.FILLED_MAP)
                {
                    if (itemstack != null)
                    {
                        return null;
                    }

                    itemstack = itemstack1;
                }
                else
                {
                    if (itemstack1.getItem() != Items.MAP)
                    {
                        return null;
                    }

                    ++i;
                }
            }
        }

        if (itemstack != null && i >= 1)
        {
            ItemStack itemstack2 = new ItemStack(Items.FILLED_MAP, i + 1, itemstack.getMetadata());

            if (itemstack.hasDisplayName())
            {
                itemstack2.setStackDisplayName(itemstack.getDisplayName());
            }

            return itemstack2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 9;
    }

    @Nullable
    public ItemStack getRecipeOutput()
    {
        return null;
    }

    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        return aitemstack;
    }
}