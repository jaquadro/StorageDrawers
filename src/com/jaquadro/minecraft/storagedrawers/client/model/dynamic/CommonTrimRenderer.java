package com.jaquadro.minecraft.storagedrawers.client.model.dynamic;

import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.render.helpers.ModularBoxRenderer;
import com.jaquadro.minecraft.chameleon.render.helpers.PanelBoxRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class CommonTrimRenderer
{
    private final PanelBoxRenderer panelRenderer;

    public CommonTrimRenderer (ChamRender renderer) {
        this.panelRenderer = new PanelBoxRenderer(renderer);
    }

    private void start () {
        panelRenderer.setTrimWidth(.0625f);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);
    }

    public void render (IBlockAccess world, IBlockState state, BlockPos pos, TextureAtlasSprite iconSide, TextureAtlasSprite iconTrim) {
        start();

        panelRenderer.setTrimIcon(iconTrim);
        panelRenderer.setPanelIcon(iconSide);

        for (EnumFacing dir : EnumFacing.VALUES) {
            panelRenderer.renderFacePanel(dir, world, state, pos, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderFaceTrim(dir, world, state, pos, 0, 0, 0, 1, 1, 1);
        }
    }
}
