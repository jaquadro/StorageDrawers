package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.render.ChameleonBlockModelPart;
import com.texelsaurus.minecraft.chameleon.render.ForgeReplacementBlockPart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ForgeRender implements ChameleonRender
{
    @Override
    public ChameleonBlockModelPart createReplacementPart (BlockStateModelPart part, TextureAtlasSprite sprite) {
        return new ForgeReplacementBlockPart(part, sprite);
    }
}
