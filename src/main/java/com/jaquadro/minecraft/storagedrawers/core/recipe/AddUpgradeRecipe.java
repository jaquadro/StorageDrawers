package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddUpgradeRecipe extends CustomRecipe
{
    public AddUpgradeRecipe(ResourceLocation name) {
        super(name);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
        return findContext(inv) != null;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull CraftingContainer inv) {
        Context ctx = findContext(inv);
        if (ctx == null)
            return ItemStack.EMPTY;
        ItemStack ret = ctx.drawer.copy();
        ret.getOrCreateTag().put("tile", ctx.data.write(ret.getOrCreateTag().getCompound("tile")));
        return ret;
    }

    private static class Context {
        ItemStack drawer = ItemStack.EMPTY;
        List<ItemStack> upgrades = new ArrayList<>();
        UpgradeData data = null;
    }

    @Nullable
    private Context findContext(CraftingContainer inv) {
        Context ret = new Context();
        for (int x = 0; x < inv.getContainerSize(); x++) {
            ItemStack stack = inv.getItem(x);
            if (stack.isEmpty())
                continue;

            if (stack.getItem() instanceof ItemDrawers) {
                if (!ret.drawer.isEmpty())
                    return null;
                ret.drawer = stack;
            } else if (stack.getItem() instanceof ItemUpgrade)
                ret.upgrades.add(stack);
            else
                return null;
        }

        if (ret.drawer.isEmpty() || ret.upgrades.isEmpty())
            return null;

        ret.data = new UpgradeData(7) { //Hard coded to 7 as the only use is TileEntityDrawers$DrawerUpgradeData
            @Override
            public boolean setUpgrade(int slot, @NotNull ItemStack upgrade) { //Override this to bypass a lot of the complex logic
                if (upgrade.isEmpty())
                    return false;
                upgrade = upgrade.copy();
                upgrade.setCount(1);
                super.upgrades[slot] = upgrade;
                return true;
            }
        };

        if (ret.drawer.hasTag() && ret.drawer.getTag().contains("tile"))
            ret.data.read(ret.drawer.getTag().getCompound("tile"));

        for (ItemStack upgrade : ret.upgrades) {
            if (upgrade.getItem() == ModItems.ONE_STACK_UPGRADE.get())
                return null; //I don't want to dig into finding the stack sizes to check if we can downgrade. So just don't allow this one >.>
            if (!ret.data.hasEmptySlot() || !ret.data.canAddUpgrade(upgrade))
                return null;
            ret.data.addUpgrade(upgrade);
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
        return StorageDrawers.UPGRADE_RECIPE_SERIALIZER.get();
    }
}
