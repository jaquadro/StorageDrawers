/*package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.chameleon.integration.IntegrationModule;
import com.jaquadro.minecraft.storagedrawers.core.recipe.TemplateRecipe;
import com.jaquadro.minecraft.storagedrawers.integration.jei.TemplateRecipeHandler;
import com.jaquadro.minecraft.storagedrawers.integration.jei.TemplateRecipeWrapper;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashSet;
import java.util.Set;

@JeiPlugin
public class JustEnoughItems extends IntegrationModule implements IModPlugin
{
    private static final String MOD_ID = "JEI";
    private static Set<ItemStack> pendingHide = new HashSet<ItemStack>();
    //private static IItemBlacklist blacklist;

    @Override
    public String getModID () {
        return MOD_ID;
    }

    @Override
    public void init () throws Throwable {

    }

    @Override
    public void postInit () {

    }

    @Override
    public ResourceLocation getPluginUid () {
        return null;
    }

    @Override
    public void registerRecipes (IRecipeRegistration registration) {
        TemplateRecipeWrapper templateWrapper = new TemplateRecipeWrapper(new TemplateRecipe(), registry.getJeiHelpers());
        TemplateRecipeHandler templateHandler = new TemplateRecipeHandler(templateWrapper);

        registry.addRecipeHandlers(templateHandler);

        blacklist = registry.getJeiHelpers().getItemBlacklist();
        for (ItemStack stack : pendingHide)
            blacklist.addItemToBlacklist(stack);

        pendingHide.clear();
    }

    private static void hideItem (ItemStack stack) {
        blacklist.addItemToBlacklist(stack);
    }

    public static void hideBlock (String blockResource) {
        Block block = Block.getBlockFromName(blockResource);
        if (block != null) {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(block), 1, OreDictionary.WILDCARD_VALUE);
            if (blacklist != null)
                hideItem(stack);
            else
                pendingHide.add(stack);
        }
    }
}
*/