package com.jaquadro.minecraft.storagedrawers.client.model;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
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
            if (object != null) {
                modelCache.put(loc, object);
                modelRegistry.putObject(loc, new BasicDrawerModel(object));
            }
        }

        /*for (ModelResourceLocation loc : itemResourceLocations) {
            IBakedModel object = modelRegistry.getObject(loc);
            if (object != null) {
                modelRegistry.putObject(loc, new BasicDrawerModel(object));
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
        return ItemModelOverride.INSTANCE;
    }

    private static class ItemModelOverride extends ItemOverrideList
    {
        public static ItemModelOverride INSTANCE = new ItemModelOverride();

        private ItemModelOverride () {
            super(ImmutableList.<ItemOverride>of());
        }

        @Override
        public IBakedModel handleItemState (IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            if (stack == null)
                return originalModel;

            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("tile"))
                return originalModel;

            return new ItemModel(originalModel, stack);
        }
    }

    private static class ItemModel implements IBakedModel
    {
        private IBakedModel baseModel;
        private ItemStack stack;

        public ItemModel (IBakedModel baseModel, ItemStack stack) {
            this.baseModel = baseModel;
            this.stack = stack;
        }

        @Override
        public List<BakedQuad> getQuads (IBlockState state, EnumFacing side, long rand) {
            state = ModBlocks.basicDrawers.getStateFromMeta(stack.getMetadata());
            if (state.getValue(BlockDrawers.FACING) != side)
                return baseModel.getQuads(state, side, rand);

            List<BakedQuad> combined = new ArrayList<BakedQuad>(baseModel.getQuads(state, side, rand));
            combined.addAll(createSealedQuad(ModBlocks.basicDrawers));
            return combined;
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
            return baseModel.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture () {
            return baseModel.getParticleTexture();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms () {
            return baseModel.getItemCameraTransforms();
        }

        @Override
        public ItemOverrideList getOverrides () {
            return ItemOverrideList.NONE;
        }

        private List<BakedQuad> createSealedQuad (BlockDrawers block) {
            IBlockState blockState = block.getStateFromMeta(0);
            float depth = ModBlocks.basicDrawers.isHalfDepth(blockState) ? .5f : 1f;
            TextureAtlasSprite iconTape = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconTapeCover);

            ChamRender.instance.startBaking(DefaultVertexFormats.ITEM, 0);
            ChamRender.instance.setRenderBounds(0, 0, .995f - depth, 1, 1, 1);
            ChamRender.instance.bakeFace(ChamRender.FACE_ZNEG, blockState, iconTape);
            return ChamRender.instance.stopBaking();
        }
    }
}
