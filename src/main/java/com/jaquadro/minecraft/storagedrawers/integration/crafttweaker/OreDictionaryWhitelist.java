package com.jaquadro.minecraft.storagedrawers.integration.crafttweaker;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.CraftTweakerAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.storagedrawers.OreDictionaryWhitelist")
public class OreDictionaryWhitelist
{
    @ZenMethod
    public static void add (String name) {
        if (name != null && name.length() > 0)
            CraftTweakerAPI.apply(new AddNameAction(name));
        else
            CraftTweakerAPI.logError("Tried to add empty ore dictionary name to drawer conversion whitelist.");
    }

    @ZenMethod
    public static void remove (String name) {
        if (name != null && name.length() > 0)
            CraftTweakerAPI.apply(new RemoveNameAction(name));
        else
            CraftTweakerAPI.logError("Tried to remove empty ore dictionary name from drawer conversion whitelist.");
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
            added = StorageDrawers.oreDictRegistry.addWhitelist(name);
        }

        @Override
        public String describe () {
            if (added)
                return "Adding ore dictionary key '" + name + "' to drawer conversion whitelist.";
            else
                return "Ore dictionary key '" + name + "' already registered in drawer conversion whitelist.";
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
            removed = StorageDrawers.oreDictRegistry.removeWhitelist(name);
        }

        @Override
        public String describe () {
            if (removed)
                return "Removing existing ore dictionary key '" + name + "' from drawer conversion whitelist.";
            else
                return "";
        }
    }
}
