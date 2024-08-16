package com.jaquadro.minecraft.storagedrawers.config;

import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class CommonConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Upgrades UPGRADES = new Upgrades(BUILDER);
    public static final Integration INTEGRATION = new Integration(BUILDER);
    public static final ModConfigSpec spec = BUILDER.build();

    private static boolean loaded = false;
    private static final List<Runnable> loadActions = new ArrayList<>();

    public static void setLoaded() {
        if (!loaded)
            loadActions.forEach(Runnable::run);
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void onLoad(Runnable action) {
        if (loaded)
            action.run();
        else
            loadActions.add(action);
    }

    public static class General {
        public final ModConfigSpec.ConfigValue<Integer> baseStackStorage;
        public final ModConfigSpec.ConfigValue<Boolean> enableUI;
        public final ModConfigSpec.ConfigValue<Boolean> enableSidedInput;
        public final ModConfigSpec.ConfigValue<Boolean> enableSidedOutput;
        public final ModConfigSpec.ConfigValue<Boolean> enableItemConversion;
        public final ModConfigSpec.ConfigValue<Boolean> debugTrace;
        public final ModConfigSpec.ConfigValue<Boolean> enableExtraCompactingRules;
        public final ModConfigSpec.ConfigValue<Integer> controllerRange;
        public final ModConfigSpec.ConfigValue<Boolean> enableAnalogRedstone;
        public final ModConfigSpec.ConfigValue<List<? extends String>> compRules;

        public General(ModConfigSpec.Builder builder) {
            builder.push("General");
            List<String> test = new ArrayList<>();
            test.add("minecraft:clay, minecraft:clay_ball, 4");

            baseStackStorage = builder
                .comment("The number of item stacks held in a basic unit of storage.",
                    "1x1 drawers hold 8 units, 1x2 drawers hold 4 units, 2x2 drawers hold 2 units.",
                    "Half-depth drawers hold half those amounts.")
                .define("baseStackStorage", 4);
            controllerRange = builder
                .comment("Controller range defines how far away a drawer can be connected",
                    "on X, Y, or Z planes.  The default value of 50 gives the controller a very",
                    "large range, but not beyond the chunk load distance.")
                .defineInRange("controllerRange", 50, 1, 75);
            enableAnalogRedstone = builder
                .comment("Whether redstone upgrades should emit an analog redstone signal, requiring",
                    "the use of a comparator to read it.")
                .define("enableAnalogRedstone", true);
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
            compRules = builder
                .comment("List of rules in format \"domain:item1, domain:item2, n\".",
                    "Creates a compacting drawer rule to convert 1 of item1 into n of item2.")
                .defineList("compactingRules", test, obj -> CompTierRegistry.validateRuleSyntax((String)obj));

            builder.pop();
        }

        /*cache.compRules = config.getStringList("compactingRules", sectionRegistries.getQualifiedName(), new String[] { "minecraft:clay, minecraft:clay_ball, 4" }, "Items should be in form domain:item or domain:item:meta.", null, LANG_PREFIX + "registries.compRules");
        if (StorageDrawers.compRegistry != null) {
            for (String rule : cache.compRules)
                StorageDrawers.compRegistry.register(rule);
        }*/

        public int getBaseStackStorage() {
            if (!isLoaded())
                return 1;

            return baseStackStorage.get();
        }
    }

    public static class Integration {
        public final ModConfigSpec.ConfigValue<Boolean> wailaStackRemainder;
        public final ModConfigSpec.BooleanValue wailaRespectQuantifyKey;

        public Integration (ModConfigSpec.Builder builder) {
            builder.push("Integration");

            wailaStackRemainder = builder
                    .comment("When true, shows quantity as NxS + R (by stack size) rather than count")
                    .define("wailaStackRemainder", true);

            wailaRespectQuantifyKey = builder
                    .comment("When true, does not show current quantities unless quantify key was used")
                    .define("wailaRespectQuantifyKey", false);

            builder.pop();
        }
    }

    public static class Upgrades {
        public final ModConfigSpec.ConfigValue<Integer> level1Mult;
        public final ModConfigSpec.ConfigValue<Integer> level2Mult;
        public final ModConfigSpec.ConfigValue<Integer> level3Mult;
        public final ModConfigSpec.ConfigValue<Integer> level4Mult;
        public final ModConfigSpec.ConfigValue<Integer> level5Mult;

        public Upgrades (ModConfigSpec.Builder builder) {
            builder.push("StorageUpgrades");
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
}
