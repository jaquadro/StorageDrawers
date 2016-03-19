package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.registry.IRegistry;

import java.util.*;

public class TrimModel implements IBakedModel
{
    private static final Set<ModelResourceLocation> resourceLocations = new HashSet<ModelResourceLocation>();
    private static final Map<BlockPlanks.EnumType, ModelResourceLocation> stateMap = new HashMap<BlockPlanks.EnumType, ModelResourceLocation>();
    private static final Map<ModelResourceLocation, IBakedModel> modelCache = new HashMap<ModelResourceLocation, IBakedModel>();

    public static void initialize (IRegistry modelRegistry) {
        initailizeResourceLocations();

        for (ModelResourceLocation loc : resourceLocations) {
            Object object = modelRegistry.getObject(loc);
            if (object instanceof IBakedModel) {
                modelCache.put(loc, (IBakedModel) object);
                modelRegistry.putObject(loc, new TrimModel((IBakedModel)object));
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

    private IBakedModel parent;

    public TrimModel (IBakedModel parent) {
        this.parent = parent;
    }

    @Override
    public List<BakedQuad> getQuads (IBlockState state, EnumFacing side, long rand) {
        BlockPlanks.EnumType woodType = (BlockPlanks.EnumType)state.getValue(BlockTrim.VARIANT);

        ModelResourceLocation location = stateMap.get(woodType);
        if (location == null)
            return null;

        return parent.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion () {
        return parent.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d () {
        return parent.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer () {
        return parent.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        return parent.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        return parent.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides () {
        return parent.getOverrides();
    }
}
