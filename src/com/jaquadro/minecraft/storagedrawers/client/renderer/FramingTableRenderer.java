package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.renderer.common.CommonFramingRenderer;
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

        if ((meta & 0x8) == 0)
            framingRenderer.renderRight(world, x, y, z, framingTable);
        else
            framingRenderer.renderLeft(world, x, y, z, framingTable);

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
