package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
/*
public class DrawersRenderer //implements ISimpleBlockRenderingHandler
{
    private static final double unit = .0625f;

    private RenderHelper renderHelper = new RenderHelper();
    private ModularBoxRenderer boxRenderer = new ModularBoxRenderer();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelId, RenderBlocks renderer) {
        return;
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

        renderBaseBlock(world, tile, x, y, z, block, renderer);

        int side = tile.getDirection();
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            renderIndicator(block, x, y, z, side, renderer, tile.getEffectiveStatusLevel());
        if (StorageDrawers.config.cache.enableLockUpgrades)
            renderLock(block, x, y, z, side, renderer, tile.isLocked(LockAttribute.LOCK_POPULATED), tile.getOwner() != null);
        if (StorageDrawers.config.cache.enableVoidUpgrades)
            renderVoid(block, x, y, z, side, renderer, tile.isVoid());
        renderShroud(block, x, y, z, side, renderer, tile.isShrouded());

        return true;
    }

    protected void renderBaseBlock (IBlockAccess world, TileEntityDrawers tile, int x, int y, int z, BlockDrawers block, RenderBlocks renderer) {
        int side = tile.getDirection();
        int meta = world.getBlockMetadata(x, y, z);

        RenderHelper.instance.state.setUVRotation(RenderHelper.YPOS, RenderHelperState.ROTATION_BY_FACE_FACE[RenderHelper.ZNEG][side]);

        boxRenderer.setUnit(block.getTrimWidth());
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        for (int i = 0; i < 6; i++)
            boxRenderer.setExteriorIcon(block.getIcon(world, x, y, z, i), i);

        boxRenderer.setCutIcon(block.getIconTrim(meta));
        boxRenderer.setInteriorIcon(block.getIconTrim(meta));

        renderExterior(block, x, y, z, side, renderer);

        RenderHelper.instance.state.clearUVRotation(RenderHelper.YPOS);

        int maxStorageLevel = tile.getMaxStorageLevel();
        if (maxStorageLevel > 1 && StorageDrawers.config.cache.renderStorageUpgrades && !tile.shouldHideUpgrades()) {
            for (int i = 0; i < 6; i++)
                boxRenderer.setExteriorIcon(block.getOverlayIcon(world, x, y, z, i, maxStorageLevel), i);

            boxRenderer.setCutIcon(block.getOverlayIconTrim(maxStorageLevel));
            boxRenderer.setInteriorIcon(block.getOverlayIconTrim(maxStorageLevel));

            renderExterior(block, x, y, z, side, renderer);
        }

        boxRenderer.setUnit(0);
        boxRenderer.setInteriorIcon(block.getIcon(world, x, y, z, side), ForgeDirection.OPPOSITES[side]);

        renderInterior(block, x, y, z, side, renderer);

        if (renderer.overrideBlockTexture != null && renderer.overrideBlockTexture.getIconName().startsWith("destroy_stage"))
            return;

        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            renderIndicator(block, x, y, z, side, renderer, tile.getEffectiveStatusLevel());
        if (StorageDrawers.config.cache.enableLockUpgrades)
            renderLock(block, x, y, z, side, renderer, tile.isLocked(LockAttribute.LOCK_POPULATED), tile.getOwner() != null);
        if (StorageDrawers.config.cache.enableVoidUpgrades)
            renderVoid(block, x, y, z, side, renderer, tile.isVoid());
        if (StorageDrawers.config.cache.enableTape)
            renderTape(block, x, y, z, side, renderer, tile.isSealed());

        renderShroud(block, x, y, z, side, renderer, tile.isShrouded());
    }

    private void renderLock (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer, boolean locked, boolean owned) {
        if (!locked && !owned)
            return;

        double depth = block.halfDepth ? .5 : 1;
        IIcon iconLock = block.getLockIcon(locked, owned);

        RenderHelper.instance.setRenderBounds(0.46875, 0.9375, 0, 0.53125, 1, depth + .005);
        RenderHelper.instance.state.setRotateTransform(RenderHelper.ZPOS, side);
        RenderHelper.instance.renderPartialFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconLock, 0, 0, 1, 1);
        RenderHelper.instance.state.clearRotateTransform();
    }

    private void renderVoid (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer, boolean voided) {
        if (!voided)
            return;

        double depth = block.halfDepth ? .5 : 1;
        IIcon iconVoid = block.getVoidIcon();

        RenderHelper.instance.setRenderBounds(1 - .0625, 0.9375, 0, 1, 1, depth + .005);
        RenderHelper.instance.state.setRotateTransform(RenderHelper.ZPOS, side);
        RenderHelper.instance.renderPartialFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconVoid, 0, 0, 1, 1);
        RenderHelper.instance.state.clearRotateTransform();
    }

    private void renderTape (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer, boolean taped) {
        if (!taped)
            return;

        double depth = block.halfDepth ? .5 : 1;
        IIcon iconTape = block.getTapeIcon();

        RenderHelper.instance.setRenderBounds(0, 0, 0, 1, 1, depth + .005);
        RenderHelper.instance.state.setRotateTransform(RenderHelper.ZPOS, side);
        RenderHelper.instance.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconTape);
        RenderHelper.instance.state.clearRotateTransform();
    }

    private static final int[] cut = new int[] {
        ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZPOS,
        ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZNEG,
        ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XPOS | ModularBoxRenderer.CUT_ZNEG | ModularBoxRenderer.CUT_ZPOS,
        ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_XNEG | ModularBoxRenderer.CUT_ZNEG | ModularBoxRenderer.CUT_ZPOS,
    };

    private static final float[][] drawerXYWH1 = new float[][] {
        { 0, 0, 16, 16 },
    };

    private static final float[][] drawerXYWH2 = new float[][] {
        { 0, 8, 16, 8 }, { 0, 0, 16, 8 },
    };

    private static final float[][] drawerXYWH4 = new float[][] {
        { 0, 8, 8, 8 }, { 0, 0, 8, 8 }, { 8, 8, 8, 8 }, { 8, 0, 8, 8 },
    };

    private static final float[][] drawerXYWH3 = new float[][] {
        { 0, 8, 16, 8 }, { 0, 0, 8, 8 }, { 8, 0, 8, 8 },
    };

    private void renderShroud (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer, boolean shrouded) {
        if (!shrouded || side < 2 || side > 5)
            return;

        TileEntityDrawers tile = block.getTileEntity(renderer.blockAccess, x, y, z);

        double depth = block.halfDepth ? 8 : 16;
        double depthAdj = block.getTrimDepth() * 16;

        int count = 0;
        float w = 2;
        float h = 2;

        float[][] xywhSet = null;
        if (block.drawerCount == 1) {
            count = 1;
            w = 4;
            h = 4;
            xywhSet = drawerXYWH1;
        }
        else if (block.drawerCount == 2) {
            count = 2;
            xywhSet = drawerXYWH2;
        }
        else if (block.drawerCount == 3) {
            count = 3;
            xywhSet = drawerXYWH3;
        }
        else if (block.drawerCount == 4) {
            count = 4;
            xywhSet = drawerXYWH4;
        }

        IIcon icon = block.getIconTrim(renderer.blockAccess.getBlockMetadata(x, y, z));

        for (int i = 0; i < count; i++) {
            IDrawer drawer = tile.getDrawer(i);
            if (drawer == null || drawer.isEmpty())
                continue;

            float[] xywh = xywhSet[i];
            float subX = xywh[0] + (xywh[2] - w) / 2;
            float subY = xywh[1] + (xywh[3] - h) / 2;

            RenderHelper.instance.setRenderBounds(subX * unit, subY * unit, 0, (subX + w) * unit, (subY + h) * unit, (depth - depthAdj + .05) * unit);
            RenderHelper.instance.state.setRotateTransform(RenderHelper.ZPOS, side);
            RenderHelper.instance.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, icon);
            RenderHelper.instance.state.clearRotateTransform();
        }
    }

    private void renderIndicator (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer, int level) {
        if (level <= 0 || side < 2 || side > 5)
            return;

        TileEntityDrawers tile = block.getTileEntity(renderer.blockAccess, x, y, z);

        double depth = block.halfDepth ? 8 : 16;
        double depthAdj = block.getTrimDepth() * 16;

        int count = 0;
        float[][] xywhSet = null;
        if (block.drawerCount == 1) {
            count = 1;
            xywhSet = drawerXYWH1;
        }
        else if (block.drawerCount == 2) {
            count = 2;
            xywhSet = drawerXYWH2;
        }
        else if (block.drawerCount == 4) {
            count = 4;
            xywhSet = drawerXYWH4;
        }

        IIcon iconOff = block.getIndicatorIcon(count, false);
        IIcon iconOn = block.getIndicatorIcon(count, true);

        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);

        for (int i = 0; i < count; i++) {
            IDrawer drawer = tile.getDrawer(i);
            if (drawer == null)
                continue;

            float[] xywh = xywhSet[i];

            RenderHelper.instance.setRenderBounds(xywh[0] * unit, xywh[1] * unit, 0, (xywh[0] + xywh[2]) * unit, (xywh[1] + xywh[3]) * unit, (depth - depthAdj + .05) * unit);
            RenderHelper.instance.state.setRotateTransform(RenderHelper.ZPOS, side);
            RenderHelper.instance.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconOff);
            RenderHelper.instance.state.clearRotateTransform();

            if (level == 1 && drawer.getMaxCapacity() > 0 && drawer.getRemainingCapacity() == 0) {
                RenderHelper.instance.state.setColorMult(1, 1, .9f, 1);
                RenderHelper.instance.setRenderBounds(xywh[0] * unit, xywh[1] * unit, 0, (xywh[0] + xywh[2]) * unit, (xywh[1] + xywh[3]) * unit, (depth - depthAdj + .06) * unit);
                RenderHelper.instance.state.setRotateTransform(RenderHelper.ZPOS, side);
                RenderHelper.instance.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconOn);
                RenderHelper.instance.state.clearRotateTransform();
                RenderHelper.instance.state.resetColorMult();
            }
            else if (level >= 2) {
                double indXStart = xywh[0] + block.getIndStart() / unit;
                double indXEnd = xywh[0] + block.getIndEnd() / unit;
                double indXCur = (block.getIndSteps() == 0) ? indXEnd : getIndEnd(block, tile, i, indXStart, (block.getIndEnd() - block.getIndStart()) / unit);

                double indYStart = xywh[1];
                double indYEnd = xywh[1] + xywh[3];
                double indYCur = indYEnd;

                if (indXCur > indXStart) {
                    RenderHelper.instance.state.setColorMult(1, 1, .9f, 1);
                    RenderHelper.instance.setRenderBounds(indXStart * unit, indYStart * unit, 0, indXCur * unit, indYCur * unit, (depth - depthAdj + .06) * unit);
                    RenderHelper.instance.state.setRotateTransform(RenderHelper.ZPOS, side);
                    RenderHelper.instance.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconOn);
                    RenderHelper.instance.state.clearRotateTransform();
                    RenderHelper.instance.state.resetColorMult();
                }
            }
        }
    }

    private double getIndEnd (BlockDrawers block, TileEntityDrawers tile, int slot, double x, double w) {
        IDrawer drawer = tile.getDrawer(slot);
        if (drawer == null)
            return x;

        int cap = drawer.getMaxCapacity();
        int count = drawer.getStoredItemCount();
        if (cap == 0 || count == 0)
            return x;

        int step = block.getIndSteps() > 0 ? block.getIndSteps() : 1000;
        float fillAmt = (float)((double)step * count / cap) / step;

        return x + (w * fillAmt);
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

        boxRenderer.renderExterior(renderer.blockAccess, block, x, y, z, xMin, 0, zMin, xMax, 1, zMax, 0, ModularBoxRenderer.sideCut[side]);
    }

    private void renderInterior (BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer) {
        double unit = block.getTrimDepth();
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

        boxRenderer.renderInterior(renderer.blockAccess, block, x, y, z, xMin, unit, zMin, xMax, 1 - unit, zMax, 0, ModularBoxRenderer.sideCut[side]);
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
*/