package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersBase;
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
    private static final double unit = .0625f;

    private ModularBoxRenderer boxRenderer = new ModularBoxRenderer();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockDrawers))
            return;

        renderInventoryBlock((BlockDrawers) block, metadata, modelId, renderer);
    }

    private void renderInventoryBlock (BlockDrawers block, int metadata, int modelId, RenderBlocks renderer) {
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
        TileEntityDrawersBase tile = block.getTileEntity(world, x, y, z);
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

        boxRenderer.setIcon(block.getIndicatorIcon(false));
        /*if (block.drawerCount == 2)
            boxRenderer.renderExterior(renderer, block, x, y, z, unit * 14, unit * 1, unit * 15, unit * 15, unit * 15, unit * 15.5, 0,
                ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_ZNEG);
        else
            boxRenderer.renderExterior(renderer, block, x, y, z, unit * 7, unit * 1, unit * 15, unit * 9, unit * 15, unit * 15.5, 0,
                ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_ZNEG);*/

        /*if (block.drawerCount == 2) {
            boxRenderer.renderExterior(renderer, block, x, y, z, unit * 6, unit * 14, unit * 15, unit * 10, unit * 15, unit * 15.05, 0,
                ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZNEG);
            boxRenderer.renderExterior(renderer, block, x, y, z, unit * 6, unit * 6, unit * 15, unit * 10, unit * 7, unit * 15.05, 0,
                ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZNEG);
        }*/

        renderHandleIndicator(block, x, y, z, side, renderer);

        /*boxRenderer.renderExterior(renderer, block, x, y, z, unit * 14, unit * 14, unit * 15, unit * 15, unit * 15, unit * 16, 0,
            ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_ZNEG);
        if (block.drawerCount == 4)
            boxRenderer.renderExterior(renderer, block, x, y, z, unit * 1, unit * 14, unit * 15, unit * 2, unit * 15, unit * 16, 0,
                ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZNEG);*/

        return true;
    }

    //private static double[] handleIndicatorX = new double[] { unit * 6, unit * 10, unit * 3, unit * 4, unit * 11, unit * 12 };
    //private static double[] handleIndicatorXN = new double[] { 1 - unit * 6, 1 - unit * 10, 1 - unit * 3, 1 - unit * 4, 1 - unit * 11, 1 - unit * 12 };
    //private static double[] handleIndicatorZ = new double[] { unit * 15.05, unit * 15.05, unit * 15.05, unit * 15.05, unit * 15.05, unit * 15.05 };
    //private static double[] handleIndicatorZN = new double[] { unit * .95, unit * .95, unit * .95, unit * .95, unit * .96, unit * .95 };

    private void renderHandleIndicator (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer) {
        TileEntityDrawersBase tile = block.getTileEntity(renderer.blockAccess, x, y, z);

        double depth = block.halfDepth ? .5 : 1;
        int cut2 = ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZPOS;
        int cut3 = ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZNEG;
        int cut4 = ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_ZNEG | ModularBoxRenderer.CUT_ZPOS;
        int cut5 = ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZNEG | ModularBoxRenderer.CUT_ZPOS;

        if (block.drawerCount == 2) {
            int iconIndex0 = (tile.getItemCapacity(0) > 0 && tile.getItemCount(0) == tile.getItemCapacity(0)) ? 2
                : (tile.getItemCapacity(0) > 0 && tile.getItemCount(0) / tile.getItemCapacity(0) > .75) ? 1 : 0;
            int iconIndex1 = (tile.getItemCapacity(1) > 0 && tile.getItemCount(1) == tile.getItemCapacity(1)) ? 2
                : (tile.getItemCapacity(1) > 0 && tile.getItemCount(1) / tile.getItemCapacity(1) > .75) ? 1 : 0;

            switch (side) {
                case 2:
                    boxRenderer.setExteriorIcon(block.getIndicatorIcon(iconIndex0));
                    boxRenderer.renderExterior(renderer, block, x, y, z, 1 - unit * 10, unit * 14, 1 - depth + unit * .95, 1 - unit * 6, unit * 15, 1 - depth + unit * 1, 0, cut2);
                    boxRenderer.setExteriorIcon(block.getIndicatorIcon(iconIndex1));
                    boxRenderer.renderExterior(renderer, block, x, y, z, 1 - unit * 10, unit * 6, 1 - depth + unit * .95, 1 - unit * 6, unit * 7, 1 - depth + unit * 1, 0, cut2);
                    break;
                case 3:
                    boxRenderer.setExteriorIcon(block.getIndicatorIcon(iconIndex0));
                    boxRenderer.renderExterior(renderer, block, x, y, z, unit * 6, unit * 14, depth - unit * 1, unit * 10, unit * 15, depth - unit * .95, 0, cut3);
                    boxRenderer.setExteriorIcon(block.getIndicatorIcon(iconIndex1));
                    boxRenderer.renderExterior(renderer, block, x, y, z, unit * 6, unit * 6, depth - unit * 1, unit * 10, unit * 7, depth - unit * .95, 0, cut3);
                    break;
                case 4:
                    boxRenderer.setExteriorIcon(block.getIndicatorIcon(iconIndex0));
                    boxRenderer.renderExterior(renderer, block, x, y, z, 1 - depth + unit * .95, unit * 14, 1 - unit * 10, 1 - depth + unit * 1, unit * 15, 1 - unit * 6, 0, cut4);
                    boxRenderer.setExteriorIcon(block.getIndicatorIcon(iconIndex1));
                    boxRenderer.renderExterior(renderer, block, x, y, z, 1 - depth + unit * .95, unit * 6, 1 - unit * 10, 1 - depth + unit * 1, unit * 7, 1 - unit * 6, 0, cut4);
                    break;
                case 5:
                    boxRenderer.setExteriorIcon(block.getIndicatorIcon(iconIndex0));
                    boxRenderer.renderExterior(renderer, block, x, y, z, depth - unit * 1, unit * 14, unit * 6, depth - unit * .95, unit * 15, unit * 10, 0, cut5);
                    boxRenderer.setExteriorIcon(block.getIndicatorIcon(iconIndex1));
                    boxRenderer.renderExterior(renderer, block, x, y, z, depth - unit * 1, unit * 6, unit * 6, depth - unit * .95, unit * 7, unit * 10, 0, cut5);
                    break;
            }
        }
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
