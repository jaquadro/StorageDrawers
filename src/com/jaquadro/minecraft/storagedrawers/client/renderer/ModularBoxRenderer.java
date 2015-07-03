package com.jaquadro.minecraft.storagedrawers.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;

public class ModularBoxRenderer
{
    public static final int CONNECT_YNEG = 1 << 0;
    public static final int CONNECT_YPOS = 1 << 1;
    public static final int CONNECT_ZNEG = 1 << 2;
    public static final int CONNECT_ZPOS = 1 << 3;
    public static final int CONNECT_XNEG = 1 << 4;
    public static final int CONNECT_XPOS = 1 << 5;
    public static final int CONNECT_YNEG_ZNEG = 1 << 6;
    public static final int CONNECT_YNEG_ZPOS = 1 << 7;
    public static final int CONNECT_YNEG_XNEG = 1 << 8;
    public static final int CONNECT_YNEG_XPOS = 1 << 9;
    public static final int CONNECT_YPOS_ZNEG = 1 << 10;
    public static final int CONNECT_YPOS_ZPOS = 1 << 11;
    public static final int CONNECT_YPOS_XNEG = 1 << 12;
    public static final int CONNECT_YPOS_XPOS = 1 << 13;
    public static final int CONNECT_ZNEG_XNEG = 1 << 14;
    public static final int CONNECT_ZNEG_XPOS = 1 << 15;
    public static final int CONNECT_ZPOS_XNEG = 1 << 16;
    public static final int CONNECT_ZPOS_XPOS = 1 << 17;

    public static final int CUT_YNEG = 1 << 0;
    public static final int CUT_YPOS = 1 << 1;
    public static final int CUT_ZNEG = 1 << 2;
    public static final int CUT_ZPOS = 1 << 3;
    public static final int CUT_XNEG = 1 << 4;
    public static final int CUT_XPOS = 1 << 5;

    public static final int FACE_YNEG = 0;
    public static final int FACE_YPOS = 1;
    public static final int FACE_ZNEG = 2;
    public static final int FACE_ZPOS = 3;
    public static final int FACE_XNEG = 4;
    public static final int FACE_XPOS = 5;

    private static final int TEST_YNEG_ZNEG = CUT_YNEG | CUT_ZNEG;
    private static final int TEST_YNEG_ZPOS = CUT_YNEG | CUT_ZPOS;
    private static final int TEST_YNEG_XNEG = CUT_YNEG | CUT_XNEG;
    private static final int TEST_YNEG_XPOS = CUT_YNEG | CUT_XPOS;
    private static final int TEST_YPOS_ZNEG = CUT_YPOS | CUT_ZNEG;
    private static final int TEST_YPOS_ZPOS = CUT_YPOS | CUT_ZPOS;
    private static final int TEST_YPOS_XNEG = CUT_YPOS | CUT_XNEG;
    private static final int TEST_YPOS_XPOS = CUT_YPOS | CUT_XPOS;
    private static final int TEST_ZNEG_XNEG = CUT_ZNEG | CUT_XNEG;
    private static final int TEST_ZNEG_XPOS = CUT_ZNEG | CUT_XPOS;
    private static final int TEST_ZPOS_XNEG = CUT_ZPOS | CUT_XNEG;
    private static final int TEST_ZPOS_XPOS = CUT_ZPOS | CUT_XPOS;

    private static final int TEST_YNEG_ZNEG_XNEG = CUT_YNEG | CUT_ZNEG | CUT_XNEG;
    private static final int TEST_YNEG_ZNEG_XPOS = CUT_YNEG | CUT_ZNEG | CUT_XPOS;
    private static final int TEST_YNEG_ZPOS_XNEG = CUT_YNEG | CUT_ZPOS | CUT_XNEG;
    private static final int TEST_YNEG_ZPOS_XPOS = CUT_YNEG | CUT_ZPOS | CUT_XPOS;
    private static final int TEST_YPOS_ZNEG_XNEG = CUT_YPOS | CUT_ZNEG | CUT_XNEG;
    private static final int TEST_YPOS_ZNEG_XPOS = CUT_YPOS | CUT_ZNEG | CUT_XPOS;
    private static final int TEST_YPOS_ZPOS_XNEG = CUT_YPOS | CUT_ZPOS | CUT_XNEG;
    private static final int TEST_YPOS_ZPOS_XPOS = CUT_YPOS | CUT_ZPOS | CUT_XPOS;

    private static final int PLANE_YNEG = CONNECT_YNEG | CONNECT_YNEG_ZNEG | CONNECT_YNEG_ZPOS | CONNECT_YNEG_XNEG | CONNECT_YNEG_XPOS;
    private static final int PLANE_YPOS = CONNECT_YPOS | CONNECT_YPOS_ZNEG | CONNECT_YPOS_ZPOS | CONNECT_YPOS_XNEG | CONNECT_YPOS_XPOS;

    public static final float[] COLOR_WHITE = new float[] { 1, 1, 1 };

