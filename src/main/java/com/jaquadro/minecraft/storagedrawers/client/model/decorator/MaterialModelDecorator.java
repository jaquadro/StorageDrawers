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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MaterialModelDecorator<C extends FramedModelContext> extends ModelDecorator<C>
{
    protected final DrawerModelStore.FrameMatSet matSet;
    protected final boolean shaded;

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
    public void emitQuads (Supplier<C> contextSupplier, Consumer<BakedModel> emitModel) {
        FramedModelContext context = contextSupplier.get();
        if (context == null)
            return;

        MaterialData matData = context.materialData();
        if (matData != null && !matData.getEffectiveSide().isEmpty()) {
            if (shaded && context.renderType() == RenderType.translucent())
                emitFramedOverlayQuads(context, emitModel);
            if (context.renderType() == RenderType.cutoutMipped())
                emitFramedQuads(context, emitModel);
        }
    }

    @Override
    public void emitItemQuads (Supplier<C> contextSupplier, Consumer<BakedModel> emitModel, ItemStack stack) {
        FramedModelContext context = contextSupplier.get();
        if (context == null)
            return;

        MaterialData matData = context.materialData();
        if (matData != null && !matData.getEffectiveSide().isEmpty()) {
            emitFramedQuads(context, emitModel);
            if (shaded)
                emitFramedOverlayQuads(context, emitModel);
        }
    }

    @Override
    public List<RenderType> getRenderTypes (BlockState state) {
        if (shaded)
            return List.of(RenderType.cutoutMipped(), RenderType.translucent());
        return List.of(RenderType.cutoutMipped());
    }

    @Override
    public List<RenderType> getRenderTypes (ItemStack stack) {
        if (shaded)
            return List.of(RenderType.cutoutMipped(), RenderType.translucent());
        return List.of(RenderType.cutoutMipped());
    }

    public void emitFramedQuads(FramedModelContext context, Consumer<BakedModel> emitModel) {
        Block block = context.state().getBlock();

        if (block instanceof IFramedBlock fb) {
            MaterialData matData = context.materialData();
            if (matData != null && !matData.isEmpty()) {
                if (matSet.sidePart() != null && fb.supportsFrameMaterial(FrameMaterial.SIDE)) {
                    emitModel.accept(new SpriteReplacementModel(getStoreModel(context, matSet.sidePart()),
                        matData.getEffectiveSide()));
                }

                if (matSet.trimPart() != null && fb.supportsFrameMaterial(FrameMaterial.TRIM)) {
                    emitModel.accept(new SpriteReplacementModel(getStoreModel(context, matSet.trimPart()),
                        matData.getEffectiveTrim()));
                }

                if (matSet.frontPart() != null && fb.supportsFrameMaterial(FrameMaterial.FRONT)) {
                    emitModel.accept(new SpriteReplacementModel(getStoreModel(context, matSet.frontPart()),
                        matData.getEffectiveFront()));
                }
            }
        }
    }

    public void emitFramedOverlayQuads(FramedModelContext context, Consumer<BakedModel> emitModel) {
        MaterialData matData = context.materialData();
        if (matData != null && !matData.isEmpty()) {
            if (matSet.shadeFrontPart() != null)
                emitModel.accept(getStoreModel(context, matSet.shadeFrontPart()));
            if (matSet.shadeSidePart() != null)
                emitModel.accept(getStoreModel(context, matSet.shadeSidePart()));
        }
    }

    protected abstract BakedModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part);

    public static class Single<C extends FramedModelContext> extends MaterialModelDecorator<C>
    {
        public Single (DrawerModelStore.FrameMatSet matSet, boolean shaded) {
            super(matSet, shaded);
        }

        @Override
        protected BakedModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
            return DrawerModelStore.getModel(part);
        }
    }

    public static class Facing<C extends FramedModelContext> extends MaterialModelDecorator<C>
    {
        public Facing (DrawerModelStore.FrameMatSet matSet, boolean shaded) {
            super(matSet, shaded);
        }

        @Override
        protected BakedModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
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
        protected BakedModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
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
        protected BakedModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
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
        protected BakedModel getStoreModel (FramedModelContext context, DrawerModelStore.DynamicPart part) {
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
