package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.mojang.datafixers.util.Either;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public final class BasicDrawerModel
{
    public enum DynamicPart {
        LOCK("lock"),
        VOID("void"),
        SHROUD("shroud"),
        INDICATOR("indicator"),
        MISSING_1("missing_1"),
        MISSING_2("missing_2"),
        MISSING_3("missing_3"),
        MISSING_4("missing_4");

        private String name;

        DynamicPart(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final Map<String, BakedModel> overlayModels = new HashMap<>();

    private static final DynamicPart[] missingSlots1 = {
        DynamicPart.MISSING_1
    };
    private static final DynamicPart[] missingSlots2 = {
        DynamicPart.MISSING_1, DynamicPart.MISSING_2
    };
    private static final DynamicPart[] missingSlots4 = {
        DynamicPart.MISSING_1, DynamicPart.MISSING_2, DynamicPart.MISSING_3, DynamicPart.MISSING_4
    };
    private static final DynamicPart[][] missingSlots = {
        missingSlots1, missingSlots2, new DynamicPart[0], missingSlots4
    };

    private static boolean geometryDataLoaded = false;

    static String getVariant(Direction dir, boolean half) {
        return "facing=" + dir.getName() + ",half=" + half;
    }

    static String getVariant(Direction dir, boolean half, int slots) {
        return "facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
    }

    static String getVariant(DynamicPart part, Direction dir, boolean half) {
        return "part=" + part.getName() + ",facing=" + dir.getName() + ",half=" + half;
    }

    static String getVariant(DynamicPart part, Direction dir, boolean half, int slots) {
        return "part=" + part.getName() + ",facing=" + dir.getName() + ",half=" + half + ",slots=" + slots;
    }

    @Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Register // extends DefaultRegister
    {
        @SubscribeEvent
        public static void registerTextures (TextureStitchEvent event) {
            //if (event.getMap() != Minecraft.getInstance().getTextureMap())
            //    return;

            if (!ModBlocks.OAK_FULL_DRAWERS_1.isPresent()) {
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
        public static void registerModels(ModelEvent.ModifyBakingResult event) {
            if (!ModBlocks.OAK_FULL_DRAWERS_1.isPresent()) {
                StorageDrawers.log.warn("Block objects not set in ModelBakeEvent.  Is your mod environment broken?");
                return;
            }

            for (int i = 0; i < 4; i++) {
                Direction dir = Direction.from2DDataValue(i);
                BlockModelRotation rot = BlockModelRotation.by(0, (int)dir.toYRot() + 180);

                for (int j = 0; j < 2; j++) {
                    boolean half = j == 1;

                    overlayModels.put(getVariant(DynamicPart.LOCK, dir, half), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_locked"), getVariant(dir, half))));
                    overlayModels.put(getVariant(DynamicPart.VOID, dir, half), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_void"), getVariant(dir, half))));
                    overlayModels.put(getVariant(DynamicPart.SHROUD, dir, half), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_shroud"), getVariant(dir, half))));

                    overlayModels.put(getVariant(DynamicPart.INDICATOR, dir, half, 1), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_indicator"), getVariant(dir, half, 1))));
                    overlayModels.put(getVariant(DynamicPart.INDICATOR, dir, half, 2), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_indicator"), getVariant(dir, half, 2))));
                    overlayModels.put(getVariant(DynamicPart.INDICATOR, dir, half, 4), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_indicator"), getVariant(dir, half, 4))));
                    overlayModels.put(getVariant(DynamicPart.INDICATOR, dir, half), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_comp_indicator"), getVariant(dir, half))));

                    overlayModels.put(getVariant(DynamicPart.MISSING_1, dir, half, 1), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_missing_slot_1_1"), getVariant(dir, half))));
                    overlayModels.put(getVariant(DynamicPart.MISSING_1, dir, half, 2), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_missing_slot_2_1"), getVariant(dir, half))));
                    overlayModels.put(getVariant(DynamicPart.MISSING_1, dir, half, 4), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_missing_slot_4_1"), getVariant(dir, half))));
                    overlayModels.put(getVariant(DynamicPart.MISSING_2, dir, half, 2), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_missing_slot_2_2"), getVariant(dir, half))));
                    overlayModels.put(getVariant(DynamicPart.MISSING_2, dir, half, 4), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_missing_slot_4_2"), getVariant(dir, half))));
                    overlayModels.put(getVariant(DynamicPart.MISSING_3, dir, half, 4), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_missing_slot_4_3"), getVariant(dir, half))));
                    overlayModels.put(getVariant(DynamicPart.MISSING_4, dir, half, 4), event.getModels()
                        .get(new ModelResourceLocation(StorageDrawers.rl("meta_missing_slot_4_4"), getVariant(dir, half))));
                }
            }

            ModBlocks.getDrawers().forEach(blockDrawers -> replaceBlock(event, blockDrawers));
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

                //if (block.isHalfDepth())
                    event.getModels().put(modelResource, new Model2(parentModel));
                //else
                //    event.getModels().put(modelResource, new Model2.FullModel(parentModel));
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

    public static class Model2 implements IDynamicBakedModel {
        protected final BakedModel mainModel;
        /*
        protected final Map<Direction, BakedModel> lockOverlay;
        protected final Map<Direction, BakedModel> voidOverlay;
        protected final Map<Direction, BakedModel> shroudOverlay;
        protected final Map<Direction, BakedModel> indicator1Overlay;
        protected final Map<Direction, BakedModel> indicator2Overlay;
        protected final Map<Direction, BakedModel> indicator4Overlay;
        protected final Map<Direction, BakedModel> indicatorCompOverlay;*/

        /*public static class FullModel extends Model2 {
            FullModel(BakedModel mainModel) {
                super(mainModel, lockOverlaysFull, voidOverlaysFull, shroudOverlaysFull, indicator1Full, indicator2Full, indicator4Full);
            }
        }

        public static class HalfModel extends Model2 {
            HalfModel(BakedModel mainModel) {
                super(mainModel, lockOverlaysHalf, voidOverlaysHalf, shroudOverlaysHalf, indicator1Half, indicator2Half, indicator4Half);
            }
        }*/

        public Model2(BakedModel mainModel) {
            this.mainModel = mainModel;
        }

        /*private Model2(BakedModel mainModel,
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
        }*/

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
                    //mainQuads = new ArrayList<>();
                    //for (var q : mainModel.getQuads(state, side, rand, extraData, type)) {
                    //    mainQuads.add(new BakedQuad(q.getVertices(), q.getTintIndex(), q.getDirection(), q.getSprite(), q.isShade(), false));
                    //}
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

            boolean half = false;
            Block block = state.getBlock();
            if (block instanceof BlockDrawers drawers)
                half = drawers.isHalfDepth();

            if (attr.isItemLocked(LockAttribute.LOCK_EMPTY) || attr.isItemLocked(LockAttribute.LOCK_POPULATED)) {
                BakedModel model = overlayModels.get(getVariant(DynamicPart.LOCK, dir, half));
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand, extraData, type));
            }
            if (attr.isVoid()) {
                BakedModel model = overlayModels.get(getVariant(DynamicPart.VOID, dir, half));
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand, extraData, type));
            }
            if (attr.isConcealed()) {
                BakedModel model = overlayModels.get(getVariant(DynamicPart.SHROUD, dir, half));
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand, extraData, type));
            }
            if (attr.hasFillLevel()) {
                if (block instanceof BlockCompDrawers) {
                    BakedModel model = overlayModels.get(getVariant(DynamicPart.INDICATOR, dir, half));
                    if (model != null)
                        quads.addAll((model.getQuads(state, side, rand, extraData, type)));
                }
                else if (block instanceof BlockDrawers) {
                    int count = ((BlockDrawers) block).getDrawerCount();
                    BakedModel model = overlayModels.get(getVariant(DynamicPart.INDICATOR, dir, half, count));
                    if (model != null)
                        quads.addAll((model.getQuads(state, side, rand, extraData, type)));
                }
            }
            if (block instanceof BlockStandardDrawers && extraData.has(BlockEntityDrawers.DRAWER_GROUP)) {
                IDrawerGroup group = extraData.get(BlockEntityDrawers.DRAWER_GROUP);
                if (group != null) {
                    int count = group.getDrawerCount();
                    DynamicPart[] groupMissingSlots = missingSlots[count - 1];
                    for (int i = 0; i < groupMissingSlots.length; i++) {
                        if (group.getDrawer(i).isMissing()) {
                            BakedModel model = overlayModels.get(getVariant(groupMissingSlots[i], dir, half, count));
                            if (model != null)
                                quads.addAll((model.getQuads(state, side, rand, extraData, type)));
                        }
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
