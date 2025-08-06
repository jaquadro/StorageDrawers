package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.item.ItemDetachedDrawer;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StorageBlacklist
{
    public static final StorageBlacklist INSTANCE = new StorageBlacklist();

    private final List<String> blacklistNamespaces = new ArrayList<>();
    private final List<Item> blacklistItems = new ArrayList<>();
    private List<String> pendingRules = new ArrayList<>();
    private boolean initialized;

    public StorageBlacklist () { }

    public void initialize () {
        initialized = true;

        ModCommonConfig.INSTANCE.onLoad(() -> ModCommonConfig.INSTANCE.GENERAL.storeBlacklist.get().forEach(this::register));

        for (String rule : pendingRules) {
            register(rule);
        }

        pendingRules = null;
    }

    public boolean isBlacklisted (ItemStack stack) {
        Item item = stack.getItem();

        if (!ModCommonConfig.INSTANCE.GENERAL.enableStoreFilledDrawers.get()) {
            if (item instanceof ItemDrawers || item instanceof ItemDetachedDrawer) {
                CustomData blockData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (blockData != null || customData != null)
                    return true;
            }
        }

        if (blacklistItems.contains(item))
            return true;

        if(!blacklistNamespaces.isEmpty()) {
            ResourceKey<Item> resourceKey = BuiltInRegistries.ITEM.getResourceKey(item).orElse(null);
            if (resourceKey != null) {
                String namespace = resourceKey.location().getNamespace();
                if (blacklistNamespaces.contains(namespace))
                    return true;
            }
        }

        return false;
    }

    public boolean registerNamespace (@NotNull String namespace) {
        if (namespace.isEmpty())
            return false;

        unregisterNamespace(namespace);
        blacklistNamespaces.add(namespace);

        ModServices.log.info("New blacklisted namespace " + namespace);

        return true;
    }

    public boolean registerItem (@NotNull ItemStack item) {
        if (item.isEmpty())
            return false;

        unregisterItem(item);
        blacklistItems.add(item.getItem());

        ModServices.log.info("New blacklisted item " + item.getItem());

        return true;
    }

    public void register (List<String> entries) {
        entries.forEach(this::register);
    }

    public boolean register (String entry) {
        if (!initialized) {
            pendingRules.add(entry);
            return true;
        }

        String[] parts = entry.split("\\s*:\\s*");
        if (parts.length == 1)
            return registerNamespace(parts[0]);

        ResourceLocation resource = ResourceLocation.parse(entry);
        Item item = BuiltInRegistries.ITEM.get(resource);

        return registerItem(new ItemStack(item));
    }

    public boolean unregisterNamespace (@NotNull String namespace) {
        if (namespace.isEmpty())
            return false;

        return blacklistNamespaces.remove(namespace);
    }

    public boolean unregisterItem (@NotNull ItemStack stack) {
        if (stack.isEmpty())
            return false;

        return blacklistItems.remove(stack.getItem());
    }
}
