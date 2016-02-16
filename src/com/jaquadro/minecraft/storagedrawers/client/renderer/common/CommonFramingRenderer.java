package com.jaquadro.minecraft.storagedrawers.client.renderer.common;

import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelperState;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class CommonFramingRenderer
{
    private static double unit = .0625;
    private static double unit2 = unit * 2;
    private static double unit4 = unit * 4;
    private static double unit14 = unit * 14;

    private static double[][] baseBoundsLeftY = new double[][] {
        { unit, 1 - unit2, unit, 1, 1, 1 - unit }
    };

    private static double[][] trimBoundsLeftY = new double[][] {
        { 0, 1 - unit2, unit, unit, 1, 1 - unit },
        { 0, 1 - unit2, 0, unit, 1, unit },
        { 0, 1 - unit2, 1 - unit, unit, 1, 1 },
        { unit, 1 - unit2, 0, 1, 1, unit },
        { unit, 1 - unit2, 1 - unit, 1, 1, 1 },
    };

    private static double[][] trimBoundsLeftZ = new double[][] {
        { 0, 1 - unit2, 0, unit, 1, 1 },
        { unit, 1 - unit2, 0, 1, 1, 1 },
    };

    private static double[][] trimBoundsLeftX = new double[][] {
        { 0, 1 - unit2, 0, 1, 1, unit },
        { 0, 1 - unit2, unit, 1, 1, 1 - unit },
        { 0, 1 - unit2, 1 - unit, 1, 1, 1 },
    };

    private static double[][] baseBoundsRightY = new double[][] {
        { 0, 1 - unit2, unit, 1 - unit, 1, 1 - unit }
    };

    private static double[][] trimBoundsRightY = new double[][] {
        { 1 - unit, 1 - unit2, unit, 1, 1, 1 - unit },
        { 1 - unit, 1 - unit2, 0, 1, 1, unit },
        { 1 - unit, 1 - unit2, 1 - unit, 1, 1, 1 },
        { 0, 1 - unit2, 0, 1 - unit, 1, unit },
        { 0, 1 - unit2, 1 - unit, 1 - unit, 1, 1 },
    };

    private static double[][] trimBoundsRightZ = new double[][] {
        { 1 - unit, 1 - unit2, 0, 1, 1, 1 },
        { 0, 1 - unit2, 0, 1 - unit, 1, 1 },
    };

    private static double[][] trimBoundsRightX = new double[][] {
        { 0, 1 - unit2, 0, 1, 1, unit },
        { 0, 1 - unit2, unit, 1, 1, 1 - unit },
        { 0, 1 - unit2, 1 - unit, 1, 1, 1 },
    };

    public void renderLeft (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block) {
        renderTableBox(blockAccess, x, y, z, block, baseBoundsLeftY, trimBoundsLeftY, trimBoundsLeftZ, trimBoundsLeftX, true);
        renderStructure(blockAccess, x, y, z, block, true);
    }

    public void renderRight (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block) {
        renderTableBox(blockAccess, x, y, z, block, baseBoundsRightY, trimBoundsRightY, trimBoundsRightZ, trimBoundsRightX, false);
        renderStructure(blockAccess, x, y, z, block, false);
    }

    public void renderOverlayLeft (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block) {
        renderOverlay(blockAccess, x, y, z, block, baseBoundsLeftY, true);
    }

    public void renderOverlayRight (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block) {
        renderOverlay(blockAccess, x, y, z, block, baseBoundsRightY, false);
    }

    public void renderOverlay (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block, double[][] baseBoundsY, boolean left) {
        IIcon iconOverlay = block.getIconOverlay(left);

        RenderHelper renderer = RenderHelper.instance;

        for (double[] bound : baseBoundsY) {
            renderer.setRenderBounds(bound);
            renderer.renderFace(RenderHelper.YPOS, blockAccess, block, x, y, z, iconOverlay);
        }
    }

    private void renderStructure (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block, boolean left) {
        IIcon iconSurface = block.getIconBase();

        renderFoot(blockAccess, x, y, z, block, iconSurface, left);
        renderLegs(blockAccess, x, y, z, block, iconSurface, left);
        renderBraces(blockAccess, x, y, z, block, iconSurface, left);
    }

    private void renderTableBox (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block, double[][] baseBoundsY, double[][] trimBoundsY, double[][] trimBoundsZ, double[][] trimBoundsX, boolean left) {
        RenderHelper renderer = RenderHelper.instance;

        IIcon iconSurface = block.getIconBase();
        IIcon iconTrim = block.getIconTrim();

        int xSide = left ? RenderHelper.XNEG : RenderHelper.XPOS;

        for (double[] bound : baseBoundsY) {
            renderer.setRenderBounds(bound);
            renderer.renderFace(RenderHelper.YPOS, blockAccess, block, x, y, z, iconSurface);
            renderer.renderFace(RenderHelper.YNEG, blockAccess, block, x, y, z, iconSurface);
        }

        for (double[] bound : trimBoundsY) {
            renderer.setRenderBounds(bound);
            renderer.renderFace(RenderHelper.YPOS, blockAccess, block, x, y, z, iconTrim);
            renderer.renderFace(RenderHelper.YNEG, blockAccess, block, x, y, z, iconTrim);
        }

        for (double[] bound : trimBoundsZ) {
            renderer.setRenderBounds(bound);
            renderer.renderFace(RenderHelper.ZNEG, blockAccess, block, x, y, z, iconTrim);
            renderer.renderFace(RenderHelper.ZPOS, blockAccess, block, x, y, z, iconTrim);
        }

        for (double[] bound : trimBoundsX) {
            renderer.setRenderBounds(bound);
            renderer.renderFace(xSide, blockAccess, block, x, y, z, iconTrim);
        }
    }

    private void renderFoot (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block, IIcon icon, boolean left) {
        RenderHelper renderer = RenderHelper.instance;

        float oldColor = renderer.state.colorMultYPos;
        renderer.state.colorMultYPos = .9f;

        for (int i = 0; i < 2; i++)
            renderer.state.setUVRotation(i, (renderer.state.rotateTransform + RenderHelperState.ROTATE90) % 4);

        double xStart = left ? unit2 : 1 - unit2 - unit2;

        renderer.setRenderBounds(xStart, 0, 0, xStart + unit2, unit2, 1);
        for (int i = 0; i < 6; i++)
            renderer.renderFace(i, blockAccess, block, x,y , z, icon);

        for (int i = 0; i < 2; i++)
            renderer.state.clearUVRotation(i);

        renderer.state.colorMultYPos = oldColor;
    }

    private void renderLegs (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block, IIcon icon, boolean left) {
        RenderHelper renderer = RenderHelper.instance;

        for (int i = 2; i < 6; i++)
            renderer.state.setUVRotation(i, RenderHelperState.ROTATE90);

        double xStart = left ? unit2 : 1 - unit2 - unit2;

        renderer.setRenderBounds(xStart, unit2, unit2, xStart + unit2, 1 - unit2, unit2 + unit2);
        for (int i = 2; i < 6; i++)
            renderer.renderFace(i, blockAccess, block, x, y, z, icon);

        renderer.setRenderBounds(xStart, unit2, 1 - unit2 - unit2, xStart + unit2, 1 - unit2, 1 - unit2);
        for (int i = 2; i < 6; i++)
            renderer.renderFace(i, blockAccess, block, x, y, z, icon);

        for (int i = 2; i < 6; i++)
            renderer.state.clearUVRotation(i);
    }

    private void renderBraces (IBlockAccess blockAccess, int x, int y, int z, BlockFramingTable block, IIcon icon, boolean left) {
        RenderHelper renderer = RenderHelper.instance;

        float oldColor = renderer.state.colorMultYPos;
        renderer.state.colorMultYPos = .85f;

        for (int i = 0; i < 2; i++)
            renderer.state.setUVRotation(i, renderer.state.rotateTransform);

        double xStart = left ? unit2 + unit2 : 0;
        double xStop = left ? 1 : 1 - unit2 - unit2;

        renderer.setRenderBounds(xStart, unit4, unit2, xStop, unit4 + unit2, unit2 + unit2);
        for (int i = 0; i < 4; i++)
            renderer.renderFace(i, blockAccess, block, x, y, z, icon);

        renderer.setRenderBounds(xStart, unit4, 1 - unit2 - unit2, xStop, unit4 + unit2, 1 - unit2);
        for (int i = 0; i < 4; i++)
            renderer.renderFace(i, blockAccess, block, x, y, z, icon);

        for (int i = 0; i < 2; i++)
            renderer.state.clearUVRotation(i);

        renderer.state.colorMultYPos = oldColor;
    }
}
