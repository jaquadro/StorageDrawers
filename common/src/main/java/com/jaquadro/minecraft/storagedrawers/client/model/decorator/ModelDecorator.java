package com.jaquadro.minecraft.storagedrawers.client.model.decorator;

import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ModelDecorator<C extends ModelContext>
{
    public boolean shouldRenderItem () {
        return false;
    }

    public boolean shouldRenderBase (Supplier<C> contextSupplier) {
        return true;
    }

    public boolean shouldRenderBase (Supplier<C> contextSupplier, ItemStack stack) {
        return true;
    }

    public List<DecoratorRenderType> getRenderTypes (BlockState state) {
        return List.of(DecoratorRenderType.SOLID);
    }

    public void emitQuads(Supplier<C> contextSupplier, Consumer<BlockStateModel> emitModel) {
        emitQuads(contextSupplier, emitModel, null);
    }

    public void emitQuads(Supplier<C> contextSupplier, Consumer<BlockStateModel> emitModel, DecoratorRenderType renderType) { }

    public void emitItemQuads(Supplier<C> contextSupplier, Consumer<BlockStateModel> emitModel, ItemStack stack) {
        emitItemQuads(contextSupplier, emitModel, stack, null);
    }

    public void emitItemQuads(Supplier<C> contextSupplier, Consumer<BlockStateModel> emitModel, ItemStack stack, DecoratorRenderType renderType) { }
}