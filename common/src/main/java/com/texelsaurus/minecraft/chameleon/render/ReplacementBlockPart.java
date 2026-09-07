package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ReplacementBlockPart implements ChameleonBlockModelPart
{
    protected BlockStateModelPart parent;
    private TextureAtlasSprite sprite;
    private List<BakedQuad> quads = new ArrayList<>();

    public ReplacementBlockPart(BlockStateModelPart part, TextureAtlasSprite sprite) {
        parent = part;
        this.sprite = sprite;

        part.getQuads(null).forEach(quad -> quads.add(remapQuad(quad, sprite)));
        for (Direction dir : Direction.values()) {
            part.getQuads(dir).forEach(quad -> quads.add(remapQuad(quad, sprite)));
        }
    }

    public ReplacementBlockPart (BlockStateModelPart parent, BlockStateModelPart replacement) {
        this(parent, replacement.particleMaterial().sprite());
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable Direction direction) {
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion () {
        return parent.useAmbientOcclusion();
    }

    @Override
    public Material.Baked particleMaterial () {
        if (sprite == null)
            return parent.particleMaterial();

        return new Material.Baked(sprite, false);
    }

    @Override
    public int materialFlags () {
        return parent.materialFlags();
    }

    BakedQuad remapQuad (BakedQuad quad, TextureAtlasSprite sprite) {
        long uv0 = remapPackedUV(quad, quad.packedUV0());
        long uv1 = remapPackedUV(quad, quad.packedUV1());
        long uv2 = remapPackedUV(quad, quad.packedUV2());
        long uv3 = remapPackedUV(quad, quad.packedUV3());

        BakedQuad.MaterialInfo info = quad.materialInfo();
        BakedQuad.MaterialInfo newInfo = new BakedQuad.MaterialInfo(
            sprite, info.layer(), info.itemRenderType(), info.tintIndex(), info.shade(), info.lightEmission());

        return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
            uv0, uv1, uv2, uv3,
            quad.direction(), newInfo);
    }

    private long remapPackedUV(BakedQuad quad, long packedUV) {
        float u = UVPair.unpackU(packedUV);
        float v = UVPair.unpackV(packedUV);
        TextureAtlasSprite parentSprite = quad.materialInfo().sprite();
        float mapU = sprite.getU(getUnInterpolatedU(parentSprite, u));
        float mapV = sprite.getV(getUnInterpolatedV(parentSprite, v));

        return UVPair.pack(mapU, mapV);
    }

    private float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
        float diff = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / diff;
    }

    private float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
        float diff = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / diff;
    }
}
