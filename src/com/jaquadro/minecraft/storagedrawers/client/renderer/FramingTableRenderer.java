package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.renderer.common.CommonFramingRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class FramingTableRenderer implements ISimpleBlockRenderingHandler
{
    private CommonFramingRenderer framingRenderer = new CommonFramingRenderer();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockFramingTable))
            return false;

        BlockFramingTable framingTable = (BlockFramingTable)block;
        int meta = world.getBlockMetadata(x, y, z);
        int side = meta & 0x7;
        boolean right = (meta & 0x08) == 0;

        if (side == 2 || side == 3)
            right = !right;

        RenderHelper.instance.state.setRotateTransform(side, RenderHelper.ZNEG);
        RenderHelper.instance.state.setUVRotation(RenderHelper.YPOS, RenderHelper.instance.state.rotateTransform);

        if (ClientProxy.renderPass == 0) {
            if (right)
                framingRenderer.renderRight(world, x, y, z, framingTable);
            else
                framingRenderer.renderLeft(world, x, y, z, framingTable);
        }
        else if (ClientProxy.renderPass == 1) {
            if (right)
                framingRenderer.renderOverlayRight(world, x, y, z, framingTable);
            else
                framingRenderer.renderOverlayLeft(world, x, y, z, framingTable);
        }

        RenderHelper.instance.state.clearRotateTransform();
        RenderHelper.instance.state.clearUVRotation(RenderHelper.YPOS);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelId) {
        return true;
    }

    @Override
    public int getRenderId () {
        return StorageDrawers.proxy.framingTableRenderID;
    }
}
