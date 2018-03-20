package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipeRepairItem implements IRecipe
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null)
            {
                list.add(itemstack);

                if (list.size() > 1)
                {
                    ItemStack itemstack1 = (ItemStack)list.get(0);

                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.stackSize != 1 || itemstack.stackSize != 1 || !itemstack1.getItem().isRepairable())
                    {
                        return false;
                    }
                }
            }
        }

        return list.size() == 2;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Nullable
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null)
            {
                list.add(itemstack);

                if (list.size() > 1)
                {
                    ItemStack itemstack1 = (ItemStack)list.get(0);

                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.stackSize != 1 || itemstack.stackSize != 1 || !itemstack1.getItem().isRepairable())
                    {
                        return null;
                    }
                }
            }
        }

        if (list.size() == 2)
        {
            ItemStack itemstack2 = (ItemStack)list.get(0);
            ItemStack itemstack3 = (ItemStack)list.get(1);

            if (itemstack2.getItem() == itemstack3.getItem() && itemstack2.stackSize == 1 && itemstack3.stackSize == 1 && itemstack2.getItem().isRepairable())
            {
                // FORGE: Make itemstack sensitive // Item item = itemstack2.getItem();
                int j = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
                int k = itemstack2.getMaxDamage() - itemstack3.getItemDamage();
                int l = j + k + itemstack2.getMaxDamage() * 5 / 100;
                int i1 = itemstack2.getMaxDamage() - l;

                if (i1 < 0)
                {
                    i1 = 0;
                }

                return new ItemStack(itemstack2.getItem(), 1, i1);
            }
        }

        return null;
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 4;
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