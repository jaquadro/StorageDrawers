package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.geometry.Area2D;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.render.ChamRenderManager;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.render.IRenderLabel;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerSealedModel;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStatus;
import com.jaquadro.minecraft.storagedrawers.storage.CountFormatter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class TileEntityDrawersRenderer extends TileEntitySpecialRenderer<TileEntityDrawers>
{
    //private float brightness;

    private boolean[] renderAsBlock = new boolean[4];
    private ItemStack[] renderStacks = new ItemStack[4];

    private RenderItem renderItem;

    private float itemOffset1X[] = new float[] { .5f };
    private float itemOffset1Y[] = new float[] { 8.25f };

    private float itemOffset2X[] = new float[] { .5f, .5f };
    private float itemOffset2Y[] = new float[] { 10.25f, 2.25f };

    private float itemOffset4X[] = new float[] { .25f, .25f, .75f, .75f };
    private float itemOffset4Y[] = new float[] { 10.25f, 2.25f, 10.25f, 2.25f };

    private float itemOffset3X[] = new float[] { .5f, .25f, .75f };
    private float itemOffset3Y[] = new float[] { 9.75f, 2.25f, 2.25f };

    //private static int[] glStateRender = { GL11.GL_LIGHTING, GL11.GL_BLEND };
    //private List<int[]> savedGLStateRender = GLUtil.makeGLState(glStateRender);

    //private static int[] glStateItemRender = { GL11.GL_LIGHTING, GL11.GL_ALPHA_TEST, GL11.GL_BLEND };
    //private List<int[]> savedGLStateItemRender = GLUtil.makeGLState(glStateItemRender);

    //private static int[] glLightRender = { GL11.GL_LIGHT0, GL11.GL_LIGHT1, GL11.GL_COLOR_MATERIAL, GL12.GL_RESCALE_NORMAL };
    //private List<int[]> savedGLLightRender = GLUtil.makeGLState(glLightRender);

    @Override
    public void renderTileEntityAt (TileEntityDrawers tile, double x, double y, double z, float partialTickTime, int destroyStage) {
        if (tile == null)
            return;

        float depth = 1;

        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (state == null)
            return;

        Block block = state.getBlock();
        if (block instanceof BlockDrawers) {
            if (state.getProperties().containsKey(BlockStandardDrawers.BLOCK)) {
                EnumBasicDrawer info = (EnumBasicDrawer)state.getValue(BlockStandardDrawers.BLOCK);
                depth = info.isHalfDepth() ? .5f : 1;
            }
        }
        else
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        renderItem = Minecraft.getMinecraft().getRenderItem();

        EnumFacing side = EnumFacing.getFront(tile.getDirection());
        int ambLight = getWorld().getCombinedLight(tile.getPos().offset(side), 0);
        int lu = ambLight % 65536;
        int lv = ambLight / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lu / 1.0F, (float)lv / 1.0F);

        //brightness = tile.getWorldObj().getLightBrightness(tile.xCoord + side.offsetX, tile.yCoord + side.offsetY, tile.zCoord + side.offsetZ) * 1.25f;
        //if (brightness > 1)
        //    brightness = 1;

        ChamRender renderer = ChamRenderManager.instance.getRenderer(Tessellator.getInstance().getBuffer());

        Minecraft mc = Minecraft.getMinecraft();
        boolean cache = mc.gameSettings.fancyGraphics;
        mc.gameSettings.fancyGraphics = true;
        renderUpgrades(renderer, tile, state);
        if (!tile.isShrouded() && !tile.isSealed())
            renderFastItemSet(renderer, tile, state, side, depth, partialTickTime);

        mc.gameSettings.fancyGraphics = cache;

        //Tessellator tessellator = Tessellator.getInstance();
        //WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        //worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);



        //tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableNormalize();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();

        ChamRenderManager.instance.releaseRenderer(renderer);
    }

    private void renderFastItemSet (ChamRender renderer, TileEntityDrawers tile, IBlockState state, EnumFacing side, float depth, float partialTickTime) {
        int drawerCount = tile.getDrawerCount();
        boolean restoreItemState = false;
        boolean restoreBlockState = false;

        for (int i = 0; i < drawerCount; i++) {
            renderStacks[i] = null;
            IDrawer drawer = tile.getDrawerIfEnabled(i);
            if (drawer == null)
                continue;

            ItemStack itemStack = drawer.getStoredItemPrototype();
            if (itemStack == null)
                continue;

            renderStacks[i] = itemStack;
            renderAsBlock[i] = isItemBlockType(itemStack);

            if (renderAsBlock[i])
                restoreBlockState = true;
            else
                restoreItemState = true;
        }

        //if (restoreItemState || restoreBlockState)
        //    GLUtil.saveGLState(savedGLStateItemRender, glStateItemRender);

        //GlStateManager.pushAttrib();

        for (int i = 0; i < drawerCount; i++) {
            if (renderStacks[i] != null && !renderAsBlock[i])
                renderFastItem(renderer, renderStacks[i], tile, state, i, side, depth, partialTickTime);
        }

        //if (restoreBlockState) {
        //    GLUtil.saveGLState(savedGLLightRender, glLightRender);
        //    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        //}

        for (int i = 0; i < drawerCount; i++) {
            if (renderStacks[i] != null && renderAsBlock[i])
                renderFastItem(renderer, renderStacks[i], tile, state, i, side, depth, partialTickTime);
        }

        if (tile.isShowingQuantity()) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            BlockPos blockPos = tile.getPos().add(.5, .5, .5);
            double distance = Math.sqrt(blockPos.distanceSq(player.getPosition()));

            float alpha = 1;
            if (distance > 4)
                alpha = 1f - (float) ((distance - 4) / 6);

            if (distance < 10) {
                for (int i = 0; i < drawerCount; i++)
                    renderText(CountFormatter.format(getFontRenderer(), tile.getDrawer(i)), tile, state, i, side, depth, alpha);
            }
        }

        //GlStateManager.popAttrib();

        //if (restoreBlockState) {
        //    GLUtil.restoreGLState(savedGLLightRender);
        //    GL11.glPopAttrib();
        //}

        //if (restoreItemState || restoreBlockState)
        //    GLUtil.restoreGLState(savedGLStateItemRender);
    }

    private void renderText (String text, TileEntityDrawers tile, IBlockState state, int slot, EnumFacing side, float depth, float alpha) {
        if (text == null || text.isEmpty())
            return;

        BlockDrawers block = (BlockDrawers)state.getBlock();
        StatusModelData statusInfo = block.getStatusInfo(state);
        float frontDepth = (float)statusInfo.getFrontDepth() * .0625f;
        int textWidth = getFontRenderer().getStringWidth(text);

        Area2D statusArea = statusInfo.getSlot(slot).getLabelArea();
        float x = (float)(statusArea.getX() + statusArea.getWidth() / 2);
        float y = 16f - (float)statusArea.getY() - (float)statusArea.getHeight();

        GlStateManager.pushMatrix();
        alignRendering(side);
        moveRendering(.125f, x, y, 1f - depth + frontDepth - .005f);

        GlStateManager.disableLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.depthMask(false);
        GlStateManager.doPolygonOffset(-1, -20);

        getFontRenderer().drawString(text, -textWidth / 2, 0, (int)(255 * alpha) << 24 | 255 << 16 | 255 << 8 | 255);

        GlStateManager.depthMask(true);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    private void renderFastItem (ChamRender renderer, ItemStack itemStack, TileEntityDrawers tile, IBlockState state, int slot, EnumFacing side, float depth, float partialTickTime) {
        int drawerCount = tile.getDrawerCount();
        float xunit = getXOffset(drawerCount, slot);
        float yunit = getYOffset(drawerCount, slot);
        float size = (drawerCount == 1) ? .5f : .25f;

        BlockDrawers block = (BlockDrawers)state.getBlock();
        StatusModelData statusInfo = block.getStatusInfo(state);
        float frontDepth = (float)statusInfo.getFrontDepth() * .0625f;

        GlStateManager.pushMatrix();

        alignRendering(side);
        moveRendering(size, getOffsetXForSide(side, xunit) * 16 - (8 * size), 12.25f - yunit, 1f - depth + frontDepth - .005f);

        List<IRenderLabel> renderHandlers = StorageDrawers.renderRegistry.getRenderHandlers();
        for (int i = 0, n = renderHandlers.size(); i < n; i++) {
            renderHandlers.get(i).render(tile, tile, slot, 0, partialTickTime);
        }

        GlStateManager.disableLighting();

        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-1, -1);

        renderItem.renderItemIntoGUI(itemStack, 0, 0);

        GlStateManager.disablePolygonOffset();

        GlStateManager.popMatrix();
    }

    private boolean isItemBlockType (ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock && renderItem.shouldRenderItemIn3D(itemStack);
    }

    private float getXOffset (int drawerCount, int slot) {
        switch (drawerCount) {
            case 1: return itemOffset1X[slot];
            case 2: return itemOffset2X[slot];
            case 3: return itemOffset3X[slot];
            case 4: return itemOffset4X[slot];
            default: return 0;
        }
    }

    private float getYOffset (int drawerCount, int slot) {
        switch (drawerCount) {
            case 1: return itemOffset1Y[slot];
            case 2: return itemOffset2Y[slot];
            case 3: return itemOffset3Y[slot];
            case 4: return itemOffset4Y[slot];
            default: return 0;
        }
    }

    private void alignRendering (EnumFacing side) {
        GlStateManager.translate(.5f, .5f, .5f);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate(getRotationYForSide2D(side), 0, 1, 0);
        GlStateManager.translate(-.5f, -.5f, -.5f);
    }

    private void moveRendering (float size, float offsetX, float offsetY, float offsetZ) {
        GlStateManager.translate(0, 0, offsetZ);
        GlStateManager.scale(1 / 16f, 1 / 16f, -.00001f);
        GlStateManager.translate(offsetX, offsetY, 0);
        GlStateManager.scale(size, size, 1);
    }

    private static final float[] sideRotationY2D = { 0, 0, 0, 2, 3, 1 };

    private float getRotationYForSide2D (EnumFacing side) {
        return sideRotationY2D[side.ordinal()] * 90;
    }

    private static final float[] offsetX = { 0, 0, 0, 0, 0, 0 };

    private float getOffsetXForSide (EnumFacing side, float x) {
        return Math.abs(offsetX[side.ordinal()] - x);
    }

    private void renderUpgrades (ChamRender renderer, TileEntityDrawers tile, IBlockState state) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.enableAlpha();

        renderIndicator(renderer, tile, state, tile.getDirection(), tile.getEffectiveStatusLevel());
        renderTape(renderer, tile, state, tile.getDirection(), tile.isSealed());
    }

    private void renderIndicator (ChamRender renderer, TileEntityDrawers tile, IBlockState blockState, int side, int level) {
        if (level <= 0 || side < 2 || side > 5)
            return;

        BlockDrawers block = (BlockDrawers)blockState.getBlock();
        StatusModelData statusInfo = block.getStatusInfo(blockState);
        if (statusInfo == null)
            return;

        double depth = block.isHalfDepth(blockState) ? .5 : 1;
        int count = (tile instanceof TileEntityDrawersComp) ? 1 : block.getDrawerCount(blockState);

        double unit = 0.0625;
        double frontDepth = statusInfo.getFrontDepth() * unit;

        for (int i = 0; i < count; i++) {
            IDrawer drawer = tile.getDrawer(i);
            if (drawer == null || tile.isShrouded())
                continue;

            TextureAtlasSprite iconOff = Chameleon.instance.iconRegistry.getIcon(statusInfo.getSlot(i).getOffResource(EnumUpgradeStatus.byLevel(level)));
            TextureAtlasSprite iconOn = Chameleon.instance.iconRegistry.getIcon(statusInfo.getSlot(i).getOnResource(EnumUpgradeStatus.byLevel(level)));

            Area2D statusArea = statusInfo.getSlot(i).getStatusArea();
            Area2D activeArea = statusInfo.getSlot(i).getStatusActiveArea();

            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(-1, -1);

            renderer.setRenderBounds(statusArea.getX() * unit, statusArea.getY() * unit, 0,
                (statusArea.getX() + statusArea.getWidth()) * unit, (statusArea.getY() + statusArea.getHeight()) * unit, depth - frontDepth);
            renderer.state.setRotateTransform(ChamRender.ZPOS, side);
            renderer.renderFace(ChamRender.FACE_ZPOS, null, blockState, BlockPos.ORIGIN, iconOff, 1, 1, 1);
            renderer.state.clearRotateTransform();

            GlStateManager.doPolygonOffset(-1, -10);

            if (level == 1 && drawer.getMaxCapacity() > 0 && drawer.getRemainingCapacity() == 0) {
                renderer.setRenderBounds(statusArea.getX() * unit, statusArea.getY() * unit, 0,
                    (statusArea.getX() + statusArea.getWidth()) * unit, (statusArea.getY() + statusArea.getHeight()) * unit, depth - frontDepth);
                renderer.state.setRotateTransform(ChamRender.ZPOS, side);
                renderer.renderFace(ChamRender.FACE_ZPOS, null, blockState, BlockPos.ORIGIN, iconOn, 1, 1, 1);
                renderer.state.clearRotateTransform();
            }
            else if (level >= 2) {
                int stepX = statusInfo.getSlot(i).getActiveStepsX();
                int stepY = statusInfo.getSlot(i).getActiveStepsY();

                double indXStart = activeArea.getX();
                double indXEnd = activeArea.getX() + activeArea.getWidth();
                double indXCur = (stepX == 0) ? indXEnd : getIndEnd(block, tile, i, indXStart, activeArea.getWidth(), stepX);

                double indYStart = activeArea.getY();
                double indYEnd = activeArea.getY() + activeArea.getHeight();
                double indYCur = (stepY == 0) ? indYEnd : getIndEnd(block, tile, i, indYStart, activeArea.getHeight(), stepY);

                if (indXCur > indXStart && indYCur > indYStart) {
                    indXCur = Math.min(indXCur, indXEnd);
                    indYCur = Math.min(indYCur, indYEnd);

                    renderer.setRenderBounds(indXStart * unit, indYStart * unit, 0,
                        indXCur * unit, indYCur * unit, depth - frontDepth);
                    renderer.state.setRotateTransform(ChamRender.ZPOS, side);
                    renderer.renderFace(ChamRender.FACE_ZPOS, null, blockState, BlockPos.ORIGIN, iconOn, 1, 1, 1);
                    renderer.state.clearRotateTransform();
                }
            }

            GlStateManager.disablePolygonOffset();
        }
    }

    private void renderTape (ChamRender renderer, TileEntityDrawers tile, IBlockState blockState, int side, boolean taped) {
        if (!taped || side < 2 || side > 5)
            return;

        BlockDrawers block = (BlockDrawers)blockState.getBlock();

        double depth = block.isHalfDepth(blockState) ? .5 : 1;
        TextureAtlasSprite iconTape = Chameleon.instance.iconRegistry.getIcon(DrawerSealedModel.iconTapeCover);

        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-1, -1);

        renderer.setRenderBounds(0, 0, 0, 1, 1, depth);
        renderer.state.setRotateTransform(ChamRender.ZPOS, side);
        renderer.renderPartialFace(ChamRender.FACE_ZPOS, null, blockState, BlockPos.ORIGIN, iconTape, 0, 0, 1, 1, 1, 1, 1);
        renderer.state.clearRotateTransform();

        GlStateManager.disablePolygonOffset();
    }


    private double getIndEnd (BlockDrawers block, TileEntityDrawers tile, int slot, double x, double w, int step) {
        IDrawer drawer = tile.getDrawer(slot);
        if (drawer == null)
            return x;

        int cap = drawer.getMaxCapacity();
        int count = drawer.getStoredItemCount();
        if (cap == 0 || count == 0)
            return x;

        float fillAmt = (float)(step * count / cap) / step;

        return x + (w * fillAmt);
    }

    private class LocalRenderItem extends RenderItem {

        public LocalRenderItem (TextureManager textureManager, ModelManager modelManager, ItemColors colors) {
            super(textureManager, modelManager, colors);
        }
    }
}
