package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DrawerModelStore
{
    private static final Map<String, BakedModel> modelStore = new HashMap<>();

    public static final DecorationSet fullDrawerDecorations = new DecorationSet(false);
    public static final DecorationSet halfDrawerDecorations = new DecorationSet(true);

    public static class DecorationSet {
        private final boolean half;

        public final Set<String> targetBlocks = new HashSet<>();
        
        public final Map<Direction, ModelResourceLocation> lockOverlays = new HashMap<>();
        public final Map<Direction, ModelResourceLocation> voidOverlays = new HashMap<>();
        public final Map<Direction, ModelResourceLocation> shroudOverlays = new HashMap<>();
        public final Map<Direction, ModelResourceLocation> indicator1 = new HashMap<>();
        public final Map<Direction, ModelResourceLocation> indicator2 = new HashMap<>();
        public final Map<Direction, ModelResourceLocation> indicator4 = new HashMap<>();
        public final Map<Direction, ModelResourceLocation> indicatorComp = new HashMap<>();
        
        public DecorationSet (boolean half) {
            this.half = half;

            ModBlocks.getDrawersofTypeAndDepth(BlockDrawers.class, half).forEach(blockDrawers -> {
                for (BlockState state : blockDrawers.getStateDefinition().getPossibleStates())
                    targetBlocks.add(BlockModelShaper.stateToModelLocation(state).toString());
            });
        }
        
        public void add (Direction dir) {
            lockOverlays.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_locked"), getVariant(dir, half))));
            voidOverlays.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_void"), getVariant(dir, half))));
            shroudOverlays.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_shroud"), getVariant(dir, half))));
            indicator1.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_indicator"), getVariant(dir, half, 1))));
            indicator2.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_indicator"), getVariant(dir, half, 2))));
            indicator4.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_indicator"), getVariant(dir, half, 4))));
            indicatorComp.put(dir, addLocation(new ModelResourceLocation(ModConstants.loc("meta_comp_indicator"), getVariant(dir, false))));
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

            fullDrawerDecorations.add(dir);
            halfDrawerDecorations.add(dir);
        }
    }

    static ModelResourceLocation addLocation(ModelResourceLocation loc) {
        modelStore.put(loc.toString(), null);
        return loc;
    }

    static String getVariant(Direction dir, boolean half) {
        return "facing=" + dir.getName() + ",half=" + half;
    }

    static String getVariant(Direction dir, boolean half, int slots) {
        return "facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
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

    public static BakedModel getModel(Map<Direction, ModelResourceLocation> modelMap, Direction dir) {
        if (modelMap == null)
            return null;

        return getModel(modelMap.getOrDefault(dir, null));
    }
}
