package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("tile"))
                return true;
        }

        return isListed(stack);
    }

    @Override
    protected void logRegisterNamespace (@NotNull String namespace) {
        ModServices.log.info("New blacklisted storage namespace " + namespace);
    }

    @Override
    protected void logRegisterItem (@NotNull ItemStack item) {
        ModServices.log.info("New blacklisted storage item " + item.getItem());
    }
}
