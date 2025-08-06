package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IPortable;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.DetachedDrawerData;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.DetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ItemDetachedDrawer extends Item implements IPortable
{
    public ItemDetachedDrawer (Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance () {
        ItemStack stack = new ItemStack(this);

        DetachedDrawerData data = new DetachedDrawerData();
        data.setStorageMultiplier(ModCommonConfig.INSTANCE.GENERAL.baseStackStorage.get() * 8);
        stack.setTag(data.serializeNBT());

        return stack;
    }

    @Override
    public void appendHoverText (@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ComponentUtil.appendSplitDescription(tooltip, this);

        if (ModCommonConfig.INSTANCE.GENERAL.heavyDrawers.get() && isHeavy(stack)) {
            tooltip.add(Component.translatable("tooltip.storagedrawers.drawers.too_heavy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public String getDescriptionId () {
        if (this == ModItems.DETACHED_DRAWER.get())
            return super.getDescriptionId();

        return ModItems.DETACHED_DRAWER.get().getDescriptionId();
    }

    @NotNull
    public Component getDescription() {
        return ModCommonConfig.INSTANCE.GENERAL.enableDetachedDrawers.get()
            ? Component.translatable(this.getDescriptionId() + ".desc")
            : Component.translatable("itemConfig.storagedrawers.disabled_tool").withStyle(ChatFormatting.RED);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage (ItemStack stack) {
        DetachedDrawerData data = new DetachedDrawerData(stack.getOrCreateTag());
        ItemStack innerStack = data.getStoredItemPrototype().copy();
        innerStack.setCount(data.getStoredItemCount());
        return Optional.of(new DetachedDrawerTooltip(data, innerStack, data.getStorageMultiplier()));
    }

    @Override
    public boolean canFitInsideContainerItems () {
        return false;
    }

    @Override
    public boolean isHeavy(@NotNull ItemStack stack) {
        if (stack.getItem() != this)
            return false;

        DetachedDrawerData data = new DetachedDrawerData(stack.getTag());
        return data.isHeavy() && data.getStoredItemCount() > data.getStoredItemStackSize();
    }
}
