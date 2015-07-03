package com.jaquadro.minecraft.storagedrawers.util;

public class RenderHelperState
{
    public static final int ROTATE90 = 1;
    public static final int ROTATE180 = 2;
    public static final int ROTATE270 = 3;

    public static final int[][] ROTATION_BY_FACE_FACE_Y = {
        { 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 2, 3, 1 },
        { 0, 0, 2, 0, 1, 3 },
        { 0, 0, 1, 3, 0, 2 },
        { 0, 0, 3, 1, 2, 0 },
    };

    public static final int[][] ROTATION_BY_FACE_FACE_Z = {
        { 0, 2, 0, 0, 1, 3 },
        { 2, 0, 0, 0, 3, 1 },
        { 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0 },
        { 3, 1, 0, 0, 0, 2 },
        { 1, 3, 0, 0, 2, 0 },
    };

    public static final int[][] ROTATION_BY_FACE_FACE_X = {
        { 0, 2, 1, 3, 0, 0 },
        { 2, 0, 3, 1, 0, 0 },
        { 3, 1, 0, 2, 0, 0 },
        { 1, 3, 2, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0 },
    };

    public static final int[][] FACE_BY_FACE_ROTATION_Y = {
        { 0, 0, 0, 0 },
        { 1, 1, 1, 1 },
        { 2, 5, 3, 4 },
        { 3, 4, 2, 5 },
        { 4, 2, 5, 3 },
        { 5, 3, 4, 2 },
    };

    public static final int[][] FACE_BY_FACE_ROTATION_Z = {
        { 0, 4, 1, 5 },
        { 1, 5, 0, 4 },
        { 2, 2, 2, 2 },
        { 3, 3, 3, 3 },
        { 4, 1, 5, 0 },
        { 5, 0, 4, 1 },
    };

    public static final int[][] FACE_BY_FACE_ROTATION_X = {
        { 0, 2, 1, 3 },
        { 1, 3, 0, 2 },
        { 2, 1, 3, 0 },
        { 3, 0, 2, 1 },
        { 4, 4, 4, 4 },
        { 5, 5, 5, 5 },
    };

    public double renderMinX;
    public double renderMinY;
    public double renderMinZ;
    public double renderMaxX;
    public double renderMaxY;
    public double renderMaxZ;

    public boolean flipTexture;
    public boolean renderFromInside;
    public boolean enableAO;

    public int rotateTransformX;
    public int rotateTransformY;
    public int rotateTransformZ;

    public float shiftU;
    public float shiftV;

    public final int[] uvRotate = new int[6];

    public float colorMultYNeg;
    public float colorMultYPos;
    public float colorMultZNeg;
    public float colorMultZPos;
    public float colorMultXNeg;
    public float colorMultXPos;

    public int brightnessTopLeft;
    public int brightnessBottomLeft;
    public int brightnessBottomRight;
    public int brightnessTopRight;

    public final float[] colorTopLeft = new float[3];
    public final float[] colorBottomLeft = new float[3];
    public final float[] colorBottomRight = new float[3];
    public final float[] colorTopRight = new float[3];

    private final double[] scratchIn = new double[3];
    private final double[] scratchOut = new double[3];

    public RenderHelperState () {
        resetColorMult();
    }

    public void setRenderBounds (double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        renderMinX = xMin;
        renderMinY = yMin;
        renderMinZ = zMin;
        renderMaxX = xMax;
        renderMaxY = yMax;
        renderMaxZ = zMax;
    }

    public void resetColorMult () {
        colorMultYNeg = 0.5f;
        colorMultYPos = 1.0f;
        colorMultZNeg = 0.8f;
        colorMultZPos = 0.8f;
        colorMultXNeg = 0.6f;
        colorMultXPos = 0.6f;
    }

    public void setTextureOffset (float u, float v) {
        shiftU = u;
        shiftV = v;
    }

    public void resetTextureOffset () {
        shiftU = 0;
        shiftV = 0;
    }

    public void setUVRotation (int face, int rotation) {
        uvRotate[face] = rotation;
    }

    public void clearUVRotation (int face) {
        uvRotate[face] = 0;
    }

    public void setColor (float r, float g, float b) {
        colorTopLeft[0] = r;
        colorTopLeft[1] = g;
        colorTopLeft[2] = b;

        colorBottomLeft[0] = r;
        colorBottomLeft[1] = g;
        colorBottomLeft[2] = b;

        colorBottomRight[0] = r;
        colorBottomRight[1] = g;
        colorBottomRight[2] = b;

        colorTopRight[0] = r;
        colorTopRight[1] = g;
        colorTopRight[2] = b;
    }

    public void scaleColor (float[] color, float scale) {
        for (int i = 0; i < color.length; i++)
            color[i] *= scale;
    }

