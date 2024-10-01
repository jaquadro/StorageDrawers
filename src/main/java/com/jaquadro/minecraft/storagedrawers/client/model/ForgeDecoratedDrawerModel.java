package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawersFramed;
import com.jaquadro.minecraft.storagedrawers.block.tile.DrawerModelProperties;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ForgeDecoratedDrawerModel extends DecoratedDrawerModel implements IDynamicBakedModel
{
    public ForgeDecoratedDrawerModel (BakedModel mainModel, DrawerModelStore.DecorationSet overlays) {
        super(mainModel, overlays);
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType type) {
        List<BakedQuad> mainQuads;

        if (state == null) {
            // NB: getting here for item renders (state == null) implies that the caller has not
            // respected #getRenderPasses, since if they had this method wouldn't be called.
            // If that's the case, then we might as well return the main quads that they're looking
            // for anyway.
            return mainModel.getQuads(null, side, rand, extraData, type);
        }

        ChunkRenderTypeSet renderTypes = mainModel.getRenderTypes(state, rand, extraData);
        if (state.getBlock() instanceof BlockStandardDrawersFramed)
            mainQuads = Collections.emptyList();
        else if (type == null || renderTypes.contains(type)) {
            mainQuads = mainModel.getQuads(state, side, rand, extraData, type);
        } else {
            mainQuads = Collections.emptyList();
        }

        if (!extraData.has(DrawerModelProperties.ATTRIBUTES)) {
            // Nothing to render.
            return mainQuads;
        }

        //if (!(type == null || type == RenderType.cutoutMipped())) {
        //    // Don't render in the wrong layer.
        //    return mainQuads;
        //}

        List<BakedQuad> quads = new ArrayList<>(mainQuads);
        IDrawerAttributes attr = extraData.get(DrawerModelProperties.ATTRIBUTES);
        IDrawerGroup group = extraData.get(DrawerModelProperties.DRAWER_GROUP);
        IProtectable protectable = extraData.get(DrawerModelProperties.PROTECTABLE);

        if (attr != null) {
            Consumer<BakedModel> emitModel = model -> {
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand, extraData, type));
            };

            DrawerModelContext context = new DrawerModelContext(state, attr, group, protectable)
                .materialData(extraData.get(DrawerModelProperties.MATERIAL));

            if (state.getBlock() instanceof BlockStandardDrawersFramed) {
                if (context.materialData() == null || context.materialData().getEffectiveSide().isEmpty())
                    return mainModel.getQuads(state, side, rand, extraData, type);

                if (type != RenderType.translucent())
                    emitFramedQuads(context, emitModel);
                else
                    emitFramedOverlayQuads(context, emitModel);
            }

            if (type != RenderType.translucent())
                emitDecoratedQuads(context, emitModel);
        }

        return quads;
    }

    @Override
    public TextureAtlasSprite getParticleIcon (ModelData data) {
        return mainModel.getParticleIcon(data);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes (BlockState state, RandomSource rand, ModelData data) {
        ChunkRenderTypeSet typeSet = ChunkRenderTypeSet.union(
            ChunkRenderTypeSet.of(RenderType.cutoutMipped()),
            mainModel.getRenderTypes(state, rand, data));

        if (state.getBlock() instanceof BlockStandardDrawersFramed)
            typeSet = ChunkRenderTypeSet.union(
                ChunkRenderTypeSet.of(RenderType.cutoutMipped()),
                ChunkRenderTypeSet.of(RenderType.translucent()));

        return typeSet;
    }

    @Override
    public List<RenderType> getRenderTypes (ItemStack itemStack, boolean fabulous) {
        List<RenderType> list = new ArrayList<>();
        list.add(RenderType.cutoutMipped());
        list.add(RenderType.translucent());

        return list;
    }

    @Override
    public List<BakedModel> getRenderPasses (ItemStack itemStack, boolean fabulous) {
        return List.of(new ItemRender(mainModel, overlays, itemStack));

        // we don't render anything extra for items, so just pass through to the main model
        //return mainModel.getRenderPasses(itemStack, fabulous);
    }

    public static class ItemRender extends ForgeDecoratedDrawerModel
    {
        private ItemStack stack;

        public ItemRender (BakedModel mainModel, DrawerModelStore.DecorationSet overlays, ItemStack stack) {
            super(mainModel, overlays);
            this.stack = stack;
        }

        @Override
        public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            if (stack != null && stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof BlockStandardDrawersFramed framed) {
                    List<BakedQuad> quads = new ArrayList<>();
                    MaterialData data = new MaterialData();
                    data.read(stack.getOrCreateTag());

                    if (data.getEffectiveSide().isEmpty())
                        return super.getQuads(state, side, rand);

                    DrawerModelContext context = new DrawerModelContext(framed.defaultBlockState(), null, null, null)
                        .materialData(data);

                    Consumer<BakedModel> emitModel = model -> {
                        if (model != null)
                            quads.addAll(model.getQuads(state, side, rand));
                    };

                    emitDecoratedQuads(context, emitModel);
                    emitFramedQuads(context, emitModel);
                    emitFramedOverlayQuads(context, emitModel);
                    return quads;
                }
            }

            return super.getQuads(state, side, rand);
        }

        @Override
        public List<BakedModel> getRenderPasses (ItemStack itemStack, boolean fabulous) {
            return mainModel.getRenderPasses(itemStack, fabulous);
        }
    }
}
