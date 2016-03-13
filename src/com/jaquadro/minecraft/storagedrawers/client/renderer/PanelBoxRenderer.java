package com.jaquadro.minecraft.storagedrawers.client.renderer;
/*
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class PanelBoxRenderer
{
    public static final int FACE_YNEG = 0;
    public static final int FACE_YPOS = 1;
    public static final int FACE_ZNEG = 2;
    public static final int FACE_ZPOS = 3;
    public static final int FACE_XNEG = 4;
    public static final int FACE_XPOS = 5;

    private static final int TRIM_CUT = ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_ZNEG
        | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_ZPOS;

    private ModularBoxRenderer trimRenderer = new ModularBoxRenderer();

    private double trimWidth = 0.0625;
    private double trimDepth = 0;

    private float[] trimColor = new float[3];
    private IIcon trimIcon;

    private float[] panelColor = new float[3];
    private IIcon panelIcon;

    public void setTrimWidth (double width) {
        trimWidth = width;
    }

    public void setTrimDepth (double depth) {
        trimDepth = depth;
    }

    public void setTrimIcon (IIcon icon) {
        trimIcon = icon;
    }

    public void setTrimColor (float[] color) {
        copyFrom(trimColor, color);
    }

    public void setPanelIcon (IIcon icon) {
        panelIcon = icon;
    }

    public void setPanelColor (float[] color) {
        copyFrom(panelColor, color);
    }

    public void renderFacePanel (int face, IBlockAccess blockAccess, Block block, double x, double y, double z, double xNeg, double yNeg, double zNeg, double xPos, double yPos, double zPos) {
        RenderHelper renderer = RenderHelper.instance;

        switch (face) {
            case FACE_YNEG:
                renderer.setRenderBounds(xNeg + trimWidth, yNeg + trimDepth, zNeg + trimWidth, xPos - trimWidth, yNeg + trimDepth, zPos - trimWidth);
                renderPaneltFace(face, blockAccess, block, x, y, z);
                break;

            case FACE_YPOS:
                renderer.setRenderBounds(xNeg + trimWidth, yPos - trimDepth, zNeg + trimWidth, xPos - trimWidth, yPos - trimDepth, zPos - trimWidth);
                renderPaneltFace(face, blockAccess, block, x, y, z);
                break;

            case FACE_ZNEG:
                renderer.setRenderBounds(xNeg + trimWidth, yNeg + trimWidth, zNeg + trimDepth, xPos - trimWidth, yPos - trimWidth, zNeg + trimDepth);
                renderPaneltFace(face, blockAccess, block, x, y, z);
                break;

            case FACE_ZPOS:
                renderer.setRenderBounds(xNeg + trimWidth, yNeg + trimWidth, zPos - trimDepth, xPos - trimWidth, yPos - trimWidth, zPos - trimDepth);
                renderPaneltFace(face, blockAccess, block, x, y, z);
                break;

            case FACE_XNEG:
                renderer.setRenderBounds(xNeg + trimDepth, yNeg + trimWidth, zNeg + trimWidth, xNeg + trimDepth, yPos - trimWidth, zPos - trimWidth);
                renderPaneltFace(face, blockAccess, block, x, y, z);
                break;

            case FACE_XPOS:
                renderer.setRenderBounds(xPos - trimDepth, yNeg + trimWidth, zNeg + trimWidth, xPos - trimDepth, yPos - trimWidth, zPos - trimWidth);
                renderPaneltFace(face, blockAccess, block, x, y, z);
                break;
        }
    }

    public void renderInteriorTrim (int face, IBlockAccess blockAccess, Block block, double x, double y, double z, double xNeg, double yNeg, double zNeg, double xPos, double yPos, double zPos) {
        RenderHelper renderer = RenderHelper.instance;

        switch (face) {
            case FACE_YNEG:
                renderer.setRenderBounds(xNeg + trimWidth, yNeg, zPos - trimWidth, xPos - trimWidth, yNeg + trimDepth, zPos - trimWidth);
                renderCutFace(FACE_ZNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + trimWidth, yNeg, zNeg + trimWidth, xPos - trimWidth, yNeg + trimDepth, zNeg + trimWidth);
                renderCutFace(FACE_ZPOS, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - trimWidth, yNeg, zNeg + trimWidth, xPos - trimWidth, yNeg + trimDepth, zPos - trimWidth);
                renderCutFace(FACE_XNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + trimWidth, yNeg, zNeg + trimWidth, xNeg + trimWidth, yNeg + trimDepth, zPos - trimWidth);
                renderCutFace(FACE_XPOS, blockAccess, block, x, y, z);
                break;

            case FACE_YPOS:
                renderer.setRenderBounds(xNeg + trimWidth, yPos - trimDepth, zPos - trimWidth, xPos - trimWidth, yPos, zPos - trimWidth);
                renderCutFace(FACE_ZNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + trimWidth, yPos - trimDepth, zNeg + trimWidth, xPos - trimWidth, yPos, zNeg + trimWidth);
                renderCutFace(FACE_ZPOS, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - trimWidth, yPos - trimDepth, zNeg + trimWidth, xPos - trimWidth, yPos, zPos - trimWidth);
                renderCutFace(FACE_XNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + trimWidth, yPos - trimDepth, zNeg + trimWidth, xNeg + trimWidth, yPos, zPos - trimWidth);
                renderCutFace(FACE_XPOS, blockAccess, block, x, y, z);
                break;

            case FACE_ZNEG:
                renderer.setRenderBounds(xNeg + trimWidth, yPos - trimWidth, zNeg, xPos - trimWidth, yPos - trimWidth, zNeg + trimDepth);
                renderCutFace(FACE_YNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + trimWidth, yNeg + trimWidth, zNeg, xPos - trimWidth, yNeg + trimWidth, zNeg + trimDepth);
                renderCutFace(FACE_YPOS, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - trimWidth, yNeg + trimWidth, zNeg, xPos - trimWidth, yPos - trimWidth, zNeg + trimDepth);
                renderCutFace(FACE_XNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + trimWidth, yNeg + trimWidth, zNeg, xNeg + trimWidth, yPos - trimWidth, zNeg + trimDepth);
                renderCutFace(FACE_XPOS, blockAccess, block, x, y, z);
                break;

            case FACE_ZPOS:
                renderer.setRenderBounds(xNeg + trimWidth, yPos - trimWidth, zPos - trimDepth, xPos - trimWidth, yPos - trimWidth, zPos);
                renderCutFace(FACE_YNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + trimWidth, yNeg + trimWidth, zPos - trimDepth, xPos - trimWidth, yNeg + trimWidth, zPos);
                renderCutFace(FACE_YPOS, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - trimWidth, yNeg + trimWidth, zPos - trimDepth, xPos - trimWidth, yPos - trimWidth, zPos);
                renderCutFace(FACE_XNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + trimWidth, yNeg + trimWidth, zPos - trimDepth, xNeg + trimWidth, yPos - trimWidth, zPos);
                renderCutFace(FACE_XPOS, blockAccess, block, x, y, z);
                break;

            case FACE_XNEG:
                renderer.setRenderBounds(xNeg, yNeg + trimWidth, zPos - trimWidth, xNeg + trimDepth, yPos - trimWidth, zPos - trimWidth);
                renderCutFace(FACE_ZNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yNeg + trimWidth, zNeg + trimWidth, xNeg + trimDepth, yPos - trimWidth, zNeg + trimWidth);
                renderCutFace(FACE_ZPOS, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yPos - trimWidth, zNeg + trimWidth, xNeg + trimDepth, yPos - trimWidth, zPos - trimWidth);
                renderCutFace(FACE_YNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yNeg + trimWidth, zNeg + trimWidth, xNeg + trimDepth, yNeg + trimWidth, zPos - trimWidth);
                renderCutFace(FACE_YPOS, blockAccess, block, x, y, z);
                break;

            case FACE_XPOS:
                renderer.setRenderBounds(xPos - trimDepth, yNeg + trimWidth, zPos - trimWidth, xPos, yPos - trimWidth, zPos - trimWidth);
                renderCutFace(FACE_ZNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - trimDepth, yNeg + trimWidth, zNeg + trimWidth, xPos, yPos - trimWidth, zNeg + trimWidth);
                renderCutFace(FACE_ZPOS, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - trimDepth, yPos - trimWidth, zNeg + trimWidth, xPos, yPos - trimWidth, zPos - trimWidth);
                renderCutFace(FACE_YNEG, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - trimDepth, yNeg + trimWidth, zNeg + trimWidth, xPos, yNeg + trimWidth, zPos - trimWidth);
                renderCutFace(FACE_YPOS, blockAccess, block, x, y, z);
                break;
        }
    }

    public void renderFaceTrim (int face, IBlockAccess blockAccess, Block block, double x, double y, double z, double xNeg, double yNeg, double zNeg, double xPos, double yPos, double zPos) {
        double unit = trimWidth;
        RenderHelper renderer = RenderHelper.instance;

        switch (face) {
            case FACE_YNEG:
                renderer.setRenderBounds(xNeg, yNeg, zNeg, xNeg + unit, yNeg, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yNeg, zNeg, xPos, yNeg, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yNeg, zPos - unit, xNeg + unit, yNeg, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yNeg, zPos - unit, xPos, yNeg, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);

                renderer.setRenderBounds(xNeg + unit, yNeg, zNeg, xPos - unit, yNeg, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + unit, yNeg, zPos - unit, xPos - unit, yNeg, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yNeg, zNeg + unit, xNeg + unit, yNeg, zPos - unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yNeg, zNeg + unit, xPos, yNeg, zPos - unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                break;
            
            case FACE_YPOS:
                renderer.setRenderBounds(xNeg, yPos, zNeg, xNeg + unit, yPos, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yPos, zNeg, xPos, yPos, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yPos, zPos - unit, xNeg + unit, yPos, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yPos, zPos - unit, xPos, yPos, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);

                renderer.setRenderBounds(xNeg + unit, yPos, zNeg, xPos - unit, yPos, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + unit, yPos, zPos - unit, xPos - unit, yPos, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yPos, zNeg + unit, xNeg + unit, yPos, zPos - unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yPos, zNeg + unit, xPos, yPos, zPos - unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                break;

            case FACE_ZNEG:
                renderer.setRenderBounds(xNeg, yNeg, zNeg, xNeg + unit, yNeg + unit, zNeg);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yNeg, zNeg, xPos, yNeg + unit, zNeg);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yPos - unit, zNeg, xNeg + unit, yPos, zNeg);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yPos - unit, zNeg, xPos, yPos, zNeg);
                renderCutFace(face, blockAccess, block, x, y, z);

                renderer.setRenderBounds(xNeg + unit, yNeg, zNeg, xPos - unit, yNeg + unit, zNeg);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + unit, yPos - unit, zNeg, xPos - unit, yPos, zNeg);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yNeg + unit, zNeg, xNeg + unit, yPos - unit, zNeg);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yNeg + unit, zNeg, xPos, yPos - unit, zNeg);
                renderCutFace(face, blockAccess, block, x, y, z);
                break;

            case FACE_ZPOS:
                renderer.setRenderBounds(xNeg, yNeg, zPos, xNeg + unit, yNeg + unit, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yNeg, zPos, xPos, yNeg + unit, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yPos - unit, zPos, xNeg + unit, yPos, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yPos - unit, zPos, xPos, yPos, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);

                renderer.setRenderBounds(xNeg + unit, yNeg, zPos, xPos - unit, yNeg + unit, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg + unit, yPos - unit, zPos, xPos - unit, yPos, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yNeg + unit, zPos, xNeg + unit, yPos - unit, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos - unit, yNeg + unit, zPos, xPos, yPos - unit, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                break;

            case FACE_XNEG:
                renderer.setRenderBounds(xNeg, yNeg, zNeg, xNeg, yNeg + unit, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yPos - unit, zNeg, xNeg, yPos, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yNeg, zPos - unit, xNeg, yNeg + unit, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yPos - unit, zPos - unit, xNeg, yPos, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);

                renderer.setRenderBounds(xNeg, yNeg + unit, zNeg, xNeg, yPos - unit, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yNeg + unit, zPos - unit, xNeg, yPos - unit, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yNeg, zNeg + unit, xNeg, yNeg + unit, zPos - unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xNeg, yPos - unit, zNeg + unit, xNeg, yPos, zPos - unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                break;

            case FACE_XPOS:
                renderer.setRenderBounds(xPos, yNeg, zNeg, xPos, yNeg + unit, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos, yPos - unit, zNeg, xPos, yPos, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos, yNeg, zPos - unit, xPos, yNeg + unit, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos, yPos - unit, zPos - unit, xPos, yPos, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);

                renderer.setRenderBounds(xPos, yNeg + unit, zNeg, xPos, yPos - unit, zNeg + unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos, yNeg + unit, zPos - unit, xPos, yPos - unit, zPos);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos, yNeg, zNeg + unit, xPos, yNeg + unit, zPos - unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                renderer.setRenderBounds(xPos, yPos - unit, zNeg + unit, xPos, yPos, zPos - unit);
                renderCutFace(face, blockAccess, block, x, y, z);
                break;
        }
    }

    private void renderCutFace (int face, IBlockAccess blockAccess, Block block, double x, double y, double z) {
        RenderHelper.instance.renderFace(face, blockAccess, block, (int)x, (int)y, (int)z, trimIcon, trimColor[0], trimColor[1], trimColor[2]);
    }

    private void renderPaneltFace (int face, IBlockAccess blockAccess, Block block, double x, double y, double z) {
        RenderHelper.instance.renderFace(face, blockAccess, block, (int)x, (int)y, (int)z, panelIcon, panelColor[0], panelColor[1], panelColor[2]);
    }

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
}
*/