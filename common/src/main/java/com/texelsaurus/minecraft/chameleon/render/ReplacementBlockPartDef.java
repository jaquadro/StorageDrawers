package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ReplacementBlockPartDef
{
    private BlockStateModelPart part;
    private TextureAtlasSprite sprite;

    public ReplacementBlockPartDef (BlockStateModelPart part, TextureAtlasSprite sprite) {
        this.part = part;
        this.sprite = sprite;
    }

    public BlockStateModelPart getPart () {
        return part;
    }

    public TextureAtlasSprite getSprite () {
        return sprite;
    }
}
