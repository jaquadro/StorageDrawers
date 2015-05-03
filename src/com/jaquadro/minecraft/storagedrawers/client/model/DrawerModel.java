package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumBasicDrawer;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;

import java.util.*;

public class DrawerModel extends IFlexibleBakedModel.Wrapper implements ISmartBlockModel
{
    private static final Set<ModelResourceLocation> resourceLocations = new HashSet<ModelResourceLocation>();
    private static final Map<EnumBasicDrawer, Map<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>>> stateMap = new HashMap<EnumBasicDrawer, Map<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>>>();
    private static final Map<ModelResourceLocation, IFlexibleBakedModel> modelCache = new HashMap<ModelResourceLocation, IFlexibleBakedModel>();

    public static void initialize (IRegistry modelRegistry) {
        initailizeResourceLocations();

        for (ModelResourceLocation loc : resourceLocations) {
            Object object = modelRegistry.getObject(loc);
            if (object instanceof IFlexibleBakedModel) {
                modelCache.put(loc, (IFlexibleBakedModel)object);
                modelRegistry.putObject(loc, new DrawerModel((IFlexibleBakedModel)object));
            }
        }
    }

    public static void initailizeResourceLocations () {
        for (EnumBasicDrawer drawerType : EnumBasicDrawer.values()) {
            Map<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>> dirMap = new HashMap<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>>();
            stateMap.put(drawerType, dirMap);

            for (EnumFacing dir : EnumFacing.values()) {
                if (dir.getAxis() == EnumFacing.Axis.Y)
                    continue;

                Map<BlockPlanks.EnumType, ModelResourceLocation> typeMap = new HashMap<BlockPlanks.EnumType, ModelResourceLocation>();
                dirMap.put(dir, typeMap);

                for (BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()) {
                    String key = StorageDrawers.MOD_ID + ":basicDrawers#block=" + drawerType + ",facing=" + dir + ",variant=" + woodType;
                    ModelResourceLocation location = new ModelResourceLocation(key);

                    resourceLocations.add(location);
                    typeMap.put(woodType, location);
                }
            }
        }
    }

    public DrawerModel (IFlexibleBakedModel parent) {
        super(parent, parent.getFormat());
    }

    @Override
    public IFlexibleBakedModel handleBlockState (IBlockState state) {
        EnumBasicDrawer drawer = (EnumBasicDrawer)state.getValue(BlockDrawers.BLOCK);
        EnumFacing dir = (EnumFacing)state.getValue(BlockDrawers.FACING);
        BlockPlanks.EnumType woodType = (BlockPlanks.EnumType)state.getValue(BlockDrawers.VARIANT);

        Map<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>> dirMap = stateMap.get(drawer);
        if (dirMap == null)
            return null;

        Map<BlockPlanks.EnumType, ModelResourceLocation> typeMap = dirMap.get(dir);
        if (typeMap == null)
            return null;

        ModelResourceLocation location = typeMap.get(woodType);
        if (location == null)
            return null;

        return modelCache.get(location);
    }
}
