package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.ModRecipes;
import com.jaquadro.minecraft.storagedrawers.core.ModSecurity;
import com.jaquadro.minecraft.storagedrawers.item.ItemPersonalKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PersonalKeyRecipe extends CustomRecipe
{
    public PersonalKeyRecipe (ResourceLocation name, CraftingBookCategory cat) {
        super(name, cat);
    }

    @Override
    public boolean matches (@NotNull CraftingContainer inv, @NotNull Level world) {
        ItemStack pkey = findPersonalKey(inv);
        return !pkey.isEmpty();
    }

    private ItemStack findPersonalKey (CraftingContainer craftingInput) {
        ItemStack pkey = ItemStack.EMPTY;
        for (int i = 0; i < craftingInput.getContainerSize(); i++) {
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
    public ItemStack assemble (CraftingContainer inv, RegistryAccess registries) {
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
