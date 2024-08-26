package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.util.ItemStackMatcher;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StorageUtil
{
    public static void rebalanceDrawers (IDrawerGroup group, int slot) {
        IDrawer drawer = group.getDrawer(slot);
        if (drawer.isEnabled())
            rebalanceDrawers(group, drawer.getStoredItemPrototype());
    }
    public static void rebalanceDrawers (IDrawerGroup group, ItemStack stack) {
        if (stack.isEmpty())
            return;

        List<IDrawer> drawers = new ArrayList<>();
        for (int i = 0; i < group.getDrawerCount(); i++) {
            IDrawer drawer = group.getDrawer(i);
            if (!drawer.isEnabled())
                continue;

            if (ItemStackMatcher.areItemsEqual(drawer.getStoredItemPrototype(), stack))
                drawers.add(drawer);
        }

        rebalanceDrawers(drawers.stream());
    }

    public static void rebalanceDrawers (Stream<IDrawer> drawers, ItemStack stack) {
        if (stack.isEmpty())
            return;

        rebalanceDrawers(drawers.filter(d -> ItemStackMatcher.areItemsEqual(d.getStoredItemPrototype(), stack)));
    }

    public static void rebalanceDrawers (Stream<IDrawer> drawers) {
        if (!CommonConfig.UPGRADES.enableBalanceUpgrade.get())
            return;

        List<IDrawer> balanceDrawers = new ArrayList<>();
        int aggCount = 0;

        for (IDrawer drawer : drawers.toList()) {
            if (!drawer.isEnabled())
                continue;

            balanceDrawers.add(drawer);
            aggCount += drawer.getStoredItemCount();
        }

        if (balanceDrawers.size() > 1) {
            int dist = aggCount / balanceDrawers.size();
            int remainder = aggCount - (dist * balanceDrawers.size());

            for (int i = 0; i < balanceDrawers.size(); i++)
                balanceDrawers.get(i).setStoredItemCount(dist + (i < remainder ? 1 : 0));
        }
    }
}
