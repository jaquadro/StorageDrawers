package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.DetachedDrawerData;
import com.jaquadro.minecraft.storagedrawers.components.item.DetachedDrawerContents;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.ModRecipes;
import com.jaquadro.minecraft.storagedrawers.item.ItemDetachedDrawer;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UpgradeDetachedDrawerRecipe extends CustomRecipe
{
    public UpgradeDetachedDrawerRecipe (CraftingBookCategory cat) {
        super(cat);
    }

    @Override
    public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
        return findContext(inv) != null;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull CraftingInput inv, HolderLookup.Provider access) {
        Context ctx = findContext(inv);
        if (ctx == null)
            return ItemStack.EMPTY;

        ItemStack ret = ctx.drawer.copy();
        CustomData cdata = ret.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        DetachedDrawerData data = new DetachedDrawerData(access, cdata.copyTag());
        int cap = data.getStorageMultiplier();

        if (ctx.upgrades.isEmpty()) {
            ret = ModItems.DETACHED_DRAWER.get().getDefaultInstance();
            data = new DetachedDrawerData();
            data.setStorageMultiplier(cap);
        } else {
            int addedCap = ctx.storageMult * ModCommonConfig.INSTANCE.DRAWERS.baseStackStorage.get()
                * ModCommonConfig.INSTANCE.DRAWERS.fullDrawers1x1.unitsPerSlot.get();
            data.setStorageMultiplier(data.getStorageMultiplier() + addedCap);
        }

        // TODO: Move away from CUSTOM_DATA
        ret.set(DataComponents.CUSTOM_DATA, CustomData.of(data.serializeNBT(access)));

        ItemStack savedItem = data.getStoredItemPrototype().copyWithCount(data.getStoredItemCount());
        DetachedDrawerContents contents = new DetachedDrawerContents(savedItem, cap, data.isHeavy());
        ret.set(ModDataComponents.DETACHED_DRAWER_CONTENTS.get(), contents);

        return ret;
    }

    private static class Context {
        ItemStack drawer = ItemStack.EMPTY;
        List<ItemStack> upgrades = new ArrayList<>();
        int storageMult = 0;
    }

    @Nullable
    private Context findContext(CraftingInput inv) {
        Context ret = new Context();
        for (int x = 0; x < inv.size(); x++) {
            ItemStack stack = inv.getItem(x);
            if (stack.isEmpty())
                continue;

            if (stack.getItem() instanceof ItemDetachedDrawer) {
                if (!ret.drawer.isEmpty())
                    return null;
                ret.drawer = stack;
            } else if (stack.getItem() instanceof ItemUpgradeStorage)
                ret.upgrades.add(stack);
            else
                return null;
        }

        if (ret.drawer.isEmpty())
            return null;

        for (ItemStack upgrade : ret.upgrades) {
            if (upgrade.getItem() instanceof ItemUpgradeStorage storageUpgrade)
                ret.storageMult += ModCommonConfig.INSTANCE.UPGRADES.getLevelMult(storageUpgrade.level.getLevel());
        }

        return ret;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DETACHED_UPGRADE_RECIPE_SERIALIZER.get();
    }
}
