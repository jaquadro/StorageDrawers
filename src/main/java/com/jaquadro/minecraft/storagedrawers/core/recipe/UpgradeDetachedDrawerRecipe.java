package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.DetachedDrawerData;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemDetachedDrawer;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UpgradeDetachedDrawerRecipe extends CustomRecipe
{
    public UpgradeDetachedDrawerRecipe (ResourceLocation name, CraftingBookCategory cat) {
        super(name, cat);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
        return findContext(inv) != null;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull CraftingContainer inv, RegistryAccess access) {
        Context ctx = findContext(inv);
        if (ctx == null)
            return ItemStack.EMPTY;

        ItemStack ret = ctx.drawer.copy();
        DetachedDrawerData data = new DetachedDrawerData(ret.getOrCreateTag());
        int cap = data.getStorageMultiplier();

        if (ctx.upgrades.isEmpty()) {
            ret = ModItems.DETACHED_DRAWER.get().getDefaultInstance();
            data = new DetachedDrawerData();
            data.setStorageMultiplier(cap);
        } else {
            int addedCap = ctx.storageMult * CommonConfig.GENERAL.baseStackStorage.get() * 8;
            data.setStorageMultiplier(data.getStorageMultiplier() + addedCap);
        }

        ret.setTag(data.serializeNBT());
        return ret;
    }

    private static class Context {
        ItemStack drawer = ItemStack.EMPTY;
        List<ItemStack> upgrades = new ArrayList<>();
        int storageMult = 0;
    }

    @Nullable
    private Context findContext(CraftingContainer inv) {
        Context ret = new Context();
        for (int x = 0; x < inv.getContainerSize(); x++) {
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
                ret.storageMult += CommonConfig.UPGRADES.getLevelMult(storageUpgrade.level.getLevel());
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
        return StorageDrawers.DETACHED_UPGRADE_RECIPE_SERIALIZER.get();
    }
}
