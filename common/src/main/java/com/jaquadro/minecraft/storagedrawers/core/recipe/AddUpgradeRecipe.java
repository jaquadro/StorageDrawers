package com.jaquadro.minecraft.storagedrawers.core.recipe;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.ModRecipes;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddUpgradeRecipe extends CustomRecipe
{
    public static final MapCodec<AddUpgradeRecipe> MAP_CODEC = MapCodec.unit(AddUpgradeRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, AddUpgradeRecipe> STREAM_CODEC = StreamCodec.of((buf, recipe) -> {}, buf -> new AddUpgradeRecipe());

    private HolderLookup.Provider capturedRegistries;

    public AddUpgradeRecipe () {
        super();
    }

    @Override
    public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
        capturedRegistries = world.registryAccess();
        return findContext(inv, world.registryAccess()) != null;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull CraftingInput inv) {
        HolderLookup.Provider registries = capturedRegistries;
        Context ctx = findContext(inv, registries);
        if (ctx == null)
            return ItemStack.EMPTY;
        ItemStack ret = ctx.drawer.copy();

        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        ctx.data.write(output);

        TypedEntityData<BlockEntityType<?>> blockData = ret.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockData != null) {
            CompoundTag data = blockData.copyTagWithoutId().merge(output.buildResult());
            ret.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(blockData.type(), data));

            return ret;
        }

        CustomData upgradeData = ret.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CustomData data = CustomData.of(upgradeData.copyTag().merge(output.buildResult()));
        ret.set(DataComponents.CUSTOM_DATA, data);

        return ret;
    }

    private static class Context {
        ItemStack drawer = ItemStack.EMPTY;
        List<ItemStack> upgrades = new ArrayList<>();
        UpgradeData data = null;
    }

    @Nullable
    private Context findContext(CraftingInput inv, HolderLookup.Provider registries) {
        Context ret = new Context();
        for (int x = 0; x < inv.size(); x++) {
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

        TypedEntityData<BlockEntityType<?>> blocKEntityData = ret.drawer.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blocKEntityData != null)
            ret.data.read(TagValueInput.create(ProblemReporter.DISCARDING, registries, blocKEntityData.copyTagWithoutId()));
        else {
            CustomData customData = ret.drawer.get(DataComponents.CUSTOM_DATA);
            if (customData != null)
                ret.data.read(TagValueInput.create(ProblemReporter.DISCARDING, registries, customData.copyTag()));
        }

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
    @NotNull
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipes.UPGRADE_RECIPE_SERIALIZER.get();
    }
}