    public static final int[] sideCut = new int[] { CUT_YNEG, CUT_YPOS, CUT_ZNEG, CUT_ZPOS, CUT_XNEG, CUT_XPOS };

    private double unit = 0.0625;

    private float[][] exteriorColor = new float[6][3];
    private float[][] interiorColor = new float[6][3];
    private float[][] cutColor = new float[6][3];

    private IIcon[] exteriorIcon = new IIcon[6];
    private IIcon[] interiorIcon = new IIcon[6];
    private IIcon[] cutIcon = new IIcon[6];

    private void copyFrom (float[] target, float[] source) {
        target[0] = source[0];
        target[1] = source[1];
        target[2] = source[2];
    }

    private void copyFrom (float[] target, float r, float g, float b) {
        target[0] = r;
        target[1] = g;
        target[2] = b;
    }

    public void setColor (float[] color) {
        setExteriorColor(color);
        setInteriorColor(color);
        setCutColor(color);
    }

    public void setScaledColor (float[] color, float scale) {
        setScaledExteriorColor(color, scale);
        setScaledInteriorColor(color, scale);
        setScaledCutColor(color, scale);
    }

    public void setExteriorColor (float[] color) {
        for (int i = 0; i < 6; i++)
            copyFrom(exteriorColor[i], color);
    }

    public void setExteriorColor (float[] color, int side) {
        copyFrom(exteriorColor[side], color);
    }

    public void setScaledExteriorColor (float[] color, float scale) {
        for (int i = 0; i < 6; i++)
            copyFrom(exteriorColor[i], color[0] * scale, color[1] * scale, color[2] * scale);
    }

    public void setScaledExteriorColor (float[] color, float scale, int side) {
        copyFrom(exteriorColor[side], color[0] * scale, color[1] * scale, color[2] * scale);
    }

    public void setInteriorColor (float[] color) {
        for (int i = 0; i < 6; i++)
            copyFrom(interiorColor[i], color);
    }

    public void setInteriorColor (float[] color, int side) {
        side = (side % 2 == 0) ? side + 1 : side - 1;
        copyFrom(interiorColor[side], color);
    }

    public void setScaledInteriorColor (float[] color, float scale) {
        for (int i = 0; i < 6; i++)
            copyFrom(interiorColor[i], color[0] * scale, color[1] * scale, color[2] * scale);
    }

    public void setCutColor (float[] color) {
        for (int i = 0; i < 6; i++)
            copyFrom(cutColor[i], color);
    }

    public void setCutColor (float[] color, int side) {
        copyFrom(cutColor[side], color);
    }

    public void setScaledCutColor (float[] color, float scale) {
        for (int i = 0; i < 6; i++)
            copyFrom(cutColor[i], color[0] * scale, color[1] * scale, color[2] * scale);
    }

    public void setIcon (IIcon icon) {
        setExteriorIcon(icon);
        setInteriorIcon(icon);
        setCutIcon(icon);
    }

    public void setIcon (IIcon icon, int side) {
        setExteriorIcon(icon, side);
        setInteriorIcon(icon, side);
        setCutIcon(icon, side);
    }

    public void setExteriorIcon (IIcon icon) {
        for (int i = 0; i < 6; i++)
            exteriorIcon[i] = icon;
    }

    public void setExteriorIcon (IIcon icon, int side) {
        exteriorIcon[side] = icon;
    }

    public void setInteriorIcon (IIcon icon) {
        for (int i = 0; i < 6; i++)
            interiorIcon[i] = icon;
    }

    public void setInteriorIcon (IIcon icon, int side) {
        side = (side % 2 == 0) ? side + 1 : side - 1;
        interiorIcon[side] = icon;
    }

    public void setCutIcon (IIcon icon) {
        for (int i = 0; i < 6; i++)
            cutIcon[i] = icon;
    }

    public void setCutIcon (IIcon icon, int side) {
        cutIcon[side] = icon;
    }

    public void setUnit (double unit) {
        this.unit = unit;
    }

    public void renderOctant (RenderBlocks renderer, Block block, double x, double y, double z, int connectedFlags, int cutFlags) {
        double xBase = Math.floor(x);
        double yBase = Math.floor(y);
        double zBase = Math.floor(z);
        double xNeg = x - xBase;
        double yNeg = y - yBase;
        double zNeg = z - zBase;
        double xPos = xNeg + .5;
        double yPos = yNeg + .5;
        double zPos = zNeg + .5;

        renderExterior(renderer, block, xBase, yBase, zBase, xNeg, yNeg, zNeg, xPos, yPos, zPos, connectedFlags, cutFlags);
        renderInterior(renderer, block, xBase, yBase, zBase, xNeg, yNeg, zNeg, xPos, yPos, zPos, connectedFlags, cutFlags);
    }

