package com.jaquadro.minecraft.storagedrawers.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OreDictRegistry
{
    private Set<String> blacklist = new HashSet<String>();
    private List<String> blacklistPrefix = new ArrayList<String>();

    private Set<String> blacklistCache = new HashSet<String>();
    private Set<String> graylistCache = new HashSet<String>();

    public OreDictRegistry () {
        register("logWood");
        register("plankWood");
        register("slabWood");
        register("stairWood");
        register("treeSapling");
        register("treeLeaves");
        register("blockGlass");
        register("paneGlass");
        register("record");
        register("stone");
        register("cobblestone");
        register("sand");
        register("sandstone");

        registerPrefix("list");
    }

    public boolean register (String entry) {
        if (entry == null)
            return false;

        blacklistCache.add(entry);
        graylistCache.remove(entry);

        return blacklist.add(entry);
    }

    public boolean registerPrefix (String entry) {
        if (entry == null)
            return false;

        if (blacklistPrefix.contains(entry))
            return false;

        graylistCache.clear();

        return blacklistPrefix.add(entry);
    }

    public boolean unregister (String entry) {
        blacklistCache.remove(entry);

        return blacklist.remove(entry);
    }

    public boolean unregisterPrefix (String entry) {
        return blacklistPrefix.remove(entry);
    }

    public boolean isEntryBlacklisted (String entry) {
        if (blacklistCache.contains(entry))
            return true;
        if (graylistCache.contains(entry))
            return false;

        for (int i = 0, n = blacklistPrefix.size(); i < n; i++) {
            if (entry.startsWith(blacklistPrefix.get(i))) {
                blacklistCache.add(entry);
                return true;
            }
        }

        graylistCache.add(entry);

        return false;
    }
}
