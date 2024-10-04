package com.jaquadro.minecraft.storagedrawers.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteReplacementModel extends ParentModel
{
    private TextureAtlasSprite sprite;
    private Map<String, List<BakedQuad>> cache = new HashMap<>();

    public SpriteReplacementModel (@NotNull BakedModel parent, TextureAtlasSprite sprite) {
        super(parent);
        this.sprite = sprite;
    }

    public SpriteReplacementModel (@NotNull BakedModel parent, ItemStack stack) {
        super(parent);

        if (stack != null && stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockRenderDispatcher disp = Minecraft.getInstance().getBlockRenderer();
            BakedModel model = disp.getBlockModel(block.defaultBlockState());
            sprite = model.getParticleIcon();
        }
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction dir, RandomSource rand) {
        if (sprite == null)
            return super.getQuads(state, dir, rand);

        String key = cacheKey(state, dir);
        List<BakedQuad> list = cache.getOrDefault(key, null);
        if (list == null) {
            list = super.getQuads(state, dir, rand).stream().map(
                bakedQuad -> (BakedQuad)new ReplacementBakedQuad(bakedQuad, sprite)).toList();
            cache.put(key, list);
        }

        return list;
    }

    @Override
    public TextureAtlasSprite getParticleIcon () {
        if (sprite == null)
            return super.getParticleIcon();

        return sprite;
    }

    private String cacheKey (@Nullable BlockState state, @Nullable Direction dir) {
        return ((state != null) ? state.toString() : "") + "#" + ((dir != null) ? dir.toString() : "");
    }

    private static class ReplacementBakedQuad extends BakedQuad
    {
        TextureAtlasSprite sprite;

        public ReplacementBakedQuad (BakedQuad quad, @NotNull TextureAtlasSprite sprite) {
            super(quad.getVertices().clone(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion());
            this.sprite = sprite;
            remapQuad();
        }

        @Override
        public @NotNull TextureAtlasSprite getSprite () {
            return sprite;
        }

        private void remapQuad() {
            int uvIndex = 4;

            for(int i = 0; i < 4; ++i) {
                int blk = DefaultVertexFormat.BLOCK.getIntegerSize() * i;
                this.vertices[blk + uvIndex] = Float.floatToRawIntBits(this.sprite.getU(getUnInterpolatedU(super.sprite, Float.intBitsToFloat(this.vertices[blk + uvIndex]))));
                this.vertices[blk + uvIndex + 1] = Float.floatToRawIntBits(this.sprite.getV(getUnInterpolatedV(super.sprite, Float.intBitsToFloat(this.vertices[blk + uvIndex + 1]))));
            }

        }

        private float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
            float diff = sprite.getU1() - sprite.getU0();
            return (u - sprite.getU0()) / diff * 16.0F;
        }

        private float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
            float diff = sprite.getV1() - sprite.getV0();
            return (v - sprite.getV0()) / diff * 16.0F;
        }
    }
}
