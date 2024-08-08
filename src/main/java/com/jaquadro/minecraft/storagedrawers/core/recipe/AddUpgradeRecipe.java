package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddUpgradeRecipe extends CustomRecipe
{
    public AddUpgradeRecipe(CraftingBookCategory cat) {
        super(cat);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
        return findContext(inv, world.registryAccess()) != null;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull CraftingContainer inv, HolderLookup.Provider registries) {
        Context ctx = findContext(inv, registries);
        if (ctx == null)
            return ItemStack.EMPTY;
        ItemStack ret = ctx.drawer.copy();

        CustomData orig = ret.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        CustomData data = CustomData.of(ctx.data.write(registries, orig.copyTag()));
        ret.set(DataComponents.BLOCK_ENTITY_DATA, data);

        return ret;
    }

    private static class Context {
        ItemStack drawer = ItemStack.EMPTY;
        List<ItemStack> upgrades = new ArrayList<>();
        UpgradeData data = null;
    }

    @Nullable
    private Context findContext(CraftingContainer inv, HolderLookup.Provider registries) {
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

        CustomData custom = ret.drawer.get(DataComponents.BLOCK_ENTITY_DATA);
        if (custom != null)
            ret.data.read(registries, custom.copyTag());

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
