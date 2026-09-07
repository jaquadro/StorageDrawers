package com.jaquadro.minecraft.storagedrawers.client.model;

import com.google.common.base.Suppliers;
import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.decorator.DecoratorRenderType;
import com.jaquadro.minecraft.storagedrawers.client.model.decorator.ModelDecorator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockgetter.v2.FabricBlockGetter;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class PlatformDecoratedModel<C extends ModelContext> extends ParentModel implements FabricBlockStateModel
{
    private final ModelDecorator<C> decorator;
    private final ModelContextSupplier<C> contextSupplier;
    private final ItemStack stack;

    private static Map<BlockStateModel, Mesh> meshCache = new HashMap<>();

    private static final List<DecoratorRenderType> decoratorRenderTypes = List.of(DecoratorRenderType.SOLID, DecoratorRenderType.CUTOUT, DecoratorRenderType.TRANSLUCENT);

    public PlatformDecoratedModel (BlockStateModel parent, ModelDecorator<C> decorator, ModelContextSupplier<C> contextSupplier) {
        super(parent);
        this.decorator = decorator;
        this.contextSupplier = contextSupplier;
        this.stack = null;
    }

    public PlatformDecoratedModel (PlatformDecoratedModel<C> p, ItemStack stack) {
        super(p.parent);
        this.decorator = p.decorator;
        this.contextSupplier = p.contextSupplier;
        this.stack = stack;
    }

    @Override
    public void emitQuads (QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
        if (state == null) {
            parent.emitQuads(emitter, blockView, pos, null, random, cullTest);
            return;
        }

        FabricBlockGetter fabricView = blockView;
        if (fabricView == null)
            return;

        Object renderData = fabricView.getBlockEntityRenderData(pos);
        Supplier<C> supplier = () -> contextSupplier.makeContext(state, random, renderData);

        if (decorator.shouldRenderBase(supplier))
            parent.emitQuads(emitter, blockView, pos, state, random, cullTest);

        Consumer<BlockStateModel> emitModelOpaque = (model) -> {
            if (model != null)
                model.emitQuads(emitter, blockView, pos, state, random, cullTest);
        };

        Consumer<BlockStateModel> emitModelCutout = (model) -> {
            if (model != null) {
                Mesh mesh = getMesh(model, state, random, DecoratorRenderType.CUTOUT);
                mesh.outputTo(emitter);
            }
        };

        Consumer<BlockStateModel> emitModelTransparent = (model) -> {
            if (model != null) {
                Mesh mesh = getMesh(model, state, random, DecoratorRenderType.TRANSLUCENT);
                mesh.outputTo(emitter);
            }
        };

        try {
            decorator.emitQuads(supplier, emitModelOpaque, DecoratorRenderType.SOLID);
            decorator.emitQuads(supplier, emitModelCutout, DecoratorRenderType.CUTOUT);
            decorator.emitQuads(supplier, emitModelTransparent, DecoratorRenderType.TRANSLUCENT);
        } catch (Exception e) { }
    }

    private Mesh getMesh (BlockStateModel model, BlockState state, RandomSource randomSource, DecoratorRenderType renderType) {
        if (meshCache.containsKey(model))
            return meshCache.get(model);

        Mesh mesh = buildMesh(model, state, randomSource, renderType);
        meshCache.put(model, mesh);
        return mesh;
    }

    private Mesh buildMesh (BlockStateModel model, BlockState state, RandomSource randomSource, DecoratorRenderType renderType) {
        Renderer render = Renderer.get();

        MutableMesh builder = render.mutableMesh();
        QuadEmitter quadEmit = builder.emitter().chunkLayer(DecoratorRenderType.toChunkType(renderType));

        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(randomSource, parts);

        for (BlockStateModelPart part : parts) {
            for (var d : Direction.values()) {
                for (var quad : part.getQuads(d))
                    quadEmit.fromBakedQuad(quad).emit();
            }
            for (var quad : part.getQuads(null))
                quadEmit.fromBakedQuad(quad).emit();
        }

        return builder.immutableCopy();
    }

    public static class PlatformDecoratedItemModel implements ItemModel
    {
        private final Identifier location;
        private final String variant;
        private final ModelRenderProperties properties;
        private final Supplier<Vector3fc[]> extents;

        PlatformDecoratedModel<? extends ModelContext> parent;
        BlockStateModel model;
        ItemStack stack;
        BlockState state;

        public PlatformDecoratedItemModel (Identifier location, String variant, ModelRenderProperties properties) {
            this.location = location;
            this.variant = variant;
            this.properties = properties;

            this.extents = Suppliers.memoize(() -> {
                Vector3fc[] ext = new Vector3fc[2];
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

            if ((stack == null || !ItemStack.isSameItemSameComponents(stack, itemStack)) && parent != null) {
                stack = itemStack;
                model = new PlatformDecoratedModel<>(parent, itemStack);
            }

            if (parent == null) {
                BlockStateModel stored = ItemModelStore.models.get(state);
                if (stored instanceof PlatformDecoratedModel<?> p)
                    parent = p;
            }

            if (model != null) {
                PlatformDecoratedModel<ModelContext> pd = (PlatformDecoratedModel<ModelContext>) parent;
                Supplier<ModelContext> supplier = () -> pd.contextSupplier.makeContext(stack);

                ItemStackRenderState.LayerRenderState layer = itemStackRenderState.newLayer();
                layer.setExtents(extents);
                properties.applyToLayer(layer, itemDisplayContext);
                List<BakedQuad> quadList = layer.prepareQuadList();

                for (var renderType : decoratorRenderTypes) {
                    List<BlockStateModelPart> parts = new ArrayList<>();
                    Consumer<BlockStateModel> emitModel = (m) -> {
                        if (m != null)
                            m.collectParts(null, parts);
                    };
                    pd.decorator.emitItemQuads(supplier, emitModel, stack, renderType);

                    for (BlockStateModelPart part : parts) {
                        quadList.addAll(part.getQuads(null));
                        for (Direction direction : Direction.values())
                            quadList.addAll(part.getQuads(direction));
                    }
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
            public ItemModel bake (BakingContext bakingContext, Matrix4fc transformation) {
                ModelBaker modelbaker = bakingContext.blockModelBaker();
                ResolvedModel resolvedmodel = modelbaker.getModel(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "block/oak_full_drawers_2"));
                TextureSlots textureslots = resolvedmodel.getTopTextureSlots();

                ModelRenderProperties modelrenderproperties = ModelRenderProperties.fromResolvedModel(modelbaker, resolvedmodel, textureslots);
                return new PlatformDecoratedItemModel(model, variant, modelrenderproperties);
            }

            @Override
            public void resolveDependencies (Resolver resolver) {
                // Made from meta parts, nothing to resolve
            }
        }
    }
}
