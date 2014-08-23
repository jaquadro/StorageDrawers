package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class DrawersRenderer implements ISimpleBlockRenderingHandler
{
    private ModularBoxRenderer boxRenderer = new ModularBoxRenderer();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockDrawers))
            return false;

        return renderWorldBlock(world, x, y, z, (BlockDrawers) block, modelId, renderer);
    }

    private boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, BlockDrawers block, int modelId, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);

        double unit = .0625;
        boxRenderer.setUnit(unit);
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        for (int i = 0; i < 6; i++)
            boxRenderer.setIcon(block.getIcon(world, x, y, z, i), i);

        boxRenderer.renderExterior(renderer, block, x, y, z, 0, 0, 0, 1, .5, 1, 0, ModularBoxRenderer.sideCut[meta % 6]);
        boxRenderer.renderExterior(renderer, block, x, y, z, 0, .5, 0, 1, 1, 1, 0, ModularBoxRenderer.sideCut[meta % 6]);

        double xMin = unit;
        double xMax = 1 - unit;
        double zMin = unit;
        double zMax = 1 - unit;

        switch (meta) {
            case 2: zMin = 0; zMax = unit; break;
            case 3: zMin = 1 - unit; zMax = 1; break;
            case 4: xMin = 0; xMax = unit; break;
            case 5: xMin = 1 - unit; xMax = 1; break;
        }

        boxRenderer.setUnit(0);
        boxRenderer.setInteriorIcon(block.getIcon(world, x, y, z, meta), ForgeDirection.OPPOSITES[meta % 6]);
        boxRenderer.renderInterior(renderer, block, x, y, z, xMin, unit, zMin, xMax, unit * 7, zMax, 0, ModularBoxRenderer.sideCut[meta % 6]);
        boxRenderer.renderInterior(renderer, block, x, y, z, xMin, unit * 9, zMin, xMax, 1 - unit, zMax, 0, ModularBoxRenderer.sideCut[meta % 6]);

        /*TileEntityDrawers tile = block.getTileEntity(world, x, y, z);
        if (tile != null) {
            ItemStack item = tile.getSingleItemStack(0);
            if (item != null) {
                EntityItem itemEnt = new EntityItem(Minecraft.getMinecraft().thePlayer.getEntityWorld(), 0, 0, 0, item);
                itemEnt.hoverStart = 0;
                RenderItem.renderInFrame = true;
                RenderManager.instance.renderEntityWithPosYaw(itemEnt, 0, 0, 0, 0, 0);
                RenderItem.renderInFrame = false;
            }
        }*/

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelId) {
        return false;
    }

    @Override
    public int getRenderId () {
        return 0;
    }
}
