package com.jaquadro.minecraft.storagedrawers.client.model.dynamic;

import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.render.ChamRenderState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class CommonFramingRenderer
{
    private static double unit = .0625;
    private static double unit2 = unit * 2;
    private static double unit4 = unit * 4;

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

    private ChamRender renderer;

    public CommonFramingRenderer (ChamRender renderer) {
        this.renderer = renderer;
    }

    public void renderLeft (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite iconBase, TextureAtlasSprite iconTrim) {
        renderTableBox(blockAccess, state, pos, iconBase, iconTrim, baseBoundsLeftY, trimBoundsLeftY, trimBoundsLeftZ, trimBoundsLeftX, true);
        renderStructure(blockAccess, state, pos, iconBase, true);
    }

    public void renderRight (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite iconBase, TextureAtlasSprite iconTrim) {
        renderTableBox(blockAccess, state, pos, iconBase, iconTrim, baseBoundsRightY, trimBoundsRightY, trimBoundsRightZ, trimBoundsRightX, false);
        renderStructure(blockAccess, state, pos, iconBase, false);
    }

    public void renderOverlayLeft (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite iconOverlay) {
        renderOverlay(blockAccess, state, pos, iconOverlay, baseBoundsLeftY);
    }

    public void renderOverlayRight (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite iconOverlay) {
        renderOverlay(blockAccess, state, pos, iconOverlay, baseBoundsRightY);
    }

    public void renderOverlay (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite iconOverlay, double[][] baseBoundsY) {
        renderer.targetFaceGroup(true);
        renderer.state.setUVRotation(ChamRender.YPOS, renderer.state.rotateTransform);

        for (double[] bound : baseBoundsY) {
            renderer.setRenderBounds(bound);
            renderer.renderFace(ChamRender.FACE_YPOS, blockAccess, state, pos, iconOverlay);
        }

        renderer.state.clearUVRotation(ChamRender.YPOS);
        renderer.targetFaceGroup(false);
    }

    private void renderStructure (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite iconBase, boolean left) {
        renderFoot(blockAccess, state, pos, iconBase, left);
        renderLegs(blockAccess, state, pos, iconBase, left);
        renderBraces(blockAccess, state, pos, iconBase, left);
    }

    private void renderTableBox (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite iconBase, TextureAtlasSprite iconTrim,
                                 double[][] baseBoundsY, double[][] trimBoundsY, double[][] trimBoundsZ, double[][] trimBoundsX, boolean left) {
        EnumFacing xSide = left ? ChamRender.FACE_XNEG : ChamRender.FACE_XPOS;

        for (int i = 0; i < 2; i++)
            renderer.state.setUVRotation(i, renderer.state.rotateTransform);

        for (double[] bound : baseBoundsY)
            renderTableSurface(blockAccess, state, pos, iconBase, bound);

        for (double[] bound : trimBoundsY)
            renderTableSurface(blockAccess, state, pos, iconTrim, bound);

        renderer.targetFaceGroup(true);
        for (double[] bound : trimBoundsZ) {
            renderer.setRenderBounds(bound);
            renderer.renderFace(ChamRender.FACE_ZNEG, blockAccess, state, pos, iconTrim);
            renderer.renderFace(ChamRender.FACE_ZPOS, blockAccess, state, pos, iconTrim);
        }

        for (double[] bound : trimBoundsX) {
            renderer.setRenderBounds(bound);
            renderer.renderFace(xSide, blockAccess, state, pos, iconTrim);
        }
        renderer.targetFaceGroup(false);

        for (int i = 0; i < 2; i++)
            renderer.state.clearUVRotation(i);
    }

    private void renderTableSurface (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite icon, double[] bound) {
        renderer.setRenderBounds(bound);
        renderer.targetFaceGroup(true);
        renderer.renderFace(ChamRender.FACE_YPOS, blockAccess, state, pos, icon);
        renderer.targetFaceGroup(false);
        renderer.renderFace(ChamRender.FACE_YNEG, blockAccess, state, pos, icon);
    }

    private void renderFoot (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite icon, boolean left) {
        float oldColor = renderer.state.colorMultYPos;
        renderer.state.colorMultYPos = .9f;

        for (int i = 0; i < 2; i++)
            renderer.state.setUVRotation(i, (renderer.state.rotateTransform + ChamRenderState.ROTATE90) % 4);

        double xStart = left ? unit2 : 1 - unit2 - unit2;

        renderer.setRenderBounds(xStart, 0, 0, xStart + unit2, unit2, 1);
        for (int i = 0; i < 6; i++)
            renderer.renderFace(EnumFacing.getFront(i), blockAccess, state, pos, icon);

        for (int i = 0; i < 2; i++)
            renderer.state.clearUVRotation(i);

        renderer.state.colorMultYPos = oldColor;
    }

    private void renderLegs (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite icon, boolean left) {
        for (int i = 2; i < 6; i++)
            renderer.state.setUVRotation(i, ChamRenderState.ROTATE90);

        double xStart = left ? unit2 : 1 - unit2 - unit2;

        renderer.setRenderBounds(xStart, unit2, unit2, xStart + unit2, 1 - unit2, unit2 + unit2);
        for (int i = 2; i < 6; i++)
            renderer.renderFace(EnumFacing.getFront(i), blockAccess, state, pos, icon);

        renderer.setRenderBounds(xStart, unit2, 1 - unit2 - unit2, xStart + unit2, 1 - unit2, 1 - unit2);
        for (int i = 2; i < 6; i++)
            renderer.renderFace(EnumFacing.getFront(i), blockAccess, state, pos, icon);

        for (int i = 2; i < 6; i++)
            renderer.state.clearUVRotation(i);
    }

    private void renderBraces (IBlockAccess blockAccess, IBlockState state, BlockPos pos, TextureAtlasSprite icon, boolean left) {
        float oldColor = renderer.state.colorMultYPos;
        renderer.state.colorMultYPos = .85f;

        for (int i = 0; i < 2; i++)
            renderer.state.setUVRotation(i, renderer.state.rotateTransform);

        double xStart = left ? unit2 + unit2 : 0;
        double xStop = left ? 1 : 1 - unit2 - unit2;

        renderer.setRenderBounds(xStart, unit4, unit2, xStop, unit4 + unit2, unit2 + unit2);
        for (int i = 0; i < 4; i++)
            renderer.renderFace(EnumFacing.getFront(i), blockAccess, state, pos, icon);

        renderer.setRenderBounds(xStart, unit4, 1 - unit2 - unit2, xStop, unit4 + unit2, 1 - unit2);
        for (int i = 0; i < 4; i++)
            renderer.renderFace(EnumFacing.getFront(i), blockAccess, state, pos, icon);

        for (int i = 0; i < 2; i++)
            renderer.state.clearUVRotation(i);

        renderer.state.colorMultYPos = oldColor;
    }
}