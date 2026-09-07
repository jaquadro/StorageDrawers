package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.render.ChameleonBlockModelPart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface ChameleonRender
{
    ChameleonBlockModelPart createReplacementPart (BlockStateModelPart part, TextureAtlasSprite sprite);
}
