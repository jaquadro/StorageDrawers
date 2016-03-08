package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.geometry.Area2D;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

        if (!tile.isShrouded() && !tile.isLocked(LockAttribute.LOCK_POPULATED) && tile.getOwner() == null)
            return modelCache.get(location);

        return new CompositeModel(modelCache.get(location), xstate, drawer, dir, tile);
    }

    public class CompositeModel implements IBakedModel {
        private IBakedModel baseModel;
        private IExtendedBlockState blockState;
        private EnumBasicDrawer drawer;
        private EnumFacing dir;
        private boolean shrouded;
        private boolean locked;
        private boolean owned;

        public CompositeModel (IBakedModel baseModel, IExtendedBlockState blockState, EnumBasicDrawer drawer, EnumFacing dir, TileEntityDrawers tile) {
            this.baseModel = baseModel;
            this.blockState = blockState;
            this.drawer = drawer;
            this.dir = dir;
            this.shrouded = tile.isShrouded();
            this.locked = tile.isLocked(LockAttribute.LOCK_POPULATED);
            this.owned = tile.getOwner() != null;
        }

        @Override
        public List<BakedQuad> getFaceQuads (EnumFacing facing) {
            return baseModel.getFaceQuads(facing);
        }

        @Override
        public List<BakedQuad> getGeneralQuads () {
            ChamRender.instance.startBaking(DefaultVertexFormats.BLOCK);
            if (shrouded)
                buildShroudGeometry();
            if (locked || owned)
                buildLockGeometry();

            List<BakedQuad> quads = ChamRender.instance.stopBaking();
            quads.addAll(baseModel.getGeneralQuads());

            return quads;
        }

        @Override
        public boolean isAmbientOcclusion () {
            return baseModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d () {
            return baseModel.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer () {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture () {
            return baseModel.getParticleTexture();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms () {
            return baseModel.getItemCameraTransforms();
        }

        private void buildLockGeometry () {
            double depth = drawer.isHalfDepth() ? .5 : 1;

            TextureAtlasSprite lockIcon;
            if (locked && owned)
                lockIcon = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconClaimLockResource);
            else if (locked)
                lockIcon = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconLockResource);
            else if (owned)
                lockIcon = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconClaimResource);
            else
                return;

            ChamRender.instance.setRenderBounds(0.46875, 0.9375, 0, 0.53125, 1, depth + .003);
            ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, dir.getIndex());
            ChamRender.instance.bakePartialFace(ChamRender.FACE_ZPOS, blockState, lockIcon, 0, 0, 1, 1, 1, 1, 1);
            ChamRender.instance.state.clearRotateTransform();
        }

        private void buildShroudGeometry () {
            StatusModelData data = ModBlocks.basicDrawers.getStatusInfo(blockState);
            int count = drawer.getDrawerCount();
            double depth = drawer.isHalfDepth() ? .5 : 1;

            double unit = 0.0625;
            double frontDepth = data.getFrontDepth() * unit;

            TextureAtlasSprite iconCover = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconShroudCover);

            for (int i = 0; i < count; i++) {
                StatusModelData.Slot slot = data.getSlot(i);
                Area2D bounds = slot.getIconArea();

                ChamRender.instance.setRenderBounds(bounds.getX() * unit, bounds.getY() * unit, 0,
                    (bounds.getX() + bounds.getWidth()) * unit, (bounds.getY() + bounds.getHeight()) * unit, depth - frontDepth + .003);
                ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, dir.getIndex());
                ChamRender.instance.bakeFace(ChamRender.FACE_ZPOS, blockState, iconCover, 1, 1, 1);
                ChamRender.instance.state.clearRotateTransform();
            }
        }
    }
}
