package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.render.ChameleonBlockModelPart;
import com.texelsaurus.minecraft.chameleon.render.NeoforgeReplacementBlockPart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class NeoforgeRender implements ChameleonRender
{
    @Override
    public ChameleonBlockModelPart createReplacementPart (BlockStateModelPart part, TextureAtlasSprite sprite) {
        return new NeoforgeReplacementBlockPart(part, sprite);
    }
}
