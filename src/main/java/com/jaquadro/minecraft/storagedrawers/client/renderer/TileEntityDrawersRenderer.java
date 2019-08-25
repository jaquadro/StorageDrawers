package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.util.CountFormatter;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class TileEntityDrawersRenderer extends TileEntityRenderer<TileEntityDrawers>
{
    private boolean[] renderAsBlock = new boolean[4];
    private ItemStack[] renderStacks = new ItemStack[4];

    private ItemRenderer renderItem;

    @Override
    public void render (TileEntityDrawers tile, double x, double y, double z, float partialTickTime, int destroyStage) {
        if (tile == null)
            return;

        World world = tile.getWorld();
        if (world == null)
            return;

        BlockState state = world.getBlockState(tile.getPos());
        if (!(state.getBlock() instanceof BlockDrawers))
            return;

        BlockDrawers block = (BlockDrawers)state.getBlock();
        float depth = block.isHalfDepth() ? .5f : 1;

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        renderItem = Minecraft.getInstance().getItemRenderer();

        Direction side = state.get(BlockDrawers.HORIZONTAL_FACING);
        int ambLight = getWorld().getCombinedLight(tile.getPos().offset(side), 0);
        int lu = ambLight % 65536;
        int lv = ambLight / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)lu, (float)lv);

        //ChamRender renderer = ChamRenderManager.instance.getRenderer(Tessellator.getInstance().getBuffer());

        Minecraft mc = Minecraft.getInstance();
        boolean cache = mc.gameSettings.fancyGraphics;
        mc.gameSettings.fancyGraphics = true;
        //renderUpgrades(renderer, tile, state);
        if (!tile.getDrawerAttributes().isConcealed())
            renderFastItemSet(tile, state, side, depth, partialTickTime);

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

        //ChamRenderManager.instance.releaseRenderer(renderer);
    }

    private void renderFastItemSet (TileEntityDrawers tile, BlockState state, Direction side, float depth, float partialTickTime) {
        int drawerCount = tile.getDrawerCount();

        for (int i = 0; i < drawerCount; i++) {
            renderStacks[i] = ItemStack.EMPTY;
            IDrawer drawer = tile.getDrawer(i);
            if (!drawer.isEnabled() || drawer.isEmpty())
                continue;

            ItemStack itemStack = drawer.getStoredItemPrototype();
            renderStacks[i] = itemStack;
            renderAsBlock[i] = isItemBlockType(itemStack);
        }

        for (int i = 0; i < drawerCount; i++) {
            if (!renderStacks[i].isEmpty() && !renderAsBlock[i])
                renderFastItem(renderStacks[i], tile, state, i, side, depth, partialTickTime);
        }

        for (int i = 0; i < drawerCount; i++) {
            if (!renderStacks[i].isEmpty() && renderAsBlock[i])
                renderFastItem(renderStacks[i], tile, state, i, side, depth, partialTickTime);
        }

        if (tile.getDrawerAttributes().isShowingQuantity()) {
            PlayerEntity player = Minecraft.getInstance().player;
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

    private void renderText (String text, TileEntityDrawers tile, BlockState state, int slot, Direction side, float depth, float alpha) {
        if (text == null || text.isEmpty())
            return;

        BlockDrawers block = (BlockDrawers)state.getBlock();
        AxisAlignedBB labelGeometry = block.labelGeometry[slot];
        //StatusModelData statusInfo = block.getStatusInfo(state);
        float frontDepth = (float)labelGeometry.minZ * .0625f;
        int textWidth = getFontRenderer().getStringWidth(text);

        float x = (float)(labelGeometry.minX + labelGeometry.getXSize() / 2);
        float y = 16f - (float)labelGeometry.minY - (float)labelGeometry.getYSize();

        GlStateManager.pushMatrix();
        alignRendering(side);
        moveRendering(.125f, .125f, x, y, 1f - depth + frontDepth - .005f);

        GlStateManager.disableLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.polygonOffset(-1, -20);

        getFontRenderer().drawString(text, -textWidth / 2, 0, (int)(255 * alpha) << 24 | 255 << 16 | 255 << 8 | 255);

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    private void renderFastItem (@Nonnull ItemStack itemStack, TileEntityDrawers tile, BlockState state, int slot, Direction side, float depth, float partialTickTime) {
        int drawerCount = ((BlockDrawers)state.getBlock()).getDrawerCount();

        BlockDrawers block = (BlockDrawers)state.getBlock();
        AxisAlignedBB labelGeometry = block.labelGeometry[slot];
        float frontDepth = (float)labelGeometry.minZ * .0625f;

        GlStateManager.pushMatrix();

        alignRendering(side);

        float scaleX = (float)labelGeometry.getXSize() / 16;
        float scaleY = (float)labelGeometry.getYSize() / 16;
        float moveX = (float)labelGeometry.minX;
        float moveY = 16f - (float)labelGeometry.maxY;
        moveRendering(scaleX, scaleY, moveX, moveY, 1f - depth + frontDepth - .005f);

        //List<IRenderLabel> renderHandlers = StorageDrawers.renderRegistry.getRenderHandlers();
        //for (IRenderLabel renderHandler : renderHandlers) {
        //    renderHandler.render(tile, tile.getGroup(), slot, 0, partialTickTime);
        //}

        // At the time GL_LIGHT* are configured, the coordinates are transformed by the modelview
        // matrix. The transformations used in `RenderHelper.enableGUIStandardItemLighting` are
        // suitable for the orthographic projection used by GUI windows, but they are just a little
        // bit off when rendering a block in 3D and squishing it flat. An additional term is added
        // to account for slightly different shading on the half-size "icons" in 1x2 and 2x2
        // drawers due to the extreme angles caused by flattening the block (as noted below).

        GlStateManager.pushMatrix();
        if (drawerCount == 1) {
            GlStateManager.scalef(2.6f, 2.6f, 1);
            GlStateManager.rotatef(171.6f, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(84.9f, 1.0F, 0.0F, 0.0F);
        }
        else {
            GlStateManager.scalef(1.92f, 1.92f, 1);
            GlStateManager.rotatef(169.2f, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(79.0f, 1.0F, 0.0F, 0.0F);
        }
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        // TileEntitySkullRenderer alters both of these options on, but does not restore them.
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();

        // GL_POLYGON_OFFSET is used to offset flat icons toward the viewer (-Z) in screen space,
        // so they always appear on top of the drawer's front space.
        GlStateManager.enablePolygonOffset();
        GlStateManager.polygonOffset(-1, -1);

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

        GlStateManager.enableRescaleNormal();
        GlStateManager.disableRescaleNormal();
        GlStateManager.pushLightingAttributes();
        GlStateManager.enableRescaleNormal();
        GlStateManager.popAttributes();

        try {
            renderItem.renderItemIntoGUI(itemStack, 0, 0);
        }
        catch (Exception e) {
            // Shrug
        }
        
        GlStateManager.disableBlend(); // Clean up after RenderItem
        GlStateManager.enableAlphaTest();  // Restore world render state after RenderItem

        GlStateManager.disablePolygonOffset();

        GlStateManager.popMatrix();
    }

    private boolean isItemBlockType (@Nonnull ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem && renderItem.shouldRenderItemIn3D(itemStack);
    }

    private void alignRendering (Direction side) {
        // Rotate to face the correct direction for the drawer's orientation.

        GlStateManager.translatef(.5f, .5f, .5f);
        GlStateManager.rotatef(getRotationYForSide2D(side), 0, 1, 0);
        GlStateManager.translatef(-.5f, -.5f, -.5f);
    }

    private void moveRendering (float scaleX, float scaleY, float offsetX, float offsetY, float offsetZ) {
        // NOTE: RenderItem expects to be called in a context where Y increases toward the bottom of the screen
        // However, for in-world rendering the opposite is true. So we translate up by 1 along Y, and then flip
        // along Y. Since the item is drawn at the back of the drawer, we also translate by `1-offsetZ` to move
        // it to the front.

        // The 0.00001 for the Z-scale both flattens the item and negates the 32.0 Z-scale done by RenderItem.

        GlStateManager.translatef(0, 1, 1-offsetZ);
        GlStateManager.scalef(1 / 16f, -1 / 16f, 0.00001f);

        GlStateManager.translatef(offsetX, offsetY, 0f);
        GlStateManager.scalef(scaleX, scaleY, 1);
    }

    private static final float[] sideRotationY2D = { 0, 0, 2, 0, 3, 1 };

    private float getRotationYForSide2D (Direction side) {
        return sideRotationY2D[side.ordinal()] * 90;
    }

    /*private void renderUpgrades (ChamRender renderer, TileEntityDrawers tile, IBlockState state) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.enableAlpha();

        renderIndicator(renderer, tile, state, tile.getDirection(), tile.upgrades().getStatusType());
        renderTape(renderer, tile, state, tile.getDirection(), tile.isSealed());
    }*/

    /*private void renderIndicator (ChamRender renderer, TileEntityDrawers tile, IBlockState blockState, int side, EnumUpgradeStatus level) {
        if (level == null || side < 2 || side > 5)
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
            if (drawer == null || tile.getDrawerAttributes().isConcealed())
                continue;

            TextureAtlasSprite iconOff = Chameleon.instance.iconRegistry.getIcon(statusInfo.getSlot(i).getOffResource(level));
            TextureAtlasSprite iconOn = Chameleon.instance.iconRegistry.getIcon(statusInfo.getSlot(i).getOnResource(level));

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

            if (level == EnumUpgradeStatus.LEVEL1 && !drawer.isEmpty() && drawer.getRemainingCapacity() == 0) {
                renderer.setRenderBounds(statusArea.getX() * unit, statusArea.getY() * unit, 0,
                    (statusArea.getX() + statusArea.getWidth()) * unit, (statusArea.getY() + statusArea.getHeight()) * unit, depth - frontDepth);
                renderer.state.setRotateTransform(ChamRender.ZPOS, side);
                renderer.renderFace(ChamRender.FACE_ZPOS, null, blockState, BlockPos.ORIGIN, iconOn, 1, 1, 1);
                renderer.state.clearRotateTransform();
            }
            else if (level == EnumUpgradeStatus.LEVEL2) {
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
    }*/

    /*private void renderTape (ChamRender renderer, TileEntityDrawers tile, IBlockState blockState, int side, boolean taped) {
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
    }*/


    /*private double getIndEnd (BlockDrawers block, TileEntityDrawers tile, int slot, double x, double w, int step) {
        IDrawer drawer = tile.getDrawer(slot);
        if (drawer == null)
            return x;

        int cap = drawer.getMaxCapacity();
        int count = drawer.getStoredItemCount();
        if (cap == 0 || count == 0)
            return x;

        float fillAmt = (float)(step * count / cap) / step;

        return x + (w * fillAmt);
    }*/
}
