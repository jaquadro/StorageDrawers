package com.jaquadro.minecraft.storagedrawers.integration.crafttweaker;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.CraftTweakerAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.storagedrawers.OreDictionaryBlacklist")
public class OreDictionaryBlacklist
{
    @ZenMethod
    public static void add (String name) {
        if (name != null && name.length() > 0)
            CraftTweakerAPI.apply(new AddNameAction(name));
        else
            CraftTweakerAPI.logError("Tried to add empty ore dictionary name to drawer conversion blacklist.");
    }

    @ZenMethod
    public static void remove (String name) {
        if (name != null && name.length() > 0)
            CraftTweakerAPI.apply(new RemoveNameAction(name));
        else
            CraftTweakerAPI.logError("Tried to remove empty ore dictionary name from drawer conversion blacklist.");
    }

    @ZenMethod
    public static void addPrefix (String prefix) {
        if (prefix != null && prefix.length() > 0)
            CraftTweakerAPI.apply(new AddPrefixAction(prefix));
        else
            CraftTweakerAPI.logError("Tried to add empty ore dictionary prefix to drawer conversion blacklist.");
    }

    @ZenMethod
    public static void removePrefix (String prefix) {
        if (prefix != null && prefix.length() > 0)
            CraftTweakerAPI.apply(new RemovePrefixAction(prefix));
        else
            CraftTweakerAPI.logError("Tried to remove empty ore dictionary prefix from drawer conversion blacklist.");
    }

    private static class AddNameAction implements IAction
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
        public String describe () {
            if (added)
                return "Adding ore dictionary key '" + name + "' to drawer conversion blacklist.";
            else
                return "Ore dictionary key '" + name + "' already registered in drawer conversion blacklist.";
        }
    }

    private static class RemoveNameAction implements IAction
    {
        String name;
        boolean removed;

        public RemoveNameAction (String name) {
            this.name = name;
        }

        @Override
        public void apply () {
            removed = StorageDrawers.oreDictRegistry.removeBlacklist(name);
        }

        @Override
        public String describe () {
            if (removed)
                return "Removing existing ore dictionary key '" + name + "' from drawer conversion blacklist.";
            else
                return "";
        }
    }

    private static class AddPrefixAction implements IAction
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
        public String describe () {
            if (added)
                return "Adding ore dictionary prefix '" + name + "' to drawer conversion blacklist.";
            else
                return "Ore dictionary prefix '" + name + "' already registered in drawer conversion blacklist.";
        }
    }

    private static class RemovePrefixAction implements IAction
    {
        String name;
        boolean removed;

        public RemovePrefixAction (String name) {
            this.name = name;
        }

        @Override
        public void apply () {
            removed = StorageDrawers.oreDictRegistry.removeBlacklistPrefix(name);
        }

        @Override
        public String describe () {
            if (removed)
                return "Removing existing ore dictionary prefix '" + name + "' from drawer conversion blacklist.";
            else
                return "";
        }
    }
}
