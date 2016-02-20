package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.common.CommonDrawerRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class DrawersCustomRenderer extends DrawersRenderer
{
    private CommonDrawerRenderer commonRender = new CommonDrawerRenderer();

    @Override
    protected void renderBaseBlock (IBlockAccess world, TileEntityDrawers tile, int x, int y, int z, BlockDrawers block, RenderBlocks renderer) {
        BlockDrawersCustom custom = (BlockDrawersCustom)block;

        ItemStack materialSide = tile.getMaterialSide();
        if (materialSide == null)
            materialSide = new ItemStack(block);

        ItemStack materialFront = tile.getMaterialFront();
        if (materialFront == null)
            materialFront = materialSide;

        ItemStack materialTrim = tile.getMaterialTrim();
        if (materialTrim == null)
            materialTrim = materialSide;

        IIcon trimIcon = Block.getBlockFromItem(materialTrim.getItem()).getIcon(4, materialTrim.getItemDamage());
        IIcon panelIcon = Block.getBlockFromItem(materialSide.getItem()).getIcon(4, materialSide.getItemDamage());
        IIcon frontIcon = Block.getBlockFromItem(materialFront.getItem()).getIcon(4, materialFront.getItemDamage());

        if (trimIcon == null)
            trimIcon = custom.getDefaultTrimIcon();
        if (panelIcon == null)
            panelIcon = custom.getDefaultFaceIcon();
        if (frontIcon == null)
            frontIcon = custom.getDefaultFaceIcon();

        if (ClientProxy.renderPass == 0)
            commonRender.renderBasePass(world, x, y, z, custom, tile.getDirection(), panelIcon, trimIcon, frontIcon);
        else if (ClientProxy.renderPass == 1)
            commonRender.renderOverlayPass(world, x, y, z, custom, tile.getDirection(), trimIcon, frontIcon);
    }

    @Override
    public int getRenderId () {
        return StorageDrawers.proxy.drawersCustomRenderID;
    }
}
