package net.minecraft.item.crafting;

import javax.annotation.Nullable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.world.World;

public class RecipesBanners
{
    /**
     * Adds the banner recipes to the CraftingManager.
     */
    void addRecipes(CraftingManager manager)
    {
        for (EnumDyeColor enumdyecolor : EnumDyeColor.values())
        {
            manager.addRecipe(new ItemStack(Items.BANNER, 1, enumdyecolor.getDyeDamage()), new Object[] {"###", "###", " | ", '#', new ItemStack(Blocks.WOOL, 1, enumdyecolor.getMetadata()), '|', Items.STICK});
        }

        manager.addRecipe(new RecipesBanners.RecipeDuplicatePattern());
        manager.addRecipe(new RecipesBanners.RecipeAddPattern());
    }

    public static class RecipeAddPattern implements IRecipe
        {
            private RecipeAddPattern()
            {
            }

            /**
             * Used to check if a recipe matches current crafting inventory
             */
            public boolean matches(InventoryCrafting inv, World worldIn)
            {
                boolean flag = false;

                for (int i = 0; i < inv.getSizeInventory(); ++i)
                {
                    ItemStack itemstack = inv.getStackInSlot(i);

                    if (itemstack != null && itemstack.getItem() == Items.BANNER)
                    {
                        if (flag)
                        {
                            return false;
                        }

                        if (TileEntityBanner.getPatterns(itemstack) >= 6)
                        {
                            return false;
                        }

                        flag = true;
                    }
                }

                if (!flag)
                {
                    return false;
                }
                else
                {
                    return this.matchPatterns(inv) != null;
                }
            }

            /**
             * Returns an Item that is the result of this recipe
             */
            @Nullable
            public ItemStack getCraftingResult(InventoryCrafting inv)
            {
                ItemStack itemstack = null;

                for (int i = 0; i < inv.getSizeInventory(); ++i)
                {
                    ItemStack itemstack1 = inv.getStackInSlot(i);

                    if (itemstack1 != null && itemstack1.getItem() == Items.BANNER)
                    {
                        itemstack = itemstack1.copy();
                        itemstack.stackSize = 1;
                        break;
                    }
                }

                TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern = this.matchPatterns(inv);

                if (tileentitybanner$enumbannerpattern != null)
                {
                    int k = 0;

                    for (int j = 0; j < inv.getSizeInventory(); ++j)
                    {
                        ItemStack itemstack2 = inv.getStackInSlot(j);

                        int color = getColor(itemstack2);
                        if (color != -1)
                        {
                            k = color;
                            break;
                        }
                    }

                    NBTTagCompound nbttagcompound1 = itemstack.getSubCompound("BlockEntityTag", true);
                    NBTTagList nbttaglist;

                    if (nbttagcompound1.hasKey("Patterns", 9))
                    {
                        nbttaglist = nbttagcompound1.getTagList("Patterns", 10);
                    }
                    else
                    {
                        nbttaglist = new NBTTagList();
                        nbttagcompound1.setTag("Patterns", nbttaglist);
                    }

                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.setString("Pattern", tileentitybanner$enumbannerpattern.getPatternID());
                    nbttagcompound.setInteger("Color", k);
                    nbttaglist.appendTag(nbttagcompound);
                }

                return itemstack;
            }

