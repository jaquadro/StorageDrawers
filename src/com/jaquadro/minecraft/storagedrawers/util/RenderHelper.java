package com.jaquadro.minecraft.storagedrawers.util;

public class RenderHelper
{
    /*public static final int YNEG = 0;
    public static final int YPOS = 1;
    public static final int ZNEG = 2;
    public static final int ZPOS = 3;
    public static final int XNEG = 4;
    public static final int XPOS = 5;

    private static final int TL = 0;
    private static final int BL = 1;
    private static final int BR = 2;
    private static final int TR = 3;

    private static final int xyzuvMap[][][] = {
        {       // Y-NEG
            { 0, 2, 5, 0, 3 },
            { 0, 2, 4, 0, 2 },
            { 1, 2, 4, 1, 2 },
            { 1, 2, 5, 1, 3 }
        }, {    // Y-POS
            { 1, 3, 5, 1, 3 },
            { 1, 3, 4, 1, 2 },
            { 0, 3, 4, 0, 2 },
            { 0, 3, 5, 0, 3 }
        }, {    // Z-NEG
            { 0, 3, 4, 1, 2 },
            { 1, 3, 4, 0, 2 },
            { 1, 2, 4, 0, 3 },
            { 0, 2, 4, 1, 3 }
        }, {    // Z-POS
            { 0, 3, 5, 0, 2 },
            { 0, 2, 5, 0, 3 },
            { 1, 2, 5, 1, 3 },
            { 1, 3, 5, 1, 2 }
        }, {    // X-NEG
            { 0, 3, 5, 1, 2 },
            { 0, 3, 4, 0, 2 },
            { 0, 2, 4, 0, 3 },
            { 0, 2, 5, 1, 3 }
        }, {    // X-POS
            { 1, 2, 5, 0, 3 },
            { 1, 2, 4, 1, 3 },
            { 1, 3, 4, 1, 2 },
            { 1, 3, 5, 0, 2 }
        },
    };

    private static final float rgbMap[][] = {
        { 0.5f, 0.5f, 0.5f },
        { 1.0f, 1.0f, 1.0f },
        { 0.8f, 0.8f, 0.8f },
        { 0.8f, 0.8f, 0.8f },
        { 0.6f, 0.6f, 0.6f },
        { 0.6f, 0.6f, 0.6f },
    };

    private static final float normMap[][] = {
        { 0, -1, 0 },
        { 0, 1, 0 },
        { 0, 0, -1 },
        { 0, 0, 1 },
        { -1, 0, 0 },
        { 1, 0, 0 },
    };

    private RenderHelperAO aoHelper = new RenderHelperAO();

    // u-min, u-max, v-min, v-max
    private double[] uv = new double[4];

    // x-min, x-max, y-min, y-max, z-min, z-max
    private double[] xyz = new double[6];

    private float[] colorScratch = new float[3];

    private void setUV (IIcon icon, double uMin, double vMin, double uMax, double vMax) {
        uv[0] = uMin + icon.getMinU();
        uv[1] = uMax + icon.getMaxU();
        uv[2] = vMin + icon.getMinV();
        uv[3] = vMax + icon.getMaxV();
    }

    private void setXYZ (RenderBlocks renderer, double x, double y, double z) {
        xyz[0] = x + renderer.renderMinX;
        xyz[1] = x + renderer.renderMaxX;
        xyz[2] = y + renderer.renderMinY;
        xyz[3] = y + renderer.renderMaxY;
        xyz[4] = z + renderer.renderMinZ;
        xyz[5] = z + renderer.renderMaxZ;
    }

    private void renderXYZUV (RenderBlocks renderer, int[][] index) {
        Tessellator tessellator = Tessellator.instance;

        int[] tl = index[TL];
        int[] bl = index[BL];
        int[] br = index[BR];
        int[] tr = index[TR];

        tessellator.addVertexWithUV(xyz[tl[0]], xyz[tl[1]], xyz[tl[2]], uv[tl[3]], uv[tl[4]]);
        tessellator.addVertexWithUV(xyz[bl[0]], xyz[bl[1]], xyz[bl[2]], uv[bl[3]], uv[bl[4]]);
        tessellator.addVertexWithUV(xyz[br[0]], xyz[br[1]], xyz[br[2]], uv[br[3]], uv[br[4]]);
        tessellator.addVertexWithUV(xyz[tr[0]], xyz[tr[1]], xyz[tr[2]], uv[tr[3]], uv[tr[4]]);
    }

    private void renderXYZUVAO (RenderBlocks renderer, int[][] index) {
        Tessellator tessellator = Tessellator.instance;

        int[] tl = index[TL];
        int[] bl = index[BL];
        int[] br = index[BR];
        int[] tr = index[TR];

        tessellator.setColorOpaque_F(renderer.colorRedTopLeft, renderer.colorGreenTopLeft, renderer.colorBlueTopLeft);
        tessellator.setBrightness(renderer.brightnessTopLeft);
        tessellator.addVertexWithUV(xyz[tl[0]], xyz[tl[1]], xyz[tl[2]], uv[tl[3]], uv[tl[4]]);

        tessellator.setColorOpaque_F(renderer.colorRedBottomLeft, renderer.colorGreenBottomLeft, renderer.colorBlueBottomLeft);
        tessellator.setBrightness(renderer.brightnessBottomLeft);
        tessellator.addVertexWithUV(xyz[bl[0]], xyz[bl[1]], xyz[bl[2]], uv[bl[3]], uv[bl[4]]);

        tessellator.setColorOpaque_F(renderer.colorRedBottomRight, renderer.colorGreenBottomRight, renderer.colorBlueBottomRight);
        tessellator.setBrightness(renderer.brightnessBottomRight);
        tessellator.addVertexWithUV(xyz[br[0]], xyz[br[1]], xyz[br[2]], uv[br[3]], uv[br[4]]);

        tessellator.setColorOpaque_F(renderer.colorRedTopRight, renderer.colorGreenTopRight, renderer.colorBlueTopRight);
        tessellator.setBrightness(renderer.brightnessTopRight);
        tessellator.addVertexWithUV(xyz[tr[0]], xyz[tr[1]], xyz[tr[2]], uv[tr[3]], uv[tr[4]]);
    }

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

    public static void renderEmptyPlane (Block block, int x, int y, int z, RenderBlocks renderer) {
        renderer.setRenderBounds(0, 0, 0, 0, 0, 0);
        renderer.renderFaceYNeg(block, x, y, z, Blocks.dirt.getIcon(0, 0));
    }

    public void renderBlock (RenderBlocks renderer, Block block, int x, int y, int z) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        float r = colorScratch[0];
        float g = colorScratch[1];
        float b = colorScratch[2];

        renderFace(YNEG, renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 0), r, g, b);
        renderFace(YPOS, renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 1), r, g, b);
        renderFace(ZNEG, renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 2), r, g, b);
        renderFace(ZPOS, renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 3), r, g, b);
        renderFace(XNEG, renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 4), r, g, b);
        renderFace(XPOS, renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 5), r, g, b);
    }

    public void renderFace (int face, RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        renderFace(face, renderer, block, x, y, z, icon, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public void renderFace (int face, RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && renderer.blockAccess != null && block.getLightValue(renderer.blockAccess, x, y, z) == 0)
            renderFaceAOPartial(face, renderer, block, x, y, z, icon, r, g, b);
        else
            renderFaceColorMult(face, renderer, block, x, y, z, icon, r, g, b);
    }

    public void renderFaceColorMult (int face, RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        setupColorMult(face, renderer, block, x, y, z, r, g, b);

        switch (face) {
            case YNEG: renderer.renderFaceYNeg(block, x, y, z, icon); break;
            case YPOS: renderer.renderFaceYPos(block, x, y, z, icon); break;
            case ZNEG: renderer.renderFaceZNeg(block, x, y, z, icon); break;
            case ZPOS: renderer.renderFaceZPos(block, x, y, z, icon); break;
            case XNEG: renderer.renderFaceXNeg(block, x, y, z, icon); break;
            case XPOS: renderer.renderFaceXPos(block, x, y, z, icon); break;
        }

        if (renderer.blockAccess == null)
            Tessellator.instance.draw();
    }

    public void renderFaceAOPartial (int face, RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        renderer.enableAO = true;

        switch (face) {
            case YNEG:
                aoHelper.setupYNegAOPartial(renderer, block, x, y, z, r, g, b);
                renderer.renderFaceYNeg(block, x, y, z, icon);
                break;
            case YPOS:
                aoHelper.setupYPosAOPartial(renderer, block, x, y, z, r, g, b);
                renderer.renderFaceYPos(block, x, y, z, icon);
                break;
            case ZNEG:
                aoHelper.setupZNegAOPartial(renderer, block, x, y, z, r, g, b);
                renderer.renderFaceZNeg(block, x, y, z, icon);
                break;
            case ZPOS:
                aoHelper.setupZPosAOPartial(renderer, block, x, y, z, r, g, b);
                renderer.renderFaceZPos(block, x, y, z, icon);
                break;
            case XNEG:
                aoHelper.setupXNegAOPartial(renderer, block, x, y, z, r, g, b);
                renderer.renderFaceXNeg(block, x, y, z, icon);
                break;
            case XPOS:
                aoHelper.setupXPosAOPartial(renderer, block, x, y, z, r, g, b);
                renderer.renderFaceXPos(block, x, y, z, icon);
                break;
        }

        renderer.enableAO = false;
    }

    public void renderPartialFace (int face, RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, double uMin, double vMin, double uMax, double vMax) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        renderPartialFace(face, renderer, block, x, y, z, icon, uMin, vMin, uMax, vMax, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public void renderPartialFace (int face, RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, double uMin, double vMin, double uMax, double vMax, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && renderer.blockAccess != null && block.getLightValue(renderer.blockAccess, x, y, z) == 0)
            renderPartialFaceAOPartial(face, renderer, block, x, y, z, icon, uMin, vMin, uMax, vMax, r, g, b);
        else
            renderPartialFaceColorMult(face, renderer, block, x, y, z, icon, uMin, vMin, uMax, vMax, r, g, b);
    }

    public void renderPartialFaceColorMult (int face, RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, double uMin, double vMin, double uMax, double vMax, float r, float g, float b) {
        setupColorMult(face, renderer, block, x, y, z, r, g, b);
        renderPartialFace(face, renderer, x, y, z, icon, uMin, vMin, uMax, vMax);

        if (renderer.blockAccess == null)
            Tessellator.instance.draw();
    }

    public void renderPartialFaceAOPartial (int face, RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, double uMin, double vMin, double uMax, double vMax, float r, float g, float b) {
        renderer.enableAO = true;

        switch (face) {
            case YNEG:
                aoHelper.setupYNegAOPartial(renderer, block, x, y, z, r, g, b);
                break;
            case YPOS:
                aoHelper.setupYPosAOPartial(renderer, block, x, y, z, r, g, b);
                break;
            case ZNEG:
                aoHelper.setupZNegAOPartial(renderer, block, x, y, z, r, g, b);
                break;
            case ZPOS:
                aoHelper.setupZPosAOPartial(renderer, block, x, y, z, r, g, b);
                break;
            case XNEG:
                aoHelper.setupXNegAOPartial(renderer, block, x, y, z, r, g, b);
                break;
            case XPOS:
                aoHelper.setupXPosAOPartial(renderer, block, x, y, z, r, g, b);
                break;
        }

        renderPartialFace(face, renderer, x, y, z, icon, uMin, vMin, uMax, vMax);
        renderer.enableAO = false;
    }

    public void renderPartialFace (int face, RenderBlocks renderer, double x, double y, double z, IIcon icon, double uMin, double vMin, double uMax, double vMax) {
        if (renderer.hasOverrideBlockTexture())
            icon = renderer.overrideBlockTexture;

        setXYZ(renderer, x, y, z);
        setUV(icon, uMin, vMin, uMax, vMax);

        if (renderer.enableAO)
            renderXYZUVAO(renderer, xyzuvMap[face]);
        else
            renderXYZUV(renderer, xyzuvMap[face]);
    }

    private void setupColorMult (int face, RenderBlocks renderer, Block block, int x, int y, int z, float r, float g, float b) {
        Tessellator tessellator = Tessellator.instance;
        float[] rgb = rgbMap[face];
        float[] norm = normMap[face];

        tessellator.setColorOpaque_F(rgb[0] * r, rgb[1] * g, rgb[2] * b);
        if (renderer.blockAccess == null) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(norm[0], norm[1], norm[2]);
        }
        else {
            int brightX = x;
            int brightY = y;
            int brightZ = z;

            switch (face) {
                case YNEG: brightY = (renderer.renderMinY > 0) ? y : y - 1; break;
                case YPOS: brightY = (renderer.renderMaxY < 1) ? y : y + 1; break;
                case ZNEG: brightZ = (renderer.renderMinZ > 0) ? z : z - 1; break;
                case ZPOS: brightZ = (renderer.renderMaxZ < 1) ? z : z + 1; break;
                case XNEG: brightX = (renderer.renderMinX > 0) ? x : x - 1; break;
                case XPOS: brightX = (renderer.renderMaxX < 1) ? x : x + 1; break;
            }

            tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, brightX, brightY, brightZ));
        }

        renderer.enableAO = false;
    }*/

