package com.jaquadro.minecraft.storagedrawers.config;

import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.service.ChameleonConfig;
import com.texelsaurus.minecraft.chameleon.config.ConfigSpec;

import java.util.ArrayList;
import java.util.List;

public final class ModCommonConfig extends ConfigSpec
{
    public static ModCommonConfig INSTANCE = new ModCommonConfig();

    private final ChameleonConfig commonConfig;
    public General GENERAL;
    public Integration INTEGRATION;
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

        public ChameleonConfig.ConfigEntry<List<? extends String>> compRules;

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

            forceDetachedDrawersMaxCapacityCheck = commonConfig.define("forceDetachedDrawersMaxCapacityCheck", true)
                .comment("Drawers track the capacity upgrades from the block they were taken from.",
                    "Drawers can only be placed back into a block with the same or lower max capacity.",
                    "Drawers can still only be inserted into a block with enough capacity for the items held.")
                .build();

            heavyDrawers = commonConfig.define("heavyDrawers", false)
                .comment("If enabled, carrying filled drawers in your inventory gives slowness debuff,",
                    "unless a Portability Upgrade is used.")
                .build();

            debugTrace = commonConfig.define("debugTrace", true).build();
            //compRules = ModServices.CONFIG.defineList("compactingRules", test, obj -> CompTierRegistry.validateRuleSyntax((String)obj)).build();

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

    public class Upgrades {
        public final ChameleonConfig.ConfigEntry<Integer> level1Mult;
        public final ChameleonConfig.ConfigEntry<Integer> level2Mult;
        public final ChameleonConfig.ConfigEntry<Integer> level3Mult;
        public final ChameleonConfig.ConfigEntry<Integer> level4Mult;
        public final ChameleonConfig.ConfigEntry<Integer> level5Mult;

        public final ChameleonConfig.ConfigEntry<Boolean> enableBalanceUpgrade;


        public Upgrades () {
            commonConfig.pushGroup("StorageUpgrades");

            // builder.comment("Storage upgrades multiply storage capacity by the given amount.",
            //     "When multiple storage upgrades are used together, their multipliers are added before being applied.");

            level1Mult = commonConfig.define("level1Mult", 2).build();
            level2Mult = commonConfig.define("level2Mult", 4).build();
            level3Mult = commonConfig.define("level3Mult", 8).build();
            level4Mult = commonConfig.define("level4Mult", 16).build();
            level5Mult = commonConfig.define("level5Mult", 32).build();

            enableBalanceUpgrade = commonConfig.define("enableBalanceUpgrade", true)
                .comment("Balance upgrades allow same-item slots to balance out their amounts when items are",
                    "added or removed from a lot.  Works across networks when acting through a controller.")
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
