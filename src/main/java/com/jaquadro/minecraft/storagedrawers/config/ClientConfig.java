package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Render RENDER = new Render(BUILDER);
    public static final Integration INTEGRATION = new Integration(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

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
        public final ForgeConfigSpec.ConfigValue<Boolean> invertShift;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            invertShift = builder
                .comment("Invert the behavior of the shift key for extracting items")
                .define("invertShift", false);

            builder.pop();
        }
    }

    public static class Integration {
        public final ForgeConfigSpec.ConfigValue<Boolean> enableWaila;
        public final ForgeConfigSpec.ConfigValue<Boolean> enableTheOneProbe;

        public Integration(ForgeConfigSpec.Builder builder) {
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
        public final ForgeConfigSpec.ConfigValue<Double> labelRenderDistance;
        public final ForgeConfigSpec.ConfigValue<Double> quantityRenderDistance;
        public final ForgeConfigSpec.ConfigValue<Double> quantityFadeDistance;

        public Render(ForgeConfigSpec.Builder builder) {
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