            /**
             * Returns the size of the recipe area
             */
            public int getRecipeSize()
            {
                return 10;
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

            @Nullable
            private TileEntityBanner.EnumBannerPattern matchPatterns(InventoryCrafting invCrafting)
            {
                for (TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern : TileEntityBanner.EnumBannerPattern.values())
                {
                    if (tileentitybanner$enumbannerpattern.hasValidCrafting())
                    {
                        boolean flag = true;

                        if (tileentitybanner$enumbannerpattern.hasCraftingStack())
                        {
                            boolean flag1 = false;
                            boolean flag2 = false;

                            for (int i = 0; i < invCrafting.getSizeInventory() && flag; ++i)
                            {
                                ItemStack itemstack = invCrafting.getStackInSlot(i);

                                if (itemstack != null && itemstack.getItem() != Items.BANNER)
                                {
                                    if (isDye(itemstack))
                                    {
                                        if (flag2)
                                        {
                                            flag = false;
                                            break;
                                        }

                                        flag2 = true;
                                    }
                                    else
                                    {
                                        if (flag1 || !itemstack.isItemEqual(tileentitybanner$enumbannerpattern.getCraftingStack()))
                                        {
                                            flag = false;
                                            break;
                                        }

                                        flag1 = true;
                                    }
                                }
                            }

                            if (!flag1)
                            {
                                flag = false;
                            }
                        }
                        else if (invCrafting.getSizeInventory() == tileentitybanner$enumbannerpattern.getCraftingLayers().length * tileentitybanner$enumbannerpattern.getCraftingLayers()[0].length())
                        {
                            int j = -1;

                            for (int k = 0; k < invCrafting.getSizeInventory() && flag; ++k)
                            {
                                int l = k / 3;
                                int i1 = k % 3;
                                ItemStack itemstack1 = invCrafting.getStackInSlot(k);

                                if (itemstack1 != null && itemstack1.getItem() != Items.BANNER)
                                {
                                    if (!isDye(itemstack1))
                                    {
                                        flag = false;
                                        break;
                                    }

                                    if (j != -1 && j != itemstack1.getMetadata())
                                    {
                                        flag = false;
                                        break;
                                    }

                                    if (tileentitybanner$enumbannerpattern.getCraftingLayers()[l].charAt(i1) == 32)
                                    {
                                        flag = false;
                                        break;
                                    }

                                    j = itemstack1.getMetadata();
                                }
                                else if (tileentitybanner$enumbannerpattern.getCraftingLayers()[l].charAt(i1) != 32)
                                {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            flag = false;
                        }

                        if (flag)
                        {
                            return tileentitybanner$enumbannerpattern;
                        }
                    }
                }

                return null;
            }

            private static String[] colors = { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White" };
            @SuppressWarnings("unchecked") //Why java...
            private static java.util.List<ItemStack>[] colored = new java.util.List[colors.length];
            private static java.util.List<ItemStack> dyes;
            private static boolean hasInit = false;
            private static void init()
            {
                if (hasInit) return;
                for (int x = 0; x < colors.length; x++)
                    colored[x] = net.minecraftforge.oredict.OreDictionary.getOres("dye" + colors[x]);
                dyes = net.minecraftforge.oredict.OreDictionary.getOres("dye");
                hasInit = true;
            }
            private boolean isDye(ItemStack stack)
            {
                init();
                for (ItemStack ore : dyes)
                    if (net.minecraftforge.oredict.OreDictionary.itemMatches(ore, stack, false))
                        return true;
                return false;
            }
            private int getColor(ItemStack stack)
            {
                init();
                if (stack == null) return -1;
                for (int x = 0; x < colored.length; x++)
                    for (ItemStack ore : colored[x])
                        if (net.minecraftforge.oredict.OreDictionary.itemMatches(ore, stack, true))
                            return x;
                return -1;
            }
        }

    public static class RecipeDuplicatePattern implements IRecipe
        {
            private RecipeDuplicatePattern()
            {
            }

            /**
             * Used to check if a recipe matches current crafting inventory
             */
            public boolean matches(InventoryCrafting inv, World worldIn)
            {
                ItemStack itemstack = null;
                ItemStack itemstack1 = null;

                for (int i = 0; i < inv.getSizeInventory(); ++i)
                {
                    ItemStack itemstack2 = inv.getStackInSlot(i);

                    if (itemstack2 != null)
                    {
                        if (itemstack2.getItem() != Items.BANNER)
                        {
                            return false;
                        }

                        if (itemstack != null && itemstack1 != null)
                        {
                            return false;
                        }

                        int j = TileEntityBanner.getBaseColor(itemstack2);
                        boolean flag = TileEntityBanner.getPatterns(itemstack2) > 0;

                        if (itemstack != null)
                        {
                            if (flag)
                            {
                                return false;
                            }

                            if (j != TileEntityBanner.getBaseColor(itemstack))
                            {
                                return false;
                            }

                            itemstack1 = itemstack2;
                        }
                        else if (itemstack1 != null)
                        {
                            if (!flag)
                            {
                                return false;
                            }

                            if (j != TileEntityBanner.getBaseColor(itemstack1))
                            {
                                return false;
                            }

                            itemstack = itemstack2;
                        }
                        else if (flag)
                        {
                            itemstack = itemstack2;
                        }
                        else
                        {
                            itemstack1 = itemstack2;
                        }
                    }
                }

                return itemstack != null && itemstack1 != null;
            }

            /**
             * Returns an Item that is the result of this recipe
             */
            @Nullable
            public ItemStack getCraftingResult(InventoryCrafting inv)
            {
                for (int i = 0; i < inv.getSizeInventory(); ++i)
                {
                    ItemStack itemstack = inv.getStackInSlot(i);

                    if (itemstack != null && TileEntityBanner.getPatterns(itemstack) > 0)
                    {
                        ItemStack itemstack1 = itemstack.copy();
                        itemstack1.stackSize = 1;
                        return itemstack1;
                    }
                }

                return null;
            }

            /**
             * Returns the size of the recipe area
             */
            public int getRecipeSize()
            {
                return 2;
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

                    if (itemstack != null)
                    {
                        if (itemstack.getItem().hasContainerItem(itemstack))
                        {
                            aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
                        }
                        else if (itemstack.hasTagCompound() && TileEntityBanner.getPatterns(itemstack) > 0)
                        {
                            aitemstack[i] = itemstack.copy();
                            aitemstack[i].stackSize = 1;
                        }
                    }
                }

                return aitemstack;
            }
        }
}