package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

public class StorageBlacklist extends ConfigItemList
{
    public static final StorageBlacklist INSTANCE = new StorageBlacklist();

    @Override
    protected void innerInitialize () {
        ModCommonConfig.INSTANCE.onLoad(() -> ModCommonConfig.INSTANCE.DRAWERS.storage.storeBlacklist.get().forEach(this::register));
    }

    public boolean isBlacklisted (ItemStack stack) {
        Item item = stack.getItem();

        boolean canStore = true;
        if (item instanceof ItemDrawers)
            canStore = ModCommonConfig.INSTANCE.DRAWERS.filled.canStoreInDrawers.get();

        if (!canStore) {
            CustomData blockData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (blockData != null || customData != null)
                return true;
        }

        return isListed(stack);
    }

    @Override
    protected void logRegisterNamespace (@NotNull String namespace) {
        ModServices.log.info("New denied storage namespace " + namespace);
    }

    @Override
    protected void logRegisterItem (@NotNull ItemStack item) {
        ModServices.log.info("New denied storage item " + item.getItem());
    }
}
