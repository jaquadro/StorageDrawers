package com.jaquadro.minecraft.storagedrawers.config;

import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.service.ChameleonConfig;
import com.texelsaurus.minecraft.chameleon.config.ConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ModCommonConfig extends ConfigSpec
{
    public static ModCommonConfig INSTANCE = new ModCommonConfig();

    private final ChameleonConfig commonConfig;
    public General GENERAL;
    public Integration INTEGRATION;
    public Conversion CONVERSION;
    public Upgrades UPGRADES;

    private ModCommonConfig () {
        commonConfig = ChameleonServices.CONFIG.create(this);
    }

    public ChameleonConfig context() {
        return commonConfig;
    }

    @Override
    public void init() {
        GENERAL = new General();
        INTEGRATION = new Integration();
        CONVERSION = new Conversion();
        UPGRADES = new Upgrades();
    }

    public class General {
        public ChameleonConfig.ConfigEntry<Integer> baseStackStorage;
        public ChameleonConfig.ConfigEntry<Boolean> enableUI;
        public ChameleonConfig.ConfigEntry<Boolean> enableSidedInput;
        public ChameleonConfig.ConfigEntry<Boolean> enableSidedOutput;
        public ChameleonConfig.ConfigEntry<Boolean> enableItemConversion;
        public ChameleonConfig.ConfigEntry<Boolean> debugTrace;
        public ChameleonConfig.ConfigEntry<Boolean> enableExtraCompactingRules;
        public ChameleonConfig.ConfigEntry<Integer> controllerRange;
        public ChameleonConfig.ConfigEntry<Boolean> enableAnalogRedstone;
        public ChameleonConfig.ConfigEntry<Boolean> enableDetachedDrawers;
        public ChameleonConfig.ConfigEntry<Boolean> forceDetachedDrawersMaxCapacityCheck;
        public ChameleonConfig.ConfigEntry<Boolean> heavyDrawers;
        public ChameleonConfig.ConfigEntry<Boolean> enablePersonalKey;
        public ChameleonConfig.ConfigEntry<Boolean> restrictFramingMaterials;

        public ChameleonConfig.ConfigEntry<List<? extends String>> compRules;

        public ChameleonConfig.ConfigEntry<Boolean> enableStoreFilledDrawers;
        public ChameleonConfig.ConfigEntry<List<? extends String>> storeBlacklist;

        public General() {
            commonConfig.pushGroup("General");

            List<String> test = new ArrayList<>();
            test.add("minecraft:clay, minecraft:clay_ball, 4");

            baseStackStorage = commonConfig.define("baseStackStorage", 4)
                .comment("The number of item stacks held in a basic unit of storage.",
                    "1x1 drawers hold 8 units, 1x2 drawers hold 4 units, 2x2 drawers hold 2 units.",
                    "Half-depth drawers hold half those amounts.")
                .build();

            controllerRange = commonConfig.defineInRange("controllerRange", 50, 1, 75)
                .comment("Controller range defines how far away a drawer can be connected",
                    "on X, Y, or Z planes.  The default value of 50 gives the controller a very",
                    "large range, but not beyond the chunk load distance.")
                .build();

            enableAnalogRedstone = commonConfig.define("enableAnalogRedstone", true)
                .comment("Whether redstone upgrades should emit an analog redstone signal, requiring",
                    "the use of a comparator to read it.")
                .build();

            enableUI = commonConfig.define("enableUI", true).build();
            enableSidedInput = commonConfig.define("enableSidedInput", true).build();
            enableSidedOutput = commonConfig.define("enableSidedOutput", true).build();
            enableItemConversion = commonConfig.define("enableItemConversion", true).build();
            enableExtraCompactingRules = commonConfig.define("enableExtraCompactingRules", true).build();

            enableDetachedDrawers = commonConfig.define("enableDetachedDrawers", true)
                .comment("Allows drawers to be pulled from their block and inserted into another block.")
                .build();

            forceDetachedDrawersMaxCapacityCheck = commonConfig.define("forceDetachedDrawersMaxCapacityCheck", false)
                .comment("Drawers track the capacity upgrades from the block they were taken from.",
                    "Drawers can only be placed back into a block with the same or lower max capacity.",
                    "Drawers can still only be inserted into a block with enough capacity for the items held.")
                .build();

            heavyDrawers = commonConfig.define("heavyDrawers", false)
                .comment("If enabled, carrying filled drawers in your inventory gives slowness debuff,",
                    "unless a Portability Upgrade is used.")
                .build();

            enablePersonalKey = commonConfig.define("enablePersonalKey", true)
                .comment("If enabled, players can lock drawer interactions to just themselves.")
                .build();

            restrictFramingMaterials = commonConfig.define("restrictFramingMaterials", true)
                .comment("If enabled, limits framing materials to solid, opaque blocks [by best effort]")
                .build();

            debugTrace = commonConfig.define("debugTrace", false).build();

            enableStoreFilledDrawers = commonConfig.define("enableStoreFilledDrawers", false)
                .comment("If enabled, allows drawers with contents to be stored in another drawer block")
                .build();
            storeBlacklist = commonConfig.defineList("storeBlacklist", Arrays.asList("storagedrawers:creative_vending_upgrade"), null)
                .comment("", "Each entry should be a namespace or fully namespaced item, e.g. minecraft:cobblestone",
                    "Any items on the blacklist are prevented from being stored in drawers")
                .build();

            compRules = commonConfig.defineList("compactingRules", test, obj -> CompTierRegistry.validateRuleSyntax((String)obj)).build();

            commonConfig.popGroup();
        }

        public int getBaseStackStorage() {
            if (!isLoaded())
                return 1;

            return baseStackStorage.get();
        }
    }

    public class Integration {
        public final ChameleonConfig.ConfigEntry<Boolean> wailaStackRemainder;
        public final ChameleonConfig.ConfigEntry<Boolean> wailaRespectQuantifyKey;

        public Integration () {
            commonConfig.pushGroup("Integration");

            wailaStackRemainder = commonConfig.define("wailaStackRemainder", true)
                .comment("When true, shows quantity as NxS + R (by stack size) rather than count")
                .build();

            wailaRespectQuantifyKey = commonConfig.define("wailaRespectQuantifyKey", false)
                .comment("When true, does not show current quantities unless quantify key was used")
                .build();

            commonConfig.popGroup();
        }
    }

    public class Conversion {
        public final ChameleonConfig.ConfigEntry<List<? extends String>> oreTypes;
        public final ChameleonConfig.ConfigEntry<List<? extends String>> oreMaterials;
        public final ChameleonConfig.ConfigEntry<List<? extends String>> tagWhitelist;
        public final ChameleonConfig.ConfigEntry<List<? extends String>> tagBlacklist;
        public final ChameleonConfig.ConfigEntry<List<? extends String>> itemEquivGroups;
        public Conversion () {
            commonConfig.pushGroup("Conversion");
            oreTypes = commonConfig.defineList("oreTypeWhitelist", Arrays.asList(
                    "forge:storage_blocks", "forge:ingots", "forge:nuggets"), null)
                .comment("", "Each type will be combined with each material to create a set of whitelist entries.",
                    "This is mainly a convenience for common ore-based materials.")
                .build();
            oreMaterials = commonConfig.defineList("oreMaterialWhitelist", Arrays.asList(
                    "aluminum", "constantan", "steel", "uranium", "invar", "tin", "lead", "silver", "platinum", "nickel", "osmium", "bronze", "electrum"), null)
                .comment("", "Each type will be combined with each material to create a set of whitelist entries.",
                    "This is mainly a convenience for common ore-based materials.")
                .build();
            tagWhitelist = commonConfig.defineList("tagWhitelist", new ArrayList<String>(), null)
                .comment("", "Each whitelist entry should be a fully namespaced tag, e.g. c:ingots/copper")
                .build();
            tagBlacklist = commonConfig.defineList("tagBlacklist", new ArrayList<String>(), null)
                .comment("", "Each blacklist entry should be a fully namespaced tag, e.g. c:ingots/copper.",
                    "All items not on the whitelist are blacklisted implicitly.  This can be used to exclude",
                    "specific entries created from the ore whitelist set.")
                .build();
            itemEquivGroups = commonConfig.defineList("itemEquivalenceGroups", new ArrayList<String>(), null)
                .comment("", "Each entry is a semicolon-separated list of fully-namespaced items. All items within the",
                    "same entry are considered equivalent and convertible/interchangeable.",
                    "Example: [\"thermal:nickel_ore;immersiveengineering:ore_nickel\"]")
                .build();
            commonConfig.popGroup();
        }
    }

    public class Upgrades {
        public final ChameleonConfig.ConfigEntry<Integer> level1Mult;
        public final ChameleonConfig.ConfigEntry<Integer> level2Mult;
        public final ChameleonConfig.ConfigEntry<Integer> level3Mult;
        public final ChameleonConfig.ConfigEntry<Integer> level4Mult;
        public final ChameleonConfig.ConfigEntry<Integer> level5Mult;

        public final ChameleonConfig.ConfigEntry<Boolean> enableStorageUpgrade;
        public final ChameleonConfig.ConfigEntry<Boolean> enableFillLevelUpgrade;
        public final ChameleonConfig.ConfigEntry<Boolean> enableRedstoneUpgrade;
        public final ChameleonConfig.ConfigEntry<Boolean> enableIlluminationUpgrade;
        public final ChameleonConfig.ConfigEntry<Boolean> enableVoidUpgrade;
        public final ChameleonConfig.ConfigEntry<Boolean> enableBalanceUpgrade;
        public final ChameleonConfig.ConfigEntry<Boolean> enablePortabilityUpgrade;
        public final ChameleonConfig.ConfigEntry<Boolean> enableRemoteUpgrade;
        public final ChameleonConfig.ConfigEntry<Boolean> enableRemoteGroupUpgrade;


        public Upgrades () {
            commonConfig.pushGroup("StorageUpgrades");

            // builder.comment("Storage upgrades multiply storage capacity by the given amount.",
            //     "When multiple storage upgrades are used together, their multipliers are added before being applied.");

            level1Mult = commonConfig.define("level1Mult", 2).build();
            level2Mult = commonConfig.define("level2Mult", 4).build();
            level3Mult = commonConfig.define("level3Mult", 8).build();
            level4Mult = commonConfig.define("level4Mult", 16).build();
            level5Mult = commonConfig.define("level5Mult", 32).build();

            enableStorageUpgrade = commonConfig.define("enableStorageUpgrade", true)
                .comment("Storage upgrades increase capacity of drawers.")
                .build();

            enableFillLevelUpgrade = commonConfig.define("enableFillLevelUpgrade", true)
                .comment("Fill level upgrades add fill bars to the faces of drawers.")
                .build();

            enableRedstoneUpgrade = commonConfig.define("enableRedstoneUpgrade", true)
                .comment("Adds redstone output to drawers based on fill levels.")
                .build();

            enableIlluminationUpgrade = commonConfig.define("enableIlluminationUpgrade", true)
                .comment("Renders drawer labels brighter than the surrounding environment would allow.")
                .build();

            enableVoidUpgrade = commonConfig.define("enableVoidUpgrade", true)
                .comment("Causes drawers to accept but void compatible items when they are filled to capacity.")
                .build();

            enableBalanceUpgrade = commonConfig.define("enableBalanceUpgrade", true)
                .comment("Balance upgrades allow same-item slots to balance out their amounts when items are",
                    "added or removed from a lot.  Works across networks when acting through a controller.")
                .build();

            enablePortabilityUpgrade = commonConfig.define("enablePortabilityUpgrade", true)
                .comment("Allows drawers with contents to be freely carried when heavy drawers is enabled.")
                .build();

            enableRemoteUpgrade = commonConfig.define("enableRemoteUpgrade", true)
                .comment("Allows a single drawer to connect to a controller remotely.")
                .build();

            enableRemoteGroupUpgrade = commonConfig.define("enableRemoteGroupUpgrade", true)
                .comment("Allows a drawer and all drawers connected to it to connect to a controller remotely.")
                .build();

            commonConfig.popGroup();
        }

        public int getLevelMult(int level) {
            if (!isLoaded())
                return 1;

            return switch (level) {
                case 1 -> level1Mult.get();
                case 2 -> level2Mult.get();
                case 3 -> level3Mult.get();
                case 4 -> level4Mult.get();
                case 5 -> level5Mult.get();
                default -> 1;
            };
        }
    }

    public enum Mode {
        NONE,
        LIST,
        ALL;

        public static Mode fromValueIgnoreCase (String value) {
            if (value.compareToIgnoreCase("NONE") == 0)
                return Mode.NONE;
            else if (value.compareToIgnoreCase("LIST") == 0)
                return Mode.LIST;
            else if (value.compareToIgnoreCase("ALL") == 0)
                return Mode.ALL;

            return LIST;
        }
    }
}
