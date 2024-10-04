package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContextSupplier;
import com.jaquadro.minecraft.storagedrawers.client.model.decorator.ModelDecorator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeDecoratedModel<C extends ModelContext> extends ParentModel implements IDynamicBakedModel
{
    private final ModelDecorator<C> decorator;
    private final ModelContextSupplier<C> contextSupplier;

    public ForgeDecoratedModel (BakedModel parent, ModelDecorator<C> decorator, ModelContextSupplier<C> contextSupplier) {
        super(parent);
        this.decorator = decorator;
        this.contextSupplier = contextSupplier;
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType type) {
        if (state == null) {
            // NB: getting here for item renders (state == null) implies that the caller has not
            // respected #getRenderPasses, since if they had this method wouldn't be called.
            // If that's the case, then we might as well return the main quads that they're looking
            // for anyway.
            return parent.getQuads(state, side, rand, extraData, type);
        }

        List<BakedQuad> quads = new ArrayList<>();

        Supplier<C> supplier = () -> contextSupplier.makeContext(state, side, rand, extraData, type);
        if (decorator.shouldRenderBase(supplier))
            quads.addAll(parent.getQuads(state, side, rand, extraData, type));

        Consumer<BakedModel> emitModel = model -> {
            if (model != null)
                quads.addAll(model.getQuads(state, side, rand, extraData, type));
        };

        try {
            decorator.emitQuads(supplier, emitModel);
        } catch (Exception e) {
            return quads;
        }

        return quads;
    }

    @Override
    public TextureAtlasSprite getParticleIcon (ModelData data) {
        return parent.getParticleIcon(data);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes (BlockState state, RandomSource rand, ModelData data) {
        return ChunkRenderTypeSet.of(decorator.getRenderTypes(state));
    }

    @Override
    public List<RenderType> getRenderTypes (ItemStack itemStack, boolean fabulous) {
        return decorator.getRenderTypes(itemStack);
    }

    @Override
    public List<BakedModel> getRenderPasses (ItemStack itemStack, boolean fabulous) {
        if (decorator.shouldRenderItem())
            return List.of(new ItemRender(itemStack));

        return parent.getRenderPasses(itemStack, fabulous);
    }

    public class ItemRender extends ParentModel
    {
        private ItemStack stack;

        public ItemRender (ItemStack stack) {
            super(ForgeDecoratedModel.this.parent);
            this.stack = stack;
        }

        @Override
        public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            List<BakedQuad> quads = new ArrayList<>();

            Supplier<C> supplier = () -> ForgeDecoratedModel.this.contextSupplier.makeContext(stack);
            ModelDecorator<C> decorator = ForgeDecoratedModel.this.decorator;
            if (decorator.shouldRenderBase(supplier, stack))
                quads.addAll(ForgeDecoratedModel.this.parent.getQuads(state, side, rand));

            Consumer<BakedModel> emitModel = model -> {
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand));
            };

            try {
                decorator.emitItemQuads(supplier, emitModel, stack);
            } catch (Exception e) {
                return quads;
            }

            return quads;
        }

        @Override
        public List<BakedModel> getRenderPasses (ItemStack itemStack, boolean fabulous) {
            return ForgeDecoratedModel.this.parent.getRenderPasses(itemStack, fabulous);
        }

        @Override
        public TextureAtlasSprite getParticleIcon (ModelData data) {
            return ForgeDecoratedModel.this.parent.getParticleIcon(data);
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes (BlockState state, RandomSource rand, ModelData data) {
            return ForgeDecoratedModel.this.getRenderTypes(state, rand, data);
        }

        @Override
        public List<RenderType> getRenderTypes (ItemStack itemStack, boolean fabulous) {
            return ForgeDecoratedModel.this.getRenderTypes(itemStack, fabulous);
        }
    }
}
