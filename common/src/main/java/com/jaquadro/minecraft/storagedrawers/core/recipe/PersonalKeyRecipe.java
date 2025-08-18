package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.ModRecipes;
import com.jaquadro.minecraft.storagedrawers.core.ModSecurity;
import com.jaquadro.minecraft.storagedrawers.item.ItemPersonalKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class PersonalKeyRecipe extends CustomRecipe
{
    public PersonalKeyRecipe (CraftingBookCategory cat) {
        super(cat);
    }

    @Override
    public boolean matches (CraftingInput craftingInput, Level level) {
        ItemStack pkey = findPersonalKey(craftingInput);
        return !pkey.isEmpty();
    }

    private ItemStack findPersonalKey (CraftingInput craftingInput) {
        ItemStack pkey = ItemStack.EMPTY;
        for (int i = 0; i < craftingInput.size(); i++) {
            ItemStack item = craftingInput.getItem(i);
            if (item == ItemStack.EMPTY)
                continue;
            if (!pkey.isEmpty())
                return ItemStack.EMPTY;

            if (item.getItem() instanceof ItemPersonalKey pitem) {
                if (checkPersonalKey(pitem)) {
                    pkey = item;
                    continue;
                }
            }

            return ItemStack.EMPTY;
        }

        return pkey;
    }

    private boolean checkPersonalKey (ItemPersonalKey item) {
        String provider = item.getSecurityProviderKey();
        if (provider == null)
            provider = "default";

        if (provider.equals("default"))
            return true;
        if (provider.equals("ftb") && ftbEnabled())
            return true;

        return false;
    }

    private boolean ftbEnabled () {
        return ModSecurity.registry.getProvider("ftb") != null
            && ModCommonConfig.INSTANCE.INTEGRATION.ftbTeams.enableCycleRecipe.get();
    }

    @Override
    public ItemStack assemble (CraftingInput inv, HolderLookup.Provider registries) {
        ItemStack pkey = findPersonalKey(inv);

        List<Item> cycle = new ArrayList<>();
        cycle.add(ModItems.PERSONAL_KEY.get());
        if (ftbEnabled())
            cycle.add(ModItems.PERSONAL_KEY_FTB.get());

        int index = cycle.indexOf(pkey.getItem());
        if (index == -1)
            return ItemStack.EMPTY;

        index += 1;
        if (index >= cycle.size())
            index = 0;

        return new ItemStack(cycle.get(index), 1);
    }

    @Override
    public boolean canCraftInDimensions (int i, int i1) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer () {
        return ModRecipes.PERSONAL_KEY_RECIPE_SERIALIZER.get();
    }
}
