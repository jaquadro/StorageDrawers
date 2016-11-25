package com.jaquadro.minecraft.storagedrawers.integration.minetweaker;
/*
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.storagedrawers.OreDictionaryWhitelist")
public class OreDictionaryWhitelist
{
    @ZenMethod
    public static void add (String name) {
        if (name != null && name.length() > 0)
            MineTweakerAPI.apply(new AddNameAction(name));
        else
            MineTweakerAPI.logError("Tried to add empty ore dictionary name to drawer conversion whitelist.");
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
            added = StorageDrawers.oreDictRegistry.addWhitelist(name);
        }

        @Override
        public boolean canUndo () {
            return true;
        }

        @Override
        public void undo () {
            if (added)
                StorageDrawers.oreDictRegistry.removeWhitelist(name);
        }

        @Override
        public String describe () {
            if (added)
                return "Adding ore dictionary key '" + name + "' to drawer conversion whitelist.";
            else
                return "Ore dictionary key '" + name + "' already registered in drawer conversion whitelist.";
        }

        @Override
        public String describeUndo () {
            if (added)
                return "Removing previously added ore dictionary key '" + name + "' from drawer conversion whitelist.";
            else
                return "";
        }

        @Override
        public Object getOverrideKey () {
            return null;
        }
    }
}
*/