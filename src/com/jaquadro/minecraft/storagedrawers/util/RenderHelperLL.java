package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

public class RenderHelperLL
{
    private static final int TL = 0;
    private static final int BL = 1;
    private static final int BR = 2;
    private static final int TR = 3;

    private static final int MINX = 0;
    private static final int MAXX = 1;
    private static final int MINY = 2;
    private static final int MAXY = 3;
    private static final int MINZ = 4;
    private static final int MAXZ = 5;

    private static final int xyzuvMap[][][] = {
        {       // Y-NEG
            { 0, 2, 5, 0, 2 },
            { 0, 2, 4, 0, 3 },
            { 1, 2, 4, 1, 3 },
            { 1, 2, 5, 1, 2 }
        }, {    // Y-POS
            { 1, 3, 5, 0, 2 },
            { 1, 3, 4, 0, 3 },
            { 0, 3, 4, 1, 3 },
            { 0, 3, 5, 1, 2 }
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

    private RenderHelperState state;

    private double[] minUDiv = new double[24];
    private double[] maxUDiv = new double[24];
    private double[] minVDiv = new double[24];
    private double[] maxVDiv = new double[24];

    private int[][] brightnessLerp = new int[10][10];

    // u-min, u-max, v-min, v-max
    private double[] uv = new double[4];

    // x-min, x-max, y-min, y-max, z-min, z-max
    private double[] xyz = new double[6];

    public RenderHelperLL (RenderHelperState state) {
        this.state = state;
    }

    public void drawFace (int face, double x, double y, double z, IIcon icon) {
        boolean flip = state.flipTexture;

        switch (face) {
            case RenderHelper.YNEG:
            case RenderHelper.YPOS:
                drawFaceY(face, x, y, z, icon);
                break;
            case RenderHelper.ZNEG:
            case RenderHelper.ZPOS:
                if (state.rotateTransform == RenderHelperState.ROTATE180 || state.rotateTransform == RenderHelperState.ROTATE90)
                    state.flipTexture = !state.flipTexture;
                drawFaceZ(face, x, y, z, icon);
                break;
            case RenderHelper.XNEG:
            case RenderHelper.XPOS:
                if (state.rotateTransform == RenderHelperState.ROTATE180 || state.rotateTransform == RenderHelperState.ROTATE270)
                    state.flipTexture = !state.flipTexture;
                drawFaceX(face, x, y, z, icon);
                break;
        }

        state.flipTexture = flip;
    }

    private void drawFaceY (int face, double x, double y, double z, IIcon icon) {
        int rangeX = (int)(Math.ceil(state.renderMaxX + state.shiftU) - Math.floor(state.renderMinX + state.shiftU));
        int rangeZ = (int)(Math.ceil(state.renderMaxZ + state.shiftV) - Math.floor(state.renderMinZ + state.shiftV));

        setXYZ(x, y, z);
        if (state.renderFromInside) {
            xyz[MINX] = z + state.renderMaxX;
            xyz[MAXX] = z + state.renderMinX;
        }

        int rotate = (face == 0) ? state.uvRotate[0] : state.uvRotate[1];

        if (rangeX <= 1 && rangeZ <= 1) {
            if (face == RenderHelper.YNEG)
                setFaceYNegUV(icon, state.renderMinX, state.renderMinZ, state.renderMaxX, state.renderMaxZ);
            else
                setFaceYPosUV(icon, state.renderMinX, state.renderMinZ, state.renderMaxX, state.renderMaxZ);

            if (state.enableAO)
                renderXYZUVAO(xyzuvMap[face], state.uvRotate[face]);
            else
                renderXYZUV(xyzuvMap[face], state.uvRotate[face]);
            return;
        }

        double uStart = (state.renderMinX + state.shiftU + rangeX) % 1.0;
        double uStop = (state.renderMaxX + state.shiftU + rangeX) % 1.0;
        double vStart = (state.renderMinZ + state.shiftV + rangeZ) % 1.0;
        double vStop = (state.renderMaxZ + state.shiftV + rangeZ) % 1.0;

        setupUVPoints(uStart, vStart, uStop, vStop, rangeX, rangeZ, icon);
        setupAOBrightnessLerp(state.renderMinX, state.renderMaxX, state.renderMinZ, state.renderMaxZ, rangeX, rangeZ);

        for (int ix = 0; ix < rangeX; ix++) {
            xyz[MAXX] = xyz[MINX] + maxUDiv[ix] - minUDiv[ix];
            xyz[MINZ] = z + state.renderMinZ;

            for (int iz = 0; iz < rangeZ; iz++) {
                xyz[MAXZ] = xyz[MINZ] + maxVDiv[iz] - minVDiv[iz];

                state.brightnessTopLeft = brightnessLerp[ix][iz];
                state.brightnessTopRight = brightnessLerp[ix + 1][iz];
                state.brightnessBottomLeft = brightnessLerp[ix][iz + 1];
                state.brightnessBottomRight = brightnessLerp[ix + 1][iz + 1];

                switch (rotate) {
                    case RenderHelperState.ROTATE90:
                        setUV(icon, maxVDiv[ix], minVDiv[ix], minUDiv[iz], maxUDiv[iz]);
                        break;
                    case RenderHelperState.ROTATE180:
                        setUV(icon, maxUDiv[ix], minUDiv[ix], maxVDiv[iz], minVDiv[iz]);
                        break;
                    case RenderHelperState.ROTATE270:
                        setUV(icon, minVDiv[ix], maxVDiv[ix], maxUDiv[iz], minUDiv[iz]);
                        break;
                    default:
                        setUV(icon, minUDiv[ix], maxUDiv[ix], minVDiv[iz], maxVDiv[iz]);
                        break;
                }

                renderXYZUVAO(xyzuvMap[face], state.uvRotate[face]);

                xyz[MINZ] = xyz[MAXZ];
            }
            xyz[MINX] = xyz[MAXX];
        }
    }

    private void setFaceYNegUV (IIcon icon, double minX, double minZ, double maxX, double maxZ) {
        int rotate = state.uvRotate[0];
        if (rotate == RenderHelperState.ROTATE0)
            setUV(icon, minX + state.shiftU, maxX + state.shiftU, maxZ + state.shiftV, minZ + state.shiftV);
        if (rotate == RenderHelperState.ROTATE90)
            setUV(icon, 1 - maxZ + state.shiftU, 1 - minZ + state.shiftU, minX + state.shiftV, maxX + state.shiftV);
        if (rotate == RenderHelperState.ROTATE180)
            setUV(icon, 1 - minX + state.shiftU, 1 - maxX + state.shiftU, 1 - maxZ + state.shiftV, 1 - minZ + state.shiftV);
        if (rotate == RenderHelperState.ROTATE270)
            setUV(icon, maxZ + state.shiftU, minZ + state.shiftU, 1 - minX + state.shiftV, 1 - maxX + state.shiftV);
    }

    private void setFaceYPosUV (IIcon icon, double minX, double minZ, double maxX, double maxZ) {
        int rotate = state.uvRotate[1];
        if (rotate == RenderHelperState.ROTATE0)
            setUV(icon, maxX + state.shiftU, minX + state.shiftU, maxZ + state.shiftV, minZ + state.shiftV);
        if (rotate == RenderHelperState.ROTATE90)
            setUV(icon, maxZ + state.shiftU, minZ + state.shiftU, 1 - maxX + state.shiftV, 1 - minX + state.shiftV);
        if (rotate == RenderHelperState.ROTATE180)
            setUV(icon, 1 - maxX + state.shiftU, 1 - minX + state.shiftU, 1 - maxZ + state.shiftV, 1 - minZ + state.shiftV);
        if (rotate == RenderHelperState.ROTATE270)
            setUV(icon, 1 - maxZ + state.shiftU, 1 - minZ + state.shiftU, maxX + state.shiftV, minX + state.shiftV);
    }

    private void drawFaceZ (int face, double x, double y, double z, IIcon icon) {
        int rangeX = (int)(Math.ceil(state.renderMaxX + state.shiftU) - Math.floor(state.renderMinX + state.shiftU));
        int rangeY = (int)(Math.ceil(state.renderMaxY + state.shiftV) - Math.floor(state.renderMinY + state.shiftV));

        setXYZ(x, y, z);
        if (state.renderFromInside) {
            xyz[MINX] = z + state.renderMaxX;
            xyz[MAXX] = z + state.renderMinX;
        }

        if (rangeX <= 1 && rangeY <= 1) {
            if (state.flipTexture)
                setUV(icon, state.renderMaxX + state.shiftU, state.renderMinX + state.shiftU, 1 - state.renderMaxY + state.shiftV, 1 - state.renderMinY + state.shiftV);
            else
                setUV(icon, state.renderMinX + state.shiftU, state.renderMaxX + state.shiftU, 1 - state.renderMaxY + state.shiftV, 1 - state.renderMinY + state.shiftV);

            if (state.enableAO)
                renderXYZUVAO(xyzuvMap[face], state.uvRotate[face]);
            else
                renderXYZUV(xyzuvMap[face], state.uvRotate[face]);
            return;
        }

        double uStart = (state.renderMinX + state.shiftU + rangeX) % 1.0;
        double uStop = (state.renderMaxX + state.shiftU + rangeX) % 1.0;
        double vStart = (state.renderMinY + state.shiftV + rangeY) % 1.0;
        double vStop = (state.renderMaxY + state.shiftV + rangeY) % 1.0;

        setupUVPoints(uStart, vStart, uStop, vStop, rangeX, rangeY, icon);
        setupAOBrightnessLerp(state.renderMinX, state.renderMaxX, state.renderMinY, state.renderMaxY, rangeX, rangeY);

        //int rotate = (face == 2) ? state.uvRotate[2] : state.uvRotate[3];

        for (int ix = 0; ix < rangeX; ix++) {
            xyz[MAXX] = xyz[MINX] + maxUDiv[ix] - minUDiv[ix];
            xyz[MINY] = y + state.renderMinY;

            for (int iy = 0; iy < rangeY; iy++) {
                xyz[MAXY] = xyz[MINY] + maxVDiv[iy] - minVDiv[iy];

                state.brightnessTopLeft = brightnessLerp[ix][iy];
                state.brightnessTopRight = brightnessLerp[ix + 1][iy];
                state.brightnessBottomLeft = brightnessLerp[ix][iy + 1];
                state.brightnessBottomRight = brightnessLerp[ix + 1][iy + 1];

                if (state.flipTexture)
                    setUV(icon, 1 - minUDiv[ix], 1 - maxUDiv[ix], 1 - maxVDiv[iy], 1 - minVDiv[iy]);
                else
                    setUV(icon, minUDiv[ix], maxUDiv[ix], 1 - maxVDiv[iy], 1 - minVDiv[iy]);

                /*switch (rotate) {
                    case RenderHelperState.ROTATE90:
                        setUV(icon, 1 - minVDiv[ix], minUDiv[iy], 1 - maxVDiv[ix], maxUDiv[iy]);
                        break;
                    case RenderHelperState.ROTATE180:
                        setUV(icon, maxUDiv[ix], 1 - minVDiv[iy], minUDiv[ix], 1 - maxVDiv[iy]);
                        break;
                    case RenderHelperState.ROTATE270:
                        setUV(icon, 1 - maxVDiv[ix], maxUDiv[iy], 1 - minVDiv[ix], minUDiv[iy]);
                        break;
                    default:
                        setUV(icon, minUDiv[ix], 1 - maxVDiv[iy], maxUDiv[ix], 1 - minVDiv[iy]);
                        break;
                }*/

                renderXYZUVAO(xyzuvMap[face], state.uvRotate[face]);

                xyz[MINY] = xyz[MAXY];
            }
            xyz[MINX] = xyz[MAXX];
        }
    }

    private void drawFaceX (int face, double x, double y, double z, IIcon icon) {
        int rangeZ = (int)(Math.ceil(state.renderMaxZ + state.shiftU) - Math.floor(state.renderMinZ + state.shiftU));
        int rangeY = (int)(Math.ceil(state.renderMaxY + state.shiftV) - Math.floor(state.renderMinY + state.shiftV));

        setXYZ(x, y, z);
        if (state.renderFromInside) {
            xyz[MINZ] = z + state.renderMaxZ;
            xyz[MAXZ] = z + state.renderMinZ;
        }

        if (rangeZ <= 1 && rangeY <= 1) {
            if (state.flipTexture)
                setUV(icon, state.renderMaxZ + state.shiftU, state.renderMinZ + state.shiftU, 1 - state.renderMaxY + state.shiftV, 1 - state.renderMinY + state.shiftV);
            else
                setUV(icon, state.renderMinZ + state.shiftU, state.renderMaxZ + state.shiftU, 1 - state.renderMaxY + state.shiftV, 1 - state.renderMinY + state.shiftV);

            if (state.enableAO)
                renderXYZUVAO(xyzuvMap[face], state.uvRotate[face]);
            else
                renderXYZUV(xyzuvMap[face], state.uvRotate[face]);
            return;
        }

        double uStart = (state.renderMinZ + state.shiftU + rangeZ) % 1.0;
        double uStop = (state.renderMaxZ + state.shiftU + rangeZ) % 1.0;
        double vStart = (state.renderMinY + state.shiftV + rangeY) % 1.0;
        double vStop = (state.renderMaxY + state.shiftV + rangeY) % 1.0;

        setupUVPoints(uStart, vStart, uStop, vStop, rangeZ, rangeY, icon);
        setupAOBrightnessLerp(state.renderMinZ, state.renderMaxZ, state.renderMinY, state.renderMaxY, rangeZ, rangeY);

        for (int iz = 0; iz < rangeZ; iz++) {
            xyz[MAXZ] = xyz[MINZ] + maxUDiv[iz] - minUDiv[iz];
            xyz[MINY] = y + state.renderMinY;

            for (int iy = 0; iy < rangeY; iy++) {
                xyz[MAXY] = xyz[MINY] + maxVDiv[iy] - minVDiv[iy];

                state.brightnessTopLeft = brightnessLerp[iz][iy];
                state.brightnessTopRight = brightnessLerp[iz + 1][iy];
                state.brightnessBottomLeft = brightnessLerp[iz][iy + 1];
                state.brightnessBottomRight = brightnessLerp[iz + 1][iy + 1];

                if (state.flipTexture)
                    setUV(icon, 1 - minUDiv[iz], 1 - maxUDiv[iz], 1 - maxVDiv[iy], 1 - minVDiv[iy]);
                else
                    setUV(icon, minUDiv[iz], maxUDiv[iz], 1 - maxVDiv[iy], 1 - minVDiv[iy]);

                renderXYZUVAO(xyzuvMap[face], state.uvRotate[face]);

                xyz[MINY] = xyz[MAXY];
            }
            xyz[MINZ] = xyz[MAXZ];
        }
    }

    public void drawPartialFace (int face, double x, double y, double z, IIcon icon, double uMin, double vMin, double uMax, double vMax) {
        setXYZ(x, y, z);
        setUV(icon, uMin, uMax, vMin, vMax);

        if (state.enableAO)
            renderXYZUVAO(xyzuvMap[face], state.uvRotate[face]);
        else
            renderXYZUV(xyzuvMap[face], state.uvRotate[face]);
    }

    private void setupUVPoints (double uStart, double vStart, double uStop, double vStop, int rangeU, int rangeV, IIcon icon) {
        if (rangeU <= 1) {
            minUDiv[0] = uStart;
            maxUDiv[0] = uStop;
        }
        else {
            minUDiv[0] = uStart;
            maxUDiv[0] = 1;
            for (int i = 1; i < rangeU - 1; i++) {
                minUDiv[i] = 0;
                maxUDiv[i] = 1;
            }
            minUDiv[rangeU - 1] = 0;
            maxUDiv[rangeU - 1] = uStop;
        }

        if (rangeV <= 1) {
            minVDiv[0] = vStart;
            maxVDiv[0] = vStop;
        }
        else {
            minVDiv[0] = vStart;
            maxVDiv[0] = 1;
            for (int i = 1; i < rangeV - 1; i++) {
                minVDiv[i] = 0;
                maxVDiv[i] = 1;
            }
            minVDiv[rangeV - 1] = 0;
            maxVDiv[rangeV - 1] = vStop;
        }
    }

    private void setupAOBrightnessLerp (double left, double right, double top, double bottom, int rangeLR, int rangeTB) {
        double diffLR = right - left;
        double diffTB = bottom - top;

        double posLR = 0;

        for (int lr = 0; lr <= rangeLR; lr++) {
            float lerpLR = (float)(posLR / diffLR);

            int brightTop = RenderHelperAO.mixAOBrightness(state.brightnessTopLeft, state.brightnessTopRight, 1 - lerpLR, lerpLR);
            int brightBottom = RenderHelperAO.mixAOBrightness(state.brightnessBottomLeft, state.brightnessBottomRight, 1 - lerpLR, lerpLR);

            double posTB = 0;
            for (int tb = 0; tb <= rangeTB; tb++) {
                float lerpTB = (float)(posTB / diffTB);

                brightnessLerp[lr][tb] = RenderHelperAO.mixAOBrightness(brightTop, brightBottom, 1 - lerpTB, lerpTB);

                if (tb < rangeTB)
                    posTB += maxVDiv[tb] - minVDiv[tb];
            }

            if (lr < rangeLR)
                posLR += maxUDiv[lr] - minUDiv[lr];
        }
    }

    private void setUV (IIcon icon, double uMin, double uMax, double vMin, double vMax) {
        uv[0] = icon.getInterpolatedU(uMin * 16);
        uv[1] = icon.getInterpolatedU(uMax * 16);
        uv[2] = icon.getInterpolatedV(vMin * 16);
        uv[3] = icon.getInterpolatedV(vMax * 16);
    }

    private void setUV (double uMin, double uMax, double vMin, double vMax) {
        uv[0] = uMin;
        uv[1] = uMax;
        uv[2] = vMin;
        uv[3] = vMax;
    }

    private void setXYZ (double x, double y, double z) {
        xyz[0] = x + state.renderOffsetX + state.renderMinX;
        xyz[1] = x + state.renderOffsetX + state.renderMaxX;
        xyz[2] = y + state.renderOffsetY + state.renderMinY;
        xyz[3] = y + state.renderOffsetY + state.renderMaxY;
        xyz[4] = z + state.renderOffsetZ + state.renderMinZ;
        xyz[5] = z + state.renderOffsetZ + state.renderMaxZ;
    }

    private void renderXYZUV (int[][] index, int uvRotate) {
        Tessellator tessellator = Tessellator.instance;

        int[] tl = index[TL];
        int[] bl = index[BL];
        int[] br = index[BR];
        int[] tr = index[TR];

        double ubl = uv[bl[3]];
        double vbl = uv[bl[4]];
        double utr = uv[tr[3]];
        double vtr = uv[tr[4]];

        if (uvRotate == RenderHelperState.ROTATE90 || uvRotate == RenderHelperState.ROTATE270) {
            ubl = uv[tr[3]];
            vbl = uv[tr[4]];
            utr = uv[bl[3]];
            vtr = uv[bl[4]];
        }

        tessellator.addVertexWithUV(xyz[tl[0]], xyz[tl[1]], xyz[tl[2]], uv[tl[3]], uv[tl[4]]);
        tessellator.addVertexWithUV(xyz[bl[0]], xyz[bl[1]], xyz[bl[2]], ubl, vbl);
        tessellator.addVertexWithUV(xyz[br[0]], xyz[br[1]], xyz[br[2]], uv[br[3]], uv[br[4]]);
        tessellator.addVertexWithUV(xyz[tr[0]], xyz[tr[1]], xyz[tr[2]], utr, vtr);
    }

    private void renderXYZUVAO (int[][] index, int uvRotate) {
        Tessellator tessellator = Tessellator.instance;

        int[] tl = index[TL];
        int[] bl = index[BL];
        int[] br = index[BR];
        int[] tr = index[TR];

        double ubl = uv[bl[3]];
        double vbl = uv[bl[4]];
        double utr = uv[tr[3]];
        double vtr = uv[tr[4]];

        if (uvRotate == RenderHelperState.ROTATE90 || uvRotate == RenderHelperState.ROTATE270) {
            ubl = uv[tr[3]];
            vbl = uv[tr[4]];
            utr = uv[bl[3]];
            vtr = uv[bl[4]];
        }

        tessellator.setColorOpaque_F(state.colorTopLeft[0], state.colorTopLeft[1], state.colorTopLeft[2]);
        tessellator.setBrightness(state.brightnessTopLeft);
        tessellator.addVertexWithUV(xyz[tl[0]], xyz[tl[1]], xyz[tl[2]], uv[tl[3]], uv[tl[4]]);

        tessellator.setColorOpaque_F(state.colorBottomLeft[0], state.colorBottomLeft[1], state.colorBottomLeft[2]);
        tessellator.setBrightness(state.brightnessBottomLeft);
        tessellator.addVertexWithUV(xyz[bl[0]], xyz[bl[1]], xyz[bl[2]], ubl, vbl);

        tessellator.setColorOpaque_F(state.colorBottomRight[0], state.colorBottomRight[1], state.colorBottomRight[2]);
        tessellator.setBrightness(state.brightnessBottomRight);
        tessellator.addVertexWithUV(xyz[br[0]], xyz[br[1]], xyz[br[2]], uv[br[3]], uv[br[4]]);

        tessellator.setColorOpaque_F(state.colorTopRight[0], state.colorTopRight[1], state.colorTopRight[2]);
        tessellator.setBrightness(state.brightnessTopRight);
        tessellator.addVertexWithUV(xyz[tr[0]], xyz[tr[1]], xyz[tr[2]], utr, vtr);
    }
}