    public int getTransformedFace (int face) {
        face = FACE_BY_FACE_ROTATION_X[face][rotateTransformX];
        face = FACE_BY_FACE_ROTATION_Y[face][rotateTransformY];
        face = FACE_BY_FACE_ROTATION_Z[face][rotateTransformZ];

        return face;
    }

    public void setRotateTransform (int faceFrom, int faceTo) {
        rotateTransformX = ROTATION_BY_FACE_FACE_X[faceFrom][faceTo];
        faceFrom = FACE_BY_FACE_ROTATION_X[faceFrom][rotateTransformX];
        rotateTransformY = ROTATION_BY_FACE_FACE_Y[faceFrom][faceTo];
        faceFrom = FACE_BY_FACE_ROTATION_Y[faceFrom][rotateTransformY];
        rotateTransformZ = ROTATION_BY_FACE_FACE_Z[faceFrom][faceTo];

        transformRenderBound();
    }

    public void clearRotateTransform () {
        rotateTransformX = 0;
        rotateTransformY = 0;
        rotateTransformZ = 0;
    }

    private void transformRenderBound () {
        if (rotateTransformX == 0 && rotateTransformY == 0 && rotateTransformZ == 0)
            return;

        scratchIn[0] = renderMinX;
        scratchIn[1] = renderMinY;
        scratchIn[2] = renderMinZ;
        transformCoordX(scratchIn, scratchOut, rotateTransformX);
        transformCoordY(scratchOut, scratchIn, rotateTransformY);
        transformCoordZ(scratchIn, scratchOut, rotateTransformZ);
        renderMinX = scratchOut[0];
        renderMinY = scratchOut[1];
        renderMinZ = scratchOut[2];

        scratchIn[0] = renderMaxX;
        scratchIn[1] = renderMaxY;
        scratchIn[2] = renderMaxZ;
        transformCoordX(scratchIn, scratchOut, rotateTransformX);
        transformCoordY(scratchOut, scratchIn, rotateTransformY);
        transformCoordZ(scratchIn, scratchOut, rotateTransformZ);
        renderMaxX = scratchOut[0];
        renderMaxY = scratchOut[1];
        renderMaxZ = scratchOut[2];

        if (renderMinX > renderMaxX) {
            double temp = renderMinX;
            renderMinX = renderMaxX;
            renderMaxX = temp;
        }

        if (renderMinY > renderMaxY) {
            double temp = renderMinY;
            renderMinY = renderMaxY;
            renderMaxY = temp;
        }

        if (renderMinZ > renderMaxZ) {
            double temp = renderMinZ;
            renderMinZ = renderMaxZ;
            renderMaxZ = temp;
        }
    }

    public void transformCoordY (double[] coordIn, double[] coordOut, int rotation) {
        coordOut[1] = coordIn[1];

        switch (rotation) {
            case 1: // 90
                coordOut[0] = 1 - coordIn[2];
                coordOut[2] = coordIn[0];
                break;
            case 2: // 180
                coordOut[0] = 1 - coordIn[0];
                coordOut[2] = 1 - coordIn[2];
                break;
            case 3: // 270
                coordOut[0] = coordIn[2];
                coordOut[2] = 1 - coordIn[0];
                break;
            case 0: // 0
            default:
                coordOut[0] = coordIn[0];
                coordOut[2] = coordIn[2];
                break;
        }
    }

    public void transformCoordZ (double[] coordIn, double[] coordOut, int rotation) {
        coordOut[2] = coordIn[2];

        switch (rotation) {
            case 1: // 90
                coordOut[0] = coordIn[1];
                coordOut[1] = 1 - coordIn[0];
                break;
            case 2: // 180
                coordOut[0] = 1 - coordIn[0];
                coordOut[1] = 1 - coordIn[1];
                break;
            case 3: // 270
                coordOut[0] = 1 - coordIn[1];
                coordOut[1] = coordIn[0];
                break;
            case 0: // 0
            default:
                coordOut[0] = coordIn[0];
                coordOut[1] = coordIn[1];
                break;
        }
    }

    public void transformCoordX (double[] coordIn, double[] coordOut, int rotation) {
        coordOut[0] = coordIn[0];

        switch (rotation) {
            case 1: // 90
                coordOut[2] = coordIn[1];
                coordOut[1] = 1 - coordIn[2];
                break;
            case 2: // 180
                coordOut[2] = 1 - coordIn[2];
                coordOut[1] = 1 - coordIn[1];
                break;
            case 3: // 270
                coordOut[2] = 1 - coordIn[1];
                coordOut[1] = coordIn[2];
                break;
            case 0: // 0
            default:
                coordOut[2] = coordIn[2];
                coordOut[1] = coordIn[1];
                break;
        }
    }
}
