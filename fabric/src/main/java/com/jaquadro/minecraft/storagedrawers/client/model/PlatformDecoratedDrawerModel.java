package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class PlatformDecoratedDrawerModel extends DecoratedDrawerModel implements FabricBakedModel
{
    public static class FullModel extends PlatformDecoratedDrawerModel
    {
        FullModel(BakedModel mainModel) {
            super(mainModel, DrawerModelStore.fullDrawerDecorations);
        }
    }

    public static class HalfModel extends PlatformDecoratedDrawerModel
    {
        HalfModel(BakedModel mainModel) {
            super(mainModel, DrawerModelStore.halfDrawerDecorations);
        }
    }

    protected PlatformDecoratedDrawerModel (BakedModel mainModel, DrawerModelStore.DecorationSet overlays) {
        super(mainModel, overlays);
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

        Object renderData = fabricView.getBlockEntityRenderData(pos);
        if (renderData instanceof IDrawerAttributes attr) {
            Consumer<BakedModel> emitModel = model -> {
                if (model != null)
                    model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            };

            emitDecoratedQuads(state, attr, emitModel);
        }
    }
}
