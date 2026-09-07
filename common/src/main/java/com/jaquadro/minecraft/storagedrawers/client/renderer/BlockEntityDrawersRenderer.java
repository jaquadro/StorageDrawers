package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.state.DrawersRenderState;
import com.jaquadro.minecraft.storagedrawers.config.ModClientConfig;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.util.CountFormatter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class BlockEntityDrawersRenderer implements BlockEntityRenderer<BlockEntityDrawers, DrawersRenderState>
{
    private final BlockEntityRendererProvider.Context context;

    public BlockEntityDrawersRenderer (BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public DrawersRenderState createRenderState () {
        return new DrawersRenderState();
    }

    @Override
    public void extractRenderState (BlockEntityDrawers blockEntity, DrawersRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumbleOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumbleOverlay);

        renderState.blockState = blockEntity.getBlockState();
        renderState.cameraPos = cameraPos;
        renderState.enforcedLightLevel = blockEntity.upgrades().hasIlluminationUpgrade()
            ? ModCommonConfig.INSTANCE.UPGRADES.illuminationUpgrade.illuminationLevel.get()
            : ModCommonConfig.INSTANCE.UPGRADES.illuminationUpgrade.minIlluminationLevel.get();

        int blockLight = Math.max(renderState.lightCoords % 65536, renderState.enforcedLightLevel * 16);
        renderState.lightCoords = (renderState.lightCoords & 0xFFFF0000) | blockLight;

        IDrawerAttributes attr = blockEntity.getDrawerAttributes();
        renderState.isConcealed = attr.isConcealed();
        renderState.showCount = attr.isShowingQuantity();
        renderState.showFill = attr.hasFillLevel();

        int longPos = (int)blockEntity.getBlockPos().asLong();

        renderState.items = new ArrayList<>();
        IDrawerGroup group = blockEntity.getGroup();

        for (int i = 0; i < group.getDrawerCount(); i++) {
            IDrawer drawer = group.getDrawer(i);
            ItemStackRenderState itemState = new ItemStackRenderState();
            context.itemModelResolver().updateForTopItem(itemState, drawer.getStoredItemPrototype(), ItemDisplayContext.GUI, blockEntity.getLevel(), null, longPos + i);
            renderState.items.add(new DrawersRenderState.SlotState(itemState, drawer.getStoredItemCount(), drawer.getMaxCapacity()));
        }
    }

    @Override
    public void submit (DrawersRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (!(renderState.blockState.getBlock() instanceof BlockDrawers))
            return;

        Direction side = renderState.blockState.getValue(BlockDrawers.FACING);
        if (playerBehindBlock(renderState.blockPos, side))
            return;

        float distance = (float)Math.sqrt(renderState.blockPos.distToCenterSqr(renderState.cameraPos));
        double renderDistance = ModClientConfig.INSTANCE.RENDER.labelRenderDistance.get();
        if (renderDistance > 0 && distance > renderDistance)
            return;

        if (!renderState.isConcealed)
            renderFastItemSet(renderState, poseStack, submitNodeCollector, cameraRenderState, distance);

        if (renderState.showFill)
            renderIndicator(renderState, poseStack, submitNodeCollector, cameraRenderState);
    }

    private boolean playerBehindBlock(BlockPos blockPos, Direction facing) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return false;

        BlockPos playerPos = player.blockPosition();
        return switch (facing) {
            case NORTH -> playerPos.getZ() > blockPos.getZ();
            case SOUTH -> playerPos.getZ() < blockPos.getZ();
            case WEST -> playerPos.getX() > blockPos.getX();
            case EAST -> playerPos.getX() < blockPos.getX();
            default -> false;
        };
    }

    private void renderFastItemSet (DrawersRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, float distance) {
        int drawerCount = renderState.items.size();
        for (int i = 0; i < drawerCount; i++)
            renderFastItem(i, renderState, poseStack, submitNodeCollector, cameraRenderState);

        if (renderState.showCount) {
            float alpha = 1;
            double fadeDistance = ModClientConfig.INSTANCE.RENDER.quantityFadeDistance.get();
            if (fadeDistance == 0 || distance > fadeDistance)
                alpha = Math.max(1f - ((distance - 4) / 6), 0.05f);

            double renderDistance = ModClientConfig.INSTANCE.RENDER.quantityRenderDistance.get();
            if (renderDistance == 0 || distance < renderDistance) {
                for (int i = 0; i < drawerCount; i++) {
                      String format = CountFormatter.format(this.context.font(), renderState.items.get(i).count());
                    renderText(i, format, renderState, poseStack, submitNodeCollector, cameraRenderState, alpha);
                }
            }
        }
    }

    private static final int TEXT_COLOR_TRANSPARENT = ARGB.color(0, 255, 255, 255);

    private void renderText (int slot, String text, DrawersRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, float alpha) {
        DrawersRenderState.SlotState slotInfo = renderState.items.get(slot);
        ItemStackRenderState itemState = slotInfo.itemState();
        if (itemState == null || itemState.isEmpty())
            return;

        if (text == null || text.isEmpty())
            return;

        Font fontRenderer = this.context.font();

        if (!(renderState.blockState.getBlock() instanceof BlockDrawers block))
            return;

        AABB labelGeometry = block.countGeometry[slot];
        int textWidth = fontRenderer.width(text);

        float x = (float)(labelGeometry.minX + labelGeometry.getXsize() / 2);
        float y = 16f - (float)labelGeometry.minY - (float)labelGeometry.getYsize();
        float z = (float)labelGeometry.minZ * .0625f - .01f;

        poseStack.pushPose();

        Direction side = renderState.blockState.getValue(BlockDrawers.FACING);
        alignRendering(poseStack, side);
        poseStack.translate(x / 16, 1 - y / 16, 1 - z);
        // Text is rendered upside-down and flipped by default, so Y needs to be inverted
        poseStack.scale(1/128f, -1/128f, 1);

        int color = (int)(255 * alpha) << 24 | TEXT_COLOR_TRANSPARENT;
        submitNodeCollector.submitText(poseStack, -textWidth / 2f, 0, FormattedCharSequence.forward(text, Style.EMPTY), false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightCoords, color, 0, 0);
        // fontRenderer.drawInBatch(text, -textWidth / 2f, 0, color, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, combinedLight); // 15728880

        poseStack.popPose();
    }

    private static final Matrix3f ITEM_LIGHT_ROTATION_3D = (new Matrix3f()).rotationYXZ(.36f, -.36f, -.014f);

    private void renderFastItem(int slot, DrawersRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        DrawersRenderState.SlotState slotInfo = renderState.items.get(slot);
        ItemStackRenderState itemState = slotInfo.itemState();
        if (itemState == null || itemState.isEmpty())
            return;

        if (!(renderState.blockState.getBlock() instanceof BlockDrawers block))
            return;

        AABB labelGeometry = block.labelGeometry[slot];
        float scaleX = (float)labelGeometry.getXsize() / 16;
        float scaleY = (float)labelGeometry.getYsize() / 16;
        float moveX = (float)labelGeometry.minX + (8 * scaleX);
        float moveY = 16f - (float)labelGeometry.maxY + (8 * scaleY);
        float moveZ = (float)labelGeometry.minZ * .0625f - 0.0025f;

        poseStack.pushPose();

        Direction side = renderState.blockState.getValue(BlockDrawers.FACING);
        alignRendering(poseStack, side);
        poseStack.translate(moveX / 16, 1 - moveY / 16, 1 - moveZ);
        poseStack.mulPose((new Matrix4f()).scale(scaleX, scaleY, 0.001f));
        poseStack.last().trustedNormals = true;

        try {

            //context.itemModelResolver().updateForTopItem(
            //    this.itemRenderState, itemStack, ItemDisplayContext.GUI, context.blockEntityRenderDispatcher(), null, 0
            //);

            poseStack.last().normal().rotateYXZ(-getRotationYForSide2D(side), 0, 0).mul(ITEM_LIGHT_ROTATION_3D);
            itemState.submit(poseStack, submitNodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            //this.itemRenderState.render(matrix, buffer, combinedLight, combinedOverlay);
        } catch (Exception e) {
            // Shrug
        }

        poseStack.popPose();
    }

    private void alignRendering (PoseStack poseStack, Direction side) {
        // Rotate to face the correct direction for the drawer's orientation.

        poseStack.translate(.5f, 0, .5f);
        poseStack.mulPose((new Matrix4f()).rotateYXZ(getRotationYForSide2D(side), 0, 0));
        poseStack.translate(-.5f, 0, -.5f);
    }

    private static final float[] sideRotationY2D = { 0, 0, 2, 0, 3, 1 };

    private float getRotationYForSide2D (Direction side) {
        return sideRotationY2D[side.ordinal()] * 90 * (float)Math.PI / 180f;
    }

    private void renderIndicator (DrawersRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        Direction side = renderState.blockState.getValue(BlockDrawers.FACING);
        alignRendering(poseStack, side);

        QuadBuilder quadBuilder = new QuadBuilder(renderState);
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.solidMovingBlock(), quadBuilder);

        poseStack.popPose();
    }

    static class QuadBuilder implements SubmitNodeCollector.CustomGeometryRenderer
    {
        public static final Identifier TEXTURE_IND_1 = ModConstants.loc("block/indicator/indicator_1_on");
        public static final Identifier TEXTURE_IND_2 = ModConstants.loc("block/indicator/indicator_2_on");
        public static final Identifier TEXTURE_IND_4 = ModConstants.loc("block/indicator/indicator_4_on");
        public static final Identifier TEXTURE_IND_COMP_3 = ModConstants.loc("block/indicator/indicator_comp_on");
        public static final Identifier TEXTURE_IND_COMP_2 = ModConstants.loc("block/indicator/indicator_comp2_on");

        public static final SpriteId MAT_IND_1 = new SpriteId(TextureAtlas.LOCATION_BLOCKS, TEXTURE_IND_1);
        public static final SpriteId MAT_IND_2 = new SpriteId(TextureAtlas.LOCATION_BLOCKS, TEXTURE_IND_2);
        public static final SpriteId MAT_IND_4 = new SpriteId(TextureAtlas.LOCATION_BLOCKS, TEXTURE_IND_4);
        public static final SpriteId MAT_IND_COMP_3 = new SpriteId(TextureAtlas.LOCATION_BLOCKS, TEXTURE_IND_COMP_3);
        public static final SpriteId MAT_IND_COMP_2 = new SpriteId(TextureAtlas.LOCATION_BLOCKS, TEXTURE_IND_COMP_2);

        DrawersRenderState renderState;

        public QuadBuilder (DrawersRenderState renderState) {
            this.renderState = renderState;
        }

        @Override
        public void render (PoseStack.Pose pose, VertexConsumer vertexConsumer) {
            if (renderState.isConcealed)
                return;

            int count = renderState.items.size();
            if (renderState.blockState.getBlock() instanceof BlockCompDrawers)
                count = 1;

            SpriteId mat = MAT_IND_1;
            if (renderState.blockState.getBlock() instanceof BlockCompDrawers)
                mat = renderState.items.size() == 2 ? MAT_IND_COMP_2 : MAT_IND_COMP_3;
            else if (count == 2)
                mat = MAT_IND_2;
            else if (count == 4)
                mat = MAT_IND_4;

            TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().get(mat);
            SpriteContents contents = sprite.contents();
            float u1 = sprite.getU0();
            float u2 = sprite.getU1();
            float v1 = sprite.getV0();
            float v2 = sprite.getV1();
            float pxW = contents.width();
            float pxH = contents.height();

            float unit = 0.0625f;
            float divU = unit * (u2 - u1);
            float divV = unit * (v2 - v1);

            BlockDrawers block = (BlockDrawers)renderState.blockState.getBlock();
            for (int i = 0; i < renderState.items.size(); i++) {
                DrawersRenderState.SlotState slot = renderState.items.get(i);

                AABB bb = block.indGeometry[i];
                AABB bbbase = block.indBaseGeometry[i];
                float x1 = unit * (float)bb.minX;
                float x2 = unit * (float)bb.maxX;
                float xb2 = unit * (float)bbbase.maxX;
                float y1 = unit * (float)bb.minY;
                float y2 = unit * (float)bb.maxY;
                float yb2 = unit * (float)bbbase.maxY;
                float z = 1 - (unit * (float)bb.minZ);

                float su1 = u1 + (float)bb.minX * divU;
                float su2 = u1 + (float)bb.maxX * divU;
                float sv1 = v2 - (float)bb.minY * divV;
                float sv2 = v2 - (float)bb.maxY * divV;

                int stepX = (int)((x2 - xb2) * pxW);
                int stepY = (int)((y2 - yb2) * pxH);

                float xCur = (stepX == 0) ? x2 : getIndEnd(slot.count(), slot.limit(), i, x1, x2 - xb2, stepX);
                float xFrac = (x2 == xb2) ? 1 : (xCur - x1) / (x2 - xb2);
                float uCur = su1 + xFrac * (su2 - su1);

                float yCur = (stepY == 0) ? y2 : getIndEnd(slot.count(), slot.limit(), i, y1, y2 - yb2, stepY);
                float yFrac = (y2 == yb2) ? 1 : (yCur - y1) / (y2 - yb2);
                float vCur = sv1 + yFrac * (sv2 - sv1);

                if (xCur > x1 && yCur > y1) {
                    Matrix4f matrix = pose.pose();
                    addQuad(matrix, pose, vertexConsumer, renderState.lightCoords, x1, xCur, y1, yCur, z, uCur, su1, sv1, vCur);
                }
            }
        }

        private static float getIndEnd (int storedCount, int maxCapacity, int slot, float x, float w, int step) {
            if (maxCapacity == 0 || storedCount == 0)
                return x;

            float fillAmt = (float)(step * storedCount / maxCapacity) / step;

            return x + (w * fillAmt);
        }

        public static void addQuad(Matrix4f matrix, PoseStack.Pose normal, VertexConsumer buffer, int combinedLight, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
            addVertex(matrix, normal, buffer, combinedLight, x2, y1, z, u1, v1);
            addVertex(matrix, normal, buffer, combinedLight, x2, y2, z, u1, v2);
            addVertex(matrix, normal, buffer, combinedLight, x1, y2, z, u2, v2);
            addVertex(matrix, normal, buffer, combinedLight, x1, y1, z, u2, v1);
        }

        private static void addVertex(Matrix4f matrix, PoseStack.Pose normal, VertexConsumer buffer, int combinedLight, float x, float y, float z, float u, float v) {
            buffer.addVertex(matrix, x, y, z).setColor(1f, 1f, 1f, 1f).setUv(u, v).setLight(combinedLight).setNormal(normal, 0, 1, 0);
        }
    }
}
