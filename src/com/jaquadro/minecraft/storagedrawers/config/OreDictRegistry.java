package com.jaquadro.minecraft.storagedrawers.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OreDictRegistry
{
    private Set<String> blacklist = new HashSet<String>();
    private List<String> blacklistPrefix = new ArrayList<String>();

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

        registerPrefix("list");
    }

    public void register (String entry) {
        if (!blacklist.contains(entry))
            blacklist.add(entry);
    }

    public void registerPrefix (String entry) {
        blacklistPrefix.add(entry);
    }

    public boolean isEntryBlacklisted (String entry) {
        if (blacklist.contains(entry))
            return true;

        for (int i = 0; i < blacklistPrefix.size(); i++) {
            if (entry.startsWith(blacklistPrefix.get(i)))
                return true;
        }

        return false;
    }
}
