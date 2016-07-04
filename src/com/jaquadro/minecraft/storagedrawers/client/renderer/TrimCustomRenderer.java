package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrimCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;
import com.jaquadro.minecraft.storagedrawers.client.renderer.common.CommonTrimRenderer;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class TrimCustomRenderer implements ISimpleBlockRenderingHandler
{
    private CommonTrimRenderer commonRender = new CommonTrimRenderer();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockTrimCustom))
            return false;

        return renderWorldBlock(world, x, y, z, (BlockTrimCustom) block, modelId, renderer);
    }

    private boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, BlockTrimCustom block, int modelId, RenderBlocks renderer) {
        TileEntityTrim tile = block.getTileEntity(world, x, y, z);
        if (tile == null)
            return false;

        ItemStack materialSide = tile.getMaterialSide();
        if (materialSide == null)
            materialSide = new ItemStack(block);

        ItemStack materialTrim = tile.getMaterialTrim();
        if (materialTrim == null)
            materialTrim = materialSide;

        IIcon trimIcon = Block.getBlockFromItem(materialTrim.getItem()).getIcon(4, materialTrim.getItemDamage());
        IIcon panelIcon = Block.getBlockFromItem(materialSide.getItem()).getIcon(4, materialSide.getItemDamage());

        if (trimIcon == null)
            trimIcon = block.getDefaultTrimIcon();
        if (panelIcon == null)
            panelIcon = block.getDefaultFaceIcon();

        commonRender.render(world, x, y, z, block, panelIcon, trimIcon);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelId) {
        return true;
    }

    @Override
    public int getRenderId () {
        return StorageDrawers.proxy.trimCustomRenderID;
    }
}
