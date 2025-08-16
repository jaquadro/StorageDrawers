package com.jaquadro.minecraft.storagedrawers.config;

import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.config.ConfigSpec;
import com.texelsaurus.minecraft.chameleon.service.ChameleonConfig;

public class ModClientConfig extends ConfigSpec
{
    public static ModClientConfig INSTANCE = new ModClientConfig();

    private final ChameleonConfig commonConfig;
    public General GENERAL;
    public Integration INTEGRATION;
    public Render RENDER;

    private ModClientConfig () {
        commonConfig = ChameleonServices.CONFIG.create(this);
    }

    public ChameleonConfig context() {
        return commonConfig;
    }

    @Override
    public void init() {
        GENERAL = new General();
        INTEGRATION = new Integration();
        RENDER = new Render();
    }

    class ConfigSection {
        protected final String name;
        protected final String[] comment;

        public ConfigSection (String name, String... comment) {
            this.name = name;
            this.comment = comment;
        }

        public ConfigSection build () {
            if (comment != null && comment.length > 0)
                commonConfig.comment(comment);
            commonConfig.pushGroup(name);

            buildEntries();

            commonConfig.popGroup();
            return this;
        }

        protected void buildEntries () { }
    }

    public class General {
        public final ChameleonConfig.ConfigEntry<Boolean> invertShift;
        public final ChameleonConfig.ConfigEntry<Boolean> invertClick;

        public General() {
            commonConfig.pushGroup("General");

            invertShift = commonConfig.define("invertShift", false)
                .comment("Invert the behavior of the shift key for extracting items")
                .build();
            invertClick = commonConfig.define("invertClick", false)
                .comment("Invert left and right click action on drawers")
                .build();

            commonConfig.popGroup();
        }
    }

    public class Integration {
        public final ChameleonConfig.ConfigEntry<Boolean> enableWaila;
        public final ChameleonConfig.ConfigEntry<Boolean> enableTheOneProbe;

        public Integration() {
            commonConfig.pushGroup("Integration");

            enableWaila = commonConfig.define("enableWaila", true)
                .comment("Enable extended data display in WAILA if present")
                .build();
            enableTheOneProbe = commonConfig.define("enableTheOneProbe", true)
                .comment("Enable extended data display in The One Probe if present")
                .build();

            commonConfig.popGroup();
        }
    }

    public class Render
    {
        public class FramedDrawers extends ConfigSection
        {
            public ChameleonConfig.ConfigEntry<Boolean> renderTranslucentMaterials;

            public FramedDrawers (String name, String... comment) {
                super(name, comment);

                renderTranslucentMaterials = commonConfig.define("renderTranslucentMaterials", true)
                    .comment("", "Attempts to render 'non-opaque' materials on the translucent render pass.",
                        "This may cause artifacting, particularly if using non-opaque materials on the 'front' face.");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                renderTranslucentMaterials.build();
            }

            @Override
            public FramedDrawers build () {
                super.build();
                return this;
            }
        }

        public final ChameleonConfig.ConfigEntry<Double> labelRenderDistance;
        public final ChameleonConfig.ConfigEntry<Double> quantityRenderDistance;
        public final ChameleonConfig.ConfigEntry<Double> quantityFadeDistance;

        public final FramedDrawers framedDrawers;

        public Render() {
            commonConfig.pushGroup("Render");

            labelRenderDistance = commonConfig.define("labelRenderDistance", 20.0)
                .comment("Distance in blocks before item labels stop rendering")
                .build();
            quantityRenderDistance = commonConfig.define("quantityRenderDistance", 10.0)
                .comment("", "Distance in blocks before quantity numbers stop rendering")
                .build();
            quantityFadeDistance = commonConfig.define("quantityFadeDistance", 20.0)
                .comment("", "Distance in blocks before quantity numbers begin to fade out")
                .build();

            framedDrawers = new FramedDrawers("FramedDrawers",
                "Render settings specific to framed drawers.").build();

            commonConfig.popGroup();
        }
    }
}
