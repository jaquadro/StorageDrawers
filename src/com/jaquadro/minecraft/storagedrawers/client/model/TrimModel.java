package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.IRegistry;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrimModel extends IFlexibleBakedModel.Wrapper implements ISmartBlockModel
{
    private static final Set<ModelResourceLocation> resourceLocations = new HashSet<ModelResourceLocation>();
    private static final Map<BlockPlanks.EnumType, ModelResourceLocation> stateMap = new HashMap<BlockPlanks.EnumType, ModelResourceLocation>();
    private static final Map<ModelResourceLocation, IFlexibleBakedModel> modelCache = new HashMap<ModelResourceLocation, IFlexibleBakedModel>();

    public static void initialize (IRegistry modelRegistry) {
        initailizeResourceLocations();

        for (ModelResourceLocation loc : resourceLocations) {
            Object object = modelRegistry.getObject(loc);
            if (object instanceof IFlexibleBakedModel) {
                modelCache.put(loc, (IFlexibleBakedModel)object);
                modelRegistry.putObject(loc, new TrimModel((IFlexibleBakedModel)object));
            }
        }
    }

    public static void initailizeResourceLocations () {
        for (BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()) {
            String key = StorageDrawers.MOD_ID + ":trim#variant=" + woodType;
            ModelResourceLocation location = new ModelResourceLocation(key);

            resourceLocations.add(location);
            stateMap.put(woodType, location);
        }
    }

    public TrimModel (IFlexibleBakedModel parent) {
        super(parent, parent.getFormat());
    }

    @Override
    public IFlexibleBakedModel handleBlockState (IBlockState state) {
        BlockPlanks.EnumType woodType = (BlockPlanks.EnumType)state.getValue(BlockTrim.VARIANT);

        ModelResourceLocation location = stateMap.get(woodType);
        if (location == null)
            return null;

        return modelCache.get(location);
    }
}
