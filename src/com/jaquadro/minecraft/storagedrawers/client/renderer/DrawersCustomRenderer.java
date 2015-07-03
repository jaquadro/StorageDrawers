package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class DrawersCustomRenderer extends DrawersRenderer
{
    private PanelBoxRenderer panelRenderer = new PanelBoxRenderer();

    @Override
    protected void renderBaseBlock (IBlockAccess world, TileEntityDrawers tile, int x, int y, int z, BlockDrawers block, RenderBlocks renderer) {
        BlockDrawersCustom custom = (BlockDrawersCustom)block;

        panelRenderer.setTrimWidth(block.trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setTrimIcon(Blocks.lapis_block.getIcon(0, 0));
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelIcon(Blocks.planks.getIcon(0, 0));

        if (ClientProxy.renderPass == 0) {
            for (int i = 0; i < 6; i++) {
                if (i == 2)
                    continue;
                panelRenderer.renderFaceTrim(i, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
                panelRenderer.renderInteriorTrim(i, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
                panelRenderer.renderFacePanel(i, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
            }

            panelRenderer.setPanelIcon(Blocks.planks.getIcon(0, 2));
            panelRenderer.setTrimDepth(block.trimDepth);

            panelRenderer.renderFaceTrim(2, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderInteriorTrim(2, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderFacePanel(2, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
        }
        else if (ClientProxy.renderPass == 1) {
            panelRenderer.setTrimDepth(block.trimDepth);
            panelRenderer.setTrimIcon(custom.getTrimShadowOverlay());
            panelRenderer.renderFaceTrim(2, world, block, x, y, z, 0, 0, 0, 1, 1, 1);

            panelRenderer.setPanelIcon(custom.getHandleOverlay());
            panelRenderer.renderFacePanel(2, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
            panelRenderer.setPanelIcon(custom.getFaceShadowOverlay());
            panelRenderer.renderFacePanel(2, world, block, x, y, z, 0, 0, 0, 1, 1, 1);
        }
    }

    @Override
    public int getRenderId () {
        return StorageDrawers.proxy.drawersCustomRenderID;
    }
}
