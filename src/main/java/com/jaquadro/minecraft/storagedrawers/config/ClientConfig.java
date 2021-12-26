package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final Render RENDER = new Render(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    private static boolean loaded = false;
    private static List<Runnable> loadActions = new ArrayList<>();

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
                .define("invertShift", false);

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
                .define("labelRenderDistance", 20.0);
            quantityRenderDistance = builder
                .define("quantityRenderDistance", 10.0);
            quantityFadeDistance = builder
                .define("quantityFadeDistance", 4.0);

            builder.pop();
        }
    }
}
