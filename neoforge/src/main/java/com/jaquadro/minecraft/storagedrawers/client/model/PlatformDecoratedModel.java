package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.DrawerModelProperties;
import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.FramedModelProperties;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.decorator.ModelDecorator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PlatformDecoratedModel<C extends ModelContext> extends ParentModel implements IDynamicBakedModel
{
    private final ModelDecorator<C> decorator;
    private final ModelContextSupplier<C> contextSupplier;

    public PlatformDecoratedModel (BakedModel parent, ModelDecorator<C> decorator, ModelContextSupplier<C> contextSupplier) {
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

        BiConsumer<BakedModel, RenderType> emitModel = (model, renderType) -> {
            if (model != null && renderType == type)
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
        MaterialData matData = null;
        if (data.has(DrawerModelProperties.MATERIAL))
            matData = new MaterialData(data.get(DrawerModelProperties.MATERIAL));
        else if (data.has(FramedModelProperties.MATERIAL))
            matData = new MaterialData(data.get(FramedModelProperties.MATERIAL));

        if (matData != null) {
            ItemStack side = matData.getEffectiveSide();
            if (side != ItemStack.EMPTY) {
                if (side.getItem() instanceof BlockItem blockItem) {
                    ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
                    BakedModel model = renderer.getModel(side, null, null, 0);
                    return model.getParticleIcon();
                }
            }
        }
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
        private List<RenderType> lastRenderTypes = new ArrayList<>();
        private int nextRenderType;

        public ItemRender (ItemStack stack) {
            super(PlatformDecoratedModel.this.parent);
            this.stack = stack;
        }

        @Override
        public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            List<BakedQuad> quads = new ArrayList<>();

            RenderType limitRenderType = (nextRenderType < lastRenderTypes.size()) ? lastRenderTypes.get(nextRenderType) : null;

            Supplier<C> supplier = () -> PlatformDecoratedModel.this.contextSupplier.makeContext(stack, limitRenderType);
            ModelDecorator<C> decorator = PlatformDecoratedModel.this.decorator;
            if (decorator.shouldRenderBase(supplier, stack))
                quads.addAll(PlatformDecoratedModel.this.parent.getQuads(state, side, rand));

            BiConsumer<BakedModel, RenderType> emitModel = (model, renderType) -> {
                if (renderType == RenderType.solid())
                    renderType = Sheets.solidBlockSheet();
                else if (renderType == RenderType.cutoutMipped())
                    renderType = Sheets.cutoutBlockSheet();
                else if (renderType == RenderType.translucent())
                    renderType = Sheets.translucentCullBlockSheet();

                if (limitRenderType != null && limitRenderType != renderType)
                    return;
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand));
            };

            try {
                decorator.emitItemQuads(supplier, emitModel, stack);
                if (side == null)
                    nextRenderType += 1;
            } catch (Exception e) {
                return quads;
            }

            return quads;
        }

        @Override
        public List<BakedModel> getRenderPasses (ItemStack itemStack, boolean fabulous) {
            return PlatformDecoratedModel.this.parent.getRenderPasses(itemStack, fabulous);
        }

        @Override
        public TextureAtlasSprite getParticleIcon (ModelData data) {
            return PlatformDecoratedModel.this.parent.getParticleIcon(data);
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes (BlockState state, RandomSource rand, ModelData data) {
            return PlatformDecoratedModel.this.getRenderTypes(state, rand, data);
        }

        @Override
        public List<RenderType> getRenderTypes (ItemStack itemStack, boolean fabulous) {
            lastRenderTypes = PlatformDecoratedModel.this.getRenderTypes(itemStack, fabulous);
            nextRenderType = 0;

            return lastRenderTypes;
        }
    }
}