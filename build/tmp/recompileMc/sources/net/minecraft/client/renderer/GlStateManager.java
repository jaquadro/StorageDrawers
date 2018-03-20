package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Quaternion;

@SideOnly(Side.CLIENT)
public class GlStateManager
{
    private static final FloatBuffer BUF_FLOAT_16 = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer BUF_FLOAT_4 = BufferUtils.createFloatBuffer(4);
    private static final GlStateManager.AlphaState alphaState = new GlStateManager.AlphaState();
    private static final GlStateManager.BooleanState lightingState = new GlStateManager.BooleanState(2896);
    private static final GlStateManager.BooleanState[] lightState = new GlStateManager.BooleanState[8];
    private static final GlStateManager.ColorMaterialState colorMaterialState;
    private static final GlStateManager.BlendState blendState;
    private static final GlStateManager.DepthState depthState;
    private static final GlStateManager.FogState fogState;
    private static final GlStateManager.CullState cullState;
    private static final GlStateManager.PolygonOffsetState polygonOffsetState;
    private static final GlStateManager.ColorLogicState colorLogicState;
    private static final GlStateManager.TexGenState texGenState;
    private static final GlStateManager.ClearState clearState;
    private static final GlStateManager.StencilState stencilState;
    private static final GlStateManager.BooleanState normalizeState;
    private static int activeTextureUnit;
    private static final GlStateManager.TextureState[] textureState;
    private static int activeShadeModel;
    private static final GlStateManager.BooleanState rescaleNormalState;
    private static final GlStateManager.ColorMask colorMaskState;
    private static final GlStateManager.Color colorState;

    public static void pushAttrib()
    {
        GL11.glPushAttrib(8256);
    }

    public static void popAttrib()
    {
        GL11.glPopAttrib();
    }

    public static void disableAlpha()
    {
        alphaState.alphaTest.setDisabled();
    }

    public static void enableAlpha()
    {
        alphaState.alphaTest.setEnabled();
    }

    public static void alphaFunc(int func, float ref)
    {
        if (func != alphaState.func || ref != alphaState.ref)
        {
            alphaState.func = func;
            alphaState.ref = ref;
            GL11.glAlphaFunc(func, ref);
        }
    }

    public static void enableLighting()
    {
        lightingState.setEnabled();
    }

    public static void disableLighting()
    {
        lightingState.setDisabled();
    }

    public static void enableLight(int light)
    {
        lightState[light].setEnabled();
    }

    public static void disableLight(int light)
    {
        lightState[light].setDisabled();
    }

    public static void enableColorMaterial()
    {
        colorMaterialState.colorMaterial.setEnabled();
    }

    public static void disableColorMaterial()
    {
        colorMaterialState.colorMaterial.setDisabled();
    }

    public static void colorMaterial(int face, int mode)
    {
        if (face != colorMaterialState.face || mode != colorMaterialState.mode)
        {
            colorMaterialState.face = face;
            colorMaterialState.mode = mode;
            GL11.glColorMaterial(face, mode);
        }
    }

    public static void glLight(int light, int pname, FloatBuffer params)
    {
        GL11.glLight(light, pname, params);
    }

    public static void glLightModel(int pname, FloatBuffer params)
    {
        GL11.glLightModel(pname, params);
    }

    public static void glNormal3f(float nx, float ny, float nz)
    {
        GL11.glNormal3f(nx, ny, nz);
    }

    public static void disableDepth()
    {
        depthState.depthTest.setDisabled();
    }

    public static void enableDepth()
    {
        depthState.depthTest.setEnabled();
    }

    public static void depthFunc(int depthFunc)
    {
        if (depthFunc != depthState.depthFunc)
        {
            depthState.depthFunc = depthFunc;
            GL11.glDepthFunc(depthFunc);
        }
    }

    public static void depthMask(boolean flagIn)
    {
        if (flagIn != depthState.maskEnabled)
        {
            depthState.maskEnabled = flagIn;
            GL11.glDepthMask(flagIn);
        }
    }

    public static void disableBlend()
    {
        blendState.blend.setDisabled();
    }

    public static void enableBlend()
    {
        blendState.blend.setEnabled();
    }

    public static void blendFunc(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor)
    {
        blendFunc(srcFactor.factor, dstFactor.factor);
    }

    public static void blendFunc(int srcFactor, int dstFactor)
    {
        if (srcFactor != blendState.srcFactor || dstFactor != blendState.dstFactor)
        {
            blendState.srcFactor = srcFactor;
            blendState.dstFactor = dstFactor;
            GL11.glBlendFunc(srcFactor, dstFactor);
        }
    }

