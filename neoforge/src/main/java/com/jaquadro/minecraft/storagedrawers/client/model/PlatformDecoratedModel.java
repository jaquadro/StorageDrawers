package com.jaquadro.minecraft.storagedrawers.client.model;

import com.google.common.base.Suppliers;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.RenderDataProvider;
import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.decorator.ModelDecorator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.*;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
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
    public void collectParts (BlockAndTintGetter level, BlockPos blockPos, BlockState state, RandomSource randomSource, List<BlockStateModelPart> list) {
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

    public static class PlatformDecoratedItemModel implements ItemModel
    {
        private final Identifier location;
        private final String variant;
        private final ModelRenderProperties properties;
        private final Supplier<Vector3fc[]> extents;

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
                List<BlockStateModelPart> parts = new ArrayList<>();
                model.collectParts(null, parts);

                ItemStackRenderState.LayerRenderState layer = itemStackRenderState.newLayer();
                layer.setExtents(extents);
                properties.applyToLayer(layer, itemDisplayContext);

                List<BakedQuad> quadList = layer.prepareQuadList();
                for (BlockStateModelPart part : parts) {
                    quadList.addAll(part.getQuads(null));
                    for (Direction direction : Direction.values())
                        quadList.addAll(part.getQuads(direction));
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
        public void collectParts (RandomSource randomSource, List<BlockStateModelPart> list) {
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