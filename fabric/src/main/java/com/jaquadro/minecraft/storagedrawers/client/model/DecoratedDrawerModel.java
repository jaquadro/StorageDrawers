package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DecoratedDrawerModel implements BakedModel, FabricBakedModel
{
    protected final BakedModel mainModel;
    protected final DrawerModelStore.DecorationSet overlays;

    public static class FullModel extends DecoratedDrawerModel {
        FullModel(BakedModel mainModel) {
            super(mainModel, DrawerModelStore.fullDrawerDecorations);
        }
    }

    public static class HalfModel extends DecoratedDrawerModel {
        HalfModel(BakedModel mainModel) {
            super(mainModel, DrawerModelStore.halfDrawerDecorations);
        }
    }

    private DecoratedDrawerModel(BakedModel mainModel, DrawerModelStore.DecorationSet overlays) {
        this.mainModel = mainModel;
        this.overlays = overlays;
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        return mainModel.getQuads(blockState, direction, randomSource);
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
    public TextureAtlasSprite getParticleIcon () {
        return mainModel.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms () {
        return mainModel.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides () {
        return mainModel.getOverrides();
    }

    @Override
    public boolean isVanillaAdapter () {
        return false;
    }

    @Override
    public void emitItemQuads (ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        mainModel.emitItemQuads(stack, randomSupplier, context);
    }

    @Override
    public void emitBlockQuads (BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        mainModel.emitBlockQuads(blockView, state, pos, randomSupplier, context);

        FabricBlockView fabricView = blockView;
        if (fabricView == null)
            return;

        Object renderdata = fabricView.getBlockEntityRenderData(pos);
        if (renderdata instanceof IDrawerAttributes attr) {
            Consumer<BakedModel> emitModel = model -> {
                if (model != null)
                    model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            };

            Direction dir = state.getValue(BlockDrawers.FACING);

            if (attr.isItemLocked(LockAttribute.LOCK_EMPTY) || attr.isItemLocked(LockAttribute.LOCK_POPULATED))
                emitModel.accept(DrawerModelStore.getModel(overlays.lockOverlays, dir));
            if (attr.isVoid())
                emitModel.accept(DrawerModelStore.getModel(overlays.voidOverlays, dir));
            if (attr.isConcealed())
                emitModel.accept(DrawerModelStore.getModel(overlays.shroudOverlays, dir));
            if (attr.hasFillLevel()) {
                Block block = state.getBlock();
                if (block instanceof BlockCompDrawers)
                    emitModel.accept(DrawerModelStore.getModel(overlays.indicatorComp, dir));
                else if (block instanceof BlockStandardDrawers stdBlock) {
                    int count = stdBlock.getDrawerCount();
                    if (count == 1)
                        emitModel.accept(DrawerModelStore.getModel(overlays.indicator1, dir));
                    else if (count == 2)
                        emitModel.accept(DrawerModelStore.getModel(overlays.indicator2, dir));
                    else if (count == 4)
                        emitModel.accept(DrawerModelStore.getModel(overlays.indicator4, dir));
                }
            }
        }
    }
}
