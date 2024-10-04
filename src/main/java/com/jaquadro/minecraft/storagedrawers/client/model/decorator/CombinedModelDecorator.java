package com.jaquadro.minecraft.storagedrawers.client.model.decorator;

import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CombinedModelDecorator<C extends ModelContext> extends ModelDecorator<C>
{
    private final List<ModelDecorator<C>> decorators = new ArrayList<>();

    public CombinedModelDecorator () { }

    public void add (ModelDecorator<C> decorator) {
        this.decorators.add(decorator);
    }

    @Override
    public boolean shouldRenderItem () {
        for (var decorator : decorators) {
            if (decorator.shouldRenderItem())
                return true;
        }

        return false;
    }

    @Override
    public boolean shouldRenderBase (Supplier<C> contextSupplier) {
        for (var decorator : decorators) {
            if (!decorator.shouldRenderBase(contextSupplier))
                return false;
        }

        return super.shouldRenderBase(contextSupplier);
    }

    @Override
    public boolean shouldRenderBase (Supplier<C> contextSupplier, ItemStack stack) {
        for (var decorator : decorators) {
            if (!decorator.shouldRenderBase(contextSupplier, stack))
                return false;
        }

        return super.shouldRenderBase(contextSupplier, stack);
    }

    @Override
    public List<RenderType> getRenderTypes (BlockState state) {
        if (decorators.isEmpty())
            return super.getRenderTypes(state);
        else if (decorators.size() == 1)
            return decorators.get(0).getRenderTypes(state);

        List<RenderType> types = new ArrayList<>(decorators.get(0).getRenderTypes(state));
        for (int i = 1; i < decorators.size(); i++) {
            for (var type : decorators.get(i).getRenderTypes(state)) {
                if (!types.contains(type))
                    types.add(type);
            }
        }

        return types;
    }

    @Override
    public List<RenderType> getRenderTypes (ItemStack stack) {
        if (decorators.isEmpty())
            return super.getRenderTypes(stack);
        else if (decorators.size() == 1)
            return decorators.get(0).getRenderTypes(stack);

        List<RenderType> types = new ArrayList<>(decorators.get(0).getRenderTypes(stack));
        for (int i = 1; i < decorators.size(); i++) {
            for (var type : decorators.get(i).getRenderTypes(stack)) {
                if (!types.contains(type))
                    types.add(type);
            }
        }

        return types;
    }

    @Override
    public void emitQuads (Supplier<C> contextSupplier, Consumer<BakedModel> emitModel) {
        for (var decorator : decorators)
            decorator.emitQuads(contextSupplier, emitModel);
    }

    @Override
    public void emitItemQuads (Supplier<C> contextSupplier, Consumer<BakedModel> emitModel, ItemStack stack) {
        for (var decorator : decorators)
            decorator.emitItemQuads(contextSupplier, emitModel, stack);
    }
}
