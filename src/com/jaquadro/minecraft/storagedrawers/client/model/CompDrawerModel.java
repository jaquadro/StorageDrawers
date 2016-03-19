package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.model.BlockModel;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.*;

public class CompDrawerModel implements IBakedModel
{
    private static final Set<ModelResourceLocation> resourceLocations = new HashSet<ModelResourceLocation>();
    private static final Set<ModelResourceLocation> itemResourceLocations = new HashSet<ModelResourceLocation>();
    private static final Map<EnumCompDrawer, Map<EnumFacing, ModelResourceLocation>> stateMap = new HashMap<EnumCompDrawer, Map<EnumFacing, ModelResourceLocation>>();
    private static final Map<ModelResourceLocation, IBakedModel> modelCache = new HashMap<ModelResourceLocation, IBakedModel>();

    public static void initialize (IRegistry<ModelResourceLocation, IBakedModel> modelRegistry) {
        initailizeResourceLocations();

        for (ModelResourceLocation loc : resourceLocations) {
            Object object = modelRegistry.getObject(loc);
            if (object instanceof IBakedModel) {
                modelCache.put(loc, (IBakedModel) object);
                modelRegistry.putObject(loc, new CompDrawerModel((IBakedModel)object));
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

    private IBakedModel parent;

    public CompDrawerModel (IBakedModel parent) {
        this.parent = parent;
    }

    @Override
    public List<BakedQuad> getQuads (IBlockState state, EnumFacing side, long rand) {
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
            return parent.getQuads(state, side, rand);

        return new DrawerDecoratorModel(parent, xstate, drawer, dir, tile).getQuads(state, side, rand);
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
