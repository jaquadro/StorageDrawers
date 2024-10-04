package com.jaquadro.minecraft.storagedrawers.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ParentModel implements BakedModel
{
    @NotNull
    protected final BakedModel parent;

    public ParentModel (@NotNull BakedModel parent) {
        this.parent = parent;
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction dir, RandomSource rand) {
        return parent.getQuads(state, dir, rand);
    }

    @Override
    public boolean useAmbientOcclusion () {
        return parent.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d () {
        return parent.isGui3d();
    }

    @Override
    public boolean usesBlockLight () {
        return parent.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer () {
        return parent.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon () {
        return parent.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides () {
        return parent.getOverrides();
    }

    @Override
    public ItemTransforms getTransforms () {
        return parent.getTransforms();
    }
}
