package com.jaquadro.minecraft.storagedrawers.client.model.decorator;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.client.model.DrawerModelStore;
import com.jaquadro.minecraft.storagedrawers.client.model.SpriteReplacementModel;
import com.jaquadro.minecraft.storagedrawers.client.model.context.FramedModelContext;
import com.jaquadro.minecraft.storagedrawers.config.ModClientConfig;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MaterialModelDecorator<C extends FramedModelContext> extends ModelDecorator<C>
{
    protected final DrawerModelStore.FrameMatSet matSet;
    protected final boolean shaded;

    private static final Map<BlockStateModel, Map<Identifier, BlockStateModel>> replacementCache = new HashMap<>();

    private static final List<DecoratorRenderType> defaultRenderList = List.of(DecoratorRenderType.SOLID);
    private static final List<DecoratorRenderType> defaultShadedRenderList = List.of(DecoratorRenderType.SOLID, DecoratorRenderType.TRANSLUCENT);

    public MaterialModelDecorator (DrawerModelStore.FrameMatSet matSet, boolean shaded) {
        this.matSet = matSet;
        this.shaded = shaded;
    }

    @Override
    public boolean shouldRenderItem () {
        return true;
    }

    @Override
    public boolean shouldRenderBase (Supplier<C> contextSupplier) {
        FramedModelContext context = contextSupplier.get();
        if (context == null)
            return true;

        MaterialData matData = context.materialData();
        if (matData == null || matData.getEffectiveSide().isEmpty())
            return true;

        return false;
    }

    @Override
    public boolean shouldRenderBase (Supplier<C> contextSupplier, ItemStack stack) {
        return shouldRenderBase(contextSupplier);
    }

    @Override
    public List<DecoratorRenderType> getRenderTypes (BlockState state) {
        if (shaded)
            return defaultShadedRenderList;
        return defaultRenderList;
    }

    @Override
    public void emitQuads (Supplier<C> contextSupplier, Consumer<BlockStateModel> emitModel, DecoratorRenderType renderType) {
        FramedModelContext context = contextSupplier.get();
        if (context == null)
            return;

        MaterialData matData = context.materialData();
        if (matData != null && !matData.getEffectiveSide().isEmpty()) {
            boolean shouldRender = renderType == null || renderType == DecoratorRenderType.CUTOUT;
            if (ModClientConfig.INSTANCE.RENDER.framedDrawers.renderTranslucentMaterials.get())
                shouldRender = shouldRender || renderType == DecoratorRenderType.TRANSLUCENT;

            if (shouldRender)
                emitFramedQuads(context, emitModel, renderType);
            if (shaded && (renderType == null || renderType == DecoratorRenderType.TRANSLUCENT))
                emitFramedOverlayQuads(context, emitModel, renderType);
        }
    }

    @Override
    public void emitItemQuads (Supplier<C> contextSupplier, Consumer<BlockStateModel> emitModel, ItemStack stack, DecoratorRenderType renderType) {
        FramedModelContext context = contextSupplier.get();
        if (context == null)
            return;

        MaterialData matData = context.materialData();
        if (matData != null && !matData.getEffectiveSide().isEmpty()) {
            boolean shouldRender = renderType == null || renderType == DecoratorRenderType.CUTOUT;
            if (ModClientConfig.INSTANCE.RENDER.framedDrawers.renderTranslucentMaterials.get())
                shouldRender = shouldRender || renderType == DecoratorRenderType.TRANSLUCENT;

            if (shouldRender)
                emitFramedQuads(context, emitModel, renderType);
            if (shaded && (renderType == null || renderType == DecoratorRenderType.TRANSLUCENT))
                emitFramedOverlayQuads(context, emitModel, renderType);
        }
    }

    private BlockStateModel getReplacementModel (BlockStateModel baseModel, ItemStack material, DecoratorRenderType renderType) {
        Map<Identifier, BlockStateModel> matCache;
        if (replacementCache.containsKey(baseModel))
            matCache = replacementCache.get(baseModel);
        else {
            matCache = new HashMap<>();
            replacementCache.put(baseModel, matCache);
        }

        Identifier matName = BuiltInRegistries.ITEM.getKey(material.getItem());
        BlockStateModel replacedModel = null;
        if (matCache.containsKey(matName))
            replacedModel = matCache.get(matName);
        else {
            ChunkSectionLayer layer = ChunkSectionLayer.SOLID;
            if (renderType == DecoratorRenderType.CUTOUT)
                layer = ChunkSectionLayer.CUTOUT;
            else if (renderType == DecoratorRenderType.TRANSLUCENT)
                layer = ChunkSectionLayer.TRANSLUCENT;

            replacedModel = new SpriteReplacementModel(baseModel, material, layer);
            matCache.put(matName, replacedModel);
        }

        return replacedModel;
    }

    public void emitFramedQuads(FramedModelContext context, Consumer<BlockStateModel> emitModel, DecoratorRenderType renderType) {
        Block block = context.state().getBlock();

        boolean renderTrans = ModClientConfig.INSTANCE.RENDER.framedDrawers.renderTranslucentMaterials.get();
        boolean checkOpaque = renderTrans && renderType != null;
        boolean opaquePass = renderType == DecoratorRenderType.CUTOUT;

        if (block instanceof IFramedBlock fb) {
            MaterialData matData = context.materialData();
            if (matData != null && !matData.isEmpty()) {
                BiConsumer<ItemStack, DrawerModelStore.DynamicPart> emitPart = (item, part) -> {
                    boolean opaque = matData.isMatOpaque(item);
                    DecoratorRenderType render = (!renderTrans || opaque)
                        ? DecoratorRenderType.CUTOUT : DecoratorRenderType.TRANSLUCENT;

                    if (!checkOpaque || opaquePass == opaque) {
                        BlockStateModel storeModel = getStoreModel(context, part, render == DecoratorRenderType.TRANSLUCENT);
                        emitModel.accept(getReplacementModel(storeModel, item, render));
                    }
                };

                if (matSet.sidePart() != null && fb.supportsFrameMaterial(FrameMaterial.SIDE))
                    emitPart.accept(matData.getEffectiveSide(), matSet.sidePart());
                if (matSet.trimPart() != null && fb.supportsFrameMaterial(FrameMaterial.TRIM))
                    emitPart.accept(matData.getEffectiveTrim(), matSet.trimPart());
                if (matSet.frontPart() != null && fb.supportsFrameMaterial(FrameMaterial.FRONT))
                    emitPart.accept(matData.getEffectiveFront(), matSet.frontPart());
            }
        }

        /*if (block instanceof IFramedBlock fb) {
            MaterialData matData = context.materialData();
            if (matData != null && !matData.isEmpty()) {
                if (matSet.sidePart() != null && fb.supportsFrameMaterial(FrameMaterial.SIDE)) {
                    emitModel.accept(getReplacementModel(getStoreModel(context, matSet.sidePart()),
                        matData.getEffectiveSide()));
                }

                if (matSet.trimPart() != null && fb.supportsFrameMaterial(FrameMaterial.TRIM)) {
                    emitModel.accept(getReplacementModel(getStoreModel(context, matSet.trimPart()),
                        matData.getEffectiveTrim()));
                }

                if (matSet.frontPart() != null && fb.supportsFrameMaterial(FrameMaterial.FRONT)) {
                    emitModel.accept(getReplacementModel(getStoreModel(context, matSet.frontPart()),
                        matData.getEffectiveFront()));
                }
            }
        }*/
    }

    public void emitFramedOverlayQuads(FramedModelContext context, Consumer<BlockStateModel> emitModel, DecoratorRenderType renderType) {
        MaterialData matData = context.materialData();
        if (matData != null && !matData.isEmpty()) {
            if (matSet.shadeFrontPart() != null)
                emitModel.accept(getStoreModel(context, matSet.shadeFrontPart()));
            if (matSet.shadeSidePart() != null)
                emitModel.accept(getStoreModel(context, matSet.shadeSidePart()));
        }
    }

    protected abstract BlockStateModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part);

    protected BlockStateModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part, boolean trans) {
        return getStoreModel(context, part);
    }

    public static class Single<C extends FramedModelContext> extends MaterialModelDecorator<C>
    {
        public Single (DrawerModelStore.FrameMatSet matSet, boolean shaded) {
            super(matSet, shaded);
        }

        @Override
        protected BlockStateModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
            return DrawerModelStore.getModel(part);
        }

        /*@Override
        protected BlockStateModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part, boolean trans) {
            return DrawerModelStore.getModel(part, trans);
        }*/
    }

    public static class Facing<C extends FramedModelContext> extends MaterialModelDecorator<C>
    {
        public Facing (DrawerModelStore.FrameMatSet matSet, boolean shaded) {
            super(matSet, shaded);
        }

        @Override
        protected BlockStateModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
            Direction dir = context.state().getValue(BlockDrawers.FACING);
            return DrawerModelStore.getModel(part, dir);
        }
    }

    public static class FacingSized<C extends FramedModelContext> extends MaterialModelDecorator<C>
    {
        public FacingSized (DrawerModelStore.FrameMatSet matSet, boolean shaded) {
            super(matSet, shaded);
        }

        @Override
        protected BlockStateModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
            Direction dir = context.state().getValue(BlockDrawers.FACING);
            boolean half = false;
            Block block = context.state().getBlock();
            if (block instanceof BlockDrawers drawers)
                half = drawers.isHalfDepth();

            return DrawerModelStore.getModel(part, dir, half);
        }
    }

    public static class FacingSizedSlotted<C extends FramedModelContext> extends MaterialModelDecorator<C>
    {
        public FacingSizedSlotted (DrawerModelStore.FrameMatSet matSet, boolean shaded) {
            super(matSet, shaded);
        }

        @Override
        protected BlockStateModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
            Direction dir = context.state().getValue(BlockDrawers.FACING);
            boolean half = false;
            int count = 1;

            Block block = context.state().getBlock();
            if (block instanceof BlockDrawers drawers) {
                half = drawers.isHalfDepth();
                count = drawers.getDrawerCount();
            }

            return DrawerModelStore.getModel(part, dir, half, count);
        }
    }

    public static class FacingSizedOpen<C extends FramedModelContext> extends MaterialModelDecorator<C>
    {
        public FacingSizedOpen (DrawerModelStore.FrameMatSet matSet, boolean shaded) {
            super(matSet, shaded);
        }

        @Override
        protected BlockStateModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
            Direction dir = context.state().getValue(BlockDrawers.FACING);
            boolean half = false;
            EnumCompDrawer open = EnumCompDrawer.OPEN1;

            Block block = context.state().getBlock();
            if (block instanceof BlockCompDrawers drawers) {
                half = drawers.isHalfDepth();
                open = context.state().getValue(BlockCompDrawers.SLOTS);
            }

            return DrawerModelStore.getModel(part, dir, half, open);
        }
    }
}