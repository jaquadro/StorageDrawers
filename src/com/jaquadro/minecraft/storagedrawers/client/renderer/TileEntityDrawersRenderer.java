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

import javax.annotation.Nonnull;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TileEntityDrawersRenderer extends TileEntitySpecialRenderer<TileEntityDrawers>
{
    private boolean[] renderAsBlock = new boolean[4];
    private ItemStack[] renderStacks = new ItemStack[4];

    private RenderItem renderItem;

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
                EnumBasicDrawer info = state.getValue(BlockStandardDrawers.BLOCK);
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

        ChamRender renderer = ChamRenderManager.instance.getRenderer(Tessellator.getInstance().getBuffer());

        Minecraft mc = Minecraft.getMinecraft();
        boolean cache = mc.gameSettings.fancyGraphics;
        mc.gameSettings.fancyGraphics = true;
        renderUpgrades(renderer, tile, state);
        if (!tile.isShrouded() && !tile.isSealed())
            renderFastItemSet(renderer, tile, state, side, depth, partialTickTime);

        mc.gameSettings.fancyGraphics = cache;

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

        for (int i = 0; i < drawerCount; i++) {
            renderStacks[i] = ItemStack.EMPTY;
            IDrawer drawer = tile.getDrawerIfEnabled(i);
            if (drawer == null || drawer.isEmpty())
                continue;

            ItemStack itemStack = drawer.getStoredItemPrototype();
            renderStacks[i] = itemStack;
            renderAsBlock[i] = isItemBlockType(itemStack);
        }

        for (int i = 0; i < drawerCount; i++) {
            if (!renderStacks[i].isEmpty() && !renderAsBlock[i])
                renderFastItem(renderer, renderStacks[i], tile, state, i, side, depth, partialTickTime);
        }

        for (int i = 0; i < drawerCount; i++) {
            if (!renderStacks[i].isEmpty() && renderAsBlock[i])
                renderFastItem(renderer, renderStacks[i], tile, state, i, side, depth, partialTickTime);
        }

        if (tile.isShowingQuantity()) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            BlockPos blockPos = tile.getPos().add(.5, .5, .5);
            double distance = Math.sqrt(blockPos.distanceSq(player.getPosition()));

            float alpha = 1;
            if (distance > 4)
                alpha = Math.max(1f - (float) ((distance - 4) / 6), 0.05f);

            if (distance < 10) {
                for (int i = 0; i < drawerCount; i++)
                    renderText(CountFormatter.format(getFontRenderer(), tile.getDrawer(i)), tile, state, i, side, depth, alpha);
            }
        }
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
        GlStateManager.enableBlend();
        GlStateManager.doPolygonOffset(-1, -20);

        getFontRenderer().drawString(text, -textWidth / 2, 0, (int)(255 * alpha) << 24 | 255 << 16 | 255 << 8 | 255);

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    private void renderFastItem (ChamRender renderer, @Nonnull ItemStack itemStack, TileEntityDrawers tile, IBlockState state, int slot, EnumFacing side, float depth, float partialTickTime) {
        int drawerCount = tile.getDrawerCount();
        float size = (drawerCount == 1) ? .5f : .25f;

        BlockDrawers block = (BlockDrawers)state.getBlock();
        StatusModelData statusInfo = block.getStatusInfo(state);
        float frontDepth = (float)statusInfo.getFrontDepth() * .0625f;
        Area2D slotArea = statusInfo.getSlot(slot).getSlotArea();

        GlStateManager.pushMatrix();

        float xCenter = (float)slotArea.getX() + (float)slotArea.getWidth() / 2 - (8 * size);
        float yCenter = 16 - (float)slotArea.getY() - (float)slotArea.getHeight() / 2 - (8 * size);

        alignRendering(side);
        moveRendering(size, xCenter, yCenter, 1f - depth + frontDepth - .005f);

        List<IRenderLabel> renderHandlers = StorageDrawers.renderRegistry.getRenderHandlers();
        for (IRenderLabel renderHandler : renderHandlers) {
            renderHandler.render(tile, tile, slot, 0, partialTickTime);
        }

        // At the time GL_LIGHT* are configured, the coordinates are transformed by the modelview
        // matrix. The transformations used in `RenderHelper.enableGUIStandardItemLighting` are
        // suitable for the orthographic projection used by GUI windows, but they are just a little
        // bit off when rendering a block in 3D and squishing it flat. An additional term is added
        // to account for slightly different shading on the half-size "icons" in 1x2 and 2x2
        // drawers due to the extreme angles caused by flattening the block (as noted below).

        GlStateManager.pushMatrix();
        if (drawerCount == 1) {
            GlStateManager.scale(2.6f, 2.6f, 1);
            GlStateManager.rotate(171.6f, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(84.9f, 1.0F, 0.0F, 0.0F);
        }
        else {
            GlStateManager.scale(1.92f, 1.92f, 1);
            GlStateManager.rotate(169.2f, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(79.0f, 1.0F, 0.0F, 0.0F);
        }
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        // TileEntitySkullRenderer alters both of these options on, but does not restore them.
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();

        // GL_POLYGON_OFFSET is used to offset flat icons toward the viewer (-Z) in screen space,
        // so they always appear on top of the drawer's front space.
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-1, -1);

        // DIRTY HACK: Fool GlStateManager into thinking GL_RESCALE_NORMAL is enabled, but disable
        // it using popAttrib This prevents RenderItem from enabling it again.
        //
        // Normals are transformed by the inverse of the modelview and projection matrices that
        // excludes the translate terms. When put through the extreme Z scale used to flatten the
        // block, this makes them point away from the drawer face at a very sharp angle. These
        // normals are no longer unit scale (normalized), and normalizing them via
        // GL_RESCALE_NORMAL causes a loss of precision that results in the normals pointing
        // directly away from the face, which is visible as the block faces having identical
        // (dark) shading.

        GlStateManager.pushAttrib();
        GlStateManager.enableRescaleNormal();
        GlStateManager.popAttrib();

        renderItem.renderItemIntoGUI(itemStack, 0, 0);
        GlStateManager.disableBlend(); // Clean up after RenderItem
        GlStateManager.enableAlpha();  // Restore world render state after RenderItem

        GlStateManager.disablePolygonOffset();

        GlStateManager.popMatrix();
    }

    private boolean isItemBlockType (@Nonnull ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock && renderItem.shouldRenderItemIn3D(itemStack);
    }

    private void alignRendering (EnumFacing side) {
        // Rotate to face the correct direction for the drawer's orientation.

        GlStateManager.translate(.5f, .5f, .5f);
        GlStateManager.rotate(getRotationYForSide2D(side), 0, 1, 0);
        GlStateManager.translate(-.5f, -.5f, -.5f);
    }

    private void moveRendering (float size, float offsetX, float offsetY, float offsetZ) {
        // NOTE: RenderItem expects to be called in a context where Y increases toward the bottom of the screen
        // However, for in-world rendering the opposite is true. So we translate up by 1 along Y, and then flip
        // along Y. Since the item is drawn at the back of the drawer, we also translate by `1-offsetZ` to move
        // it to the front.

        // The 0.00001 for the Z-scale both flattens the item and negates the 32.0 Z-scale done by RenderItem.

        GlStateManager.translate(0, 1, 1-offsetZ);
        GlStateManager.scale(1 / 16f, -1 / 16f, 0.00001);

        GlStateManager.translate(offsetX, offsetY, 0.);
        GlStateManager.scale(size, size, 1);
    }

    private static final float[] sideRotationY2D = { 0, 0, 2, 0, 3, 1 };

    private float getRotationYForSide2D (EnumFacing side) {
        return sideRotationY2D[side.ordinal()] * 90;
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
}
