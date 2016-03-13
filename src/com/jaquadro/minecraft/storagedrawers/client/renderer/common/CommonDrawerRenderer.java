package com.jaquadro.minecraft.storagedrawers.client.renderer.common;
/*
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.PanelBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelperState;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class CommonDrawerRenderer
{
    private PanelBoxRenderer panelRenderer = new PanelBoxRenderer();

    private double depth;
    private double trimWidth;
    private double trimDepth;

    private static double unit7 = 0.4375;
    private static double unit9 = 0.5625;

    private RenderHelper start (IBlockAccess world, int x, int y, int z, BlockDrawersCustom block, int direction) {
        depth = block.halfDepth ? .5 : 0;
        trimWidth = block.getTrimWidth();
        trimDepth = block.getTrimDepth();
        unit7 = 0.4375;
        unit9 = 0.5625;

        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);

        RenderHelper renderHelper = RenderHelper.instance;
        if (world != null)
            renderHelper.setColorAndBrightness(world, block, x, y, z);

        renderHelper.state.setRotateTransform(RenderHelper.ZNEG, direction);
        renderHelper.state.setUVRotation(RenderHelper.YPOS, RenderHelperState.ROTATION_BY_FACE_FACE[RenderHelper.ZNEG][direction]);

        return renderHelper;
    }

    private void end () {
        RenderHelper renderHelper = RenderHelper.instance;

        renderHelper.state.clearRotateTransform();
        renderHelper.state.clearUVRotation(RenderHelper.YPOS);
    }

    public void renderBasePass (IBlockAccess world, int x, int y, int z, BlockDrawersCustom block, int direction, IIcon iconSide, IIcon iconTrim, IIcon iconFront) {
        RenderHelper renderHelper = start(world, x, y, z, block, direction);

        panelRenderer.setTrimIcon(iconTrim);
        panelRenderer.setPanelIcon(iconSide);

        for (int i = 0; i < 6; i++) {
            if (i != RenderHelper.ZNEG)
                panelRenderer.renderFacePanel(i, world, block, x, y, z, 0, 0, depth, 1, 1, 1);
            panelRenderer.renderFaceTrim(i, world, block, x, y, z, 0, 0, depth, 1, 1, 1);
        }

        panelRenderer.setTrimDepth(trimDepth);
        panelRenderer.renderInteriorTrim(RenderHelper.ZNEG, world, block, x, y, z, 0, 0, depth, 1, 1, 1);

        if (block.drawerCount == 1) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconFront);
        }
        else if (block.drawerCount == 2) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, 1 - trimWidth, unit7, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconFront);
            renderHelper.setRenderBounds(trimWidth, unit9, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconFront);

            renderHelper.setRenderBounds(trimWidth, unit7, depth + trimDepth, 1 - trimWidth, unit9, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconTrim);
        }
        else if (block.drawerCount == 4) {
            renderHelper.state.flipTexture = true;
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, unit7, unit7, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconFront);
            renderHelper.setRenderBounds(trimWidth, unit9, depth + trimDepth, unit7, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconFront);
            renderHelper.setRenderBounds(unit9, trimWidth, depth + trimDepth, 1 - trimWidth, unit7, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconFront);
            renderHelper.setRenderBounds(unit9, unit9, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconFront);

            renderHelper.setRenderBounds(trimWidth, unit7, depth + trimDepth, unit7, unit9, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconTrim);
            renderHelper.setRenderBounds(unit9, unit7, depth + trimDepth, 1 - trimWidth, unit9, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconTrim);
            renderHelper.setRenderBounds(unit7, trimWidth, depth + trimDepth, unit9, unit7, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconTrim);
            renderHelper.setRenderBounds(unit7, unit9, depth + trimDepth, unit9, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconTrim);
            renderHelper.setRenderBounds(unit7, unit7, depth + trimDepth, unit9, unit9, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, iconTrim);
            renderHelper.state.flipTexture = false;
        }

        end();
    }

    public void renderOverlayPass (IBlockAccess world, int x, int y, int z, BlockDrawersCustom block, int direction, IIcon iconTrim, IIcon iconFront) {
        RenderHelper renderHelper = start(world, x, y, z, block, direction);

        IIcon trimShadow = block.getTrimShadowOverlay(iconTrim == iconFront);

        panelRenderer.setTrimIcon(trimShadow);
        panelRenderer.renderFaceTrim(RenderHelper.ZNEG, world, block, x, y, z, 0, 0, depth, 1, 1, 1);

        if (block.drawerCount == 1) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getHandleOverlay());
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getFaceShadowOverlay());
        }
        else if (block.drawerCount == 2) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, 1 - trimWidth, unit7, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getHandleOverlay());
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getFaceShadowOverlay());

            renderHelper.setRenderBounds(trimWidth, unit9, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getHandleOverlay());
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getFaceShadowOverlay());

            renderHelper.setRenderBounds(trimWidth, unit7, depth + trimDepth, 1 - trimWidth, unit9, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, trimShadow);
        }
        else if (block.drawerCount == 4) {
            renderHelper.setRenderBounds(trimWidth, trimWidth, depth + trimDepth, unit7, unit7, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getHandleOverlay());
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getFaceShadowOverlay());

            renderHelper.setRenderBounds(trimWidth, unit9, depth + trimDepth, unit7, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getHandleOverlay());
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getFaceShadowOverlay());

            renderHelper.setRenderBounds(unit9, trimWidth, depth + trimDepth, 1 - trimWidth, unit7, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getHandleOverlay());
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getFaceShadowOverlay());

            renderHelper.setRenderBounds(unit9, unit9, depth + trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getHandleOverlay());
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, block.getFaceShadowOverlay());

            renderHelper.setRenderBounds(trimWidth, unit7, depth + trimDepth, unit7, unit9, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, trimShadow);
            renderHelper.setRenderBounds(unit9, unit7, depth + trimDepth, 1 - trimWidth, unit9, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, trimShadow);
            renderHelper.setRenderBounds(unit7, trimWidth, depth + trimDepth, unit9, unit7, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, trimShadow);
            renderHelper.setRenderBounds(unit7, unit9, depth + trimDepth, unit9, 1 - trimWidth, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, trimShadow);
            renderHelper.setRenderBounds(unit7, unit7, depth + trimDepth, unit9, unit9, 1);
            renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, trimShadow);
        }
        else
            RenderHelper.instance.renderEmptyPlane(x, y, z);

        end();
    }
}
*/