    /*    uMin += icon.getMinU();
        uMax += icon.getMaxU();
        vMin += icon.getMinV();
        vMax += icon.getMaxV();

        double xMin = x + renderer.renderMinX;
        double xMax = x + renderer.renderMaxX;
        double yMin = y + renderer.renderMinY;
        double yMax = y + renderer.renderMaxY;
        double zMin = z + renderer.renderMinZ;

        if (renderer.enableAO) {
            tessellator.setColorOpaque_F(renderer.colorRedTopLeft, renderer.colorGreenTopLeft, renderer.colorBlueTopLeft);
            tessellator.setBrightness(renderer.brightnessTopLeft);
            tessellator.addVertexWithUV(xMin, yMax, zMin, uMax, vMin);

            tessellator.setColorOpaque_F(renderer.colorRedBottomLeft, renderer.colorGreenBottomLeft, renderer.colorBlueBottomLeft);
            tessellator.setBrightness(renderer.brightnessBottomLeft);
            tessellator.addVertexWithUV(xMax, yMax, zMin, uMin, vMin);

            tessellator.setColorOpaque_F(renderer.colorRedBottomRight, renderer.colorGreenBottomRight, renderer.colorBlueBottomRight);
            tessellator.setBrightness(renderer.brightnessBottomRight);
            tessellator.addVertexWithUV(xMax, yMin, zMin, uMin, vMax);

            tessellator.setColorOpaque_F(renderer.colorRedTopRight, renderer.colorGreenTopRight, renderer.colorBlueTopRight);
            tessellator.setBrightness(renderer.brightnessTopRight);
            tessellator.addVertexWithUV(xMin, yMin, zMin, uMax, vMax);
        }
        else {
            tessellator.addVertexWithUV(xMin, yMax, zMin, uMax, vMin);
            tessellator.addVertexWithUV(xMax, yMax, zMin, uMin, vMin);
            tessellator.addVertexWithUV(xMax, yMin, zMin, uMin, vMax);
            tessellator.addVertexWithUV(xMin, yMin, zMin, uMax, vMax);
        }
    }*/