    public void renderBox (RenderBlocks renderer, Block block, double x, double y, double z, double xNeg, double yNeg, double zNeg, double xPos, double yPos, double zPos, int connectedFlags, int cutFlags) {
        renderExterior(renderer, block, x, y, z, xNeg, yNeg, zNeg, xPos, yPos, zPos, connectedFlags, cutFlags);
        renderInterior(renderer, block, x, y, z, xNeg, yNeg, zNeg, xPos, yPos, zPos, connectedFlags, cutFlags);
    }

    public void renderSolidBox (RenderBlocks renderer, Block block, double x, double y, double z, double xNeg, double yNeg, double zNeg, double xPos, double yPos, double zPos) {
        renderExterior(renderer, block, x, y, z, xNeg, yNeg, zNeg, xPos, yPos, zPos, 0, 0);
    }

    public void renderExterior (RenderBlocks renderer, Block block, double x, double y, double z, double xNeg, double yNeg, double zNeg, double xPos, double yPos, double zPos, int connectedFlags, int cutFlags) {
        if ((cutFlags & CUT_YNEG) != 0)
            connectedFlags |= CONNECT_YNEG;
        if ((cutFlags & CUT_YPOS) != 0)
            connectedFlags |= CONNECT_YPOS;
        if ((cutFlags & CUT_ZNEG) != 0)
            connectedFlags |= CONNECT_ZNEG;
        if ((cutFlags & CUT_ZPOS) != 0)
            connectedFlags |= CONNECT_ZPOS;
        if ((cutFlags & CUT_XNEG) != 0)
            connectedFlags |= CONNECT_XNEG;
        if ((cutFlags & CUT_XPOS) != 0)
            connectedFlags |= CONNECT_XPOS;

        renderer.setRenderBounds(xNeg, yNeg, zNeg, xPos, yPos, zPos);

        // Render solid faces
        if ((connectedFlags & CONNECT_YNEG) == 0)
            renderExteriorFace(FACE_YNEG, renderer, block, x, y, z);
        if ((connectedFlags & CONNECT_YPOS) == 0)
            renderExteriorFace(FACE_YPOS, renderer, block, x, y, z);
        if ((connectedFlags & CONNECT_ZNEG) == 0)
            renderExteriorFace(FACE_ZNEG, renderer, block, x, y, z);
        if ((connectedFlags & CONNECT_ZPOS) == 0)
            renderExteriorFace(FACE_ZPOS, renderer, block, x, y, z);
        if ((connectedFlags & CONNECT_XNEG) == 0)
            renderExteriorFace(FACE_XNEG, renderer, block, x, y, z);
        if ((connectedFlags & CONNECT_XPOS) == 0)
            renderExteriorFace(FACE_XPOS, renderer, block, x, y, z);

        if (unit == 0)
            return;

        // Render edge faces
        if ((cutFlags & TEST_YNEG_ZNEG) != 0) {
            renderer.setRenderBounds(xNeg + unit, yNeg, zNeg, xPos - unit, yNeg + unit, zNeg + unit);
            if ((cutFlags & CUT_YNEG) != 0 && (connectedFlags & CONNECT_ZNEG) == 0)
                renderCutFace(FACE_YNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZNEG) != 0 && (connectedFlags & CONNECT_YNEG) == 0)
                renderCutFace(FACE_ZNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YNEG_ZPOS) != 0) {
            renderer.setRenderBounds(xNeg + unit, yNeg, zPos - unit, xPos - unit, yNeg + unit, zPos);
            if ((cutFlags & CUT_YNEG) != 0 && (connectedFlags & CONNECT_ZPOS) == 0)
                renderCutFace(FACE_YNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZPOS) != 0 && (connectedFlags & CONNECT_YNEG) == 0)
                renderCutFace(FACE_ZPOS, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YNEG_XNEG) != 0) {
            renderer.setRenderBounds(xNeg, yNeg, zNeg + unit, xNeg + unit, yNeg + unit, zPos - unit);
            if ((cutFlags & CUT_YNEG) != 0 && (connectedFlags & CONNECT_XNEG) == 0)
                renderCutFace(FACE_YNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_XNEG) != 0 && (connectedFlags & CONNECT_YNEG) == 0)
                renderCutFace(FACE_XNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YNEG_XPOS) != 0) {
            renderer.setRenderBounds(xPos - unit, yNeg, zNeg + unit, xPos, yNeg + unit, zPos - unit);
            if ((cutFlags & CUT_YNEG) != 0 && (connectedFlags & CONNECT_XPOS) == 0)
                renderCutFace(FACE_YNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_XPOS) != 0 && (connectedFlags & CONNECT_YNEG) == 0)
                renderCutFace(FACE_XPOS, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YPOS_ZNEG) != 0) {
            renderer.setRenderBounds(xNeg + unit, yPos - unit, zNeg, xPos - unit, yPos, zNeg + unit);
            if ((cutFlags & CUT_YPOS) != 0 && (connectedFlags & CONNECT_ZNEG) == 0)
                renderCutFace(FACE_YPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZNEG) != 0 && (connectedFlags & CONNECT_YPOS) == 0)
                renderCutFace(FACE_ZNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YPOS_ZPOS) != 0) {
            renderer.setRenderBounds(xNeg + unit, yPos - unit, zPos - unit, xPos - unit, yPos, zPos);
            if ((cutFlags & CUT_YPOS) != 0 && (connectedFlags & CONNECT_ZPOS) == 0)
                renderCutFace(FACE_YPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZPOS) != 0 && (connectedFlags & CONNECT_YPOS) == 0)
                renderCutFace(FACE_ZPOS, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YPOS_XNEG) != 0) {
            renderer.setRenderBounds(xNeg, yPos - unit, zNeg + unit, xNeg + unit, yPos, zPos - unit);
            if ((cutFlags & CUT_YPOS) != 0 && (connectedFlags & CONNECT_XNEG) == 0)
                renderCutFace(FACE_YPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_XNEG) != 0 && (connectedFlags & CONNECT_YPOS) == 0)
                renderCutFace(FACE_XNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YPOS_XPOS) != 0) {
            renderer.setRenderBounds(xPos - unit, yPos - unit, zNeg + unit, xPos, yPos, zPos - unit);
            if ((cutFlags & CUT_YPOS) != 0 && (connectedFlags & CONNECT_XPOS) == 0)
                renderCutFace(FACE_YPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_XPOS) != 0 && (connectedFlags & CONNECT_YPOS) == 0)
                renderCutFace(FACE_XPOS, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_ZNEG_XNEG) != 0) {
            renderer.setRenderBounds(xNeg, yNeg + unit, zNeg, xNeg + unit, yPos - unit, zNeg + unit);
            if ((cutFlags & CUT_ZNEG) != 0 && (connectedFlags & CONNECT_XNEG) == 0)
                renderCutFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_XNEG) != 0 && (connectedFlags & CONNECT_ZNEG) == 0)
                renderCutFace(FACE_XNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_ZNEG_XPOS) != 0) {
            renderer.setRenderBounds(xPos - unit, yNeg + unit, zNeg, xPos, yPos - unit, zNeg + unit);
            if ((cutFlags & CUT_ZNEG) != 0 && (connectedFlags & CONNECT_XPOS) == 0)
                renderCutFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_XPOS) != 0 && (connectedFlags & CONNECT_ZNEG) == 0)
                renderCutFace(FACE_XPOS, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_ZPOS_XNEG) != 0) {
            renderer.setRenderBounds(xNeg, yNeg + unit, zPos - unit, xNeg + unit, yPos - unit, zPos);
            if ((cutFlags & CUT_ZPOS) != 0 && (connectedFlags & CONNECT_XNEG) == 0)
                renderCutFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_XNEG) != 0 && (connectedFlags & CONNECT_ZPOS) == 0)
                renderCutFace(FACE_XNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_ZPOS_XPOS) != 0) {
            renderer.setRenderBounds(xPos - unit, yNeg + unit, zPos - unit, xPos, yPos - unit, zPos);
            if ((cutFlags & CUT_ZPOS) != 0 && (connectedFlags & CONNECT_XPOS) == 0)
                renderCutFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_XPOS) != 0 && (connectedFlags & CONNECT_ZPOS) == 0)
                renderCutFace(FACE_XPOS, renderer, block, x, y, z);
        }

        // Render corner faces
        if ((cutFlags & TEST_YNEG_ZNEG_XNEG) != 0) {
            renderer.setRenderBounds(xNeg, yNeg, zNeg, xNeg + unit, yNeg + unit, zNeg + unit);
            if ((cutFlags & CUT_YNEG) != 0 && (connectedFlags | CONNECT_ZNEG | CONNECT_XNEG | CONNECT_ZNEG_XNEG) != connectedFlags)
                renderCutFace(FACE_YNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZNEG) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_XNEG | CONNECT_YNEG_XNEG) != connectedFlags)
                renderCutFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_XNEG) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZNEG | CONNECT_YNEG_ZNEG) != connectedFlags)
                renderCutFace(FACE_XNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YNEG_ZNEG_XPOS) != 0) {
            renderer.setRenderBounds(xPos - unit, yNeg, zNeg, xPos, yNeg + unit, zNeg + unit);
            if ((cutFlags & CUT_YNEG) != 0 && (connectedFlags | CONNECT_ZNEG | CONNECT_XPOS | CONNECT_ZNEG_XPOS) != connectedFlags)
                renderCutFace(FACE_YNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZNEG) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_XPOS | CONNECT_YNEG_XPOS) != connectedFlags)
                renderCutFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_XPOS) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZNEG | CONNECT_YNEG_ZNEG) != connectedFlags)
                renderCutFace(FACE_XPOS, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YNEG_ZPOS_XNEG) != 0) {
            renderer.setRenderBounds(xNeg, yNeg, zPos - unit, xNeg + unit, yNeg + unit, zPos);
            if ((cutFlags & CUT_YNEG) != 0 && (connectedFlags | CONNECT_ZPOS | CONNECT_XNEG | CONNECT_ZPOS_XNEG) != connectedFlags)
                renderCutFace(FACE_YNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZPOS) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_XNEG | CONNECT_YNEG_XNEG) != connectedFlags)
                renderCutFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_XNEG) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZPOS | CONNECT_YNEG_ZPOS) != connectedFlags)
                renderCutFace(FACE_XNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YNEG_ZPOS_XPOS) != 0) {
            renderer.setRenderBounds(xPos - unit, yNeg, zPos - unit, xPos, yNeg + unit, zPos);
            if ((cutFlags & CUT_YNEG) != 0 && (connectedFlags | CONNECT_ZPOS | CONNECT_XPOS | CONNECT_ZPOS_XPOS) != connectedFlags)
                renderCutFace(FACE_YNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZPOS) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_XPOS | CONNECT_YNEG_XPOS) != connectedFlags)
                renderCutFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_XPOS) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZPOS | CONNECT_YNEG_ZPOS) != connectedFlags)
                renderCutFace(FACE_XPOS, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YPOS_ZNEG_XNEG) != 0) {
            renderer.setRenderBounds(xNeg, yPos - unit, zNeg, xNeg + unit, yPos, zNeg + unit);
            if ((cutFlags & CUT_YPOS) != 0 && (connectedFlags | CONNECT_ZNEG | CONNECT_XNEG | CONNECT_ZNEG_XNEG) != connectedFlags)
                renderCutFace(FACE_YPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZNEG) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_XNEG | CONNECT_YPOS_XNEG) != connectedFlags)
                renderCutFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_XNEG) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZNEG | CONNECT_YPOS_ZNEG) != connectedFlags)
                renderCutFace(FACE_XNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YPOS_ZNEG_XPOS) != 0) {
            renderer.setRenderBounds(xPos - unit, yPos - unit, zNeg, xPos, yPos, zNeg + unit);
            if ((cutFlags & CUT_YPOS) != 0 && (connectedFlags | CONNECT_ZNEG | CONNECT_XPOS | CONNECT_ZNEG_XPOS) != connectedFlags)
                renderCutFace(FACE_YPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZNEG) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_XPOS | CONNECT_YPOS_XPOS) != connectedFlags)
                renderCutFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((cutFlags & CUT_XPOS) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZNEG | CONNECT_YPOS_ZNEG) != connectedFlags)
                renderCutFace(FACE_XPOS, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YPOS_ZPOS_XNEG) != 0) {
            renderer.setRenderBounds(xNeg, yPos - unit, zPos - unit, xNeg + unit, yPos, zPos);
            if ((cutFlags & CUT_YPOS) != 0 && (connectedFlags | CONNECT_ZPOS | CONNECT_XNEG | CONNECT_ZPOS_XNEG) != connectedFlags)
                renderCutFace(FACE_YPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZPOS) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_XNEG | CONNECT_YPOS_XNEG) != connectedFlags)
                renderCutFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_XNEG) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZPOS | CONNECT_YPOS_ZPOS) != connectedFlags)
                renderCutFace(FACE_XNEG, renderer, block, x, y, z);
        }
        if ((cutFlags & TEST_YPOS_ZPOS_XPOS) != 0) {
            renderer.setRenderBounds(xPos - unit, yPos - unit, zPos - unit, xPos, yPos, zPos);
            if ((cutFlags & CUT_YPOS) != 0 && (connectedFlags | CONNECT_ZPOS | CONNECT_XPOS | CONNECT_ZPOS_XPOS) != connectedFlags)
                renderCutFace(FACE_YPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_ZPOS) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_XPOS | CONNECT_YPOS_XPOS) != connectedFlags)
                renderCutFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((cutFlags & CUT_XPOS) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZPOS | CONNECT_YPOS_ZPOS) != connectedFlags)
                renderCutFace(FACE_XPOS, renderer, block, x, y, z);
        }
    }

    public void renderExteriorCut (RenderBlocks renderer, Block block, double x, double y, double z, double xNeg, double yNeg, double zNeg, double xPos, double yPos, double zPos, int connectedFlags, int cutFlags) {

    }

    public void renderInterior (RenderBlocks renderer, Block block, double x, double y, double z, double xNeg, double yNeg, double zNeg, double xPos, double yPos, double zPos, int connectedFlags, int cutFlags) {
        if ((cutFlags & CUT_YNEG) != 0)
            connectedFlags |= PLANE_YNEG;
        if ((cutFlags & CUT_YPOS) != 0)
            connectedFlags |= PLANE_YPOS;
        if ((cutFlags & CUT_ZNEG) != 0)
            connectedFlags |= CONNECT_ZNEG;
        if ((cutFlags & CUT_ZPOS) != 0)
            connectedFlags |= CONNECT_ZPOS;
        if ((cutFlags & CUT_XNEG) != 0)
            connectedFlags |= CONNECT_XNEG;
        if ((cutFlags & CUT_XPOS) != 0)
            connectedFlags |= CONNECT_XPOS;

        renderer.setRenderBounds(xNeg + unit, yNeg + unit, zNeg + unit, xPos - unit, yPos - unit, zPos - unit);

        // Render solid faces
        if ((connectedFlags & CONNECT_YNEG) == 0) {
            renderer.setRenderBounds(xNeg + unit, yNeg, zNeg + unit, xPos - unit, yNeg + unit, zPos - unit);
            renderInteriorFace(FACE_YPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & CONNECT_YPOS) == 0) {
            renderer.setRenderBounds(xNeg + unit, yPos - unit, zNeg + unit, xPos - unit, yPos, zPos - unit);
            renderInteriorFace(FACE_YNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & CONNECT_ZNEG) == 0) {
            renderer.setRenderBounds(xNeg + unit, yNeg + unit, zNeg, xPos - unit, yPos - unit, zNeg + unit);
            renderInteriorFace(FACE_ZPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & CONNECT_ZPOS) == 0) {
            renderer.setRenderBounds(xNeg + unit, yNeg + unit, zPos - unit, xPos - unit, yPos - unit, zPos);
            renderInteriorFace(FACE_ZNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & CONNECT_XNEG) == 0) {
            renderer.setRenderBounds(xNeg, yNeg + unit, zNeg + unit, xNeg + unit, yPos - unit, zPos - unit);
            renderInteriorFace(FACE_XPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & CONNECT_XPOS) == 0) {
            renderer.setRenderBounds(xPos - unit, yNeg + unit, zNeg + unit, xPos, yPos - unit, zPos - unit);
            renderInteriorFace(FACE_XNEG, renderer, block, x, y, z);
        }

        if (unit == 0)
            return;

        // Render edge faces
        if ((connectedFlags & TEST_YNEG_ZNEG) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZNEG | CONNECT_YNEG_ZNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg + unit, yNeg, zNeg, xPos - unit, yNeg + unit, zNeg + unit);
            if ((connectedFlags & CONNECT_YNEG) != 0)
                renderInteriorFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_ZNEG) != 0)
                renderInteriorFace(FACE_YPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YNEG_ZPOS) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZPOS | CONNECT_YNEG_ZPOS) != connectedFlags) {
            renderer.setRenderBounds(xNeg + unit, yNeg, zPos - unit, xPos - unit, yNeg + unit, zPos);
            if ((connectedFlags & CONNECT_YNEG) != 0)
                renderInteriorFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_ZPOS) != 0)
                renderInteriorFace(FACE_YPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YNEG_XNEG) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_XNEG | CONNECT_YNEG_XNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg, yNeg, zNeg + unit, xNeg + unit, yNeg + unit, zPos - unit);
            if ((connectedFlags & CONNECT_YNEG) != 0)
                renderInteriorFace(FACE_XPOS, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_XNEG) != 0)
                renderInteriorFace(FACE_YPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YNEG_XPOS) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_XPOS | CONNECT_YNEG_XPOS) != connectedFlags) {
            renderer.setRenderBounds(xPos - unit, yNeg, zNeg + unit, xPos, yNeg + unit, zPos - unit);
            if ((connectedFlags & CONNECT_YNEG) != 0)
                renderInteriorFace(FACE_XNEG, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_XPOS) != 0)
                renderInteriorFace(FACE_YPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YPOS_ZNEG) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZNEG | CONNECT_YPOS_ZNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg + unit, yPos - unit, zNeg, xPos - unit, yPos, zNeg + unit);
            if ((connectedFlags & CONNECT_YPOS) != 0)
                renderInteriorFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_ZNEG) != 0)
                renderInteriorFace(FACE_YNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YPOS_ZPOS) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZPOS | CONNECT_YPOS_ZPOS) != connectedFlags) {
            renderer.setRenderBounds(xNeg + unit, yPos - unit, zPos - unit, xPos - unit, yPos, zPos);
            if ((connectedFlags & CONNECT_YPOS) != 0)
                renderInteriorFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_ZPOS) != 0)
                renderInteriorFace(FACE_YNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YPOS_XNEG) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_XNEG | CONNECT_YPOS_XNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg, yPos - unit, zNeg + unit, xNeg + unit, yPos, zPos - unit);
            if ((connectedFlags & CONNECT_YPOS) != 0)
                renderInteriorFace(FACE_XPOS, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_XNEG) != 0)
                renderInteriorFace(FACE_YNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YPOS_XPOS) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_XPOS | CONNECT_YPOS_XPOS) != connectedFlags) {
            renderer.setRenderBounds(xPos - unit, yPos - unit, zNeg + unit, xPos, yPos, zPos - unit);
            if ((connectedFlags & CONNECT_YPOS) != 0)
                renderInteriorFace(FACE_XNEG, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_XPOS) != 0)
                renderInteriorFace(FACE_YNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_ZNEG_XNEG) != 0 && (connectedFlags | CONNECT_ZNEG | CONNECT_XNEG | CONNECT_ZNEG_XNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg, yNeg + unit, zNeg, xNeg + unit, yPos - unit, zNeg + unit);
            if ((connectedFlags & CONNECT_ZNEG) != 0)
                renderInteriorFace(FACE_XPOS, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_XNEG) != 0)
                renderInteriorFace(FACE_ZPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_ZNEG_XPOS) != 0 && (connectedFlags | CONNECT_ZNEG | CONNECT_XPOS | CONNECT_ZNEG_XPOS) != connectedFlags) {
            renderer.setRenderBounds(xPos - unit, yNeg + unit, zNeg, xPos, yPos - unit, zNeg + unit);
            if ((connectedFlags & CONNECT_ZNEG) != 0)
                renderInteriorFace(FACE_XNEG, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_XPOS) != 0)
                renderInteriorFace(FACE_ZPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_ZPOS_XNEG) != 0 && (connectedFlags | CONNECT_ZPOS | CONNECT_XNEG | CONNECT_ZPOS_XNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg, yNeg + unit, zPos - unit, xNeg + unit, yPos - unit, zPos);
            if ((connectedFlags & CONNECT_ZPOS) != 0)
                renderInteriorFace(FACE_XPOS, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_XNEG) != 0)
                renderInteriorFace(FACE_ZNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_ZPOS_XPOS) != 0 && (connectedFlags | CONNECT_ZPOS | CONNECT_XPOS | CONNECT_ZPOS_XPOS) != connectedFlags) {
            renderer.setRenderBounds(xPos - unit, yNeg + unit, zPos - unit, xPos, yPos - unit, zPos);
            if ((connectedFlags & CONNECT_ZPOS) != 0)
                renderInteriorFace(FACE_XNEG, renderer, block, x, y, z);
            if ((connectedFlags & CONNECT_XPOS) != 0)
                renderInteriorFace(FACE_ZNEG, renderer, block, x, y, z);
        }

        // Render corner faces

        if ((connectedFlags & TEST_YNEG_ZNEG_XNEG) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZNEG | CONNECT_XNEG | CONNECT_YNEG_ZNEG | CONNECT_YNEG_XNEG | CONNECT_ZNEG_XNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg, yNeg, zNeg, xNeg + unit, yNeg + unit, zNeg + unit);
            if ((connectedFlags | CONNECT_YNEG | CONNECT_ZNEG) == connectedFlags)
                renderInteriorFace(FACE_XPOS, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_YNEG | CONNECT_XNEG) == connectedFlags)
                renderInteriorFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_ZNEG | CONNECT_XNEG) == connectedFlags)
                renderInteriorFace(FACE_YPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YNEG_ZNEG_XPOS) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZNEG | CONNECT_XPOS | CONNECT_YNEG_ZNEG | CONNECT_YNEG_XPOS | CONNECT_ZNEG_XPOS) != connectedFlags) {
            renderer.setRenderBounds(xPos - unit, yNeg, zNeg, xPos, yNeg + unit, zNeg + unit);
            if ((connectedFlags | CONNECT_YNEG | CONNECT_ZNEG) == connectedFlags)
                renderInteriorFace(FACE_XNEG, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_YNEG | CONNECT_XPOS) == connectedFlags)
                renderInteriorFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_ZNEG | CONNECT_XPOS) == connectedFlags)
                renderInteriorFace(FACE_YPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YNEG_ZPOS_XNEG) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZPOS | CONNECT_XNEG | CONNECT_YNEG_ZPOS | CONNECT_YNEG_XNEG | CONNECT_ZPOS_XNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg, yNeg, zPos - unit, xNeg + unit, yNeg + unit, zPos);
            if ((connectedFlags | CONNECT_YNEG | CONNECT_ZPOS) == connectedFlags)
                renderInteriorFace(FACE_XPOS, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_YNEG | CONNECT_XNEG) == connectedFlags)
                renderInteriorFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_ZPOS | CONNECT_XNEG) == connectedFlags)
                renderInteriorFace(FACE_YPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YNEG_ZPOS_XPOS) != 0 && (connectedFlags | CONNECT_YNEG | CONNECT_ZPOS | CONNECT_XPOS | CONNECT_YNEG_ZPOS | CONNECT_YNEG_XPOS | CONNECT_ZPOS_XPOS) != connectedFlags) {
            renderer.setRenderBounds(xPos - unit, yNeg, zPos - unit, xPos, yNeg + unit, zPos);
            if ((connectedFlags | CONNECT_YNEG | CONNECT_ZPOS) == connectedFlags)
                renderInteriorFace(FACE_XNEG, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_YNEG | CONNECT_XPOS) == connectedFlags)
                renderInteriorFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_ZPOS | CONNECT_XPOS) == connectedFlags)
                renderInteriorFace(FACE_YPOS, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YPOS_ZNEG_XNEG) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZNEG | CONNECT_XNEG | CONNECT_YPOS_ZNEG | CONNECT_YPOS_XNEG | CONNECT_ZNEG_XNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg, yPos - unit, zNeg, xNeg + unit, yPos, zNeg + unit);
            if ((connectedFlags | CONNECT_YPOS | CONNECT_ZNEG) == connectedFlags)
                renderInteriorFace(FACE_XPOS, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_YPOS | CONNECT_XNEG) == connectedFlags)
                renderInteriorFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_ZNEG | CONNECT_XNEG) == connectedFlags)
                renderInteriorFace(FACE_YNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YPOS_ZNEG_XPOS) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZNEG | CONNECT_XPOS | CONNECT_YPOS_ZNEG | CONNECT_YPOS_XPOS | CONNECT_ZNEG_XPOS) != connectedFlags) {
            renderer.setRenderBounds(xPos - unit, yPos - unit, zNeg, xPos, yPos, zNeg + unit);
            if ((connectedFlags | CONNECT_YPOS | CONNECT_ZNEG) == connectedFlags)
                renderInteriorFace(FACE_XNEG, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_YPOS | CONNECT_XPOS) == connectedFlags)
                renderInteriorFace(FACE_ZPOS, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_ZNEG | CONNECT_XPOS) == connectedFlags)
                renderInteriorFace(FACE_YNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YPOS_ZPOS_XNEG) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZPOS | CONNECT_XNEG | CONNECT_YPOS_ZPOS | CONNECT_YPOS_XNEG | CONNECT_ZPOS_XNEG) != connectedFlags) {
            renderer.setRenderBounds(xNeg, yPos - unit, zPos - unit, xNeg + unit, yPos, zPos);
            if ((connectedFlags | CONNECT_YPOS | CONNECT_ZPOS) == connectedFlags)
                renderInteriorFace(FACE_XPOS, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_YPOS | CONNECT_XNEG) == connectedFlags)
                renderInteriorFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_ZPOS | CONNECT_XNEG) == connectedFlags)
                renderInteriorFace(FACE_YNEG, renderer, block, x, y, z);
        }
        if ((connectedFlags & TEST_YPOS_ZPOS_XPOS) != 0 && (connectedFlags | CONNECT_YPOS | CONNECT_ZPOS | CONNECT_XPOS | CONNECT_YPOS_ZPOS | CONNECT_YPOS_XPOS | CONNECT_ZPOS_XPOS) != connectedFlags) {
            renderer.setRenderBounds(xPos - unit, yPos - unit, zPos - unit, xPos, yPos, zPos);
            if ((connectedFlags | CONNECT_YPOS | CONNECT_ZPOS) == connectedFlags)
                renderInteriorFace(FACE_XNEG, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_YPOS | CONNECT_XPOS) == connectedFlags)
                renderInteriorFace(FACE_ZNEG, renderer, block, x, y, z);
            if ((connectedFlags | CONNECT_ZPOS | CONNECT_XPOS) == connectedFlags)
                renderInteriorFace(FACE_YNEG, renderer, block, x, y, z);
        }
    }

    private void renderFace (int face, RenderBlocks renderer, Block block, double x, double y, double z, IIcon icon, float r, float g, float b) {
        switch (face) {
            case FACE_YNEG:
                RenderUtil.renderFaceYNeg(renderer, block, (int)x, (int)y, (int)z, icon, r, g, b);
                break;
            case FACE_YPOS:
                RenderUtil.renderFaceYPos(renderer, block, (int) x, (int) y, (int) z, icon, r, g, b);
                break;
            case FACE_ZNEG:
                RenderUtil.renderFaceZNeg(renderer, block, (int) x, (int) y, (int) z, icon, r, g, b);
                break;
            case FACE_ZPOS:
                RenderUtil.renderFaceZPos(renderer, block, (int) x, (int) y, (int) z, icon, r, g, b);
                break;
            case FACE_XNEG:
                RenderUtil.renderFaceXNeg(renderer, block, (int) x, (int) y, (int) z, icon, r, g, b);
                break;
            case FACE_XPOS:
                RenderUtil.renderFaceXPos(renderer, block, (int) x, (int) y, (int) z, icon, r, g, b);
                break;
        }
    }

    private void renderExteriorFace (int face, RenderBlocks renderer, Block block, double x, double y, double z) {
        renderFace(face, renderer, block, x, y, z, exteriorIcon[face], exteriorColor[face][0], exteriorColor[face][1], exteriorColor[face][2]);
    }

    private void renderInteriorFace (int face, RenderBlocks renderer, Block block, double x, double y, double z) {
        IIcon icon = interiorIcon[face];
        float r = interiorColor[face][0];
        float g = interiorColor[face][1];
        float b = interiorColor[face][2];

        renderFace(face, renderer, block, x, y, z, icon, r, g, b);
    }

    private void renderCutFace (int face, RenderBlocks renderer, Block block, double x, double y, double z) {
        renderFace(face, renderer, block, x, y, z, cutIcon[face], cutColor[face][0], cutColor[face][1], cutColor[face][2]);
    }
}
