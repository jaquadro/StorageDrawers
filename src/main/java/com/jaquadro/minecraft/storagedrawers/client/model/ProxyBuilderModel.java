package com.jaquadro.minecraft.storagedrawers.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ProxyBuilderModel implements IBakedModel
{
    private static final List<BakedQuad> EMPTY = new ArrayList<BakedQuad>(0);
    private static final List<Object> EMPTY_KEY = new ArrayList<Object>();

    private IBakedModel parent;
    private IBakedModel proxy;
    private BlockState stateCache;
    private TextureAtlasSprite iconParticle;

    public ProxyBuilderModel (TextureAtlasSprite iconParticle) {
        this.iconParticle = iconParticle;
    }

    public ProxyBuilderModel (IBakedModel parent) {
        this.parent = parent;
    }

    @Override
    public List<BakedQuad> getQuads (BlockState state, Direction side, Random rand) {
        if (proxy == null || stateCache != state)
            setProxy(state);

        if (proxy == null)
            return EMPTY;

        return proxy.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion () {
        IBakedModel model = getActiveModel();
        return (model != null) ? model.isAmbientOcclusion() : true;
    }

    @Override
    public boolean isGui3d () {
        IBakedModel model = getActiveModel();
        return (model != null) ? model.isGui3d() : false;
    }

    @Override
    public boolean isBuiltInRenderer () {
        IBakedModel model = getActiveModel();
        return (model != null) ? model.isBuiltInRenderer() : false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        IBakedModel model = getActiveModel();
        return (model != null) ? model.getParticleTexture() : iconParticle;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        IBakedModel model = getActiveModel();
        return (model != null) ? model.getItemCameraTransforms() : ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides () {
        IBakedModel model = getActiveModel();
        return (model != null) ? model.getOverrides() : ItemOverrideList.EMPTY;
    }

    public List<Object> getKey (BlockState state) {
        return EMPTY_KEY;
    }

    protected abstract IBakedModel buildModel (BlockState state, IBakedModel parent);

    public final IBakedModel buildModel (BlockState state) {
        return this.buildModel(state, parent);
    }

    private void setProxy (BlockState state) {
        stateCache = state;
        if (state == null)
            proxy = parent;
        else
            proxy = buildModel(state, parent);
    }

    private IBakedModel getActiveModel () {
        return (proxy != null) ? proxy : parent;
    }
}
