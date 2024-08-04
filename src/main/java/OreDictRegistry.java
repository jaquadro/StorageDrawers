/*package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OreDictRegistry
{
    private Set<String> blacklist = new HashSet<>();
    private Set<String> whitelist = new HashSet<>();
    private List<String> blacklistPrefix = new ArrayList<>();

    private Set<String> blacklistCache = new HashSet<>();
    private Set<String> graylistCache = new HashSet<>();

    public OreDictRegistry () {
        addBlacklist("logWood");
        addBlacklist("plankWood");
        addBlacklist("slabWood");
        addBlacklist("stairWood");
        addBlacklist("stickWood");
        addBlacklist("treeSapling");
        addBlacklist("treeLeaves");
        addBlacklist("leavesTree");
        addBlacklist("blockGlass");
        addBlacklist("paneGlass");
        addBlacklist("record");
        addBlacklist("stone");
        addBlacklist("cobblestone");
        addBlacklist("glowstone");
        addBlacklist("glass");
        addBlacklist("obsidian");
        addBlacklist("cobblestone");
        addBlacklist("sand");
        addBlacklist("sandstone");

        addBlacklist("accioMaterial");
        addBlacklist("crucioMaterial");
        addBlacklist("imperioMaterial");
        addBlacklist("zivicioMaterial");
        addBlacklist("resourceTaint");
        addBlacklist("slimeball");

        addBlacklist("blockMetal");
        addBlacklist("ingotMetal");
        addBlacklist("nuggetMetal");

        addBlacklistPrefix("list");
        addBlacklistPrefix("dye");
        addBlacklistPrefix("paneGlass");

        String[] oreTypes = { "ore", "block", "ingot", "nugget" };
        String[] oreMaterials = { "Iron", "Gold", "Diamond", "Emerald", "Aluminum", "Aluminium", "Tin", "Copper", "Lead",
            "Silver", "Platinum", "Nickel", "Osmium", "Invar", "Bronze", "Electrum", "Enderium" };

        for (String ore : oreMaterials) {
            for (String type : oreTypes)
                addWhitelist(type + ore);
        }

        for (String item : StorageDrawers.config.cache.oreBlacklist) {
            removeWhitelist(item);
            addBlacklist(item);
        }

        for (String item : StorageDrawers.config.cache.oreWhitelist) {
            removeBlacklist(item);
            addWhitelist(item);
        }
    }

    public boolean addBlacklist (String entry) {
        if (entry == null)
            return false;

        blacklistCache.add(entry);
        graylistCache.remove(entry);

        return blacklist.add(entry);
    }

    public boolean addBlacklistPrefix (String entry) {
        if (entry == null)
            return false;

        if (blacklistPrefix.contains(entry))
            return false;

        graylistCache.clear();

        return blacklistPrefix.add(entry);
    }

    public boolean addWhitelist (String entry) {
        if (entry == null)
            return false;

        return whitelist.add(entry);
    }

    public boolean removeBlacklist (String entry) {
        blacklistCache.remove(entry);

        return blacklist.remove(entry);
    }

    public boolean removeBlacklistPrefix (String entry) {
        return blacklistPrefix.remove(entry);
    }

    public boolean removeWhitelist (String entry) {
        return whitelist.remove(entry);
    }

    public boolean isEntryBlacklisted (String entry) {
        if (blacklistCache.contains(entry))
            return true;

        for (String aBlacklistPrefix : blacklistPrefix) {
            if (entry.startsWith(aBlacklistPrefix)) {
                blacklistCache.add(entry);
                return true;
            }
        }

        return false;
    }

    public boolean isEntryWhitelisted (String entry) {
        return whitelist.contains(entry);
    }

    public boolean isEntryValid (String entry) {
        if (graylistCache.contains(entry))
            return true;

        if (!whitelist.contains(entry)) {
            if (isEntryBlacklisted(entry))
                return false;

            if (!isValidForEquiv(entry)) {
                blacklistCache.add(entry);
                return false;
            }
        }

        graylistCache.add(entry);

        return true;
    }

    private String getModId (Item item) {
        if (item == null)
            return null;

        return item.getRegistryName().getResourceDomain();
    }

    private boolean isValidForEquiv (String oreName) {
        List<ItemStack> oreList = OreDictionary.getOres(oreName);
        if (oreList.size() == 0)
            return false;

        // Fail entries that have any wildcard items registered to them.

        HashSet<String> modIds = new HashSet<>();
        for (ItemStack anOreList : oreList) {
            if (anOreList.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                return false;

            String modId = getModId(anOreList.getItem());
            if (modId != null)
                modIds.add(modId);
        }

        // Fail entries that have multiple instances of an item registered, differing by metadata or other
        // criteria.

        if (modIds.size() < oreList.size())
            return false;

        // Fail entries where the keys in at least one stack are not the super-set of all other stacks.
        // Can be determined by merging all keys and testing cardinality.

        HashSet<Integer> mergedIds = new HashSet<>();
        int maxKeyCount = 0;

        for (ItemStack anOreList : oreList) {
            int[] ids = OreDictionary.getOreIDs(anOreList);
            maxKeyCount = Math.max(maxKeyCount, ids.length);

            for (int id : ids)
                mergedIds.add(id);
        }

        return maxKeyCount >= mergedIds.size();
    }
}
*/