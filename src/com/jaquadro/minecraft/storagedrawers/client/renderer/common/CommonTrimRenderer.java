package com.jaquadro.minecraft.storagedrawers.client.renderer.common;

import com.jaquadro.minecraft.storagedrawers.block.BlockTrimCustom;
import com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.PanelBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class CommonTrimRenderer
{
    private PanelBoxRenderer panelRenderer = new PanelBoxRenderer();

    private double trimWidth;

    private RenderHelper start (IBlockAccess world, int x, int y, int z, BlockTrimCustom block) {
        trimWidth = .0625f;

        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);

        RenderHelper renderHelper = RenderHelper.instance;
        if (world != null)
            renderHelper.setColorAndBrightness(world, block, x, y, z);

        return renderHelper;
    }

    public void render (IBlockAccess world, int x, int y, int z, BlockTrimCustom block, IIcon iconSide, IIcon iconTrim) {
        RenderHelper renderHelper = start(world, x, y, z, block);

        panelRenderer.setTrimIcon(iconTrim);
        panelRenderer.setPanelIcon(iconSide);

        for (int i = 0; i < 6; i++) {
            panelRenderer.renderFacePanel(i, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderFaceTrim(i, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
        }
    }
}