    public static void tryBlendFuncSeparate(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor, GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha)
    {
        tryBlendFuncSeparate(srcFactor.factor, dstFactor.factor, srcFactorAlpha.factor, dstFactorAlpha.factor);
    }

    public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha)
    {
        if (srcFactor != blendState.srcFactor || dstFactor != blendState.dstFactor || srcFactorAlpha != blendState.srcFactorAlpha || dstFactorAlpha != blendState.dstFactorAlpha)
        {
            blendState.srcFactor = srcFactor;
            blendState.dstFactor = dstFactor;
            blendState.srcFactorAlpha = srcFactorAlpha;
            blendState.dstFactorAlpha = dstFactorAlpha;
            OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void glBlendEquation(int blendEquation)
    {
        GL14.glBlendEquation(blendEquation);
    }

    public static void enableOutlineMode(int p_187431_0_)
    {
        BUF_FLOAT_4.put(0, (float)(p_187431_0_ >> 16 & 255) / 255.0F);
        BUF_FLOAT_4.put(1, (float)(p_187431_0_ >> 8 & 255) / 255.0F);
        BUF_FLOAT_4.put(2, (float)(p_187431_0_ >> 0 & 255) / 255.0F);
        BUF_FLOAT_4.put(3, (float)(p_187431_0_ >> 24 & 255) / 255.0F);
        glTexEnv(8960, 8705, BUF_FLOAT_4);
        glTexEnvi(8960, 8704, 34160);
        glTexEnvi(8960, 34161, 7681);
        glTexEnvi(8960, 34176, 34166);
        glTexEnvi(8960, 34192, 768);
        glTexEnvi(8960, 34162, 7681);
        glTexEnvi(8960, 34184, 5890);
        glTexEnvi(8960, 34200, 770);
    }

    public static void disableOutlineMode()
    {
        glTexEnvi(8960, 8704, 8448);
        glTexEnvi(8960, 34161, 8448);
        glTexEnvi(8960, 34162, 8448);
        glTexEnvi(8960, 34176, 5890);
        glTexEnvi(8960, 34184, 5890);
        glTexEnvi(8960, 34192, 768);
        glTexEnvi(8960, 34200, 770);
    }

    public static void enableFog()
    {
        fogState.fog.setEnabled();
    }

    public static void disableFog()
    {
        fogState.fog.setDisabled();
    }

    public static void setFog(GlStateManager.FogMode fogMode)
    {
        setFog(fogMode.capabilityId);
    }

    private static void setFog(int param)
    {
        if (param != fogState.mode)
        {
            fogState.mode = param;
            GL11.glFogi(GL11.GL_FOG_MODE, param);
        }
    }

    public static void setFogDensity(float param)
    {
        if (param != fogState.density)
        {
            fogState.density = param;
            GL11.glFogf(GL11.GL_FOG_DENSITY, param);
        }
    }

    public static void setFogStart(float param)
    {
        if (param != fogState.start)
        {
            fogState.start = param;
            GL11.glFogf(GL11.GL_FOG_START, param);
        }
    }

    public static void setFogEnd(float param)
    {
        if (param != fogState.end)
        {
            fogState.end = param;
            GL11.glFogf(GL11.GL_FOG_END, param);
        }
    }

    public static void glFog(int pname, FloatBuffer param)
    {
        GL11.glFog(pname, param);
    }

    public static void glFogi(int pname, int param)
    {
        GL11.glFogi(pname, param);
    }

    public static void enableCull()
    {
        cullState.cullFace.setEnabled();
    }

    public static void disableCull()
    {
        cullState.cullFace.setDisabled();
    }

    public static void cullFace(GlStateManager.CullFace cullFace)
    {
        cullFace(cullFace.mode);
    }

    private static void cullFace(int mode)
    {
        if (mode != cullState.mode)
        {
            cullState.mode = mode;
            GL11.glCullFace(mode);
        }
    }

    public static void glPolygonMode(int face, int mode)
    {
        GL11.glPolygonMode(face, mode);
    }

    public static void enablePolygonOffset()
    {
        polygonOffsetState.polygonOffsetFill.setEnabled();
    }

    public static void disablePolygonOffset()
    {
        polygonOffsetState.polygonOffsetFill.setDisabled();
    }

    public static void doPolygonOffset(float factor, float units)
    {
        if (factor != polygonOffsetState.factor || units != polygonOffsetState.units)
        {
            polygonOffsetState.factor = factor;
            polygonOffsetState.units = units;
            GL11.glPolygonOffset(factor, units);
        }
    }

    public static void enableColorLogic()
    {
        colorLogicState.colorLogicOp.setEnabled();
    }

    public static void disableColorLogic()
    {
        colorLogicState.colorLogicOp.setDisabled();
    }

    public static void colorLogicOp(GlStateManager.LogicOp logicOperation)
    {
        colorLogicOp(logicOperation.opcode);
    }

    public static void colorLogicOp(int opcode)
    {
        if (opcode != colorLogicState.opcode)
        {
            colorLogicState.opcode = opcode;
            GL11.glLogicOp(opcode);
        }
    }

    public static void enableTexGenCoord(GlStateManager.TexGen texGen)
    {
        texGenCoord(texGen).textureGen.setEnabled();
    }

    public static void disableTexGenCoord(GlStateManager.TexGen texGen)
    {
        texGenCoord(texGen).textureGen.setDisabled();
    }

    public static void texGen(GlStateManager.TexGen texGen, int param)
    {
        GlStateManager.TexGenCoord glstatemanager$texgencoord = texGenCoord(texGen);

        if (param != glstatemanager$texgencoord.param)
        {
            glstatemanager$texgencoord.param = param;
            GL11.glTexGeni(glstatemanager$texgencoord.coord, GL11.GL_TEXTURE_GEN_MODE, param);
        }
    }

    public static void texGen(GlStateManager.TexGen texGen, int pname, FloatBuffer params)
    {
        GL11.glTexGen(texGenCoord(texGen).coord, pname, params);
    }

    private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen texGen)
    {
        switch (texGen)
        {
            case S:
                return texGenState.s;
            case T:
                return texGenState.t;
            case R:
                return texGenState.r;
            case Q:
                return texGenState.q;
            default:
                return texGenState.s;
        }
    }

    public static void setActiveTexture(int texture)
    {
        if (activeTextureUnit != texture - OpenGlHelper.defaultTexUnit)
        {
            activeTextureUnit = texture - OpenGlHelper.defaultTexUnit;
            OpenGlHelper.setActiveTexture(texture);
        }
    }

    public static void enableTexture2D()
    {
        textureState[activeTextureUnit].texture2DState.setEnabled();
    }

    public static void disableTexture2D()
    {
        textureState[activeTextureUnit].texture2DState.setDisabled();
    }

    public static void glTexEnv(int p_187448_0_, int p_187448_1_, FloatBuffer p_187448_2_)
    {
        GL11.glTexEnv(p_187448_0_, p_187448_1_, p_187448_2_);
    }

    public static void glTexEnvi(int p_187399_0_, int p_187399_1_, int p_187399_2_)
    {
        GL11.glTexEnvi(p_187399_0_, p_187399_1_, p_187399_2_);
    }

    public static void glTexEnvf(int p_187436_0_, int p_187436_1_, float p_187436_2_)
    {
        GL11.glTexEnvf(p_187436_0_, p_187436_1_, p_187436_2_);
    }

    public static void glTexParameterf(int p_187403_0_, int p_187403_1_, float p_187403_2_)
    {
        GL11.glTexParameterf(p_187403_0_, p_187403_1_, p_187403_2_);
    }

    public static void glTexParameteri(int p_187421_0_, int p_187421_1_, int p_187421_2_)
    {
        GL11.glTexParameteri(p_187421_0_, p_187421_1_, p_187421_2_);
    }

    public static int glGetTexLevelParameteri(int p_187411_0_, int p_187411_1_, int p_187411_2_)
    {
        return GL11.glGetTexLevelParameteri(p_187411_0_, p_187411_1_, p_187411_2_);
    }

    public static int generateTexture()
    {
        return GL11.glGenTextures();
    }

    public static void deleteTexture(int texture)
    {
        GL11.glDeleteTextures(texture);

        for (GlStateManager.TextureState glstatemanager$texturestate : textureState)
        {
            if (glstatemanager$texturestate.textureName == texture)
            {
                glstatemanager$texturestate.textureName = -1;
            }
        }
    }

    public static void bindTexture(int texture)
    {
        if (texture != textureState[activeTextureUnit].textureName)
        {
            textureState[activeTextureUnit].textureName = texture;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        }
    }

    public static void glTexImage2D(int p_187419_0_, int p_187419_1_, int p_187419_2_, int p_187419_3_, int p_187419_4_, int p_187419_5_, int p_187419_6_, int p_187419_7_, @Nullable IntBuffer p_187419_8_)
    {
        GL11.glTexImage2D(p_187419_0_, p_187419_1_, p_187419_2_, p_187419_3_, p_187419_4_, p_187419_5_, p_187419_6_, p_187419_7_, p_187419_8_);
    }

    public static void glTexSubImage2D(int p_187414_0_, int p_187414_1_, int p_187414_2_, int p_187414_3_, int p_187414_4_, int p_187414_5_, int p_187414_6_, int p_187414_7_, IntBuffer p_187414_8_)
    {
        GL11.glTexSubImage2D(p_187414_0_, p_187414_1_, p_187414_2_, p_187414_3_, p_187414_4_, p_187414_5_, p_187414_6_, p_187414_7_, p_187414_8_);
    }

    public static void glCopyTexSubImage2D(int p_187443_0_, int p_187443_1_, int p_187443_2_, int p_187443_3_, int p_187443_4_, int p_187443_5_, int p_187443_6_, int p_187443_7_)
    {
        GL11.glCopyTexSubImage2D(p_187443_0_, p_187443_1_, p_187443_2_, p_187443_3_, p_187443_4_, p_187443_5_, p_187443_6_, p_187443_7_);
    }

    public static void glGetTexImage(int p_187433_0_, int p_187433_1_, int p_187433_2_, int p_187433_3_, IntBuffer p_187433_4_)
    {
        GL11.glGetTexImage(p_187433_0_, p_187433_1_, p_187433_2_, p_187433_3_, p_187433_4_);
    }

    public static void enableNormalize()
    {
        normalizeState.setEnabled();
    }

    public static void disableNormalize()
    {
        normalizeState.setDisabled();
    }

    public static void shadeModel(int mode)
    {
        if (mode != activeShadeModel)
        {
            activeShadeModel = mode;
            GL11.glShadeModel(mode);
        }
    }

    public static void enableRescaleNormal()
    {
        rescaleNormalState.setEnabled();
    }

    public static void disableRescaleNormal()
    {
        rescaleNormalState.setDisabled();
    }

    public static void viewport(int x, int y, int width, int height)
    {
        GL11.glViewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha)
    {
        if (red != colorMaskState.red || green != colorMaskState.green || blue != colorMaskState.blue || alpha != colorMaskState.alpha)
        {
            colorMaskState.red = red;
            colorMaskState.green = green;
            colorMaskState.blue = blue;
            colorMaskState.alpha = alpha;
            GL11.glColorMask(red, green, blue, alpha);
        }
    }

    public static void clearDepth(double depth)
    {
        if (depth != clearState.depth)
        {
            clearState.depth = depth;
            GL11.glClearDepth(depth);
        }
    }

    public static void clearColor(float red, float green, float blue, float alpha)
    {
        if (red != clearState.color.red || green != clearState.color.green || blue != clearState.color.blue || alpha != clearState.color.alpha)
        {
            clearState.color.red = red;
            clearState.color.green = green;
            clearState.color.blue = blue;
            clearState.color.alpha = alpha;
            GL11.glClearColor(red, green, blue, alpha);
        }
    }

    public static void clear(int mask)
    {
        GL11.glClear(mask);
    }

    public static void matrixMode(int mode)
    {
        GL11.glMatrixMode(mode);
    }

    public static void loadIdentity()
    {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix()
    {
        GL11.glPushMatrix();
    }

    public static void popMatrix()
    {
        GL11.glPopMatrix();
    }

    public static void getFloat(int pname, FloatBuffer params)
    {
        GL11.glGetFloat(pname, params);
    }

    public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar)
    {
        GL11.glOrtho(left, right, bottom, top, zNear, zFar);
    }

    public static void rotate(float angle, float x, float y, float z)
    {
        GL11.glRotatef(angle, x, y, z);
    }

    public static void scale(float x, float y, float z)
    {
        GL11.glScalef(x, y, z);
    }

    public static void scale(double x, double y, double z)
    {
        GL11.glScaled(x, y, z);
    }

    public static void translate(float x, float y, float z)
    {
        GL11.glTranslatef(x, y, z);
    }

    public static void translate(double x, double y, double z)
    {
        GL11.glTranslated(x, y, z);
    }

    public static void multMatrix(FloatBuffer matrix)
    {
        GL11.glMultMatrix(matrix);
    }

    public static void rotate(Quaternion p_187444_0_)
    {
        multMatrix(quatToGlMatrix(BUF_FLOAT_16, p_187444_0_));
    }

    public static FloatBuffer quatToGlMatrix(FloatBuffer p_187418_0_, Quaternion p_187418_1_)
    {
        p_187418_0_.clear();
        float f = p_187418_1_.x * p_187418_1_.x;
        float f1 = p_187418_1_.x * p_187418_1_.y;
        float f2 = p_187418_1_.x * p_187418_1_.z;
        float f3 = p_187418_1_.x * p_187418_1_.w;
        float f4 = p_187418_1_.y * p_187418_1_.y;
        float f5 = p_187418_1_.y * p_187418_1_.z;
        float f6 = p_187418_1_.y * p_187418_1_.w;
        float f7 = p_187418_1_.z * p_187418_1_.z;
        float f8 = p_187418_1_.z * p_187418_1_.w;
        p_187418_0_.put(1.0F - 2.0F * (f4 + f7));
        p_187418_0_.put(2.0F * (f1 + f8));
        p_187418_0_.put(2.0F * (f2 - f6));
        p_187418_0_.put(0.0F);
        p_187418_0_.put(2.0F * (f1 - f8));
        p_187418_0_.put(1.0F - 2.0F * (f + f7));
        p_187418_0_.put(2.0F * (f5 + f3));
        p_187418_0_.put(0.0F);
        p_187418_0_.put(2.0F * (f2 + f6));
        p_187418_0_.put(2.0F * (f5 - f3));
        p_187418_0_.put(1.0F - 2.0F * (f + f4));
        p_187418_0_.put(0.0F);
        p_187418_0_.put(0.0F);
        p_187418_0_.put(0.0F);
        p_187418_0_.put(0.0F);
        p_187418_0_.put(1.0F);
        p_187418_0_.rewind();
        return p_187418_0_;
    }

    public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha)
    {
        if (colorRed != colorState.red || colorGreen != colorState.green || colorBlue != colorState.blue || colorAlpha != colorState.alpha)
        {
            colorState.red = colorRed;
            colorState.green = colorGreen;
            colorState.blue = colorBlue;
            colorState.alpha = colorAlpha;
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
        }
    }

    public static void color(float colorRed, float colorGreen, float colorBlue)
    {
        color(colorRed, colorGreen, colorBlue, 1.0F);
    }

    public static void glTexCoord2f(float p_187426_0_, float p_187426_1_)
    {
        GL11.glTexCoord2f(p_187426_0_, p_187426_1_);
    }

    public static void glVertex3f(float p_187435_0_, float p_187435_1_, float p_187435_2_)
    {
        GL11.glVertex3f(p_187435_0_, p_187435_1_, p_187435_2_);
    }

    public static void resetColor()
    {
        colorState.red = -1.0F;
        colorState.green = -1.0F;
        colorState.blue = -1.0F;
        colorState.alpha = -1.0F;
    }

    public static void glNormalPointer(int p_187446_0_, int p_187446_1_, ByteBuffer p_187446_2_)
    {
        GL11.glNormalPointer(p_187446_0_, p_187446_1_, p_187446_2_);
    }

    public static void glTexCoordPointer(int p_187405_0_, int p_187405_1_, int p_187405_2_, int p_187405_3_)
    {
        GL11.glTexCoordPointer(p_187405_0_, p_187405_1_, p_187405_2_, (long)p_187405_3_);
    }

    public static void glTexCoordPointer(int p_187404_0_, int p_187404_1_, int p_187404_2_, ByteBuffer p_187404_3_)
    {
        GL11.glTexCoordPointer(p_187404_0_, p_187404_1_, p_187404_2_, p_187404_3_);
    }

    public static void glVertexPointer(int p_187420_0_, int p_187420_1_, int p_187420_2_, int p_187420_3_)
    {
        GL11.glVertexPointer(p_187420_0_, p_187420_1_, p_187420_2_, (long)p_187420_3_);
    }

    public static void glVertexPointer(int p_187427_0_, int p_187427_1_, int p_187427_2_, ByteBuffer p_187427_3_)
    {
        GL11.glVertexPointer(p_187427_0_, p_187427_1_, p_187427_2_, p_187427_3_);
    }

    public static void glColorPointer(int p_187406_0_, int p_187406_1_, int p_187406_2_, int p_187406_3_)
    {
        GL11.glColorPointer(p_187406_0_, p_187406_1_, p_187406_2_, (long)p_187406_3_);
    }

    public static void glColorPointer(int p_187400_0_, int p_187400_1_, int p_187400_2_, ByteBuffer p_187400_3_)
    {
        GL11.glColorPointer(p_187400_0_, p_187400_1_, p_187400_2_, p_187400_3_);
    }

    public static void glDisableClientState(int p_187429_0_)
    {
        GL11.glDisableClientState(p_187429_0_);
    }

    public static void glEnableClientState(int p_187410_0_)
    {
        GL11.glEnableClientState(p_187410_0_);
    }

    public static void glBegin(int p_187447_0_)
    {
        GL11.glBegin(p_187447_0_);
    }

    public static void glEnd()
    {
        GL11.glEnd();
    }

    public static void glDrawArrays(int p_187439_0_, int p_187439_1_, int p_187439_2_)
    {
        GL11.glDrawArrays(p_187439_0_, p_187439_1_, p_187439_2_);
    }

    public static void glLineWidth(float p_187441_0_)
    {
        GL11.glLineWidth(p_187441_0_);
    }

    public static void callList(int list)
    {
        GL11.glCallList(list);
    }

    public static void glDeleteLists(int p_187449_0_, int p_187449_1_)
    {
        GL11.glDeleteLists(p_187449_0_, p_187449_1_);
    }

    public static void glNewList(int p_187423_0_, int p_187423_1_)
    {
        GL11.glNewList(p_187423_0_, p_187423_1_);
    }

    public static void glEndList()
    {
        GL11.glEndList();
    }

    public static int glGenLists(int p_187442_0_)
    {
        return GL11.glGenLists(p_187442_0_);
    }

    public static void glPixelStorei(int p_187425_0_, int p_187425_1_)
    {
        GL11.glPixelStorei(p_187425_0_, p_187425_1_);
    }

    public static void glReadPixels(int p_187413_0_, int p_187413_1_, int p_187413_2_, int p_187413_3_, int p_187413_4_, int p_187413_5_, IntBuffer p_187413_6_)
    {
        GL11.glReadPixels(p_187413_0_, p_187413_1_, p_187413_2_, p_187413_3_, p_187413_4_, p_187413_5_, p_187413_6_);
    }

    public static int glGetError()
    {
        return GL11.glGetError();
    }

    public static String glGetString(int p_187416_0_)
    {
        return GL11.glGetString(p_187416_0_);
    }

    public static void glGetInteger(int p_187445_0_, IntBuffer p_187445_1_)
    {
        GL11.glGetInteger(p_187445_0_, p_187445_1_);
    }

    public static int glGetInteger(int p_187397_0_)
    {
        return GL11.glGetInteger(p_187397_0_);
    }

    public static void enableBlendProfile(GlStateManager.Profile p_187408_0_)
    {
        p_187408_0_.apply();
    }

    public static void disableBlendProfile(GlStateManager.Profile p_187440_0_)
    {
        p_187440_0_.clean();
    }

    static
    {
        for (int i = 0; i < 8; ++i)
        {
            lightState[i] = new GlStateManager.BooleanState(16384 + i);
        }

        colorMaterialState = new GlStateManager.ColorMaterialState();
        blendState = new GlStateManager.BlendState();
        depthState = new GlStateManager.DepthState();
        fogState = new GlStateManager.FogState();
        cullState = new GlStateManager.CullState();
        polygonOffsetState = new GlStateManager.PolygonOffsetState();
        colorLogicState = new GlStateManager.ColorLogicState();
        texGenState = new GlStateManager.TexGenState();
        clearState = new GlStateManager.ClearState();
        stencilState = new GlStateManager.StencilState();
        normalizeState = new GlStateManager.BooleanState(2977);
        textureState = new GlStateManager.TextureState[8];

        for (int j = 0; j < 8; ++j)
        {
            textureState[j] = new GlStateManager.TextureState();
        }

        activeShadeModel = 7425;
        rescaleNormalState = new GlStateManager.BooleanState(32826);
        colorMaskState = new GlStateManager.ColorMask();
        colorState = new GlStateManager.Color();
    }

    @SideOnly(Side.CLIENT)
    static class AlphaState
        {
            public GlStateManager.BooleanState alphaTest;
            public int func;
            public float ref;

            private AlphaState()
            {
                this.alphaTest = new GlStateManager.BooleanState(3008);
                this.func = 519;
                this.ref = -1.0F;
            }
        }

    @SideOnly(Side.CLIENT)
    static class BlendState
        {
            public GlStateManager.BooleanState blend;
            public int srcFactor;
            public int dstFactor;
            public int srcFactorAlpha;
            public int dstFactorAlpha;

            private BlendState()
            {
                this.blend = new GlStateManager.BooleanState(3042);
                this.srcFactor = 1;
                this.dstFactor = 0;
                this.srcFactorAlpha = 1;
                this.dstFactorAlpha = 0;
            }
        }

    @SideOnly(Side.CLIENT)
    static class BooleanState
        {
            private final int capability;
            private boolean currentState;

            public BooleanState(int capabilityIn)
            {
                this.capability = capabilityIn;
            }

            public void setDisabled()
            {
                this.setState(false);
            }

            public void setEnabled()
            {
                this.setState(true);
            }

            public void setState(boolean state)
            {
                if (state != this.currentState)
                {
                    this.currentState = state;

                    if (state)
                    {
                        GL11.glEnable(this.capability);
                    }
                    else
                    {
                        GL11.glDisable(this.capability);
                    }
                }
            }
        }

    @SideOnly(Side.CLIENT)
    static class ClearState
        {
            public double depth;
            public GlStateManager.Color color;

            private ClearState()
            {
                this.depth = 1.0D;
                this.color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
            }
        }

    @SideOnly(Side.CLIENT)
    static class Color
        {
            public float red;
            public float green;
            public float blue;
            public float alpha;

            public Color()
            {
                this(1.0F, 1.0F, 1.0F, 1.0F);
            }

            public Color(float redIn, float greenIn, float blueIn, float alphaIn)
            {
                this.red = 1.0F;
                this.green = 1.0F;
                this.blue = 1.0F;
                this.alpha = 1.0F;
                this.red = redIn;
                this.green = greenIn;
                this.blue = blueIn;
                this.alpha = alphaIn;
            }
        }

    @SideOnly(Side.CLIENT)
    static class ColorLogicState
        {
            public GlStateManager.BooleanState colorLogicOp;
            public int opcode;

            private ColorLogicState()
            {
                this.colorLogicOp = new GlStateManager.BooleanState(3058);
                this.opcode = 5379;
            }
        }

    @SideOnly(Side.CLIENT)
    static class ColorMask
        {
            public boolean red;
            public boolean green;
            public boolean blue;
            public boolean alpha;

            private ColorMask()
            {
                this.red = true;
                this.green = true;
                this.blue = true;
                this.alpha = true;
            }
        }

    @SideOnly(Side.CLIENT)
    static class ColorMaterialState
        {
            public GlStateManager.BooleanState colorMaterial;
            public int face;
            public int mode;

            private ColorMaterialState()
            {
                this.colorMaterial = new GlStateManager.BooleanState(2903);
                this.face = 1032;
                this.mode = 5634;
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum CullFace
    {
        FRONT(1028),
        BACK(1029),
        FRONT_AND_BACK(1032);

        public final int mode;

        private CullFace(int modeIn)
        {
            this.mode = modeIn;
        }
    }

    @SideOnly(Side.CLIENT)
    static class CullState
        {
            public GlStateManager.BooleanState cullFace;
            public int mode;

            private CullState()
            {
                this.cullFace = new GlStateManager.BooleanState(2884);
                this.mode = 1029;
            }
        }

    @SideOnly(Side.CLIENT)
    static class DepthState
        {
            public GlStateManager.BooleanState depthTest;
            public boolean maskEnabled;
            public int depthFunc;

            private DepthState()
            {
                this.depthTest = new GlStateManager.BooleanState(2929);
                this.maskEnabled = true;
                this.depthFunc = 513;
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum DestFactor
    {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        private DestFactor(int factorIn)
        {
            this.factor = factorIn;
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum FogMode
    {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        /** The capability ID of this {@link FogMode} */
        public final int capabilityId;

        private FogMode(int capabilityIn)
        {
            this.capabilityId = capabilityIn;
        }
    }

    @SideOnly(Side.CLIENT)
    static class FogState
        {
            public GlStateManager.BooleanState fog;
            public int mode;
            public float density;
            public float start;
            public float end;

            private FogState()
            {
                this.fog = new GlStateManager.BooleanState(2912);
                this.mode = 2048;
                this.density = 1.0F;
                this.end = 1.0F;
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum LogicOp
    {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int opcode;

        private LogicOp(int opcodeIn)
        {
            this.opcode = opcodeIn;
        }
    }

    @SideOnly(Side.CLIENT)
    static class PolygonOffsetState
        {
            public GlStateManager.BooleanState polygonOffsetFill;
            public GlStateManager.BooleanState polygonOffsetLine;
            public float factor;
            public float units;

            private PolygonOffsetState()
            {
                this.polygonOffsetFill = new GlStateManager.BooleanState(32823);
                this.polygonOffsetLine = new GlStateManager.BooleanState(10754);
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum Profile
    {
        DEFAULT {
            public void apply()
            {
                GlStateManager.disableAlpha();
                GlStateManager.alphaFunc(519, 0.0F);
                GlStateManager.disableLighting();
                GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, (FloatBuffer)RenderHelper.setColorBuffer(0.2F, 0.2F, 0.2F, 1.0F));

                for (int i = 0; i < 8; ++i)
                {
                    GlStateManager.disableLight(i);
                    GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_AMBIENT, (FloatBuffer)RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_POSITION, (FloatBuffer)RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));

                    if (i == 0)
                    {
                        GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_DIFFUSE, (FloatBuffer)RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                        GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_SPECULAR, (FloatBuffer)RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                    }
                    else
                    {
                        GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_DIFFUSE, (FloatBuffer)RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                        GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_SPECULAR, (FloatBuffer)RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    }
                }

                GlStateManager.disableColorMaterial();
                GlStateManager.colorMaterial(1032, 5634);
                GlStateManager.disableDepth();
                GlStateManager.depthFunc(513);
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GL14.glBlendEquation(GL14.GL_FUNC_ADD);
                GlStateManager.disableFog();
                GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
                GlStateManager.setFogDensity(1.0F);
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(1.0F);
                GL11.glFog(GL11.GL_FOG_COLOR, (FloatBuffer)RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));

                if (GLContext.getCapabilities().GL_NV_fog_distance)
                {
                    GL11.glFogi(GL11.GL_FOG_MODE, 34140);
                }

                GlStateManager.doPolygonOffset(0.0F, 0.0F);
                GlStateManager.disableColorLogic();
                GlStateManager.colorLogicOp(5379);
                GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
                GlStateManager.texGen(GlStateManager.TexGen.S, 9216);
                GlStateManager.texGen(GlStateManager.TexGen.S, 9474, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.texGen(GlStateManager.TexGen.S, 9217, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
                GlStateManager.texGen(GlStateManager.TexGen.T, 9216);
                GlStateManager.texGen(GlStateManager.TexGen.T, 9474, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GlStateManager.texGen(GlStateManager.TexGen.T, 9217, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
                GlStateManager.texGen(GlStateManager.TexGen.R, 9216);
                GlStateManager.texGen(GlStateManager.TexGen.R, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.texGen(GlStateManager.TexGen.R, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
                GlStateManager.texGen(GlStateManager.TexGen.Q, 9216);
                GlStateManager.texGen(GlStateManager.TexGen.Q, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.texGen(GlStateManager.TexGen.Q, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.setActiveTexture(0);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 1000);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, 1000);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, -1000);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, (FloatBuffer)RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL11.GL_MODULATE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_RGB, GL11.GL_TEXTURE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_RGB, GL13.GL_PREVIOUS);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC2_RGB, GL13.GL_CONSTANT);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_ALPHA, GL11.GL_TEXTURE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_ALPHA, GL13.GL_PREVIOUS);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC2_ALPHA, GL13.GL_CONSTANT);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_ALPHA, GL11.GL_SRC_ALPHA);
                GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL13.GL_RGB_SCALE, 1.0F);
                GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_ALPHA_SCALE, 1.0F);
                GlStateManager.disableNormalize();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableRescaleNormal();
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.clearDepth(1.0D);
                GL11.glLineWidth(1.0F);
                GL11.glNormal3f(0.0F, 0.0F, 1.0F);
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
                GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_FILL);
            }

            public void clean()
            {
            }
        },
        PLAYER_SKIN {
            public void apply()
            {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            }

            public void clean()
            {
                GlStateManager.disableBlend();
            }
        },
        TRANSPARENT_MODEL {
            public void apply()
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            public void clean()
            {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.depthMask(true);
            }
        };

        private Profile()
        {
        }

        public abstract void apply();

        public abstract void clean();
    }

    @SideOnly(Side.CLIENT)
    public static enum SourceFactor
    {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        private SourceFactor(int factorIn)
        {
            this.factor = factorIn;
        }
    }

    @SideOnly(Side.CLIENT)
    static class StencilFunc
        {
            public int func;
            public int mask;

            private StencilFunc()
            {
                this.func = 519;
                this.mask = -1;
            }
        }

    @SideOnly(Side.CLIENT)
    static class StencilState
        {
            public GlStateManager.StencilFunc func;
            public int mask;
            public int fail;
            public int zfail;
            public int zpass;

            private StencilState()
            {
                this.func = new GlStateManager.StencilFunc();
                this.mask = -1;
                this.fail = 7680;
                this.zfail = 7680;
                this.zpass = 7680;
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum TexGen
    {
        S,
        T,
        R,
        Q;
    }

    @SideOnly(Side.CLIENT)
    static class TexGenCoord
        {
            public GlStateManager.BooleanState textureGen;
            public int coord;
            public int param = -1;

            public TexGenCoord(int coordIn, int capabilityIn)
            {
                this.coord = coordIn;
                this.textureGen = new GlStateManager.BooleanState(capabilityIn);
            }
        }

    @SideOnly(Side.CLIENT)
    static class TexGenState
        {
            public GlStateManager.TexGenCoord s;
            public GlStateManager.TexGenCoord t;
            public GlStateManager.TexGenCoord r;
            public GlStateManager.TexGenCoord q;

            private TexGenState()
            {
                this.s = new GlStateManager.TexGenCoord(8192, 3168);
                this.t = new GlStateManager.TexGenCoord(8193, 3169);
                this.r = new GlStateManager.TexGenCoord(8194, 3170);
                this.q = new GlStateManager.TexGenCoord(8195, 3171);
            }
        }

    @SideOnly(Side.CLIENT)
    static class TextureState
        {
            public GlStateManager.BooleanState texture2DState;
            public int textureName;

            private TextureState()
            {
                this.texture2DState = new GlStateManager.BooleanState(3553);
            }
        }
}