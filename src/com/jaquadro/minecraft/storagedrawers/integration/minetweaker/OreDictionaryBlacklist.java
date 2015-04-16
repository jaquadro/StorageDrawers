package com.jaquadro.minecraft.storagedrawers.integration.minetweaker;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.storagedrawers.OreDictionaryBlacklist")
public class OreDictionaryBlacklist
{
    @ZenMethod
    public static void add (String name) {
        if (name != null && name.length() > 0)
            MineTweakerAPI.apply(new AddNameAction(name));
        else
            MineTweakerAPI.logError("Tried to add empty ore dictionary name to drawer conversion blacklist.");
    }

    @ZenMethod
    public static void addPrefix (String prefix) {
        if (prefix != null && prefix.length() > 0)
            MineTweakerAPI.apply(new AddPrefixAction(prefix));
        else
            MineTweakerAPI.logError("Tried to add empty ore dictionary prefix to drawer conversion blacklist.");
    }

    private static class AddNameAction implements IUndoableAction
    {
        String name;
        boolean added;

        public AddNameAction (String name) {
            this.name = name;
        }

        @Override
        public void apply () {
            added = StorageDrawers.oreDictRegistry.addBlacklist(name);
        }

        @Override
        public boolean canUndo () {
            return true;
        }

        @Override
        public void undo () {
            if (added)
                StorageDrawers.oreDictRegistry.removeBlacklist(name);
        }

        @Override
        public String describe () {
            if (added)
                return "Adding ore dictionary key '" + name + "' to drawer conversion blacklist.";
            else
                return "Ore dictionary key '" + name + "' already registered in drawer conversion blacklist.";
        }

        @Override
        public String describeUndo () {
            if (added)
                return "Removing previously added ore dictionary key '" + name + "' from drawer conversion blacklist.";
            else
                return "";
        }

        @Override
        public Object getOverrideKey () {
            return null;
        }
    }

    private static class AddPrefixAction implements IUndoableAction
    {
        String name;
        boolean added;

        public AddPrefixAction (String name) {
            this.name = name;
        }

        @Override
        public void apply () {
            added = StorageDrawers.oreDictRegistry.addBlacklistPrefix(name);
        }

        @Override
        public boolean canUndo () {
            return true;
        }

        @Override
        public void undo () {
            if (added)
                StorageDrawers.oreDictRegistry.removeBlacklistPrefix(name);
        }

        @Override
        public String describe () {
            if (added)
                return "Adding ore dictionary prefix '" + name + "' to drawer conversion blacklist.";
            else
                return "Ore dictionary prefix '" + name + "' already registered in drawer conversion blacklist.";
        }

        @Override
        public String describeUndo () {
            if (added)
                return "Removing previously added ore dictionary prefix '" + name + "' from drawer conversion blacklist.";
            else
                return "";
        }

        @Override
        public Object getOverrideKey () {
            return null;
        }
    }
}
