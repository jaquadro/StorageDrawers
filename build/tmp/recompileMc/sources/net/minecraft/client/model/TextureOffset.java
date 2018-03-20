package net.minecraft.client.model;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureOffset
{
    /** The x coordinate offset of the texture */
    public final int textureOffsetX;
    /** The y coordinate offset of the texture */
    public final int textureOffsetY;

    public TextureOffset(int textureOffsetXIn, int textureOffsetYIn)
    {
        this.textureOffsetX = textureOffsetXIn;
        this.textureOffsetY = textureOffsetYIn;
    }
}