package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureAtlasSprite
{
    private final String iconName;
    protected List<int[][]> framesTextureData = Lists.<int[][]>newArrayList();
    protected int[][] interpolatedFrameData;
    private AnimationMetadataSection animationMetadata;
    protected boolean rotated;
    protected int originX;
    protected int originY;
    protected int width;
    protected int height;
    private float minU;
    private float maxU;
    private float minV;
    private float maxV;
    protected int frameCounter;
    protected int tickCounter;

    protected TextureAtlasSprite(String spriteName)
    {
        this.iconName = spriteName;
    }

    protected static TextureAtlasSprite makeAtlasSprite(ResourceLocation spriteResourceLocation)
    {
        return new TextureAtlasSprite(spriteResourceLocation.toString());
    }

    public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn)
    {
        this.originX = originInX;
        this.originY = originInY;
        this.rotated = rotatedIn;
        this.minU = (float)originInX / (float)inX;
        this.maxU = (float)(originInX + this.width) / (float)inX;
        this.minV = (float)originInY / (float)inY;
        this.maxV = (float)(originInY + this.height) / (float)inY;
    }

    public void copyFrom(TextureAtlasSprite atlasSpirit)
    {
        this.originX = atlasSpirit.originX;
        this.originY = atlasSpirit.originY;
        this.width = atlasSpirit.width;
        this.height = atlasSpirit.height;
        this.rotated = atlasSpirit.rotated;
        this.minU = atlasSpirit.minU;
        this.maxU = atlasSpirit.maxU;
        this.minV = atlasSpirit.minV;
        this.maxV = atlasSpirit.maxV;
    }

    /**
     * Returns the X position of this icon on its texture sheet, in pixels.
     */
    public int getOriginX()
    {
        return this.originX;
    }

    /**
     * Returns the Y position of this icon on its texture sheet, in pixels.
     */
    public int getOriginY()
    {
        return this.originY;
    }

    /**
     * Returns the width of the icon, in pixels.
     */
    public int getIconWidth()
    {
        return this.width;
    }

    /**
     * Returns the height of the icon, in pixels.
     */
    public int getIconHeight()
    {
        return this.height;
    }

    /**
     * Returns the minimum U coordinate to use when rendering with this icon.
     */
    public float getMinU()
    {
        return this.minU;
    }

    /**
     * Returns the maximum U coordinate to use when rendering with this icon.
     */
    public float getMaxU()
    {
        return this.maxU;
    }

    /**
     * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other arguments return in-between values.
     */
    public float getInterpolatedU(double u)
    {
        float f = this.maxU - this.minU;
        return this.minU + f * (float)u / 16.0F;
    }

    /**
     * The opposite of getInterpolatedU. Takes the return value of that method and returns the input to it.
     */
    public float getUnInterpolatedU(float p_188537_1_)
    {
        float f = this.maxU - this.minU;
        return (p_188537_1_ - this.minU) / f * 16.0F;
    }

    /**
     * Returns the minimum V coordinate to use when rendering with this icon.
     */
    public float getMinV()
    {
        return this.minV;
    }

    /**
     * Returns the maximum V coordinate to use when rendering with this icon.
     */
    public float getMaxV()
    {
        return this.maxV;
    }

    /**
     * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other arguments return in-between values.
     */
    public float getInterpolatedV(double v)
    {
        float f = this.maxV - this.minV;
        return this.minV + f * (float)v / 16.0F;
    }

    /**
     * The opposite of getInterpolatedV. Takes the return value of that method and returns the input to it.
     */
    public float getUnInterpolatedV(float p_188536_1_)
    {
        float f = this.maxV - this.minV;
        return (p_188536_1_ - this.minV) / f * 16.0F;
    }

    public String getIconName()
    {
        return this.iconName;
    }

    public void updateAnimation()
    {
        ++this.tickCounter;

        if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter))
        {
            int i = this.animationMetadata.getFrameIndex(this.frameCounter);
            int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % j;
            this.tickCounter = 0;
            int k = this.animationMetadata.getFrameIndex(this.frameCounter);

            if (i != k && k >= 0 && k < this.framesTextureData.size())
            {
                TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(k), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
        else if (this.animationMetadata.isInterpolate())
        {
            this.updateAnimationInterpolated();
        }
    }

    private void updateAnimationInterpolated()
    {
        double d0 = 1.0D - (double)this.tickCounter / (double)this.animationMetadata.getFrameTimeSingle(this.frameCounter);
        int i = this.animationMetadata.getFrameIndex(this.frameCounter);
        int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
        int k = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % j);

        if (i != k && k >= 0 && k < this.framesTextureData.size())
        {
            int[][] aint = (int[][])this.framesTextureData.get(i);
            int[][] aint1 = (int[][])this.framesTextureData.get(k);

            if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != aint.length)
            {
                this.interpolatedFrameData = new int[aint.length][];
            }

            for (int l = 0; l < aint.length; ++l)
            {
                if (this.interpolatedFrameData[l] == null)
                {
                    this.interpolatedFrameData[l] = new int[aint[l].length];
                }

                if (l < aint1.length && aint1[l].length == aint[l].length)
                {
                    for (int i1 = 0; i1 < aint[l].length; ++i1)
                    {
                        int j1 = aint[l][i1];
                        int k1 = aint1[l][i1];
                        int l1 = this.interpolateColor(d0, j1 >> 16 & 255, k1 >> 16 & 255);
                        int i2 = this.interpolateColor(d0, j1 >> 8 & 255, k1 >> 8 & 255);
                        int j2 = this.interpolateColor(d0, j1 & 255, k1 & 255);
                        this.interpolatedFrameData[l][i1] = j1 & -16777216 | l1 << 16 | i2 << 8 | j2;
                    }
                }
            }

            TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
        }
    }

    private int interpolateColor(double p_188535_1_, int p_188535_3_, int p_188535_4_)
    {
        return (int)(p_188535_1_ * (double)p_188535_3_ + (1.0D - p_188535_1_) * (double)p_188535_4_);
    }

    public int[][] getFrameTextureData(int index)
    {
        return (int[][])this.framesTextureData.get(index);
    }

    public int getFrameCount()
    {
        return this.framesTextureData.size();
    }

    public void setIconWidth(int newWidth)
    {
        this.width = newWidth;
    }

    public void setIconHeight(int newHeight)
    {
        this.height = newHeight;
    }

    public void loadSprite(PngSizeInfo sizeInfo, boolean p_188538_2_) throws IOException
    {
        this.resetSprite();
        this.width = sizeInfo.pngWidth;
        this.height = sizeInfo.pngHeight;

        if (p_188538_2_)
        {
            this.height = this.width;
        }
        else if (sizeInfo.pngHeight != sizeInfo.pngWidth)
        {
            throw new RuntimeException("broken aspect ratio and not an animation");
        }
    }

    public void loadSpriteFrames(IResource resource, int mipmaplevels) throws IOException
    {
        BufferedImage bufferedimage = TextureUtil.readBufferedImage(resource.getInputStream());
        AnimationMetadataSection animationmetadatasection = (AnimationMetadataSection)resource.getMetadata("animation");
        int[][] aint = new int[mipmaplevels][];
        aint[0] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
        bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[0], 0, bufferedimage.getWidth());

        if (animationmetadatasection == null)
        {
            this.framesTextureData.add(aint);
        }
        else
        {
            int i = bufferedimage.getHeight() / this.width;

            if (animationmetadatasection.getFrameCount() > 0)
            {
                Iterator iterator = animationmetadatasection.getFrameIndexSet().iterator();

                while (iterator.hasNext())
                {
                    int j = ((Integer)iterator.next()).intValue();

                    if (j >= i)
                    {
                        throw new RuntimeException("invalid frameindex " + j);
                    }

                    this.allocateFrameTextureData(j);
                    this.framesTextureData.set(j, getFrameTextureData(aint, this.width, this.width, j));
                }

                this.animationMetadata = animationmetadatasection;
            }
            else
            {
                List<AnimationFrame> list = Lists.<AnimationFrame>newArrayList();

                for (int k = 0; k < i; ++k)
                {
                    this.framesTextureData.add(getFrameTextureData(aint, this.width, this.width, k));
                    list.add(new AnimationFrame(k, -1));
                }

                this.animationMetadata = new AnimationMetadataSection(list, this.width, this.height, animationmetadatasection.getFrameTime(), animationmetadatasection.isInterpolate());
            }
        }
    }

    public void generateMipmaps(int level)
    {
        List<int[][]> list = Lists.<int[][]>newArrayList();

        for (int i = 0; i < this.framesTextureData.size(); ++i)
        {
            final int[][] aint = (int[][])this.framesTextureData.get(i);

            if (aint != null)
            {
                try
                {
                    list.add(TextureUtil.generateMipmapData(level, this.width, aint));
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
                    crashreportcategory.addCrashSection("Frame index", Integer.valueOf(i));
                    crashreportcategory.setDetail("Frame sizes", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            StringBuilder stringbuilder = new StringBuilder();

                            for (int[] aint1 : aint)
                            {
                                if (stringbuilder.length() > 0)
                                {
                                    stringbuilder.append(", ");
                                }

                                stringbuilder.append(aint1 == null ? "null" : Integer.valueOf(aint1.length));
                            }

                            return stringbuilder.toString();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }

        this.setFramesTextureData(list);
    }

    private void allocateFrameTextureData(int index)
    {
        if (this.framesTextureData.size() <= index)
        {
            for (int i = this.framesTextureData.size(); i <= index; ++i)
            {
                this.framesTextureData.add((int[][])null);
            }
        }
    }

    private static int[][] getFrameTextureData(int[][] data, int rows, int columns, int p_147962_3_)
    {
        int[][] aint = new int[data.length][];

        for (int i = 0; i < data.length; ++i)
        {
            int[] aint1 = data[i];

            if (aint1 != null)
            {
                aint[i] = new int[(rows >> i) * (columns >> i)];
                System.arraycopy(aint1, p_147962_3_ * aint[i].length, aint[i], 0, aint[i].length);
            }
        }

        return aint;
    }

    public void clearFramesTextureData()
    {
        this.framesTextureData.clear();
    }

    public boolean hasAnimationMetadata()
    {
        return this.animationMetadata != null;
    }

    public void setFramesTextureData(List<int[][]> newFramesTextureData)
    {
        this.framesTextureData = newFramesTextureData;
    }

    private void resetSprite()
    {
        this.animationMetadata = null;
        this.setFramesTextureData(Lists.<int[][]>newArrayList());
        this.frameCounter = 0;
        this.tickCounter = 0;
    }

    public String toString()
    {
        return "TextureAtlasSprite{name=\'" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size() + ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
    }

    /*===================================== FORGE START =====================================*/
    /**
     * The result of this function determines is the below 'load' function is called, and the
     * default vanilla loading code is bypassed completely.
     * @param manager
     * @param location
     * @return True to use your own custom load code and bypass vanilla loading.
     */
    public boolean hasCustomLoader(net.minecraft.client.resources.IResourceManager manager, net.minecraft.util.ResourceLocation location)
    {
        return false;
    }

    /**
     * Load the specified resource as this sprite's data.
     * Returning false from this function will prevent this icon from being stitched onto the master texture.
     * @param manager Main resource manager
     * @param location File resource location
     * @return False to prevent this Icon from being stitched
     */
    public boolean load(net.minecraft.client.resources.IResourceManager manager, net.minecraft.util.ResourceLocation location)
    {
        return true;
    }
    /*===================================== FORGE END ======================================*/
}