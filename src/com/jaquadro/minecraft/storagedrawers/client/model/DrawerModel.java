package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.*;

public class DrawerModel implements ISmartBlockModel, IFlexibleBakedModel
{
    private static final Set<ModelResourceLocation> resourceLocations = new HashSet<ModelResourceLocation>();
    private static final Map<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>> stateMap = new HashMap<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>>();
    private static final Map<ModelResourceLocation, IBakedModel> modelCache = new HashMap<ModelResourceLocation, IBakedModel>();

    //private static final ModelResourceLocation fullDrawers2OakResourceLocation = new ModelResourceLocation(StorageDrawers.MOD_ID + ":fullDrawers2#facing=north,variant=oak");

    private IBakedModel fullDrawers2Oak;

    public static void initialize (IRegistry modelRegistry) {
        initailizeResourceLocations();

        DrawerModel instance = new DrawerModel();
        for (ModelResourceLocation loc : resourceLocations) {
            Object object = modelRegistry.getObject(loc);
            if (object instanceof IBakedModel) {
                instance.fullDrawers2Oak = (IBakedModel) object;
                modelCache.put(loc, (IBakedModel)object);
                modelRegistry.putObject(loc, instance);
            }
        }
    }

    public static void initailizeResourceLocations () {
        for (EnumFacing dir : EnumFacing.values()) {
            if (dir.getAxis() == EnumFacing.Axis.Y)
                continue;

            Map<BlockPlanks.EnumType, ModelResourceLocation> typeMap = new HashMap<BlockPlanks.EnumType, ModelResourceLocation>();
            stateMap.put(dir, typeMap);

            for (BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()) {
                String key = StorageDrawers.MOD_ID + ":fullDrawers2#facing=" + dir + ",variant=" + woodType;
                ModelResourceLocation location = new ModelResourceLocation(key);

                resourceLocations.add(location);
                typeMap.put(woodType, location);
            }
        }
    }

    @Override
    public IBakedModel handleBlockState (IBlockState state) {
        EnumFacing dir = (EnumFacing)state.getValue(BlockDrawers.FACING);
        BlockPlanks.EnumType woodType = (BlockPlanks.EnumType)state.getValue(BlockDrawers.VARIANT);

        Map<BlockPlanks.EnumType, ModelResourceLocation> typeMap = stateMap.get(dir);
        ModelResourceLocation location = typeMap.get(woodType);

        return modelCache.get(location);
    }

    @Override
    public List getFaceQuads (EnumFacing p_177551_1_) {
        return null;
    }

    @Override
    public List getGeneralQuads () {
        return null;
    }

    @Override
    public VertexFormat getFormat () {
        return null;
    }

    @Override
    public boolean isAmbientOcclusion () {
        return false;
    }

    @Override
    public boolean isGui3d () {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer () {
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture () {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        return null;
    }
}
