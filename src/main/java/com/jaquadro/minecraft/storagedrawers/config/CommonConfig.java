package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class CommonConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Upgrades UPGRADES = new Upgrades(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    private static boolean loaded = false;
    public static void setLoaded() {
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Integer> baseStackStorage;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableUI;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableSidedInput;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableSidedOutput;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableItemConversion;
        public final ForgeConfigSpec.ConfigValue<Boolean> debugTrace;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableExtraCompactingRules;
        public final ForgeConfigSpec.ConfigValue<Integer> controllerRange;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            baseStackStorage = builder
                .comment("The number of item stacks held in a basic unit of storage.",
                    "1x1 drawers hold 8 units, 1x2 drawers hold 4 units, 2x2 drawers hold 2 units.",
                    "Half-depth drawers hold half those amounts.")
                .define("baseStackStorage", 4);
            controllerRange = builder
                .defineInRange("controllerRange", 12, 1, 50);
            enableUI = builder
                .define("enableUI", true);
            enableSidedInput = builder
                .define("enableSidedInput", true);
            enableSidedOutput = builder
                .define("enableSidedOutput", true);
            enableItemConversion = builder
                .define("enableItemConversion", true);
            enableExtraCompactingRules = builder
                .define("enableExtraCompactingRules", true);
            debugTrace = builder
                .define("debugTrace", false);

            builder.pop();
        }
    }

    public static class Upgrades {
        public final ForgeConfigSpec.ConfigValue<Integer> level1Mult;
        public final ForgeConfigSpec.ConfigValue<Integer> level2Mult;
        public final ForgeConfigSpec.ConfigValue<Integer> level3Mult;
        public final ForgeConfigSpec.ConfigValue<Integer> level4Mult;
        public final ForgeConfigSpec.ConfigValue<Integer> level5Mult;

        public Upgrades (ForgeConfigSpec.Builder builder) {
            builder.push("Storage Upgrades");
            builder.comment("Storage upgrades multiply storage capacity by the given amount.",
                "When multiple storage upgrades are used together, their multipliers are added before being applied.");

            level1Mult = builder
                .define("level1Mult", 2);
            level2Mult = builder
                .define("level2Mult", 4);
            level3Mult = builder
                .define("level3Mult", 8);
            level4Mult = builder
                .define("level4Mult", 16);
            level5Mult = builder
                .define("level5Mult", 32);

            builder.pop();
        }

        public int getLevelMult(int level) {
            if (!isLoaded())
                return 1;

            switch (level) {
                case 1: return level1Mult.get();
                case 2: return level2Mult.get();
                case 3: return level3Mult.get();
                case 4: return level4Mult.get();
                case 5: return level5Mult.get();
                default: return 1;
            }
        }
    }
}
