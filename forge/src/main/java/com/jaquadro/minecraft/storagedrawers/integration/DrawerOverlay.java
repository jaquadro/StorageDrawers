package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DrawerOverlay {
    public boolean showContent = true;
    public boolean showStackLimit = true;
    public boolean showStatus = true;
    public boolean showStackRemainder = CommonConfig.INTEGRATION.wailaStackRemainder.get();
    public boolean respectQuantifyKey = CommonConfig.INTEGRATION.wailaRespectQuantifyKey.get();

    public List<Component> getOverlay(final BlockEntityDrawers tile) {
        final List<Component> result = new ArrayList<>();

        final IDrawerAttributes attr = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY, null).orElse(EmptyDrawerAttributes.EMPTY);
        addContent(result, tile, attr);
        addStackLimit(result, tile, attr);
        addStatus(result, tile, attr);

        return result;
    }

    private void addContent(final List<Component> result, final BlockEntityDrawers tile, final IDrawerAttributes attr) {
        if (!this.showContent || attr.isConcealed()) return;
        final boolean showCounts = !this.respectQuantifyKey || attr.isShowingQuantity();

        final IDrawerGroup group = tile.getGroup();
        for (int i = 0; i < group.getDrawerCount(); i++) {
            final IDrawer drawer = group.getDrawer(i);
            if (!drawer.isEnabled())
                continue;

            Component name = Component.translatable("tooltip.storagedrawers.waila.empty");

            final ItemStack stack = drawer.getStoredItemPrototype();
            if (!stack.isEmpty()) {
                final MutableComponent stackName = Component.translatable("").append(stack.getDisplayName());

                if (showCounts) {
                    if (drawer.getStoredItemCount() == Integer.MAX_VALUE) {
                        name = stackName.append("[\u221E]");
                    } else if (drawer instanceof IFractionalDrawer && ((IFractionalDrawer) drawer).getConversionRate() > 1) {
                        final String text = ((i == 0) ? " [" : " [+") + ((IFractionalDrawer) drawer).getStoredItemRemainder() + "]";
                        name = stackName.append(text);
                    } else if (this.showStackRemainder) {
                        final int stacks = drawer.getStoredItemCount() / drawer.getStoredItemStackSize();
                        final int remainder = drawer.getStoredItemCount() - (stacks * drawer.getStoredItemStackSize());
                        if (stacks > 0 && remainder > 0)
                            name = stackName.append(" [" + stacks + "x" + drawer.getStoredItemStackSize() + " + " + remainder + "]");
                        else if (stacks > 0)
                            name = stackName.append(" [" + stacks + "x" + drawer.getStoredItemStackSize() + "]");
                        else
                            name = stackName.append(" [" + remainder + "]");
                    } else
                        name = stackName.append(" [" + drawer.getStoredItemCount() + "]");
                } else {
                    name = stackName;
                }
            }
            result.add(Component.translatable("tooltip.storagedrawers.waila.drawer", i + 1, name));
        }

    }

    private void addStackLimit(List<Component> result, BlockEntityDrawers tile, IDrawerAttributes attr) {
        if (!this.showStackLimit) return;

        if (attr.isUnlimitedStorage() || tile.getDrawerAttributes().isUnlimitedVending())
            result.add(Component.translatable("tooltip.storagedrawers.waila.nolimit"));
        else {
            int multiplier = tile.upgrades().getStorageMultiplier();
            int limit = tile.getEffectiveDrawerCapacity() * multiplier;
            result.add(Component.translatable("tooltip.storagedrawers.waila.limit", limit, multiplier));
        }
    }

    private void addStatus(List<Component> result, BlockEntityDrawers tile, IDrawerAttributes attr) {
        if (!this.showStatus) return;

        List<MutableComponent> attribs = new ArrayList<>();
        if (attr.isItemLocked(LockAttribute.LOCK_POPULATED))
            attribs.add(Component.translatable("tooltip.storagedrawers.waila.locked"));
        if (attr.isVoid())
            attribs.add(Component.translatable("tooltip.storagedrawers.waila.void"));
        //if (tile.getOwner() != null)
        //    attribs.add(new TranslationTextComponent("tooltip.storagedrawers.waila.protected"));

        if (!attribs.isEmpty())
            result.add(attribs.stream().reduce((a, b) ->
                    a.append(Component.literal(", ")).append(b)).get());
    }
}
