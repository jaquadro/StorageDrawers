package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigItemList
{
    private final List<String> listedNamespaces = new ArrayList<>();
    private final List<Item> listedItems = new ArrayList<>();
    private List<String> pendingRules = new ArrayList<>();
    private boolean initialized;

    public ConfigItemList () { }

    public void initialize () {
        initialized = true;

        innerInitialize();

        for (String rule : pendingRules) {
            register(rule);
        }

        pendingRules = null;
    }

    protected void innerInitialize () { }

    public boolean isListed (ItemStack stack) {
        Item item = stack.getItem();

        if (listedItems.contains(item))
            return true;

        if(!listedNamespaces.isEmpty()) {
            ResourceKey<Item> resourceKey = BuiltInRegistries.ITEM.getResourceKey(item).orElse(null);
            if (resourceKey != null) {
                String namespace = resourceKey.location().getNamespace();
                if (listedNamespaces.contains(namespace))
                    return true;
            }
        }

        return false;
    }

    public boolean registerNamespace (@NotNull String namespace) {
        if (namespace.isEmpty())
            return false;

        unregisterNamespace(namespace);
        listedNamespaces.add(namespace);

        logRegisterNamespace(namespace);

        return true;
    }

    protected void logRegisterNamespace (@NotNull String namespace) { }

    public boolean registerItem (@NotNull ItemStack item) {
        if (item.isEmpty())
            return false;

        unregisterItem(item);
        listedItems.add(item.getItem());

        logRegisterItem(item);

        return true;
    }

    protected void logRegisterItem (@NotNull ItemStack item) { }

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

        return listedNamespaces.remove(namespace);
    }

    public boolean unregisterItem (@NotNull ItemStack stack) {
        if (stack.isEmpty())
            return false;

        return listedItems.remove(stack.getItem());
    }

}
