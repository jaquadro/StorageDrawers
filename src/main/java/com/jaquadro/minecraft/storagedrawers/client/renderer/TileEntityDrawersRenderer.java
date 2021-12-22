package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.util.CountFormatter;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class TileEntityDrawersRenderer implements BlockEntityRenderer<TileEntityDrawers>
{
    private boolean[] renderAsBlock = new boolean[4];
    private ItemStack[] renderStacks = new ItemStack[4];

    private ItemRenderer renderItem;

    private BlockEntityRendererProvider.Context context;

    public TileEntityDrawersRenderer (BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render (TileEntityDrawers tile, float partialTickTime, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (tile == null)
            return;

        Level world = tile.getLevel();
        if (world == null)
            return;

        BlockState state = tile.getBlockState();
        if (state == null)
            return;

        if (!(state.getBlock() instanceof BlockDrawers))
            return;

        if (tile.upgrades().hasIlluminationUpgrade()) {
            int blockLight = Math.max(combinedLight % 65536, 208);
            combinedLight = (combinedLight & 0xFFFF0000) | blockLight;
        }

        renderItem = Minecraft.getInstance().getItemRenderer();
        Direction side = state.getValue(BlockDrawers.FACING);

        Minecraft mc = Minecraft.getInstance();
        GraphicsStatus cache = mc.options.graphicsMode;
        mc.options.graphicsMode = GraphicsStatus.FANCY;
        //renderUpgrades(renderer, tile, state);

        if (!tile.getDrawerAttributes().isConcealed())
            renderFastItemSet(tile, state, matrix, buffer, combinedLight, combinedOverlay, side, partialTickTime);

        mc.options.graphicsMode = cache;

        matrix.popPose();
        Lighting.setupLevel(matrix.last().pose());
        matrix.pushPose();
    }

    private void renderFastItemSet (TileEntityDrawers tile, BlockState state, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Direction side, float partialTickTime) {
        int drawerCount = tile.getGroup().getDrawerCount();

        for (int i = 0; i < drawerCount; i++) {
            renderStacks[i] = ItemStack.EMPTY;
            IDrawer drawer = tile.getGroup().getDrawer(i);
            if (!drawer.isEnabled() || drawer.isEmpty())
                continue;

            ItemStack itemStack = drawer.getStoredItemPrototype();
            renderStacks[i] = itemStack;
            renderAsBlock[i] = isItemBlockType(itemStack);
        }

        for (int i = 0; i < drawerCount; i++) {
            if (!renderStacks[i].isEmpty() && !renderAsBlock[i])
                renderFastItem(renderStacks[i], tile, state, i, matrix, buffer, combinedLight, combinedOverlay, side, partialTickTime);
        }

        for (int i = 0; i < drawerCount; i++) {
            if (!renderStacks[i].isEmpty() && renderAsBlock[i])
                renderFastItem(renderStacks[i], tile, state, i, matrix, buffer, combinedLight, combinedOverlay, side, partialTickTime);
        }

        if (tile.getDrawerAttributes().isShowingQuantity()) {
            Player player = Minecraft.getInstance().player;
            BlockPos blockPos = tile.getBlockPos().offset(.5, .5, .5);
            double distance = Math.sqrt(blockPos.distSqr(player.blockPosition()));

            float alpha = 1;
            if (distance > 4)
                alpha = Math.max(1f - (float) ((distance - 4) / 6), 0.05f);

            if (distance < 10) {
                MultiBufferSource.BufferSource txtBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                for (int i = 0; i < drawerCount; i++) {
                    String format = CountFormatter.format(this.context.getFont(), tile.getGroup().getDrawer(i));
                    renderText(format, state, i, matrix, txtBuffer, combinedLight, side, alpha);
                }
                txtBuffer.endBatch();
            }
        }
    }

    private void renderText (String text, BlockState state, int slot, PoseStack matrix, MultiBufferSource buffer, int combinedLight, Direction side, float alpha) {
        if (text == null || text.isEmpty())
            return;

        Font fontRenderer = this.context.getFont();

        BlockDrawers block = (BlockDrawers)state.getBlock();
        AABB labelGeometry = block.countGeometry[slot];
        int textWidth = fontRenderer.width(text);

        float x = (float)(labelGeometry.minX + labelGeometry.getXsize() / 2);
        float y = 16f - (float)labelGeometry.minY - (float)labelGeometry.getYsize();
        float z = (float)labelGeometry.minZ * .0625f - .01f;

        matrix.pushPose();

        alignRendering(matrix, side);
        moveRendering(matrix, .125f, .125f, x, y, z);

        int color = (int)(255 * alpha) << 24 | 255 << 16 | 255 << 8 | 255;
        fontRenderer.drawInBatch(text, -textWidth / 2f, 0.5f, color, false, matrix.last().pose(), buffer, false, 0, combinedLight); // 15728880

        matrix.popPose();
    }

    private void renderFastItem (@Nonnull ItemStack itemStack, TileEntityDrawers tile, BlockState state, int slot, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Direction side, float partialTickTime) {
        BlockDrawers block = (BlockDrawers)state.getBlock();
        AABB labelGeometry = block.labelGeometry[slot];

        float scaleX = (float)labelGeometry.getXsize() / 16;
        float scaleY = (float)labelGeometry.getYsize() / 16;
        float moveX = (float)labelGeometry.minX + (8 * scaleX);
        float moveY = 16f - (float)labelGeometry.maxY + (8 * scaleY);
        float moveZ = (float)labelGeometry.minZ * .0625f;

        matrix.pushPose();

        alignRendering(matrix, side);
        moveRendering(matrix, scaleX, scaleY, moveX, moveY, moveZ);

        //List<IRenderLabel> renderHandlers = StorageDrawers.renderRegistry.getRenderHandlers();
        //for (IRenderLabel renderHandler : renderHandlers) {
        //    renderHandler.render(tile, tile.getGroup(), slot, 0, partialTickTime);
        //}

        Consumer<MultiBufferSource> finish = (MultiBufferSource buf) -> {
            if (buf instanceof MultiBufferSource.BufferSource)
                ((MultiBufferSource.BufferSource) buf).endBatch();
        };

        try {
            matrix.translate(0, 0, 100f);
            matrix.scale(1, -1, 1);
            matrix.scale(16, 16, 16);

            //IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            BakedModel itemModel = renderItem.getModel(itemStack, null, null, 0);
            boolean render3D = itemModel.isGui3d(); // itemModel.usesBlockLight();
            finish.accept(buffer);

            if (render3D)
                Lighting.setupFor3DItems();
            else
                Lighting.setupForFlatItems();

            matrix.last().normal().load(Matrix3f.createScaleMatrix(1, 1, 1));
            renderItem.render(itemStack, ItemTransforms.TransformType.GUI, false, matrix, buffer, combinedLight, combinedOverlay, itemModel);
            finish.accept(buffer);
        }
        catch (Exception e) {
            // Shrug
        }

        matrix.popPose();
    }

    private boolean isItemBlockType (@Nonnull ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem; // && renderItem.shouldRenderItemIn3D(itemStack);
    }

    private void alignRendering (PoseStack matrix, Direction side) {
        // Rotate to face the correct direction for the drawer's orientation.

        matrix.translate(.5f, .5f, .5f);
        matrix.mulPose(new Quaternion(Vector3f.YP, getRotationYForSide2D(side), true));
        matrix.translate(-.5f, -.5f, -.5f);
    }

    private void moveRendering (PoseStack matrix, float scaleX, float scaleY, float offsetX, float offsetY, float offsetZ) {
        // NOTE: RenderItem expects to be called in a context where Y increases toward the bottom of the screen
        // However, for in-world rendering the opposite is true. So we translate up by 1 along Y, and then flip
        // along Y. Since the item is drawn at the back of the drawer, we also translate by `1-offsetZ` to move
        // it to the front.

        // The 0.00001 for the Z-scale both flattens the item and negates the 32.0 Z-scale done by RenderItem.

        matrix.translate(0, 1, 1-offsetZ);
        matrix.scale(1 / 16f, -1 / 16f, 0.00005f);

        matrix.translate(offsetX, offsetY, 0);
        matrix.scale(scaleX, scaleY, 1);
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
