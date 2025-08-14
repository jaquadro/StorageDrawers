package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IPortable;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.DetachedDrawerData;
import com.jaquadro.minecraft.storagedrawers.components.item.DetachedDrawerContents;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.DetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

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
        data.setStorageMultiplier(ModCommonConfig.INSTANCE.DRAWERS.getBaseStackStorage() * 32);

        ItemStack savedItem = data.getStoredItemPrototype().copyWithCount(data.getStoredItemCount());
        DetachedDrawerContents contents = new DetachedDrawerContents(savedItem, data.getStorageMultiplier(), data.isHeavy());
        stack.set(ModDataComponents.DETACHED_DRAWER_CONTENTS.get(), contents);

        // TODO: registry argh!
        //stack.setTag(data.serializeNBT());

        return stack;
    }

    @Override
    public void appendHoverText (@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        ComponentUtil.appendSplitDescription(tooltip, this);

        if (ModCommonConfig.INSTANCE.DRAWERS.detached.heavyDrawers.get() && isHeavy(context.registries(), stack)) {
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
        return ModCommonConfig.INSTANCE.DRAWERS.detached.enable.get()
            ? Component.translatable(this.getDescriptionId() + ".desc")
            : Component.translatable("itemConfig.storagedrawers.disabled_tool").withStyle(ChatFormatting.RED);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage (ItemStack stack) {
        if (stack.has(DataComponents.HIDE_TOOLTIP) || stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP))
            return Optional.empty();

        if (!stack.has(ModDataComponents.DETACHED_DRAWER_CONTENTS.get()))
            return Optional.empty();

        return Optional.ofNullable(stack.get(ModDataComponents.DETACHED_DRAWER_CONTENTS.get())).map(DetachedDrawerTooltip::new);
    }

    @Override
    public boolean canFitInsideContainerItems () {
        return ModCommonConfig.INSTANCE.DRAWERS.detached.canStoreInContainers.get();
    }

    @Override
    public boolean isHeavy(HolderLookup.Provider provider, @NotNull ItemStack stack) {
        if (stack.getItem() != this)
            return false;

        CustomData cdata = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        DetachedDrawerData data = new DetachedDrawerData(provider, cdata.copyTag());
        return data.isHeavy() && data.getStoredItemCount() > data.getStoredItemStackSize();
    }
}
