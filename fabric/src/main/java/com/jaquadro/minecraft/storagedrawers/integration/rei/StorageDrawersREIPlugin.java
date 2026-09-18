package com.jaquadro.minecraft.storagedrawers.integration.rei;

import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemKeyring;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class StorageDrawersREIPlugin implements REIClientPlugin
{
    @Override
    public void registerEntries (EntryRegistry registry) {
        Predicate<EntryStack<?>> filter = StorageDrawersREIPlugin::shouldHide;
        registry.removeEntryIf(filter);
    }

    private static boolean shouldHide (EntryStack<?> stack) {
        if (!(stack.getValue() instanceof ItemStack itemStack))
            return false;

        Item item = itemStack.getItem();

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof IFramedBlock)
            return true;

        if (item instanceof ItemKeyring && item != ModItems.KEYRING.get())
            return true;

        if (item == ModItems.DETACHED_DRAWER_FULL.get()
            || item == ModItems.REMOTE_GROUP_UPGRADE_BOUND.get()
            || item == ModItems.REMOTE_UPGRADE_BOUND.get())
            return true;

        return false;
    }
}
