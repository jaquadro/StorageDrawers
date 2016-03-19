package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.*;

public class BasicDrawerModel implements IBakedModel
{
    private static final Set<ModelResourceLocation> resourceLocations = new HashSet<ModelResourceLocation>();
    private static final Set<ModelResourceLocation> itemResourceLocations = new HashSet<ModelResourceLocation>();
    private static final Map<EnumBasicDrawer, Map<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>>> stateMap = new HashMap<EnumBasicDrawer, Map<EnumFacing, Map<BlockPlanks.EnumType, ModelResourceLocation>>>();
    private static final Map<ModelResourceLocation, IBakedModel> modelCache = new HashMap<ModelResourceLocation, IBakedModel>();

    public static void initialize (IRegistry<ModelResourceLocation, IBakedModel> modelRegistry) {
        initailizeResourceLocations();

        for (ModelResourceLocation loc : resourceLocations) {
            IBakedModel object = modelRegistry.getObject(loc);
            if (object instanceof IBakedModel) {
                modelCache.put(loc, (IBakedModel) object);
                modelRegistry.putObject(loc, new BasicDrawerModel((IBakedModel) object));
            }
        }

        /*for (ModelResourceLocation loc : itemResourceLocations) {
            IBakedModel object = modelRegistry.getObject(loc);
            if (object != null) {
                modelRegistry.putObject(loc, new BasicDrawerItemModel(object));
            }
        }*/
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

    private IBakedModel parent;

    public BasicDrawerModel (IBakedModel parent) {
        this.parent = parent;
    }

    @Override
    public List<BakedQuad> getQuads (IBlockState state, EnumFacing side, long rand) {
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
            return parent.getQuads(state, side, rand);

        IExtendedBlockState xstate = (IExtendedBlockState)state;
        TileEntityDrawers tile = xstate.getValue(BlockDrawers.TILE);

        if (!DrawerDecoratorModel.shouldHandleState(tile))
            return parent.getQuads(state, side, rand);

        return new DrawerDecoratorModel(modelCache.get(location), xstate, drawer, dir, tile).getQuads(state, side, rand);
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
