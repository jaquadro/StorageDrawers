package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class DrawersRenderer implements ISimpleBlockRenderingHandler
{
    private static final double unit = .0625f;

    private ModularBoxRenderer boxRenderer = new ModularBoxRenderer();

    private double[] boxCoord = new double[6];

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
        TileEntityDrawers tile = block.getTileEntity(world, x, y, z);
        if (tile == null)
            return false;

        int side = tile.getDirection();
        int meta = world.getBlockMetadata(x, y, z);

        double unit = .0625;
        boxRenderer.setUnit(unit);
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        for (int i = 0; i < 6; i++)
            boxRenderer.setExteriorIcon(block.getIcon(world, x, y, z, i), i);

        boxRenderer.setCutIcon(block.getIconTrim(meta));
        boxRenderer.setInteriorIcon(block.getIconTrim(meta));

        renderExterior(block, x, y, z, side, renderer);

        if (tile.getStorageLevel() > 1 && StorageDrawers.config.cache.renderStorageUpgrades) {
            for (int i = 0; i < 6; i++)
                boxRenderer.setExteriorIcon(block.getOverlayIcon(world, x, y, z, i, tile.getStorageLevel()), i);

            boxRenderer.setCutIcon(block.getOverlayIconTrim(tile.getStorageLevel()));
            boxRenderer.setInteriorIcon(block.getOverlayIconTrim(tile.getStorageLevel()));

            renderExterior(block, x, y, z, side, renderer);
        }

        boxRenderer.setUnit(0);
        boxRenderer.setInteriorIcon(block.getIcon(world, x, y, z, side), ForgeDirection.OPPOSITES[side]);

        renderInterior(block, x, y, z, side, renderer);

        boxRenderer.setIcon(block.getIndicatorIcon(false));

        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            renderHandleIndicator(block, x, y, z, side, renderer, tile.getStatusLevel());

        /*if (tile.isLocked()) {
            boxRenderer.setExteriorIcon(Blocks.iron_block.getIcon(0, 0));
            boxRenderer.setExteriorIcon(block.getIconLockFace(), side);

            double depth = block.halfDepth ? 8 : 0;
            setCoord(boxCoord, 7 * unit, 7 * unit, (depth + .5) * unit, 9 * unit, 9 * unit, (depth + 1) * unit, side);
            boxRenderer.renderExterior(renderer, block, x, y, z, boxCoord[0], boxCoord[1], boxCoord[2], boxCoord[3], boxCoord[4], boxCoord[5], 0, 0);
        }*/

        return true;
    }

    private static final int[] cut = new int[] {
        ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZPOS,
        ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZNEG,
        ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_ZNEG | ModularBoxRenderer.CUT_ZPOS,
        ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZNEG | ModularBoxRenderer.CUT_ZPOS,
    };

    private static final float[][] indicatorsXY1 = new float[][] {
        { 6, 14, 10, 15 }
    };

    private static final float[][] indicatorsXY2 = new float[][] {
        { 6, 14, 10, 15 }, { 6, 6, 10, 7 }
    };

    private static final float[][] indicatorsXY4 = new float[][] {
        { 11, 14, 13, 15 }, { 11, 6, 13, 7 }, { 3, 14, 5, 15 }, { 3, 6, 5, 7 }
    };

    private void renderHandleIndicator (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer, int level) {
        if (level <= 0 || side < 2 || side > 5)
            return;

        TileEntityDrawers tile = block.getTileEntity(renderer.blockAccess, x, y, z);

        double depth = block.halfDepth ? 8 : 0;
        int count = 0;
        float[][] xySet = null;
        if (block.drawerCount == 1) {
            count = 1;
            xySet = indicatorsXY1;
        }
        else if (block.drawerCount == 2 || block instanceof BlockCompDrawers) {
            count = (block instanceof BlockCompDrawers) ? 1 : 2;
            xySet = indicatorsXY2;
        }
        else if (block.drawerCount == 4) {
            count = 4;
            xySet = indicatorsXY4;
        }

        for (int i = 0; i < count; i++) {
            float[] xy = xySet[i];
            setCoord(boxCoord, xy[0] * unit, xy[1] * unit, (depth + .95) * unit, xy[2] * unit, xy[3] * unit, (depth + 1) * unit, side);

            IDrawer drawer = tile.getDrawer(i);
            int iconIndex = 0;
            if (level == 2)
                iconIndex = (drawer.getMaxCapacity() > 0 && (float)drawer.getStoredItemCount() / drawer.getMaxCapacity() > .75) ? 1 : iconIndex;
            if (level >= 1)
                iconIndex = (drawer.getMaxCapacity() > 0 && drawer.getStoredItemCount() == drawer.getMaxCapacity()) ? 2 : iconIndex;

            boxRenderer.setExteriorIcon(block.getIndicatorIcon(iconIndex));
            boxRenderer.renderExterior(renderer, block, x, y, z, boxCoord[0], boxCoord[1], boxCoord[2], boxCoord[3], boxCoord[4], boxCoord[5], 0, cut[side - 2]);
        }
    }

    private void setCoord (double[] coords, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        coords[0] = xMin;
        coords[1] = yMin;
        coords[2] = zMin;
        coords[3] = xMax;
        coords[4] = yMax;
        coords[5] = zMax;
    }

    private void setCoord (double[] coords, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, int side) {
        setCoord(coords, xMin, yMin, zMin, xMax, yMax, zMax);
        transformCoord(coords, side);
    }

    private void transformCoord (double[] coords, int side) {
        double tmpX, tmpZ;

        switch (side) {
            case 2:
                return;
            case 3:
                tmpX = coords[0];
                tmpZ = coords[2];
                coords[0] = 1 - coords[3];
                coords[3] = 1 - tmpX;
                coords[2] = 1 - coords[5];
                coords[5] = 1 - tmpZ;
                return;
            case 4:
                tmpX = coords[0];
                coords[0] = coords[2];
                coords[2] = 1 - coords[3];
                coords[3] = coords[5];
                coords[5] = 1 - tmpX;
                return;
            case 5:
                tmpX = coords[0];
                tmpZ = coords[2];
                coords[0] = 1 - coords[5];
                coords[2] = tmpX;
                tmpX = coords[3];
                coords[3] = 1 - tmpZ;
                coords[5] = tmpX;
                return;
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
