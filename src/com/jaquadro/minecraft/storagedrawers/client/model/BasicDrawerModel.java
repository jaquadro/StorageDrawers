package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.*;

public class BasicDrawerModel extends IFlexibleBakedModel.Wrapper implements ISmartBlockModel
{
    private static final Set<ModelResourceLocation> resourceLocations = new HashSet<ModelResourceLocation>();
    private static final Set<ModelResourceLocation> itemResourceLocations = new HashSet<ModelResourceLocation>();
    private static final Map<EnumBasicDrawer, Map<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>>> stateMap = new HashMap<EnumBasicDrawer, Map<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>>>();
    private static final Map<ModelResourceLocation, IFlexibleBakedModel> modelCache = new HashMap<ModelResourceLocation, IFlexibleBakedModel>();

    public static void initialize (IRegistry<ModelResourceLocation, IBakedModel> modelRegistry) {
        initailizeResourceLocations();

        for (ModelResourceLocation loc : resourceLocations) {
            IBakedModel object = modelRegistry.getObject(loc);
            if (object instanceof IFlexibleBakedModel) {
                modelCache.put(loc, (IFlexibleBakedModel)object);
                modelRegistry.putObject(loc, new BasicDrawerModel((IFlexibleBakedModel)object));
            }
        }

        for (ModelResourceLocation loc : itemResourceLocations) {
            IBakedModel object = modelRegistry.getObject(loc);
            if (object != null) {
                modelRegistry.putObject(loc, new BasicDrawerItemModel(object));
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

            for (BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()) {
                String key = StorageDrawers.MOD_ID + ":basicDrawers_" + drawerType.getName() + "_" + woodType.getName() + "#inventory";
                ModelResourceLocation location = new ModelResourceLocation(key);

                itemResourceLocations.add(location);
            }
        }
    }

    public BasicDrawerModel (IFlexibleBakedModel parent) {
        super(parent, parent.getFormat());
    }

    @Override
    public IBakedModel handleBlockState (IBlockState state) {
        EnumBasicDrawer drawer = (EnumBasicDrawer)state.getValue(BlockDrawers.BLOCK);
        EnumFacing dir = state.getValue(BlockDrawers.FACING);
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

        if (!(state instanceof IExtendedBlockState))
            return modelCache.get(location);

        IExtendedBlockState xstate = (IExtendedBlockState)state;
        TileEntityDrawers tile = xstate.getValue(BlockDrawers.TILE);

        if (!DrawerDecoratorModel.shouldHandleState(tile))
            return modelCache.get(location);

        return new DrawerDecoratorModel(modelCache.get(location), xstate, drawer, dir, tile);
    }
}
