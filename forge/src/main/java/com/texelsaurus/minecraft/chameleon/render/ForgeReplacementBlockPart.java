package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import javax.annotation.Nullable;

public class ForgeReplacementBlockPart extends ReplacementBlockPart
{
    @Nullable
    private ChunkSectionLayer renderType;

    public ForgeReplacementBlockPart (BlockStateModelPart part, TextureAtlasSprite sprite) {
        super(part, sprite);
    }

    @Override
    public void setRenderType (@Nullable ChunkSectionLayer renderType) {
        this.renderType = renderType;
    }

    @Override
    @Nullable
    public ChunkSectionLayer getRenderType () {
        return renderType;
    }
}
