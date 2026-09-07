package com.jaquadro.minecraft.storagedrawers.client.model;

import com.google.common.base.Suppliers;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.DrawerModelProperties;
import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.FramedModelProperties;
import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.RenderDataProvider;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.decorator.ModelDecorator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlatformDecoratedModel<C extends ModelContext> extends ParentModel implements DynamicBlockStateModel
{
    private final ModelDecorator<C> decorator;
    private final ModelContextSupplier<C> contextSupplier;

    public PlatformDecoratedModel (BlockStateModel parent, ModelDecorator<C> decorator, ModelContextSupplier<C> contextSupplier) {
        super(parent);
        this.decorator = decorator;
        this.contextSupplier = contextSupplier;
    }

    @Override
    public void collectParts (BlockAndTintGetter level, BlockPos blockPos, BlockState state, RandomSource randomSource, List<BlockModelPart> list) {
        BlockEntity entity = level.getBlockEntity(blockPos);
        if (entity instanceof RenderDataProvider renderProvider) {
            Object renderData = renderProvider.getRenderData();
            Supplier<C> supplier = () -> contextSupplier.makeContext(state, randomSource, renderData);

            if (decorator.shouldRenderBase(supplier))
                parent.collectParts(level, blockPos, state, randomSource, list);

            Consumer<BlockStateModel> emitModel = (model) -> {
                if (model != null) {
                    model.collectParts(level, blockPos, state, randomSource, list);
                }
            };

            try {
                decorator.emitQuads(supplier, emitModel);
            } catch (Exception e) { }
        } else {
            parent.collectParts(level, blockPos, state, randomSource, list);
        }
    }

    @Override
    public TextureAtlasSprite particleIcon (BlockAndTintGetter level, BlockPos pos, BlockState state) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof RenderDataProvider renderProvider) {
            Object renderData = renderProvider.getRenderData();
            MaterialData matData = null;
            if (renderData instanceof DrawerModelProperties drawerProps)
                matData = new MaterialData(drawerProps.material);
            else if (renderData instanceof FramedModelProperties frameProps)
                matData = new MaterialData(frameProps.material);

            if (matData != null) {
                ItemStack side = matData.getEffectiveSide();
                if (side != ItemStack.EMPTY) {
                    if (side.getItem() instanceof BlockItem blockItem) {
                        BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockItem.getBlock().defaultBlockState());
                        return model.particleIcon();
                    }
                }
            }
        }

        return super.particleIcon(level, pos, state);
    }

    public static class PlatformDecoratedItemModel implements ItemModel
    {
        private final Identifier location;
        private final String variant;
        private final ModelRenderProperties properties;
        private final Supplier<Vector3fc[]> extents;
        private final Map<MaterialData, BlockStateModel> modelCache = new HashMap<>();

        PlatformDecoratedModel<?> parent;
        BlockStateModel model;
        ItemStack stack;
        BlockState state;

        public PlatformDecoratedItemModel (Identifier location, String variant, ModelRenderProperties properties) {
            this.location = location;
            this.variant = variant;
            this.properties = properties;

            this.extents = Suppliers.memoize(() -> {
                Vector3f[] ext = new Vector3f[2];
                ext[0] = new Vector3f(0.0f, 0.0f, 0.0f);
                ext[1] = new Vector3f(1.0f, 1.0f, 1.0f);
                return ext;
            });
        }

        @Override
        public void update (ItemStackRenderState itemStackRenderState, ItemStack itemStack, ItemModelResolver itemModelResolver, ItemDisplayContext itemDisplayContext, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int i) {
            itemStackRenderState.appendModelIdentityElement(this);
            itemStackRenderState.appendModelIdentityElement(itemStack);

            if (state == null) {
                var blockOption = BuiltInRegistries.BLOCK.get(location);
                if (blockOption.isEmpty())
                    return;

                Block block = blockOption.get().value();
                state = block.defaultBlockState();

                String[] props = variant.split(",");
                for (String prop : props) {
                    String[] keyVal = prop.split("=");
                    if (keyVal.length != 2) continue;

                    String key = keyVal[0].trim();
                    String value = keyVal[1].trim();

                    Property<?> property = state.getBlock().getStateDefinition().getProperty(key);
                    if (property != null)
                        state = setProperty(state, property, value);
                }
            }

            if (parent == null) {
                BlockStateModel stored = ItemModelStore.models.get(state);
                if (stored instanceof PlatformDecoratedModel<?> p)
                    parent = p;
            }

            if ((stack == null || !ItemStack.isSameItemSameComponents(stack, itemStack)) && parent != null) {
                stack = itemStack.transmuteCopy(itemStack.getItem());
                model = new ItemRender<>(parent, stack);
            }

            if (model != null) {
                List<BlockModelPart> parts = new ArrayList<>();
                model.collectParts(null, parts);
                Map<ChunkSectionLayer, ItemStackRenderState.LayerRenderState> layers = new HashMap<>();
                for (BlockModelPart part : parts) {
                    ChunkSectionLayer partType = part.getRenderType(state);
                    if (!layers.containsKey(partType)) {
                        ItemStackRenderState.LayerRenderState renderState = itemStackRenderState.newLayer();
                        layers.put(partType, renderState);

                        RenderType itemRenderType = null;
                        if (partType == ChunkSectionLayer.SOLID)
                            itemRenderType = Sheets.solidBlockSheet();
                        if (partType == ChunkSectionLayer.CUTOUT)
                            itemRenderType = Sheets.cutoutBlockSheet();
                        else if (partType == ChunkSectionLayer.TRANSLUCENT)
                            itemRenderType = Sheets.translucentBlockItemSheet();

                        renderState.setRenderType(itemRenderType);
                        renderState.setExtents(extents);
                    }

                    ItemStackRenderState.LayerRenderState layer = layers.get(partType);
                    properties.applyToLayer(layer, itemDisplayContext);

                    layer.prepareQuadList().addAll(part.getQuads(null));
                    for (Direction direction : Direction.values())
                        layer.prepareQuadList().addAll(part.getQuads(direction));
                }
            }
        }

        private static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, String valueName) {
            Optional<T> parsed = property.getValue(valueName);
            return parsed.map(v -> state.setValue(property, v)).orElse(state);
        }

        public record Unbaked (Identifier model, String variant) implements ItemModel.Unbaked {
            public static final MapCodec<PlatformDecoratedItemModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((builder) ->
                builder.group(
                    Identifier.CODEC.fieldOf("model").forGetter(PlatformDecoratedItemModel.Unbaked::model),
                    Codec.STRING.fieldOf("variant").forGetter(PlatformDecoratedItemModel.Unbaked::variant)
                ).apply(builder, PlatformDecoratedItemModel.Unbaked::new)
            );

            @Override
            public MapCodec<? extends ItemModel.Unbaked> type () {
                return MAP_CODEC;
            }

            @Override
            public ItemModel bake (BakingContext bakingContext) {
                ModelBaker modelbaker = bakingContext.blockModelBaker();
                ResolvedModel resolvedmodel = modelbaker.getModel(Identifier.fromNamespaceAndPath(StorageDrawers.MOD_ID, "block/oak_full_drawers_2"));
                TextureSlots textureslots = resolvedmodel.getTopTextureSlots();

                ModelRenderProperties modelrenderproperties = ModelRenderProperties.fromResolvedModel(modelbaker, resolvedmodel, textureslots);
                return new PlatformDecoratedItemModel(model, variant, modelrenderproperties);
            }

            @Override
            public void resolveDependencies (Resolver resolver) {
                // Blocks are made from meta parts, nothing to resolve
            }
        }
    }

    public static class ItemRender<C extends ModelContext> extends ParentModel
    {
        PlatformDecoratedModel<C> parent;
        private ItemStack stack;

        public ItemRender (PlatformDecoratedModel<C> parent, ItemStack stack) {
            super(parent);
            this.parent = parent;
            this.stack = stack;
        }

        @Override
        public void collectParts (RandomSource randomSource, List<BlockModelPart> list) {
            Supplier<C> supplier = () -> parent.contextSupplier.makeContext(stack);
            ModelDecorator<C> decorator = parent.decorator;
            if (decorator.shouldRenderBase(supplier, stack))
                parent.collectParts(randomSource, list);

            Consumer<BlockStateModel> emitModel = (model) -> {
                if (model != null)
                    model.collectParts(randomSource, list);
            };

            try {
                decorator.emitItemQuads(supplier, emitModel, stack);
            } catch (Exception e) { }
        }
    }
}