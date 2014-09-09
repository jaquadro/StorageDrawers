package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
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
import org.lwjgl.opengl.GL11;

public class DrawersRenderer implements ISimpleBlockRenderingHandler
{
    private ModularBoxRenderer boxRenderer = new ModularBoxRenderer();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockDrawers))
            return;

        renderInventoryBlock((BlockDrawers) block, metadata, modelId, renderer);
    }

    private void renderInventoryBlock (BlockDrawers block, int metadata, int modelId, RenderBlocks renderer) {
        double unit = .0625;
        int side = 4;

        boxRenderer.setUnit(unit);
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        for (int i = 0; i < 6; i++)
            boxRenderer.setIcon(block.getIcon(i, metadata), i);

        GL11.glRotatef(90, 0, 1, 0);
        GL11.glTranslatef(-.5f, -.5f, -.5f);

        renderExterior(block, 0, 0, 0,side, renderer);

        boxRenderer.setUnit(0);
        boxRenderer.setInteriorIcon(block.getIcon(side, metadata), ForgeDirection.OPPOSITES[side]);

        renderInterior(block, 0, 0, 0, side, renderer);

        GL11.glTranslatef(.5f, .5f, .5f);
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockDrawers))
            return false;

        return renderWorldBlock(world, x, y, z, (BlockDrawers) block, modelId, renderer);
    }

    private boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, BlockDrawers block, int modelId, RenderBlocks renderer) {
        TileEntityDrawers tile = block.getTileEntity(world, x, y, z);
        if (tile == null)
            return false;

        int side = tile.getDirection();

        double unit = .0625;
        boxRenderer.setUnit(unit);
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        for (int i = 0; i < 6; i++)
            boxRenderer.setIcon(block.getIcon(world, x, y, z, i), i);

        renderExterior(block, x, y, z, side, renderer);

        if (tile.getLevel() > 1 && StorageDrawers.config.cache.renderStorageUpgrades) {
            for (int i = 0; i < 6; i++)
                boxRenderer.setIcon(block.getOverlayIcon(world, x, y, z, i, tile.getLevel()), i);

            renderExterior(block, x, y, z, side, renderer);
        }

        boxRenderer.setUnit(0);
        //for (int i = 0; i < 6; i++)
        //    boxRenderer.setIcon(block.getIcon(world, x, y, z, i), i);
        boxRenderer.setInteriorIcon(block.getIcon(world, x, y, z, side), ForgeDirection.OPPOSITES[side]);

        renderInterior(block, x, y, z, side, renderer);

        return true;
    }

    private void renderExterior (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer) {
        double depth = block.halfDepth ? .5 : 1;
        double xMin = 0, xMax = 0, zMin = 0, zMax = 0;

        switch (side) {
            case 2:
                xMin = 0; xMax = 1;
                zMin = 1 - depth; zMax = 1;
                break;
            case 3:
                xMin = 0; xMax = 1;
                zMin = 0; zMax = depth;
                break;
            case 4:
                xMin = 1 - depth; xMax = 1;
                zMin = 0; zMax = 1;
                break;
            case 5:
                xMin = 0; xMax = depth;
                zMin = 0; zMax = 1;
                break;
        }

        boxRenderer.renderExterior(renderer, block, x, y, z, xMin, 0, zMin, xMax, 1, zMax, 0, ModularBoxRenderer.sideCut[side]);

        /*if (block.drawerCount == 2) {
            boxRenderer.renderExterior(renderer, block, x, y, z, xMin, 0, zMin, xMax, .5, zMax, 0, ModularBoxRenderer.sideCut[side]);
            boxRenderer.renderExterior(renderer, block, x, y, z, xMin, .5, zMin, xMax, 1, zMax, 0, ModularBoxRenderer.sideCut[side]);
        }
        else if (block.drawerCount == 4) {
            switch (side) {
                case 2:
                case 3:
                    boxRenderer.renderExterior(renderer, block, x, y, z, 0, 0, zMin, .5, .5, zMax, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderExterior(renderer, block, x, y, z, 0, .5, zMin, .5, 1, zMax, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderExterior(renderer, block, x, y, z, .5, 0, zMin, 1, .5, zMax, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderExterior(renderer, block, x, y, z, .5, .5, zMin, 1, 1, zMax, 0, ModularBoxRenderer.sideCut[side]);
                    break;
                case 4:
                case 5:
                    boxRenderer.renderExterior(renderer, block, x, y, z, xMin, 0, 0, xMax, .5, .5, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderExterior(renderer, block, x, y, z, xMin, .5, 0, xMax, 1, .5, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderExterior(renderer, block, x, y, z, xMin, 0, .5, xMax, .5, 1, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderExterior(renderer, block, x, y, z, xMin, .5, .5, xMax, 1, 1, 0, ModularBoxRenderer.sideCut[side]);
                    break;
            }
        }*/
    }

    private void renderInterior (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer) {
        double unit = .0625;
        double depth = block.halfDepth ? .5 : 1;
        double xMin = 0, xMax = 0, zMin = 0, zMax = 0;

        switch (side) {
            case 2:
                xMin = unit; xMax = 1 - unit;
                zMin = 1 - depth; zMax = 1 - depth + unit;
                break;
            case 3:
                xMin = unit; xMax = 1 - unit;
                zMin = depth - unit; zMax = depth;
                break;
            case 4:
                xMin = 1 - depth; xMax = 1 - depth + unit;
                zMin = unit; zMax = 1 - unit;
                break;
            case 5:
                xMin = depth - unit; xMax = depth;
                zMin = unit; zMax = 1 - unit;
                break;
        }

        boxRenderer.renderInterior(renderer, block, x, y, z, xMin, unit, zMin, xMax, 1 - unit, zMax, 0, ModularBoxRenderer.sideCut[side]);

        /*if (block.drawerCount == 2) {
            boxRenderer.renderInterior(renderer, block, x, y, z, xMin, unit, zMin, xMax, unit * 7, zMax, 0, ModularBoxRenderer.sideCut[side]);
            boxRenderer.renderInterior(renderer, block, x, y, z, xMin, unit * 9, zMin, xMax, 1 - unit, zMax, 0, ModularBoxRenderer.sideCut[side]);
        }
        else if (block.drawerCount == 4) {
            switch (side) {
                case 2:
                case 3:
                    boxRenderer.renderInterior(renderer, block, x, y, z, unit, unit, zMin, .5 - unit, unit * 7, zMax, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderInterior(renderer, block, x, y, z, unit, unit * 9, zMin, .5 - unit, 1 - unit, zMax, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderInterior(renderer, block, x, y, z, .5 + unit, unit, zMin, 1 - unit, unit * 7, zMax, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderInterior(renderer, block, x, y, z, .5 + unit, unit * 9, zMin, 1 - unit, 1 - unit, zMax, 0, ModularBoxRenderer.sideCut[side]);
                    break;
                case 4:
                case 5:
                    boxRenderer.renderInterior(renderer, block, x, y, z, xMin, unit, unit, xMax, unit * 7, .5 - unit, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderInterior(renderer, block, x, y, z, xMin, unit * 9, unit, xMax, 1 - unit, .5 - unit, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderInterior(renderer, block, x, y, z, xMin, unit, .5 + unit, xMax, unit * 7, 1 - unit, 0, ModularBoxRenderer.sideCut[side]);
                    boxRenderer.renderInterior(renderer, block, x, y, z, xMin, unit * 9, .5 + unit, xMax, 1 - unit, 1 - unit, 0, ModularBoxRenderer.sideCut[side]);
                    break;
            }
        }*/
    }

    @Override
    public boolean shouldRender3DInInventory (int modelId) {
        return true;
    }

    @Override
    public int getRenderId () {
        return StorageDrawers.proxy.drawersRenderID;
    }
}
