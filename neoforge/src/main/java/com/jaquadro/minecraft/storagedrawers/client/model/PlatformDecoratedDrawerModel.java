package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.block.tile.DrawerModelProperties;
import com.jaquadro.minecraft.storagedrawers.block.tile.PlatformBlockEntityDrawersStandard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PlatformDecoratedDrawerModel extends DecoratedDrawerModel implements IDynamicBakedModel
{
    public  PlatformDecoratedDrawerModel (BakedModel mainModel, DrawerModelStore.DecorationSet overlays) {
        super(mainModel, overlays);
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType type) {
        List<BakedQuad> mainQuads;

        if (state != null) {
            ChunkRenderTypeSet renderTypes = mainModel.getRenderTypes(state, rand, extraData);
            if (type == null || renderTypes.contains(type)) {
                mainQuads = mainModel.getQuads(state, side, rand, extraData, type);
            } else {
                mainQuads = Collections.emptyList();
            }
        } else {
            // NB: getting here for item renders (state == null) implies that the caller has not
            // respected #getRenderPasses, since if they had this method wouldn't be called.
            // If that's the case, then we might as well return the main quads that they're looking
            // for anyway.
            return mainModel.getQuads(null, side, rand, extraData, type);
        }

        if (!extraData.has(DrawerModelProperties.ATTRIBUTES)) {
            // Nothing to render.
            return mainQuads;
        }

        if (!(type == null || type == RenderType.cutoutMipped())) {
            // Don't render in the wrong layer.
            return mainQuads;
        }

        List<BakedQuad> quads = new ArrayList<>(mainQuads);
        IDrawerAttributes attr = extraData.get(DrawerModelProperties.ATTRIBUTES);
        IDrawerGroup group = extraData.get(DrawerModelProperties.DRAWER_GROUP);
        IProtectable protectable = extraData.get(DrawerModelProperties.PROTECTABLE);

        if (attr != null) {
            Consumer<BakedModel> emitModel = model -> {
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand, extraData, type));
            };

            DrawerModelContext context = new DrawerModelContext(state, attr, group, protectable);
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
        return ChunkRenderTypeSet.union(
            ChunkRenderTypeSet.of(RenderType.cutoutMipped()),
            mainModel.getRenderTypes(state, rand, data)
        );
    }

    @Override
    public List<BakedModel> getRenderPasses (ItemStack itemStack, boolean fabulous) {
        // we don't render anything extra for items, so just pass through to the main model
        return mainModel.getRenderPasses(itemStack, fabulous);
    }
}
