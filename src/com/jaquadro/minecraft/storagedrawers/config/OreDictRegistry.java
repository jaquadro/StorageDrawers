package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.util.UniqueMetaIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OreDictRegistry
{
    private Set<String> blacklist = new HashSet<String>();
    private Set<String> whitelist = new HashSet<String>();
    private List<String> blacklistPrefix = new ArrayList<String>();

    private Set<String> blacklistCache = new HashSet<String>();
    private Set<String> graylistCache = new HashSet<String>();

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

        for (String item : new String[] { "oreIron", "oreGold", "oreAluminum", "oreAluminium", "oreTin", "oreCopper", "oreLead", "oreSilver", "orePlatinum", "oreNickel" })
            addWhitelist(item);

        for (String item : new String[] { "blockIron", "blockGold", "blockAluminum", "blockAluminium", "blockTin", "blockCopper", "blockLead", "blockSilver", "blockPlatinum", "blockNickel" })
            addWhitelist(item);

        for (String item : new String[] { "ingotIron", "ingotGold", "ingotAluminum", "ingotAluminium", "ingotTin", "ingotCopper", "ingotLead", "ingotSilver", "ingotPlatinum", "ingotNickel" })
            addWhitelist(item);

        for (String item : new String[] { "nuggetIron", "nuggetGold", "nuggetAluminum", "nuggetAluminium", "nuggetTin", "nuggetCopper", "nuggetLead", "nuggetSilver", "nuggetPlatinum", "nuggetNickel" })
            addWhitelist(item);

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

        for (int i = 0, n = blacklistPrefix.size(); i < n; i++) {
            if (entry.startsWith(blacklistPrefix.get(i))) {
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

        HashSet<String> modIds = new HashSet<String>();
        for (int i = 0, n = oreList.size(); i < n; i++) {
            if (oreList.get(i).getItemDamage() == OreDictionary.WILDCARD_VALUE)
                return false;

            String modId = getModId(oreList.get(i).getItem());
            if (modId != null)
                modIds.add(modId);
        }

        // Fail entries that have multiple instances of an item registered, differing by metadata or other
        // criteria.

        if (modIds.size() < oreList.size())
            return false;

        // Fail entries where the keys in at least one stack are not the super-set of all other stacks.
        // Can be determined by merging all keys and testing cardinality.

        HashSet<Integer> mergedIds = new HashSet<Integer>();
        int maxKeyCount = 0;

        for (int i = 0, n = oreList.size(); i < n; i++) {
            int[] ids = OreDictionary.getOreIDs(oreList.get(i));
            maxKeyCount = Math.max(maxKeyCount, ids.length);

            for (int id : ids)
                mergedIds.add(id);
        }

        if (maxKeyCount < mergedIds.size())
            return false;

        return true;
    }
}
