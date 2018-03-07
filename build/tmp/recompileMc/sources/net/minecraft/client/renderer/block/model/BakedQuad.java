package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedQuad implements net.minecraftforge.client.model.pipeline.IVertexProducer
{
    /**
     * Joined 4 vertex records, each stores packed data according to the VertexFormat of the quad. Vanilla minecraft
     * uses DefaultVertexFormats.BLOCK, Forge uses (usually) ITEM, use BakedQuad.getFormat() to get the correct format.
     */
    protected final int[] vertexData;
    protected final int tintIndex;
    protected final EnumFacing face;
    protected final TextureAtlasSprite sprite;

    /**
     * @deprecated Use constructor with the format argument.
     */
    @Deprecated
    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn)
    {
        this(vertexDataIn, tintIndexIn, faceIn, spriteIn, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
    }

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn, boolean applyDiffuseLighting, net.minecraft.client.renderer.vertex.VertexFormat format)
    {
        this.format = format;
        this.applyDiffuseLighting = applyDiffuseLighting;
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
        this.sprite = spriteIn;
    }

    public TextureAtlasSprite getSprite()
    {
        return this.sprite;
    }

    public int[] getVertexData()
    {
        return this.vertexData;
    }

    public boolean hasTintIndex()
    {
        return this.tintIndex != -1;
    }

    public int getTintIndex()
    {
        return this.tintIndex;
    }

    public EnumFacing getFace()
    {
        return this.face;
    }

    protected final net.minecraft.client.renderer.vertex.VertexFormat format;
    protected final boolean applyDiffuseLighting;

    @Override
    public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer)
    {
        net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(consumer, this);
    }

    public net.minecraft.client.renderer.vertex.VertexFormat getFormat()
    {
        return format;
    }

    public boolean shouldApplyDiffuseLighting()
    {
        return applyDiffuseLighting;
    }
}