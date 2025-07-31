package com.jaquadro.minecraft.storagedrawers.client.model.decorator;

import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.BiConsumer;
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

    public List<RenderType> getRenderTypes(BlockState state) {
        return List.of(RenderType.solid());
    }

    public List<RenderType> getRenderTypes(ItemStack stack) {
        return List.of(Sheets.solidBlockSheet());
    }

    public void emitQuads(Supplier<C> contextSupplier, BiConsumer<BakedModel, RenderType> emitModel) { }

    public void emitItemQuads(Supplier<C> contextSupplier, BiConsumer<BakedModel, RenderType> emitModel, ItemStack stack) { }
}