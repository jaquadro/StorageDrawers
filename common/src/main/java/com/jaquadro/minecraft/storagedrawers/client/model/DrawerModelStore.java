package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DrawerModelStore
{
    public enum DynamicPart {
        LOCK("lock"),
        VOID("void"),
        SHROUD("shroud"),
        INDICATOR("indicator"),
        INDICATOR_COMP("indicator_comp"),
        MISSING_1("missing_1"),
        MISSING_2("missing_2"),
        MISSING_3("missing_3"),
        MISSING_4("missing_4");

        private String name;

        DynamicPart(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final Map<String, BakedModel> modelStore = new HashMap<>();
    private static final Map<String, ModelResourceLocation> locationStore = new HashMap<>();

    public static final DecorationSet INSTANCE = new DecorationSet();

    private static final DynamicPart[] missingSlots1 = {
        DynamicPart.MISSING_1
    };
    private static final DynamicPart[] missingSlots2 = {
        DynamicPart.MISSING_1, DynamicPart.MISSING_2
    };
    private static final DynamicPart[] missingSlots4 = {
        DynamicPart.MISSING_1, DynamicPart.MISSING_2, DynamicPart.MISSING_3, DynamicPart.MISSING_4
    };
    public static final DynamicPart[][] missingSlots = {
        missingSlots1, missingSlots2, new DynamicPart[0], missingSlots4
    };

    public static class DecorationSet {
        public final Set<String> targetBlocks = new HashSet<>();
        public final Map<String, ModelResourceLocation> overlays = new HashMap<>();
        
        public DecorationSet () {
            ModBlocks.getDrawersOfType(BlockDrawers.class).forEach(blockDrawers -> {
                for (BlockState state : blockDrawers.getStateDefinition().getPossibleStates())
                    targetBlocks.add(BlockModelShaper.stateToModelLocation(state).toString());
            });
        }
        
        public void add (Direction dir, boolean half) {
            addOverlay(getVariant(DynamicPart.LOCK, dir, half), new ModelResourceLocation(ModConstants.loc("meta_locked"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.VOID, dir, half), new ModelResourceLocation(ModConstants.loc("meta_void"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.SHROUD, dir, half), new ModelResourceLocation(ModConstants.loc("meta_shroud"), getVariant(dir, half)));

            addOverlay(getVariant(DynamicPart.INDICATOR, dir, half, 1), new ModelResourceLocation(ModConstants.loc("meta_indicator"), getVariant(dir, half, 1)));
            addOverlay(getVariant(DynamicPart.INDICATOR, dir, half, 2), new ModelResourceLocation(ModConstants.loc("meta_indicator"), getVariant(dir, half, 2)));
            addOverlay(getVariant(DynamicPart.INDICATOR, dir, half, 4), new ModelResourceLocation(ModConstants.loc("meta_indicator"), getVariant(dir, half, 4)));
            addOverlay(getVariant(DynamicPart.INDICATOR_COMP, dir, half, 2), new ModelResourceLocation(ModConstants.loc("meta_comp_indicator"), getVariant(dir, half, 2)));
            addOverlay(getVariant(DynamicPart.INDICATOR_COMP, dir, half, 3), new ModelResourceLocation(ModConstants.loc("meta_comp_indicator"), getVariant(dir, half, 3)));

            addOverlay(getVariant(DynamicPart.MISSING_1, dir, half, 1), new ModelResourceLocation(ModConstants.loc("meta_missing_slot_1_1"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.MISSING_1, dir, half, 2), new ModelResourceLocation(ModConstants.loc("meta_missing_slot_2_1"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.MISSING_1, dir, half, 4), new ModelResourceLocation(ModConstants.loc("meta_missing_slot_4_1"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.MISSING_2, dir, half, 2), new ModelResourceLocation(ModConstants.loc("meta_missing_slot_2_2"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.MISSING_2, dir, half, 4), new ModelResourceLocation(ModConstants.loc("meta_missing_slot_4_2"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.MISSING_3, dir, half, 4), new ModelResourceLocation(ModConstants.loc("meta_missing_slot_4_3"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.MISSING_4, dir, half, 4), new ModelResourceLocation(ModConstants.loc("meta_missing_slot_4_4"), getVariant(dir, half)));

            //lockOverlays.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_locked"), getVariant(dir, half))));
            //voidOverlays.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_void"), getVariant(dir, half))));
            //shroudOverlays.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_shroud"), getVariant(dir, half))));
            //indicator1.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_indicator"), getVariant(dir, half, 1))));
            //indicator2.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_indicator"), getVariant(dir, half, 2))));
            //indicator4.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_indicator"), getVariant(dir, half, 4))));
            //indicatorComp.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_comp_indicator"), getVariant(dir, false))));
        }

        void addOverlay(String variant, ModelResourceLocation loc) {
            overlays.put(variant, addLocation(loc));
        }

        public boolean isTargetedModel (ModelResourceLocation loc) {
            if (loc == null)
                return false;
            return targetBlocks.contains(loc.toString());
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            Direction dir = Direction.from2DDataValue(i);

            INSTANCE.add(dir, true);
            INSTANCE.add(dir, false);
        }
    }

    static ModelResourceLocation addLocation(ModelResourceLocation loc) {
        locationStore.put(loc.toString(), loc);
        modelStore.put(loc.toString(), null);

        return loc;
    }

    static String getVariant(Direction dir, boolean half) {
        return "facing=" + dir.getName() + ",half=" + half;
    }

    static String getVariant(Direction dir, boolean half, int slots) {
        return "facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
    }

    static String getVariant(DynamicPart part, Direction dir, boolean half) {
        return "part=" + part.getName() + ",facing=" + dir.getName() + ",half=" + half;
    }

    static String getVariant(DynamicPart part, Direction dir, boolean half, int slots) {
        return "part=" + part.getName() + ",facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
    }

    public static Stream<ModelResourceLocation> getModelLocations() {
        return locationStore.values().stream();
    }

    public static void tryAddModel(ModelResourceLocation loc, BakedModel model) {
        if (loc == null)
            return;

        String key = loc.toString();
        if (modelStore.containsKey(key))
            modelStore.put(key, model);
    }

    public static BakedModel getModel(ModelResourceLocation loc) {
        if (loc == null)
            return null;

        return modelStore.getOrDefault(loc.toString(), null);
    }

    public static BakedModel getModel(String variant) {
        return getModel(INSTANCE.overlays.getOrDefault(variant, null));
    }

    public static BakedModel getModel(DynamicPart part, Direction dir, boolean half) {
        return getModel(getVariant(part, dir, half));
    }

    public static BakedModel getModel(DynamicPart part, Direction dir, boolean half, int slots) {
        return getModel(getVariant(part, dir, half, slots));
    }
}
