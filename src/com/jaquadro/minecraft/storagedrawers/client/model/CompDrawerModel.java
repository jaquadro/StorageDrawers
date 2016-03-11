package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.*;

public class CompDrawerModel extends IFlexibleBakedModel.Wrapper implements ISmartBlockModel
{
    private static final Set<ModelResourceLocation> resourceLocations = new HashSet<ModelResourceLocation>();
    private static final Set<ModelResourceLocation> itemResourceLocations = new HashSet<ModelResourceLocation>();
    private static final Map<EnumCompDrawer, Map<EnumFacing, ModelResourceLocation>> stateMap = new HashMap<EnumCompDrawer, Map<EnumFacing, ModelResourceLocation>>();
    private static final Map<ModelResourceLocation, IFlexibleBakedModel> modelCache = new HashMap<ModelResourceLocation, IFlexibleBakedModel>();

    public static void initialize (IRegistry<ModelResourceLocation, IBakedModel> modelRegistry) {
        initailizeResourceLocations();

        for (ModelResourceLocation loc : resourceLocations) {
            Object object = modelRegistry.getObject(loc);
            if (object instanceof IFlexibleBakedModel) {
                modelCache.put(loc, (IFlexibleBakedModel)object);
                modelRegistry.putObject(loc, new CompDrawerModel((IFlexibleBakedModel)object));
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
        for (EnumCompDrawer drawerType : EnumCompDrawer.values()) {
            Map<EnumFacing, ModelResourceLocation> dirMap = new HashMap<EnumFacing, ModelResourceLocation>();
            stateMap.put(drawerType, dirMap);

            for (EnumFacing dir : EnumFacing.values()) {
                if (dir.getAxis() == EnumFacing.Axis.Y)
                    continue;

                String key = StorageDrawers.MOD_ID + ":compDrawers#facing=" + dir + ",slots=" + drawerType;
                ModelResourceLocation location = new ModelResourceLocation(key);

                resourceLocations.add(location);
                dirMap.put(dir, location);
            }
        }

        for (EnumCompDrawer drawerType : EnumCompDrawer.values()) {
            String key = StorageDrawers.MOD_ID + ":compDrawers_" + drawerType + "#inventory";
            ModelResourceLocation location = new ModelResourceLocation(key);

            itemResourceLocations.add(location);
        }
    }

    public CompDrawerModel (IFlexibleBakedModel parent) {
        super(parent, parent.getFormat());
    }

    @Override
    public IBakedModel handleBlockState (IBlockState state) {
        EnumCompDrawer drawer = (EnumCompDrawer)state.getValue(BlockCompDrawers.SLOTS);
        EnumFacing dir = state.getValue(BlockDrawers.FACING);

        Map<EnumFacing, ModelResourceLocation> dirMap = stateMap.get(drawer);
        if (dirMap == null)
            return null;

        ModelResourceLocation location = dirMap.get(dir);
        if (location == null)
            return null;

        IExtendedBlockState xstate = (IExtendedBlockState)state;
        TileEntityDrawers tile = xstate.getValue(BlockDrawers.TILE);

        if (!DrawerDecoratorModel.shouldHandleState(tile))
            return modelCache.get(location);

        return new DrawerDecoratorModel(modelCache.get(location), xstate, drawer, dir, tile);
    }
}
