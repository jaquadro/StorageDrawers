package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class FabricReplacementBlockPart extends ReplacementBlockPart
{
    private ChunkSectionLayer renderType;

    public FabricReplacementBlockPart (BlockStateModelPart part, TextureAtlasSprite sprite) {
        super(part, sprite);
    }

    @Override
    public void setRenderType (ChunkSectionLayer renderType) {
        this.renderType = renderType;
    }

    @Override
    public ChunkSectionLayer getRenderType () {
        return renderType;
    }
}
