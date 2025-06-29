package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.meta.BlockMetaFacing;
import com.jaquadro.minecraft.storagedrawers.block.meta.BlockMetaFacingSized;
import com.jaquadro.minecraft.storagedrawers.block.meta.BlockMetaFacingSizedOpen;
import com.jaquadro.minecraft.storagedrawers.block.meta.BlockMetaFacingSizedSlotted;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
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
        VOID("void"),
        SHROUD("shroud"),
        INDICATOR("indicator"),
        INDICATOR_COMP("indicator_comp"),
        PRIORITY_P1("priority_p1"),
        PRIORITY_P2("priority_p2"),
        PRIORITY_N1("priority_n1"),
        PRIORITY_N2("priority_n2"),
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

    private static final Map<BlockState, BlockStateModel> modelStore = new HashMap<>();
    private static final Set<BlockState> locationStore = new HashSet<>();

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
        public final Set<BlockState> targetBlocks = new HashSet<>();
        public final Map<String, BlockState> overlays = new HashMap<>();

        public DecorationSet () {
            ModBlocks.getDrawersOfType(BlockDrawers.class).forEach(blockDrawers -> {
                targetBlocks.addAll(blockDrawers.getStateDefinition().getPossibleStates());
            });
            ModBlocks.getFramedBlocks().forEach(blockTrim -> {
                if (blockTrim instanceof Block mcBlock) {
                    targetBlocks.addAll(mcBlock.getStateDefinition().getPossibleStates());

                    // TODO: How handle inventory?
                    //ModelResourceLocation invLoc = new ModelResourceLocation(BuiltInRegistries.BLOCK.getKey(mcBlock), "inventory");
                    //targetBlocks.add(invLoc.toString());
                }
            });
        }

        public void add (Direction dir, boolean half) {
            addOverlay(getVariant(DynamicPart.LOCK, dir, half), ModBlocks.META_LOCKED.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.CLAIM, dir, half), ModBlocks.META_CLAIMED.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.LOCK_CLAIM, dir, half), ModBlocks.META_LOCKED_CLAIMED.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.VOID, dir, half), ModBlocks.META_VOID.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.SHROUD, dir, half), ModBlocks.META_SHROUD.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));

            addOverlay(getVariant(DynamicPart.INDICATOR, dir, half, 1), ModBlocks.META_INDICATOR.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 1));
            addOverlay(getVariant(DynamicPart.INDICATOR, dir, half, 2), ModBlocks.META_INDICATOR.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 2));
            addOverlay(getVariant(DynamicPart.INDICATOR, dir, half, 4), ModBlocks.META_INDICATOR.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 4));
            addOverlay(getVariant(DynamicPart.INDICATOR_COMP, dir, half, 2), ModBlocks.META_COMP_INDICATOR.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 2));
            addOverlay(getVariant(DynamicPart.INDICATOR_COMP, dir, half, 3), ModBlocks.META_COMP_INDICATOR.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 3));

            addOverlay(getVariant(DynamicPart.PRIORITY_P1, dir, half), ModBlocks.META_PRIORITY_P1.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.PRIORITY_P2, dir, half), ModBlocks.META_PRIORITY_P2.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.PRIORITY_N1, dir, half), ModBlocks.META_PRIORITY_N1.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.PRIORITY_N2, dir, half), ModBlocks.META_PRIORITY_N2.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));

            addOverlay(getVariant(DynamicPart.MISSING_1, dir, half, 1), ModBlocks.META_MISSING_1_1.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.MISSING_1, dir, half, 2), ModBlocks.META_MISSING_2_1.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.MISSING_1, dir, half, 4), ModBlocks.META_MISSING_4_1.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.MISSING_2, dir, half, 2), ModBlocks.META_MISSING_2_2.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.MISSING_2, dir, half, 4), ModBlocks.META_MISSING_4_2.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.MISSING_3, dir, half, 4), ModBlocks.META_MISSING_4_3.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));
            addOverlay(getVariant(DynamicPart.MISSING_4, dir, half, 4), ModBlocks.META_MISSING_4_4.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half));

            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SIDE, dir, half, 1), ModBlocks.META_FRAMED_DRAWERS_SIDE.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 1));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SIDE, dir, half, 2), ModBlocks.META_FRAMED_DRAWERS_SIDE.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 2));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SIDE, dir, half, 4), ModBlocks.META_FRAMED_DRAWERS_SIDE.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 4));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_TRIM, dir, half, 1), ModBlocks.META_FRAMED_DRAWERS_TRIM.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 1));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_TRIM, dir, half, 2), ModBlocks.META_FRAMED_DRAWERS_TRIM.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 2));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_TRIM, dir, half, 4), ModBlocks.META_FRAMED_DRAWERS_TRIM.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 4));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_FRONT, dir, half, 1), ModBlocks.META_FRAMED_DRAWERS_FRONT.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 1));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_FRONT, dir, half, 2), ModBlocks.META_FRAMED_DRAWERS_FRONT.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 2));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_FRONT, dir, half, 4), ModBlocks.META_FRAMED_DRAWERS_FRONT.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 4));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SHADING, dir, half, 1), ModBlocks.META_FRAMED_DRAWERS_SHADING.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 1));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SHADING, dir, half, 2), ModBlocks.META_FRAMED_DRAWERS_SHADING.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 2));
            addOverlay(getVariant(DynamicPart.FRAMED_DRAWERS_SHADING, dir, half, 4), ModBlocks.META_FRAMED_DRAWERS_SHADING.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedSlotted.SLOTS, 4));

            addOverlay(getVariant(DynamicPart.FRAMED_TRIM_SIDE), ModBlocks.META_FRAMED_TRIM_SIDE.get().defaultBlockState());
            addOverlay(getVariant(DynamicPart.FRAMED_TRIM_TRIM), ModBlocks.META_FRAMED_TRIM_TRIM.get().defaultBlockState());

            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_SIDE, dir), ModBlocks.META_FRAMED_CONTROLLER_SIDE.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir));
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_TRIM, dir), ModBlocks.META_FRAMED_CONTROLLER_TRIM.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir));
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_FRONT, dir), ModBlocks.META_FRAMED_CONTROLLER_FRONT.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir));
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_SHADING, dir), ModBlocks.META_FRAMED_CONTROLLER_SHADING.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir));

            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_IO_SIDE), ModBlocks.META_FRAMED_CONTROLLER_IO_SIDE.get().defaultBlockState());
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_IO_TRIM), ModBlocks.META_FRAMED_CONTROLLER_IO_TRIM.get().defaultBlockState());
            addOverlay(getVariant(DynamicPart.FRAMED_CONTROLLER_IO_SHADING), ModBlocks.META_FRAMED_CONTROLLER_IO_SHADING.get().defaultBlockState());

            for (int i = 1; i <= 2; i++) {
                EnumCompDrawer open = EnumCompDrawer.byOpenSlots(i);
                addOverlay(getVariant(DynamicPart.FRAMED_COMP2_SIDE, dir, half, open), ModBlocks.META_FRAMED_COMPDRAWERS_2_SIDE.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedOpen.SLOTS, open));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP2_TRIM, dir, half, open), ModBlocks.META_FRAMED_COMPDRAWERS_2_TRIM.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedOpen.SLOTS, open));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP2_FRONT, dir, half, open), ModBlocks.META_FRAMED_COMPDRAWERS_2_FRONT.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedOpen.SLOTS, open));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP2_SHADING, dir, half, open), ModBlocks.META_FRAMED_COMPDRAWERS_2_SHADING.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedOpen.SLOTS, open));
            }

            for (int i = 1; i <= 3; i++) {
                EnumCompDrawer open = EnumCompDrawer.byOpenSlots(i);
                addOverlay(getVariant(DynamicPart.FRAMED_COMP3_SIDE, dir, half, open), ModBlocks.META_FRAMED_COMPDRAWERS_3_SIDE.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedOpen.SLOTS, open));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP3_TRIM, dir, half, open), ModBlocks.META_FRAMED_COMPDRAWERS_3_TRIM.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedOpen.SLOTS, open));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP3_FRONT, dir, half, open), ModBlocks.META_FRAMED_COMPDRAWERS_3_FRONT.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedOpen.SLOTS, open));
                addOverlay(getVariant(DynamicPart.FRAMED_COMP3_SHADING, dir, half, open), ModBlocks.META_FRAMED_COMPDRAWERS_3_SHADING.get().defaultBlockState().setValue(BlockMetaFacingSized.FACING, dir).setValue(BlockMetaFacingSized.HALF, half).setValue(BlockMetaFacingSizedOpen.SLOTS, open));
            }
        }

        void addOverlay(String variant, BlockState loc) {
            overlays.put(variant, addLocation(loc));
        }

        public boolean isTargetedModel (BlockState loc) {
            if (loc == null)
                return false;
            return targetBlocks.contains(loc);
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            Direction dir = Direction.from2DDataValue(i);

            INSTANCE.add(dir, true);
            INSTANCE.add(dir, false);
        }
    }

    static BlockState addLocation(BlockState loc) {
        locationStore.add(loc);
        modelStore.put(loc, null);

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

    public static Stream<BlockState> getModelLocations() {
        return locationStore.stream();
    }

    public static void tryAddModel(BlockState loc, BlockStateModel model) {
        if (loc == null)
            return;

        if (modelStore.containsKey(loc))
            modelStore.put(loc, model);
    }

    public static BlockStateModel getModel(BlockState state) {
        if (state == null)
            return null;

        BlockStateModel storedModel = modelStore.getOrDefault(state, null);
        if (storedModel == null) {
            return Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
        } else {
            return storedModel;
        }
    }

    public static BlockStateModel getModel(String variant) {
        return getModel(INSTANCE.overlays.getOrDefault(variant, null));
    }

    public static BlockStateModel getModel(DynamicPart part) {
        return getModel(getVariant(part));
    }

    public static BlockStateModel getModel(DynamicPart part, Direction dir) {
        return getModel(getVariant(part, dir));
    }

    public static BlockStateModel getModel(DynamicPart part, Direction dir, boolean half) {
        return getModel(getVariant(part, dir, half));
    }

    public static BlockStateModel getModel(DynamicPart part, Direction dir, boolean half, int slots) {
        return getModel(getVariant(part, dir, half, slots));
    }

    public static BlockStateModel getModel(DynamicPart part, Direction dir, boolean half, EnumCompDrawer slots) {
        return getModel(getVariant(part, dir, half, slots));
    }
}