package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public interface ChameleonBlockModelPart extends BlockStateModelPart
{
    default void setRenderType (ChunkSectionLayer layer) { }

    ChunkSectionLayer getRenderType();
}
