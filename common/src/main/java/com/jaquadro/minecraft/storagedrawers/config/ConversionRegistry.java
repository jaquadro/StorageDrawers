package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConversionRegistry
{
    public static final ConversionRegistry INSTANCE = new ConversionRegistry();

    private Set<TagKey<Item>> tagWhitelist = new HashSet<>();
    private Set<TagKey<Item>> tagBlacklist = new HashSet<>();
    private List<Set<ResourceLocation>> equivGroups = new ArrayList<>();

    public ConversionRegistry () {
        ModCommonConfig.INSTANCE.onLoad(() -> {
            ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.tagWhitelist.get().forEach(this::addWhitelist);
            ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.tagBlacklist.get().forEach(this::addBlacklist);

            for (String oreType : ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.oreTypes.get()) {
                for (String oreMat : ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.oreMaterials.get())
                    addWhitelist(oreType + "/" + oreMat);
            }

            for (String entry : ModCommonConfig.INSTANCE.UPGRADES.conversionUpgrade.itemEquivGroups.get())
                addEquivGroup(entry);
        });
    }

    public boolean addBlacklist (String entry) {
        String[] parts = entry.split(":");
        if (parts.length != 2)
            return false;

        return addBlacklist(parts[0], parts[1]);
    }

    public boolean addBlacklist (String namespace, String path) {
        return addBlacklist(new ResourceLocation(namespace, path));
    }

    public boolean addBlacklist (ResourceLocation entry) {
        if (entry == null)
            return false;

        tagBlacklist.add(TagKey.create(Registries.ITEM, entry));
        return true;
    }

    public boolean addWhitelist (String entry) {
        String[] parts = entry.split(":");
        if (parts.length != 2)
            return false;

        return addWhitelist(parts[0], parts[1]);
    }

    public boolean addWhitelist (String namespace, String path) {
        return addWhitelist(new ResourceLocation(namespace, path));
    }

    public boolean addWhitelist (ResourceLocation entry) {
        if (entry == null)
            return false;

        tagWhitelist.add(TagKey.create(Registries.ITEM, entry));
        return true;
    }

    public void addEquivGroup (String entry) {
        String[] items = entry.split(";\\s*");
        Set<ResourceLocation> group = new HashSet<>();

        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length != 2)
                continue;

            ResourceLocation key = new ResourceLocation(parts[0], parts[1]);
            group.add(key);
        }

        if (group.size() > 1)
            equivGroups.add(group);
    }

    public boolean itemsShareEquivGroup (Item item1, Item item2) {
        ResourceLocation key1 = BuiltInRegistries.ITEM.getKey(item1);
        ResourceLocation key2 = BuiltInRegistries.ITEM.getKey(item2);

        for (Set<ResourceLocation> group : equivGroups) {
            if (!group.contains(key1))
                continue;

            if (group.contains(key2))
                return true;
        }

        return false;
    }

    public List<ItemStack> getEquivItems (Item item) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        List<ItemStack> items = new ArrayList<>();

        for (Set<ResourceLocation> group : equivGroups) {
            if (!group.contains(key))
                continue;

            for (ResourceLocation entry : group) {
                Item other = BuiltInRegistries.ITEM.get(entry);
                items.add(new ItemStack(other));
            }
        }

        return items;
    }

    public boolean isEntryValid (TagKey<Item> entry) {
        // Formerly utilized "graylist", which contained items not on the whitelist
        // but also not on the blacklist and not failing the valid equiv check
        if (tagBlacklist.contains(entry))
            return false;

        return tagWhitelist.contains(entry);
    }
}