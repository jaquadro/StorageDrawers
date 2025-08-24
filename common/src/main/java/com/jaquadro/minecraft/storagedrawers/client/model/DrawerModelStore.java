package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DrawerModelStore
{
    public enum DynamicPart
    {
        LOCK("lock"),
        CLAIM("claim"),
        LOCK_CLAIM("lock_claim"),
        VOID_ICON("void_icon"),
        SHROUD_ICON("shroud_icon"),
        SUSPEND_ICON("suspend_icon"),
        MAGNET_ICON("magnet_icon"),
        PRIORITY_P1_ICON("priority_p1_icon"),
        PRIORITY_P2_ICON("priority_p2_icon"),
        PRIORITY_N1_ICON("priority_n1_icon"),
        PRIORITY_N2_ICON("priority_n2_icon"),
        INDICATOR("indicator"),
        INDICATOR_COMP("indicator_comp"),
        RIGHT_LABEL("right_label"),
        HOPPER("hopper"),
        MISSING_1("missing_1"),
        MISSING_2("missing_2"),
        MISSING_3("missing_3"),
        MISSING_4("missing_4"),
        FRAMED_DRAWERS_SIDE("framed_drawers_side"),
        FRAMED_DRAWERS_TRIM("framed_drawers_trim"),
        FRAMED_DRAWERS_FRONT("framed_drawers_front"),
        FRAMED_DRAWERS_SHADING("framed_drawers_shading"),
        FRAMED_TRIM_SIDE("framed_trim_side"),
        FRAMED_TRIM_TRIM("framed_trim_trim"),
        FRAMED_CONTROLLER_SIDE("framed_controller_side"),
        FRAMED_CONTROLLER_TRIM("framed_controller_trim"),
        FRAMED_CONTROLLER_FRONT("framed_controller_front"),
        FRAMED_CONTROLLER_SHADING("framed_controller_shading"),
        FRAMED_CONTROLLER_IO_SIDE("framed_controller_io_side"),
        FRAMED_CONTROLLER_IO_TRIM("framed_controller_io_trim"),
        FRAMED_CONTROLLER_IO_SHADING("framed_controller_io_shading"),
        FRAMED_COMP2_SIDE("framed_comp2_side"),
        FRAMED_COMP2_TRIM("framed_comp2_trim"),
        FRAMED_COMP2_FRONT("framed_comp2_front"),
        FRAMED_COMP2_SHADING("framed_comp2_shading"),
        FRAMED_COMP3_SIDE("framed_comp3_side"),
        FRAMED_COMP3_TRIM("framed_comp3_trim"),
        FRAMED_COMP3_FRONT("framed_comp3_front"),
        FRAMED_COMP3_SHADING("framed_comp3_shading"),
        ;

        private String name;

        DynamicPart (String name) {
            this.name = name;
        }

        public String getName () {
            return name;
        }
    }

    public static class FrameMatSet {
        private DynamicPart sidePart;
        private DynamicPart trimPart;
        private DynamicPart frontPart;
        private DynamicPart shadeFrontPart;
        private DynamicPart shadeSidePart;

        public DynamicPart sidePart () {
            return sidePart;
        }

        public DynamicPart trimPart () {
            return trimPart;
        }

        public DynamicPart frontPart () {
            return frontPart;
        }

        public DynamicPart shadeFrontPart () {
            return shadeFrontPart;
        }

        public DynamicPart shadeSidePart () {
            return shadeSidePart;
        }

        public FrameMatSet sidePart (DynamicPart part) {
            this.sidePart = part;
            return this;
        }

        public FrameMatSet trimPart (DynamicPart part) {
            this.trimPart = part;
            return this;
        }

        public FrameMatSet frontPart (DynamicPart part) {
            this.frontPart = part;
            return this;
        }

        public FrameMatSet shadeFrontPart (DynamicPart part) {
            this.shadeFrontPart = part;
            return this;
        }

        public FrameMatSet shadeSidePart (DynamicPart part) {
            this.shadeSidePart = part;
            return this;
        }
    }

    public static final FrameMatSet FramedStandardDrawerMaterials = new FrameMatSet()
        .sidePart(DynamicPart.FRAMED_DRAWERS_SIDE).trimPart(DynamicPart.FRAMED_DRAWERS_TRIM)
        .frontPart(DynamicPart.FRAMED_DRAWERS_FRONT).shadeFrontPart(DynamicPart.FRAMED_DRAWERS_SHADING);
    public static final FrameMatSet FramedComp2DrawerMaterials = new FrameMatSet()
        .sidePart(DynamicPart.FRAMED_COMP2_SIDE).trimPart(DynamicPart.FRAMED_COMP2_TRIM)
        .frontPart(DynamicPart.FRAMED_COMP2_FRONT).shadeFrontPart(DynamicPart.FRAMED_COMP2_SHADING);
    public static final FrameMatSet FramedComp3DrawerMaterials = new FrameMatSet()
        .sidePart(DynamicPart.FRAMED_COMP3_SIDE).trimPart(DynamicPart.FRAMED_COMP3_TRIM)
        .frontPart(DynamicPart.FRAMED_COMP3_FRONT).shadeFrontPart(DynamicPart.FRAMED_COMP3_SHADING);
    public static final FrameMatSet FramedControllerMaterials = new FrameMatSet()
        .sidePart(DynamicPart.FRAMED_CONTROLLER_SIDE).trimPart(DynamicPart.FRAMED_CONTROLLER_TRIM)
        .frontPart(DynamicPart.FRAMED_CONTROLLER_FRONT).shadeFrontPart(DynamicPart.FRAMED_CONTROLLER_SHADING);
    public static final FrameMatSet FramedControllerIOMaterials = new FrameMatSet()
        .sidePart(DynamicPart.FRAMED_CONTROLLER_IO_SIDE).trimPart(DynamicPart.FRAMED_CONTROLLER_IO_TRIM)
        .shadeFrontPart(DynamicPart.FRAMED_CONTROLLER_IO_SHADING);
    public static final FrameMatSet FramedTrimMaterials = new FrameMatSet()
        .sidePart(DynamicPart.FRAMED_TRIM_SIDE).trimPart(DynamicPart.FRAMED_TRIM_TRIM);

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
            ModBlocks.getFramedBlocks().forEach(blockTrim -> {
                if (blockTrim instanceof Block mcBlock) {
                    for (BlockState state : mcBlock.getStateDefinition().getPossibleStates())
                        targetBlocks.add(BlockModelShaper.stateToModelLocation(state).toString());

                    ModelResourceLocation invLoc = new ModelResourceLocation(BuiltInRegistries.BLOCK.getKey(mcBlock), "inventory");
                    targetBlocks.add(invLoc.toString());
                }
            });
        }

        public void add () {
            addOverlay(getVariant(DynamicPart.VOID_ICON), new ModelResourceLocation(ModConstants.loc("meta_void_icon"), getVariant()));
            addOverlay(getVariant(DynamicPart.SHROUD_ICON), new ModelResourceLocation(ModConstants.loc("meta_shroud_icon"), getVariant()));
            addOverlay(getVariant(DynamicPart.SUSPEND_ICON), new ModelResourceLocation(ModConstants.loc("meta_suspend_icon"), getVariant()));
            addOverlay(getVariant(DynamicPart.MAGNET_ICON), new ModelResourceLocation(ModConstants.loc("meta_magnet_icon"), getVariant()));
            addOverlay(getVariant(DynamicPart.PRIORITY_P1_ICON), new ModelResourceLocation(ModConstants.loc("meta_priority_p1_icon"), getVariant()));
            addOverlay(getVariant(DynamicPart.PRIORITY_P2_ICON), new ModelResourceLocation(ModConstants.loc("meta_priority_p2_icon"), getVariant()));
            addOverlay(getVariant(DynamicPart.PRIORITY_N1_ICON), new ModelResourceLocation(ModConstants.loc("meta_priority_n1_icon"), getVariant()));
            addOverlay(getVariant(DynamicPart.PRIORITY_N2_ICON), new ModelResourceLocation(ModConstants.loc("meta_priority_n2_icon"), getVariant()));
            addOverlay(getVariant(DynamicPart.HOPPER), new ModelResourceLocation(ModConstants.loc("meta_hopper"), getVariant()));
        }

        public void add (Direction dir, boolean half) {
            addOverlay(getVariant(DynamicPart.LOCK, dir, half), new ModelResourceLocation(ModConstants.loc("meta_locked"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.CLAIM, dir, half), new ModelResourceLocation(ModConstants.loc("meta_claimed"), getVariant(dir, half)));
            addOverlay(getVariant(DynamicPart.LOCK_CLAIM, dir, half), new ModelResourceLocation(ModConstants.loc("meta_locked_claimed"), getVariant(dir, half)));

            for (int i = 1; i <= 6; i++)
                addOverlay(getSlotVariant(DynamicPart.RIGHT_LABEL, dir, half, i), new ModelResourceLocation(ModConstants.loc("meta_right_label"), getSlotVariant(dir, half, i)));

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

            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SIDE, dir, half, 1), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_side"), getVariant(dir, half, 1)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SIDE, dir, half, 2), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_side"), getVariant(dir, half, 2)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SIDE, dir, half, 4), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_side"), getVariant(dir, half, 4)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_TRIM, dir, half, 1), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_trim"), getVariant(dir, half, 1)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_TRIM, dir, half, 2), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_trim"), getVariant(dir, half, 2)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_TRIM, dir, half, 4), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_trim"), getVariant(dir, half, 4)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_FRONT, dir, half, 1), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_front"), getVariant(dir, half, 1)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_FRONT, dir, half, 2), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_front"), getVariant(dir, half, 2)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_FRONT, dir, half, 4), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_front"), getVariant(dir, half, 4)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SHADING, dir, half, 1), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_shading"), getVariant(dir, half, 1)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SHADING, dir, half, 2), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_shading"), getVariant(dir, half, 2)));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SHADING, dir, half, 4), new ModelResourceLocation(ModConstants.loc("meta_framed_drawers_shading"), getVariant(dir, half, 4)));

            addOverlay(getVariant(DynamicPart.FRAMED_TRIM_SIDE), new ModelResourceLocation(ModConstants.loc("meta_framed_trim_side"), getVariant()));
            addOverlay(getVariant(DynamicPart.FRAMED_TRIM_TRIM), new ModelResourceLocation(ModConstants.loc("meta_framed_trim_trim"), getVariant()));

            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_SIDE, dir), new ModelResourceLocation(ModConstants.loc("meta_framed_controller_side"), getVariant(dir)));
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_TRIM, dir), new ModelResourceLocation(ModConstants.loc("meta_framed_controller_trim"), getVariant(dir)));
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_FRONT, dir), new ModelResourceLocation(ModConstants.loc("meta_framed_controller_front"), getVariant(dir)));
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_SHADING, dir), new ModelResourceLocation(ModConstants.loc("meta_framed_controller_shading"), getVariant(dir)));

            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_IO_SIDE), new ModelResourceLocation(ModConstants.loc("meta_framed_controller_io_side"), getVariant()));
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_IO_TRIM), new ModelResourceLocation(ModConstants.loc("meta_framed_controller_io_trim"), getVariant()));
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_IO_SHADING), new ModelResourceLocation(ModConstants.loc("meta_framed_controller_io_shading"), getVariant()));

            for (int i = 1; i <= 2; i++) {
                EnumCompDrawer open = EnumCompDrawer.byOpenSlots(i);
                addOverlay(getVariant(DynamicPart.FRAMED_COMP2_SIDE, dir, half, open), new ModelResourceLocation(ModConstants.loc("meta_framed_compdrawers_2_side"), getVariant(dir, half, open)));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP2_TRIM, dir, half, open), new ModelResourceLocation(ModConstants.loc("meta_framed_compdrawers_2_trim"), getVariant(dir, half, open)));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP2_FRONT, dir, half, open), new ModelResourceLocation(ModConstants.loc("meta_framed_compdrawers_2_front"), getVariant(dir, half, open)));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP2_SHADING, dir, half, open), new ModelResourceLocation(ModConstants.loc("meta_framed_compdrawers_2_shading"), getVariant(dir, half, open)));
            }

            for (int i = 1; i <= 3; i++) {
                EnumCompDrawer open = EnumCompDrawer.byOpenSlots(i);
                addOverlay(getVariant(DynamicPart.FRAMED_COMP3_SIDE, dir, half, open), new ModelResourceLocation(ModConstants.loc("meta_framed_compdrawers_3_side"), getVariant(dir, half, open)));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP3_TRIM, dir, half, open), new ModelResourceLocation(ModConstants.loc("meta_framed_compdrawers_3_trim"), getVariant(dir, half, open)));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP3_FRONT, dir, half, open), new ModelResourceLocation(ModConstants.loc("meta_framed_compdrawers_3_front"), getVariant(dir, half, open)));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP3_SHADING, dir, half, open), new ModelResourceLocation(ModConstants.loc("meta_framed_compdrawers_3_shading"), getVariant(dir, half, open)));
            }
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
        INSTANCE.add();

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

    static String getVariant() {
        return "";
    }

    static String getVariant(Direction dir) {
        return "facing=" + dir.getName();
    }

    static String getVariant(Direction dir, boolean half) {
        return "facing=" + dir.getName() + ",half=" + half;
    }

    static String getVariant(Direction dir, boolean half, int slots) {
        return "facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
    }

    static String getVariant(Direction dir, boolean half, EnumCompDrawer slots) {
        return "facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
    }

    static String getSlotVariant(Direction dir, boolean half, int slot) {
        return "facing=" + dir.getName() + ",half=" + half + ",slot=" + slot;
    }

    static String getVariant(DynamicPart part) {
        return "part=" + part.getName();
    }

    static String getVariant(DynamicPart part, Direction dir) {
        return "part=" + part.getName() + ",facing=" + dir.getName();
    }

    static String getVariant(DynamicPart part, Direction dir, boolean half) {
        return "part=" + part.getName() + ",facing=" + dir.getName() + ",half=" + half;
    }

    static String getVariant(DynamicPart part, Direction dir, boolean half, int slots) {
        return "part=" + part.getName() + ",facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
    }

    static String getVariant(DynamicPart part, Direction dir, boolean half, EnumCompDrawer slots) {
        return "part=" + part.getName() + ",facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
    }

    static String getSlotVariant(DynamicPart part, Direction dir, boolean half, int slot) {
        return "part=" + part.getName() + ",facing=" + dir.getName() + ",half=" + half + ",slot=" + slot;
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

        BakedModel storedModel = modelStore.get(loc.toString());
        if (storedModel == null) {
            return Minecraft.getInstance().getModelManager().getModel(loc);
        } else {
            return storedModel;
        }
    }

    public static BakedModel getModel(String variant) {
        return getModel(INSTANCE.overlays.getOrDefault(variant, null));
    }

    public static BakedModel getModel(DynamicPart part) {
        return getModel(getVariant(part));
    }

    public static BakedModel getModel(DynamicPart part, Direction dir) {
        return getModel(getVariant(part, dir));
    }

    public static BakedModel getModel(DynamicPart part, Direction dir, boolean half) {
        return getModel(getVariant(part, dir, half));
    }

    public static BakedModel getModel(DynamicPart part, Direction dir, boolean half, int slots) {
        return getModel(getVariant(part, dir, half, slots));
    }

    public static BakedModel getModel(DynamicPart part, Direction dir, boolean half, EnumCompDrawer slots) {
        return getModel(getVariant(part, dir, half, slots));
    }

    public static BakedModel getReplacementModel(ModelResourceLocation loc, ModelResourceLocation replaceLoc) {
        String key = loc.toString() + ":" + replaceLoc.toString();
        if (modelStore.containsKey(key))
            return modelStore.get(key);

        BakedModel model = getModel(loc);
        BakedModel replacementModel = getModel(replaceLoc);
        if (replacementModel == null)
            return model;

        BakedModel merged = new SpriteReplacementModel(model, replacementModel);
        modelStore.put(key, merged);
        return merged;
    }

    public static BakedModel getReplacementModel(String variant, String replaceVariant) {
        return getReplacementModel(INSTANCE.overlays.getOrDefault(variant, null),
            INSTANCE.overlays.getOrDefault(replaceVariant, null));
    }

    public static BakedModel getReplacementModel(DynamicPart part, Direction dir, boolean half, int slot, DynamicPart iconPart) {
        return getReplacementModel(getSlotVariant(part, dir, half, slot), getVariant(iconPart));
    }
}