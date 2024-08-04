package com.jaquadro.minecraft.storagedrawers.config;

import java.util.ArrayList;
import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Render RENDER = new Render(BUILDER);
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
        public final ModConfigSpec.ConfigValue<Boolean> invertShift;
        public final ModConfigSpec.ConfigValue<Boolean> invertClick;

        public General(ModConfigSpec.Builder builder) {
            builder.push("General");

            invertShift = builder
                .comment("Invert the behavior of the shift key for extracting items")
                .define("invertShift", false);
            invertClick = builder
                .comment("Invert left and right click action on drawers")
                .define("invertClick", false);

            builder.pop();
        }
    }

    public static class Integration {
        public final ModConfigSpec.ConfigValue<Boolean> enableWaila;
        public final ModConfigSpec.ConfigValue<Boolean> enableTheOneProbe;

        public Integration(ModConfigSpec.Builder builder) {
            builder.push("Integration");

            enableWaila = builder
                .comment("Enable extended data display in WAILA if present")
                .define("enableWaila", true);
            enableTheOneProbe = builder
                .comment("Enable extended data display in The One Probe if present")
                .define("enableTheOneProbe", true);

            builder.pop();
        }
    }

    public static class Render {
        public final ModConfigSpec.ConfigValue<Double> labelRenderDistance;
        public final ModConfigSpec.ConfigValue<Double> quantityRenderDistance;
        public final ModConfigSpec.ConfigValue<Double> quantityFadeDistance;

        public Render(ModConfigSpec.Builder builder) {
            builder.push("Render");

            labelRenderDistance = builder
                .comment("Distance in blocks before item labels stop rendering")
                .define("labelRenderDistance", 25.0);
            quantityRenderDistance = builder
                .comment("Distance in blocks before quantity numbers stop rendering")
                .define("quantityRenderDistance", 10.0);
            quantityFadeDistance = builder
                .comment("Distance in blocks before quantity numbers begin to fade out")
                .define("quantityFadeDistance", 4.0);

            builder.pop();
        }
    }
}
