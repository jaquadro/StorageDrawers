package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderHelper
{
    public static final int YNEG = 0;
    public static final int YPOS = 1;
    public static final int ZNEG = 2;
    public static final int ZPOS = 3;
    public static final int XNEG = 4;
    public static final int XPOS = 5;

    public static final int FULL_BRIGHTNESS = 15728880;

    private static final float normMap[][] = {
        { 0, -1, 0 },
        { 0, 1, 0 },
        { 0, 0, -1 },
        { 0, 0, 1 },
        { -1, 0, 0 },
        { 1, 0, 0 },
    };

    public RenderHelperState state = new RenderHelperState();

    private RenderHelperAO aoHelper = new RenderHelperAO(state);
    private RenderHelperLL llHelper = new RenderHelperLL(state);

    private float[] colorScratch = new float[3];

    public static RenderHelper instance = new RenderHelper();

    public static void calculateBaseColor (float[] target, int color) {
        float r = (float)(color >> 16 & 255) / 255f;
        float g = (float)(color >> 8 & 255) / 255f;
        float b = (float)(color & 255) / 255f;

        if (EntityRenderer.anaglyphEnable) {
            float gray = (r * 30f + g * 59f + b * 11f) / 100f;
            float rg = (r * 30f + g * 70f) / 100f;
            float rb = (r * 30f + b * 70f) / 100f;

            r = gray;
            g = rg;
            b = rb;
        }

        target[0] = r;
        target[1] = g;
        target[2] = b;
    }

    public static void scaleColor (float[] target, float[] source, float scale) {
        target[0] = source[0] * scale;
        target[1] = source[1] * scale;
        target[2] = source[2] * scale;
    }

    public static void setTessellatorColor (Tessellator tessellator, float[] color) {
        tessellator.setColorOpaque_F(color[0], color[1], color[2]);
    }

    public void renderEmptyPlane (int x, int y, int z) {
        state.setRenderBounds(0, 0, 0, 0, 0, 0);
        llHelper.drawFace(RenderHelper.YNEG, x, y, z, Blocks.dirt.getIcon(0, 0));
    }

    public void setRenderBounds (double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        state.setRenderBounds(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public void setRenderBounds (Block block) {
        setRenderBounds(block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(), block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ());
    }

    public void setColorAndBrightness (IBlockAccess blockAccess, Block block, int x, int y, int z) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));

        calculateBaseColor(colorScratch, block.colorMultiplier(blockAccess, x, y, z));
        setTessellatorColor(tessellator, colorScratch);
    }

    public void renderBlock (IBlockAccess blockAccess, Block block, int meta) {
        calculateBaseColor(colorScratch, block.getRenderColor(meta));
        float r = colorScratch[0];
        float g = colorScratch[1];
        float b = colorScratch[2];

        renderFace(YNEG, blockAccess, block, block.getIcon(0, meta), r, g, b);
        renderFace(YPOS, blockAccess, block, block.getIcon(1, meta), r, g, b);
        renderFace(ZNEG, blockAccess, block, block.getIcon(2, meta), r, g, b);
        renderFace(ZPOS, blockAccess, block, block.getIcon(3, meta), r, g, b);
        renderFace(XNEG, blockAccess, block, block.getIcon(4, meta), r, g, b);
        renderFace(XPOS, blockAccess, block, block.getIcon(5, meta), r, g, b);
    }

    public void renderBlock (IBlockAccess blockAccess, Block block, int x, int y, int z) {
        calculateBaseColor(colorScratch, block.colorMultiplier(blockAccess, x, y, z));
        float r = colorScratch[0];
        float g = colorScratch[1];
        float b = colorScratch[2];

        renderFace(YNEG, blockAccess, block, x, y, z, block.getIcon(blockAccess, x, y, z, 0), r, g, b);
        renderFace(YPOS, blockAccess, block, x, y, z, block.getIcon(blockAccess, x, y, z, 1), r, g, b);
        renderFace(ZNEG, blockAccess, block, x, y, z, block.getIcon(blockAccess, x, y, z, 2), r, g, b);
        renderFace(ZPOS, blockAccess, block, x, y, z, block.getIcon(blockAccess, x, y, z, 3), r, g, b);
        renderFace(XNEG, blockAccess, block, x, y, z, block.getIcon(blockAccess, x, y, z, 4), r, g, b);
        renderFace(XPOS, blockAccess, block, x, y, z, block.getIcon(blockAccess, x, y, z, 5), r, g, b);
    }

    public void renderFace (int face, IBlockAccess blockAccess, Block block, IIcon icon, int meta) {
        calculateBaseColor(colorScratch, block.getRenderColor(meta));
        renderFaceColorMult(face, blockAccess, block, 0, 0, 0, icon, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public void renderFace (int face, IBlockAccess blockAccess, Block block, IIcon icon, float r, float g, float b) {
        renderFaceColorMult(face, blockAccess, block, 0, 0, 0, icon, r, g, b);
    }

    public void renderFace (int face, IBlockAccess blockAccess, Block block, int x, int y, int z, IIcon icon) {
        calculateBaseColor(colorScratch, block.colorMultiplier(blockAccess, x, y, z));
        renderFace(face, blockAccess, block, x, y, z, icon, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public void renderFace (int face, IBlockAccess blockAccess, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && blockAccess != null && block.getLightValue(blockAccess, x, y, z) == 0)
            renderFaceAOPartial(face, blockAccess, block, x, y, z, icon, r, g, b);
        else
            renderFaceColorMult(face, blockAccess, block, x, y, z, icon, r, g, b);
    }

    public void renderFaceColorMult (int face, IBlockAccess blockAccess, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        setupColorMult(face, blockAccess, block, x, y, z, r, g, b);

        face = RenderHelperState.FACE_BY_FACE_ROTATION[face][state.rotateTransform];
        llHelper.drawFace(face, x, y, z, icon);

        if (blockAccess == null)
            Tessellator.instance.draw();
    }

    public void renderFaceAOPartial (int face, IBlockAccess blockAccess, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        state.enableAO = true;

        face = RenderHelperState.FACE_BY_FACE_ROTATION[face][state.rotateTransform];

        switch (face) {
            case YNEG:
                aoHelper.setupYNegAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case YPOS:
                aoHelper.setupYPosAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case ZNEG:
                aoHelper.setupZNegAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case ZPOS:
                aoHelper.setupZPosAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case XNEG:
                aoHelper.setupXNegAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case XPOS:
                aoHelper.setupXPosAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
        }

        llHelper.drawFace(face, x, y, z, icon);

        state.enableAO = false;
    }

    public void renderPartialFace (int face, IBlockAccess blockAccess, Block block, int x, int y, int z, IIcon icon, double uMin, double vMin, double uMax, double vMax) {
        calculateBaseColor(colorScratch, block.colorMultiplier(blockAccess, x, y, z));
        renderPartialFace(face, blockAccess, block, x, y, z, icon, uMin, vMin, uMax, vMax, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public void renderPartialFace (int face, IBlockAccess blockAccess, Block block, int x, int y, int z, IIcon icon, double uMin, double vMin, double uMax, double vMax, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && blockAccess != null && block.getLightValue(blockAccess, x, y, z) == 0)
            renderPartialFaceAOPartial(face, blockAccess, block, x, y, z, icon, uMin, vMin, uMax, vMax, r, g, b);
        else
            renderPartialFaceColorMult(face, blockAccess, block, x, y, z, icon, uMin, vMin, uMax, vMax, r, g, b);
    }

    public void renderPartialFaceColorMult (int face, IIcon icon, double uMin, double vMin, double uMax, double vMax, float r, float g, float b) {
        setupColorMult(face, r, g, b);
        renderPartialFace(face, icon, uMin, vMin, uMax, vMax);

        Tessellator.instance.draw();
    }

    public void renderPartialFaceColorMult (int face, IBlockAccess blockAccess, Block block, int x, int y, int z, IIcon icon, double uMin, double vMin, double uMax, double vMax, float r, float g, float b) {
        setupColorMult(face, blockAccess, block, x, y, z, r, g, b);
        renderPartialFace(face, x, y, z, icon, uMin, vMin, uMax, vMax);

        if (blockAccess == null)
            Tessellator.instance.draw();
    }

    public void renderPartialFaceAOPartial (int face, IBlockAccess blockAccess, Block block, int x, int y, int z, IIcon icon, double uMin, double vMin, double uMax, double vMax, float r, float g, float b) {
        state.enableAO = true;

        switch (RenderHelperState.FACE_BY_FACE_ROTATION[face][state.rotateTransform]) {
            case YNEG:
                aoHelper.setupYNegAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case YPOS:
                aoHelper.setupYPosAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case ZNEG:
                aoHelper.setupZNegAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case ZPOS:
                aoHelper.setupZPosAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case XNEG:
                aoHelper.setupXNegAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
            case XPOS:
                aoHelper.setupXPosAOPartial(blockAccess, block, x, y, z, r, g, b);
                break;
        }

        renderPartialFace(face, x, y, z, icon, uMin, vMin, uMax, vMax);
        state.enableAO = false;
    }

    public void renderPartialFace (int face, IIcon icon, double uMin, double vMin, double uMax, double vMax) {
        state.enableAO = false;
        face = RenderHelperState.FACE_BY_FACE_ROTATION[face][state.rotateTransform];
        llHelper.drawPartialFace(face, 0, 0, 0, icon, uMin, vMin, uMax, vMax);
    }

    public void renderPartialFace (int face, double x, double y, double z, IIcon icon, double uMin, double vMin, double uMax, double vMax) {
        face = RenderHelperState.FACE_BY_FACE_ROTATION[face][state.rotateTransform];
        llHelper.drawPartialFace(face, x, y, z, icon, uMin, vMin, uMax, vMax);
    }

    public void renderCrossedSquares (Block block, int meta) {
        renderCrossedSquares(block, meta, getBlockIconFromSideAndMetadata(block, 0, meta));
    }

    public void renderCrossedSquares (Block block, int meta, IIcon icon) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(FULL_BRIGHTNESS);

        calculateBaseColor(colorScratch, block.getRenderColor(meta));
        setTessellatorColor(tessellator, colorScratch);

        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHTING);

        tessellator.startDrawingQuads();

        drawCrossedSquares(icon, 0, 0, 0, 1.0F);

        tessellator.draw();

        if (lighting)
            GL11.glEnable(GL11.GL_LIGHTING);
    }

    public void renderCrossedSquares (IBlockAccess blockAccess, Block block, int x, int y, int z) {
        renderCrossedSquares(blockAccess, block, x, y, z, getBlockIconFromSideAndMetadata(block, 0, blockAccess.getBlockMetadata(x, y, z)));
    }

    public void renderCrossedSquares (IBlockAccess blockAccess, Block block, int x, int y, int z, IIcon icon) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));

        calculateBaseColor(colorScratch, block.colorMultiplier(blockAccess, x, y, z));
        setTessellatorColor(tessellator, colorScratch);

        drawCrossedSquares(icon, x, y, z, 1.0F);
    }

    public void drawCrossedSquares(IIcon icon, double x, double y, double z, float scale)
    {
        Tessellator tessellator = Tessellator.instance;

        x += state.renderOffsetX;
        y += state.renderOffsetY;
        z += state.renderOffsetZ;

        double uMin = icon.getInterpolatedU(state.renderMinX * 16.0D);
        double uMax = icon.getInterpolatedU(state.renderMaxX * 16.0D);
        double vMin = icon.getInterpolatedV(16 - state.renderMaxY * 16.0D);
        double vMax = icon.getInterpolatedV(16 - state.renderMinY * 16.0D);

        double d7 = 0.45D * (double)scale;
        double xMin = x + 0.5D - d7;
        double xMax = x + 0.5D + d7;
        double yMin = y + state.renderMinY * scale;
        double yMax = y + state.renderMaxY * scale;
        double zMin = z + 0.5D - d7;
        double zMax = z + 0.5D + d7;

        tessellator.addVertexWithUV(xMin, yMax, zMin, uMin, vMin);
        tessellator.addVertexWithUV(xMin, yMin, zMin, uMin, vMax);
        tessellator.addVertexWithUV(xMax, yMin, zMax, uMax, vMax);
        tessellator.addVertexWithUV(xMax, yMax, zMax, uMax, vMin);
        tessellator.addVertexWithUV(xMax, yMax, zMax, uMin, vMin);
        tessellator.addVertexWithUV(xMax, yMin, zMax, uMin, vMax);
        tessellator.addVertexWithUV(xMin, yMin, zMin, uMax, vMax);
        tessellator.addVertexWithUV(xMin, yMax, zMin, uMax, vMin);

        tessellator.addVertexWithUV(xMin, yMax, zMax, uMin, vMin);
        tessellator.addVertexWithUV(xMin, yMin, zMax, uMin, vMax);
        tessellator.addVertexWithUV(xMax, yMin, zMin, uMax, vMax);
        tessellator.addVertexWithUV(xMax, yMax, zMin, uMax, vMin);
        tessellator.addVertexWithUV(xMax, yMax, zMin, uMin, vMin);
        tessellator.addVertexWithUV(xMax, yMin, zMin, uMin, vMax);
        tessellator.addVertexWithUV(xMin, yMin, zMax, uMax, vMax);
        tessellator.addVertexWithUV(xMin, yMax, zMax, uMax, vMin);
    }

    public void drawCrossedSquaresBounded(IIcon icon, double x, double y, double z, float scale)
    {
        Tessellator tessellator = Tessellator.instance;

        x += state.renderOffsetX;
        y += state.renderOffsetY;
        z += state.renderOffsetZ;

        double vMin = icon.getInterpolatedV(16 - state.renderMaxY * 16.0D);
        double vMax = icon.getInterpolatedV(16 - state.renderMinY * 16.0D);

        double xzNN = Math.max(state.renderMinX, state.renderMinZ);
        double xzPP = Math.min(state.renderMaxX, state.renderMaxZ);

        double xNN = x + .5 - (.5 - xzNN) * 0.9;
        double zNN = z + .5 - (.5 - xzNN) * 0.9;
        double xNP = x + .5 - (.5 - Math.max(state.renderMinX, 1 - state.renderMaxZ)) * 0.9;
        double zNP = z + .5 - (.5 - Math.min(1 - state.renderMinX, state.renderMaxZ)) * 0.9;
        double xPN = x + .5 - (.5 - Math.min(state.renderMaxX, 1 - state.renderMinZ)) * 0.9;
        double zPN = z + .5 - (.5 - Math.max(1 - state.renderMaxX, state.renderMinZ)) * 0.9;
        double xPP = x + .5 - (.5 - xzPP) * 0.9;
        double zPP = z + .5 - (.5 - xzPP) * 0.9;

        double yMin = y + state.renderMinY * scale;
        double yMax = y + state.renderMaxY * scale;

        double uNN = icon.getInterpolatedU(xzNN * 16.0D);
        double uPP = icon.getInterpolatedU(xzPP * 16.0D);

        tessellator.addVertexWithUV(xNN, yMax, zNN, uNN, vMin);
        tessellator.addVertexWithUV(xNN, yMin, zNN, uNN, vMax);
        tessellator.addVertexWithUV(xPP, yMin, zPP, uPP, vMax);
        tessellator.addVertexWithUV(xPP, yMax, zPP, uPP, vMin);

        uNN = icon.getInterpolatedU(16 - xzNN * 16.0D);
        uPP = icon.getInterpolatedU(16 - xzPP * 16.0D);

        tessellator.addVertexWithUV(xPP, yMax, zPP, uPP, vMin);
        tessellator.addVertexWithUV(xPP, yMin, zPP, uPP, vMax);
        tessellator.addVertexWithUV(xNN, yMin, zNN, uNN, vMax);
        tessellator.addVertexWithUV(xNN, yMax, zNN, uNN, vMin);

        double uNP = icon.getInterpolatedU(Math.max(state.renderMinX, 1 - state.renderMaxZ) * 16.0D);
        double uPN = icon.getInterpolatedU(Math.min(state.renderMaxX, 1 - state.renderMinZ) * 16.0D);

        tessellator.addVertexWithUV(xNP, yMax, zNP, uNP, vMin);
        tessellator.addVertexWithUV(xNP, yMin, zNP, uNP, vMax);
        tessellator.addVertexWithUV(xPN, yMin, zPN, uPN, vMax);
        tessellator.addVertexWithUV(xPN, yMax, zPN, uPN, vMin);

        uNP = icon.getInterpolatedU(16 - Math.max(state.renderMinX, 1 - state.renderMaxZ) * 16.0D);
        uPN = icon.getInterpolatedU(16 - Math.min(state.renderMaxX, 1 - state.renderMinZ) * 16.0D);

        tessellator.addVertexWithUV(xPN, yMax, zPN, uPN, vMin);
        tessellator.addVertexWithUV(xPN, yMin, zPN, uPN, vMax);
        tessellator.addVertexWithUV(xNP, yMin, zNP, uNP, vMax);
        tessellator.addVertexWithUV(xNP, yMax, zNP, uNP, vMin);
    }

    private void setupColorMult (int face, float r, float g, float b) {
        Tessellator tessellator = Tessellator.instance;
        float[] norm = normMap[face];
        float scale = state.getColorMult(face);

        tessellator.setColorOpaque_F(scale * r, scale * g, scale * b);
        tessellator.startDrawingQuads();
        tessellator.setNormal(norm[0], norm[1], norm[2]);

        state.enableAO = false;
    }

    private void setupColorMult (int face, IBlockAccess blockAccess, Block block, int x, int y, int z, float r, float g, float b) {
        Tessellator tessellator = Tessellator.instance;
        float[] norm = normMap[face];
        float scale = state.getColorMult(face);

        if (blockAccess == null) {
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_F(r, g, b);
            tessellator.setNormal(norm[0], norm[1], norm[2]);
        }
        else {
            int brightX = x;
            int brightY = y;
            int brightZ = z;

            switch (face) {
                case YNEG: brightY = (state.renderMinY > 0) ? y : y - 1; break;
                case YPOS: brightY = (state.renderMaxY < 1) ? y : y + 1; break;
                case ZNEG: brightZ = (state.renderMinZ > 0) ? z : z - 1; break;
                case ZPOS: brightZ = (state.renderMaxZ < 1) ? z : z + 1; break;
                case XNEG: brightX = (state.renderMinX > 0) ? x : x - 1; break;
                case XPOS: brightX = (state.renderMaxX < 1) ? x : x + 1; break;
            }

            tessellator.setColorOpaque_F(scale * r, scale * g, scale * b);
            tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, brightX, brightY, brightZ));
        }

        state.enableAO = false;
    }

    private IIcon getBlockIconFromSideAndMetadata (Block block, int side, int meta) {
        return getIconSafe(block.getIcon(side, meta));
    }

    private IIcon getIconSafe (IIcon icon) {
        if (icon == null)
            return ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");

        return icon;
    }
}
