package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class BasicDrawerModel
{
    private static final Map<Direction, BakedModel> lockOverlaysFull = new HashMap<>();
    private static final Map<Direction, BakedModel> lockOverlaysHalf = new HashMap<>();
    private static final Map<Direction, BakedModel> voidOverlaysFull = new HashMap<>();
    private static final Map<Direction, BakedModel> voidOverlaysHalf = new HashMap<>();
    private static final Map<Direction, BakedModel> shroudOverlaysFull = new HashMap<>();
    private static final Map<Direction, BakedModel> shroudOverlaysHalf = new HashMap<>();
    private static final Map<Direction, BakedModel> indicator1Full = new HashMap<>();
    private static final Map<Direction, BakedModel> indicator1Half = new HashMap<>();
    private static final Map<Direction, BakedModel> indicator2Full = new HashMap<>();
    private static final Map<Direction, BakedModel> indicator2Half = new HashMap<>();
    private static final Map<Direction, BakedModel> indicator4Full = new HashMap<>();
    private static final Map<Direction, BakedModel> indicator4Half = new HashMap<>();
    private static final Map<Direction, BakedModel> indicatorComp = new HashMap<>();

    private static boolean geometryDataLoaded = false;

    @EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Register // extends DefaultRegister
    {
        @SubscribeEvent
        public static void registerTextures (TextureAtlasStitchedEvent event) {
            //if (event.getMap() != Minecraft.getInstance().getTextureMap())
            //    return;

            if (!ModBlocks.OAK_FULL_DRAWERS_1.isBound()) {
                StorageDrawers.log.warn("Block objects not set in TextureStitchEvent.  Is your mod environment broken?");
                return;
            }

            /*loadUnbakedModel(event, StorageDrawers.rl("models/block/full_drawers_lock.json"));
            loadUnbakedModel(event, StorageDrawers.rl("models/block/full_drawers_void.json"));
            loadUnbakedModel(event, StorageDrawers.rl("models/block/full_drawers_shroud.json"));
            loadUnbakedModel(event, StorageDrawers.rl("models/block/compdrawers_indicator.json"));
            loadUnbakedModel(event, StorageDrawers.rl("models/block/full_drawers_indicator_1.json"));
            loadUnbakedModel(event, StorageDrawers.rl("models/block/full_drawers_indicator_2.json"));
            loadUnbakedModel(event, StorageDrawers.rl("models/block/full_drawers_indicator_4.json"));*/

            loadGeometryData();
        }

        /*
        private static void loadUnbakedModel(TextureStitchEvent event, ResourceLocation resource) {
            BlockModel unbakedModel = getBlockModel(resource);

            for (Either<Material, String> x : unbakedModel.textureMap.values()) {
                x.ifLeft((value) -> {
                    if (value.atlasLocation().equals(event.getAtlas().location()))
                        event.addSprite(value.texture());
                });
            }
        }
        */

        private static void loadGeometryData () {
            if (geometryDataLoaded)
                return;

            geometryDataLoaded = true;

            populateGeometryData(StorageDrawers.rl("models/block/geometry/full_drawers_icon_area_1.json"),
                StorageDrawers.rl("models/block/geometry/full_drawers_count_area_1.json"),
                StorageDrawers.rl("models/block/geometry/full_drawers_ind_area_1.json"),
                StorageDrawers.rl("models/block/geometry/full_drawers_indbase_area_1.json"),
                    ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 1, false).toArray(BlockDrawers[]::new));
            populateGeometryData(StorageDrawers.rl("models/block/geometry/full_drawers_icon_area_2.json"),
                StorageDrawers.rl("models/block/geometry/full_drawers_count_area_2.json"),
                StorageDrawers.rl("models/block/geometry/full_drawers_ind_area_2.json"),
                StorageDrawers.rl("models/block/geometry/full_drawers_indbase_area_2.json"),
                    ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 2, false).toArray(BlockDrawers[]::new));
            populateGeometryData(StorageDrawers.rl("models/block/geometry/full_drawers_icon_area_4.json"),
                StorageDrawers.rl("models/block/geometry/full_drawers_count_area_4.json"),
                StorageDrawers.rl("models/block/geometry/full_drawers_ind_area_4.json"),
                StorageDrawers.rl("models/block/geometry/full_drawers_indbase_area_4.json"),
                    ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 4, false).toArray(BlockDrawers[]::new));
            populateGeometryData(StorageDrawers.rl("models/block/geometry/half_drawers_icon_area_1.json"),
                StorageDrawers.rl("models/block/geometry/half_drawers_count_area_1.json"),
                StorageDrawers.rl("models/block/geometry/half_drawers_ind_area_1.json"),
                StorageDrawers.rl("models/block/geometry/half_drawers_indbase_area_1.json"),
                    ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 1, true).toArray(BlockDrawers[]::new));
            populateGeometryData(StorageDrawers.rl("models/block/geometry/half_drawers_icon_area_2.json"),
                StorageDrawers.rl("models/block/geometry/half_drawers_count_area_2.json"),
                StorageDrawers.rl("models/block/geometry/half_drawers_ind_area_2.json"),
                StorageDrawers.rl("models/block/geometry/half_drawers_indbase_area_2.json"),
                    ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 2, true).toArray(BlockDrawers[]::new));
            populateGeometryData(StorageDrawers.rl("models/block/geometry/half_drawers_icon_area_4.json"),
                StorageDrawers.rl("models/block/geometry/half_drawers_count_area_4.json"),
                StorageDrawers.rl("models/block/geometry/half_drawers_ind_area_4.json"),
                StorageDrawers.rl("models/block/geometry/half_drawers_indbase_area_4.json"),
                    ModBlocks.getDrawersOfTypeAndSizeAndDepth(BlockDrawers.class, 4, true).toArray(BlockDrawers[]::new));
            populateGeometryData(StorageDrawers.rl("models/block/geometry/comp_drawers_icon_area_3.json"),
                StorageDrawers.rl("models/block/geometry/comp_drawers_count_area_3.json"),
                StorageDrawers.rl("models/block/geometry/comp_drawers_ind_area_3.json"),
                StorageDrawers.rl("models/block/geometry/comp_drawers_indbase_area_3.json"),
                    ModBlocks.getDrawersOfType(BlockCompDrawers.class).toArray(BlockDrawers[]::new));
        }

        private static BlockModel getBlockModel (ResourceLocation location) {
            Resource iresource = null;
            Reader reader = null;
            try {
                iresource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(location);
                reader = new InputStreamReader(iresource.open(), StandardCharsets.UTF_8);
                return BlockModel.fromStream(reader);
            } catch (IOException e) {
                return null;
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }

        private static void populateGeometryData(ResourceLocation locationIcon,
                                                 ResourceLocation locationCount,
                                                 ResourceLocation locationInd,
                                                 ResourceLocation locationIndBase,
                                                 BlockDrawers... blocks) {
            BlockModel slotInfo = getBlockModel(locationIcon);
            BlockModel countInfo = getBlockModel(locationCount);
            BlockModel indInfo = getBlockModel(locationInd);
            BlockModel indBaseInfo = getBlockModel(locationIndBase);
            for (BlockDrawers block : blocks) {
                if (block == null)
                    continue;

                for (int i = 0; i < block.getDrawerCount(); i++) {
                    Vector3f from = slotInfo.getElements().get(i).from;
                    Vector3f to = slotInfo.getElements().get(i).to;
                    block.labelGeometry[i] = new AABB(from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
                }
                for (int i = 0; i < block.getDrawerCount(); i++) {
                    Vector3f from = countInfo.getElements().get(i).from;
                    Vector3f to = countInfo.getElements().get(i).to;
                    block.countGeometry[i] = new AABB(from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
                }
                for (int i = 0; i < block.getDrawerCount(); i++) {
                    Vector3f from = indInfo.getElements().get(i).from;
                    Vector3f to = indInfo.getElements().get(i).to;
                    block.indGeometry[i] = new AABB(from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
                }
                for (int i = 0; i < block.getDrawerCount(); i++) {
                    Vector3f from = indBaseInfo.getElements().get(i).from;
                    Vector3f to = indBaseInfo.getElements().get(i).to;
                    block.indBaseGeometry[i] = new AABB(from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
                }
            }
        }

        @SubscribeEvent
        //public static void registerModels (BakingCompleted event) {
        public static void registerModels(ModelEvent.ModifyBakingResult event) {
            if (!ModBlocks.OAK_FULL_DRAWERS_1.isBound()) {
                StorageDrawers.log.warn("Block objects not set in ModelBakeEvent.  Is your mod environment broken?");
                return;
            }

            for (int i = 0; i < 4; i++) {
                Direction dir = Direction.from2DDataValue(i);
                BlockModelRotation rot = BlockModelRotation.by(0, (int)dir.toYRot() + 180);

                lockOverlaysFull.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_locked"), getVariant(dir, false))));
                lockOverlaysHalf.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_locked"), getVariant(dir, true))));
                voidOverlaysFull.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_void"), getVariant(dir, false))));
                voidOverlaysHalf.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_void"), getVariant(dir, true))));
                shroudOverlaysFull.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_shroud"), getVariant(dir, false))));
                shroudOverlaysHalf.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_shroud"), getVariant(dir, true))));
                indicator1Full.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_indicator"), getVariant(dir, false, 1))));
                indicator1Half.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_indicator"), getVariant(dir, true, 1))));
                indicator2Full.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_indicator"), getVariant(dir, false, 2))));
                indicator2Half.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_indicator"), getVariant(dir, true, 2))));
                indicator4Full.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_indicator"), getVariant(dir, false, 4))));
                indicator4Half.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_indicator"), getVariant(dir, true, 4))));
                indicatorComp.put(dir, event.getModels().get(new ModelResourceLocation(StorageDrawers.rl("meta_comp_indicator"), getVariant(dir, false))));
            }

            ModBlocks.getDrawers().forEach(blockDrawers -> replaceBlock(event, blockDrawers));

            //event.getModelBakery().getBakedModel(StorageDrawers.rl("block/full_drawers_lock"), ModelRotation.X0_Y0, ModelLoader.defaultTextureGetter());
            //event.getModelBakery().getBakedModel(StorageDrawers.rl("block/full_drawers_void"), ModelRotation.X0_Y0, ModelLoader.defaultTextureGetter());
            //event.getModelBakery().getBakedModel(StorageDrawers.rl("block/full_drawers_shroud"), ModelRotation.X0_Y0, ModelLoader.defaultTextureGetter());
        }

        static String getVariant(Direction dir, boolean half) {
            return "facing=" + dir.getName() + ",half=" + half;
        }

        static String getVariant(Direction dir, boolean half, int slots) {
            return "facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
        }

        public static void replaceBlock(ModelEvent.ModifyBakingResult event, BlockDrawers block) {
            BakedModel missing = event.getModels().get(ModelBakery.MISSING_MODEL_LOCATION);
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                ModelResourceLocation modelResource = BlockModelShaper.stateToModelLocation(state);
                BakedModel parentModel = event.getModels().get(modelResource);
                if (parentModel == null) {
                    StorageDrawers.log.warn("Got back null model from ModelBakeEvent.ModelManager for resource " + modelResource.toString());
                    continue;
                } else if (parentModel == missing)
                    continue;

                if (block.isHalfDepth())
                    event.getModels().put(modelResource, new Model2.HalfModel(parentModel));
                else
                    event.getModels().put(modelResource, new Model2.FullModel(parentModel));
            }
        }

        /*public Register () {
            super(ModBlocks.basicDrawers);
        }

        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<>();

            for (EnumBasicDrawer drawer : EnumBasicDrawer.values()) {
                for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                    for (BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()) {
                        states.add(ModBlocks.basicDrawers.getDefaultState()
                            .withProperty(BlockStandardDrawers.BLOCK, drawer)
                            .withProperty(BlockDrawers.FACING, dir)
                            .withProperty(BlockVariantDrawers.VARIANT, woodType));
                    }
                }
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state, IBakedModel existingModel) {
            return new CachedBuilderModel(new Model(existingModel));
        }

        @Override
        public IBakedModel getModel (ItemStack stack, IBakedModel existingModel) {
            return new CachedBuilderModel(new Model(existingModel));
        }

        @Override
        public List<ResourceLocation> getTextureResources () {
            List<ResourceLocation> resource = new ArrayList<>();
            resource.add(DrawerDecoratorModel.iconClaim);
            resource.add(DrawerDecoratorModel.iconClaimLock);
            resource.add(DrawerDecoratorModel.iconLock);
            resource.add(DrawerDecoratorModel.iconShroudCover);
            resource.add(DrawerDecoratorModel.iconVoid);
            resource.add(DrawerSealedModel.iconTapeCover);
            return resource;
        }*/
    }

    public static class MergedModel implements IDynamicBakedModel {
        protected final BakedModel mainModel;
        protected final BakedModel[] models;

        public MergedModel (BakedModel mainModel, BakedModel... models) {
            this.mainModel = mainModel;
            this.models = models;
        }

        @Override
        @NotNull
        public List<BakedQuad> getQuads (@Nullable BlockState state,
                                         @Nullable Direction side,
                                         @NotNull RandomSource rand,
                                         @NotNull ModelData data,
                                         @Nullable RenderType type) {
            List<BakedQuad> quads = new ArrayList<>(
                    mainModel.getQuads(state, side, rand, data, type)
            );
            for (BakedModel model : models)
                quads.addAll(model.getQuads(state, side, rand, data, type));
            return quads;
        }

        @Override
        public boolean useAmbientOcclusion () {
            return mainModel.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d () {
            return mainModel.isGui3d();
        }

        @Override
        public boolean usesBlockLight () {
            return mainModel.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer () {
            return mainModel.isCustomRenderer();
        }

        @Override
        @NotNull
        public TextureAtlasSprite getParticleIcon () {
            return mainModel.getParticleIcon();
        }

        @Override
        @NotNull
        public ItemOverrides getOverrides () {
            return mainModel.getOverrides();
        }
    }

    public static abstract class Model2 implements IDynamicBakedModel {
        protected final BakedModel mainModel;
        protected final Map<Direction, BakedModel> lockOverlay;
        protected final Map<Direction, BakedModel> voidOverlay;
        protected final Map<Direction, BakedModel> shroudOverlay;
        protected final Map<Direction, BakedModel> indicator1Overlay;
        protected final Map<Direction, BakedModel> indicator2Overlay;
        protected final Map<Direction, BakedModel> indicator4Overlay;
        protected final Map<Direction, BakedModel> indicatorCompOverlay;

        public static class FullModel extends Model2 {
            FullModel(BakedModel mainModel) {
                super(mainModel, lockOverlaysFull, voidOverlaysFull, shroudOverlaysFull, indicator1Full, indicator2Full, indicator4Full);
            }
        }

        public static class HalfModel extends Model2 {
            HalfModel(BakedModel mainModel) {
                super(mainModel, lockOverlaysHalf, voidOverlaysHalf, shroudOverlaysHalf, indicator1Half, indicator2Half, indicator4Half);
            }
        }

        private Model2(BakedModel mainModel,
                       Map<Direction, BakedModel> lockOverlay,
                       Map<Direction, BakedModel> voidOverlay,
                       Map<Direction, BakedModel> shroudOverlay,
                       Map<Direction, BakedModel> indicator1Overlay,
                       Map<Direction, BakedModel> indicator2Overlay,
                       Map<Direction, BakedModel> indicator4Overlay) {
            this.mainModel = mainModel;
            this.lockOverlay = lockOverlay;
            this.voidOverlay = voidOverlay;
            this.shroudOverlay = shroudOverlay;
            this.indicator1Overlay = indicator1Overlay;
            this.indicator2Overlay = indicator2Overlay;
            this.indicator4Overlay = indicator4Overlay;
            this.indicatorCompOverlay = indicatorComp;
        }

        @Override
        public boolean usesBlockLight () {
            return mainModel.usesBlockLight();
        }

        @NotNull
        @Override
        public List<BakedQuad> getQuads (@Nullable BlockState state,
                                         @Nullable Direction side,
                                         @NotNull RandomSource rand,
                                         @NotNull ModelData extraData,
                                         @Nullable RenderType type) {
            List<BakedQuad> mainQuads;
            if (state != null) {
                ChunkRenderTypeSet renderTypes = mainModel.getRenderTypes(state, rand, extraData);
                if (type == null || renderTypes.contains(type)) {
                    mainQuads = mainModel.getQuads(state, side, rand, extraData, type);
                } else {
                    mainQuads = Collections.emptyList();
                }
            } else {
                // NB: getting here for item renders (state == null) implies that the caller has not
                // respected #getRenderPasses, since if they had this method wouldn't be called.
                // If that's the case, then we might as well return the main quads that they're looking
                // for anyway.
                return mainModel.getQuads(null, side, rand, extraData, type);
            }

            if (!extraData.has(BlockEntityDrawers.ATTRIBUTES)) {
                // Nothing to render.
                return mainQuads;
            }

            if (!(type == null || type == RenderType.cutoutMipped())) {
                // Don't render in the wrong layer.
                return mainQuads;
            }

            List<BakedQuad> quads = new ArrayList<>(mainQuads);
            IDrawerAttributes attr = extraData.get(BlockEntityDrawers.ATTRIBUTES);
            Direction dir = state.getValue(BlockDrawers.FACING);

            if (attr.isItemLocked(LockAttribute.LOCK_EMPTY) || attr.isItemLocked(LockAttribute.LOCK_POPULATED)) {
                BakedModel model = lockOverlay.get(dir);
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand, extraData, type));
            }
            if (attr.isVoid()) {
                BakedModel model = voidOverlay.get(dir);
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand, extraData, type));
            }
            if (attr.isConcealed()) {
                BakedModel model = shroudOverlay.get(dir);
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand, extraData, type));
            }
            if (attr.hasFillLevel()) {
                Block block = state.getBlock();
                if (block instanceof BlockCompDrawers) {
                    BakedModel model = indicatorCompOverlay.get(dir);
                    if (model != null)
                        quads.addAll((model.getQuads(state, side, rand, extraData, type)));
                }
                else if (block instanceof BlockDrawers) {
                    int count = ((BlockDrawers) block).getDrawerCount();
                    if (count == 1) {
                        BakedModel model = indicator1Overlay.get(dir);
                        if (model != null)
                            quads.addAll((model.getQuads(state, side, rand, extraData, type)));
                    }
                    else if (count == 2) {
                        BakedModel model = indicator2Overlay.get(dir);
                        if (model != null)
                            quads.addAll((model.getQuads(state, side, rand, extraData, type)));
                    }
                    else if (count == 4) {
                        BakedModel model = indicator4Overlay.get(dir);
                        if (model != null)
                            quads.addAll((model.getQuads(state, side, rand, extraData, type)));
                    }
                }
            }
            return quads;
        }

        @Override
        public boolean useAmbientOcclusion () {
            return mainModel.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d () {
            return mainModel.isGui3d();
        }

        @Override
        public boolean isCustomRenderer () {
            return mainModel.isCustomRenderer();
        }

        @Override
        @NotNull
        public TextureAtlasSprite getParticleIcon () {
            return mainModel.getParticleIcon();
        }

        @Override
        @NotNull
        public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
            return mainModel.getParticleIcon(data);
        }

        @Override
        @NotNull
        public ItemOverrides getOverrides () {
            return mainModel.getOverrides();
        }

        @NotNull
        @Override
        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
            return ChunkRenderTypeSet.union(
                    ChunkRenderTypeSet.of(RenderType.cutoutMipped()),
                    mainModel.getRenderTypes(state, rand, data)
            );
        }

        @NotNull
        @Override
        public List<RenderType> getRenderTypes(@NotNull ItemStack itemStack, boolean fabulous) {
            return Collections.emptyList();
        }

        @NotNull
        @Override
        public List<BakedModel> getRenderPasses(@NotNull ItemStack itemStack, boolean fabulous) {
            // we don't render anything extra for items, so just pass through to the main model
            return mainModel.getRenderPasses(itemStack, fabulous);
        }
    }

    public static class Model extends ProxyBuilderModel
    {
        Direction side;
        Map<Direction, BakedModel> overlays;

        public Model (BakedModel parent, Map<Direction, BakedModel> overlays, Direction side) {
            super(parent);
            this.overlays = overlays;
            this.side = side;
        }

        @Override
        protected BakedModel buildModel (BlockState state, BakedModel parent) {
            return new MergedModel(parent, overlays.get(side));
            /*try {
                //EnumBasicDrawer drawer = state.get(BlockStandardDrawers.BLOCK);
                Direction dir = state.get(BlockDrawers.HORIZONTAL_FACING);

                if (!(state instanceof IExtendedBlockState))
                    return new PassLimitedModel(parent, BlockRenderLayer.CUTOUT_MIPPED);

                IExtendedBlockState xstate = (IExtendedBlockState)state;
                DrawerStateModelData stateModel = xstate.getValue(BlockDrawers.STATE_MODEL);

                if (!DrawerDecoratorModel.shouldHandleState(stateModel))
                    return new PassLimitedModel(parent, BlockRenderLayer.CUTOUT_MIPPED);

                return new DrawerDecoratorModel(parent, xstate, drawer, dir, stateModel);
            }
            catch (Throwable t) {
                return new PassLimitedModel(parent, BlockRenderLayer.CUTOUT_MIPPED);
            }*/
        }

        /*@Override
        public ItemOverrideList getOverrides () {
            return itemHandler;
        }*/

        /*@Override
        public List<Object> getKey (BlockState state) {
            try {
                List<Object> key = new ArrayList<Object>();
                IExtendedBlockState xstate = (IExtendedBlockState)state;
                key.add(xstate.getValue(BlockDrawers.STATE_MODEL));

                return key;
            }
            catch (Throwable t) {
                return super.getKey(state);
            }
        }*/
    }

    /*private static class ItemHandler extends ItemOverrideList
    {
        public ItemHandler () {
            super(ImmutableList.<ItemOverride>of());
        }

        @Override
        public IBakedModel handleItemState (IBakedModel parent, @NotNull ItemStack stack, World world, EntityLivingBase entity) {
            if (stack.isEmpty())
                return parent;

            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("tile", Constants.NBT.TAG_COMPOUND))
                return parent;

            Block block = Block.getBlockFromItem(stack.getItem());
            IBlockState state = block.getStateFromMeta(stack.getMetadata());

            return new DrawerSealedModel(parent, state, true);
        }
    }

    private static final ItemHandler itemHandler = new ItemHandler();*/
}