    /*public void renderPartialFaceZPos (RenderBlocks renderer, double x, double y, double z, IIcon icon, double uMin, double vMin, double uMax, double vMax) {
        Tessellator tessellator = Tessellator.instance;
        if (renderer.hasOverrideBlockTexture())
            icon = renderer.overrideBlockTexture;

        uMin += icon.getMinU();
        uMax += icon.getMaxU();
        vMin += icon.getMinV();
        vMax += icon.getMaxV();

        double xMin = x + renderer.renderMinX;
        double xMax = x + renderer.renderMaxX;
        double yMin = y + renderer.renderMinY;
        double yMax = y + renderer.renderMaxY;
        double zMax = z + renderer.renderMaxZ;

        if (renderer.enableAO) {
            tessellator.setColorOpaque_F(renderer.colorRedTopLeft, renderer.colorGreenTopLeft, renderer.colorBlueTopLeft);
            tessellator.setBrightness(renderer.brightnessTopLeft);
            tessellator.addVertexWithUV(xMin, yMax, zMax, uMin, vMin);

            tessellator.setColorOpaque_F(renderer.colorRedBottomLeft, renderer.colorGreenBottomLeft, renderer.colorBlueBottomLeft);
            tessellator.setBrightness(renderer.brightnessBottomLeft);
            tessellator.addVertexWithUV(xMin, yMin, zMax, uMin, vMax);

            tessellator.setColorOpaque_F(renderer.colorRedBottomRight, renderer.colorGreenBottomRight, renderer.colorBlueBottomRight);
            tessellator.setBrightness(renderer.brightnessBottomRight);
            tessellator.addVertexWithUV(xMax, yMin, zMax, uMax, vMax);

            tessellator.setColorOpaque_F(renderer.colorRedTopRight, renderer.colorGreenTopRight, renderer.colorBlueTopRight);
            tessellator.setBrightness(renderer.brightnessTopRight);
            tessellator.addVertexWithUV(xMax, yMax, zMax, uMax, vMin);
        }
        else {
            tessellator.addVertexWithUV(xMin, yMax, zMax, uMin, vMin);
            tessellator.addVertexWithUV(xMin, yMin, zMax, uMin, vMax);
            tessellator.addVertexWithUV(xMax, yMin, zMax, uMax, vMax);
            tessellator.addVertexWithUV(xMax, yMax, zMax, uMax, vMin);
        }
    }*/
}
