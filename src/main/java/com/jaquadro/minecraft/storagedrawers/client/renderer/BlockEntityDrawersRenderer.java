package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.Drawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.config.ClientConfig;
import com.jaquadro.minecraft.storagedrawers.util.CountFormatter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BlockEntityDrawersRenderer implements BlockEntityRenderer<BlockEntityDrawers>
{
    private final ItemStack[] renderStacks = new ItemStack[4];

    private ItemRenderer itemRenderer;

    private final BlockEntityRendererProvider.Context context;

    public BlockEntityDrawersRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render (@NotNull BlockEntityDrawers blockEntityDrawers, float partialTickTime, @NotNull PoseStack matrix, @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;

        Level level = blockEntityDrawers.getLevel();
        if (level == null)
            return;

        BlockState state = blockEntityDrawers.getBlockState();
        if (!(state.getBlock() instanceof BlockDrawers))
            return;

        Direction side = state.getValue(BlockDrawers.FACING);
        if (playerBehindBlock(blockEntityDrawers.getBlockPos(), side))
            return;

        float distance = (float)Math.sqrt(blockEntityDrawers.getBlockPos().distToCenterSqr(player.position()));

        double renderDistance = ClientConfig.RENDER.labelRenderDistance.get();
        if (renderDistance > 0 && distance > renderDistance)
            return;

        itemRenderer = Minecraft.getInstance().getItemRenderer();

        if (blockEntityDrawers.upgrades().hasIlluminationUpgrade()) {
            int blockLight = Math.max(combinedLight % 65536, 208);
            combinedLight = (combinedLight & 0xFFFF0000) | blockLight;
        }

        if (!blockEntityDrawers.getDrawerAttributes().isConcealed())
            renderFastItemSet(blockEntityDrawers, state, matrix, buffer, combinedLight, combinedOverlay, side, partialTickTime, distance);

        if (blockEntityDrawers.getDrawerAttributes().hasFillLevel())
            renderIndicator((BlockDrawers)state.getBlock(), blockEntityDrawers, matrix, buffer, state.getValue(BlockDrawers.FACING), combinedLight, combinedOverlay);

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

    private void renderFastItemSet (BlockEntityDrawers blockEntityDrawers, BlockState state, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Direction side, float partialTickTime, float distance) {
        int drawerCount = blockEntityDrawers.getGroup().getDrawerCount();

        for (int i = 0; i < drawerCount; i++) {
            renderStacks[i] = ItemStack.EMPTY;
            IDrawer drawer = blockEntityDrawers.getGroup().getDrawer(i);
            if (!drawer.isEnabled() || drawer.isEmpty())
                continue;

            ItemStack itemStack = drawer.getStoredItemPrototype();
            renderStacks[i] = itemStack;
        }

        for (int i = 0; i < drawerCount; i++) {
            ItemStack itemStack = renderStacks[i];
            if (!itemStack.isEmpty())
                renderFastItem(itemStack, state, i, matrix, buffer, combinedLight, combinedOverlay, side);
        }

        if (blockEntityDrawers.getDrawerAttributes().isShowingQuantity()) {
            float alpha = 1;
            double fadeDistance = ClientConfig.RENDER.quantityFadeDistance.get();
            if (fadeDistance == 0 || distance > fadeDistance)
                alpha = Math.max(1f - ((distance - 4) / 6), 0.05f);

            double renderDistance = ClientConfig.RENDER.quantityRenderDistance.get();
            if (renderDistance == 0 || distance < renderDistance) {
                for (int i = 0; i < drawerCount; i++) {
                    String format = CountFormatter.format(this.context.getFont(), blockEntityDrawers.getGroup().getDrawer(i));
                    renderText(format, state, i, matrix, buffer, combinedLight, side, alpha);
                }
            }
        }
    }

    private static final int TEXT_COLOR_TRANSPARENT = FastColor.ARGB32.color(0, 255, 255, 255);

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
        matrix.translate(x / 16, 1 - y / 16, 1 - z);
        // Text is rendered upside-down and flipped by default, so Y needs to be inverted
        matrix.scale(1/128f, -1/128f, 1);

        int color = (int)(255 * alpha) << 24 | TEXT_COLOR_TRANSPARENT;
        fontRenderer.drawInBatch(text, -textWidth / 2f, 0, color, false, matrix.last().pose(), buffer, false, 0, combinedLight); // 15728880

        matrix.popPose();
    }

    private static final Quaternion ITEM_LIGHT_ROTATION_3D = Util.make(() -> {
        Quaternion quaternion = new Quaternion(Vector3f.XP, -15f, true);
        quaternion.mul(new Quaternion(Vector3f.YP, 15f, true));
        return quaternion;
    });
    private static final Quaternion ITEM_LIGHT_ROTATION_FLAT = new Quaternion(Vector3f.XP, -45f, true);

    private void renderFastItem(@NotNull ItemStack itemStack, BlockState state, int slot, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Direction side) {
        BlockDrawers block = (BlockDrawers)state.getBlock();
        AABB labelGeometry = block.labelGeometry[slot];

        float scaleX = (float)labelGeometry.getXsize() / 16;
        float scaleY = (float)labelGeometry.getYsize() / 16;
        float moveX = (float)labelGeometry.minX + (8 * scaleX);
        float moveY = 16f - (float)labelGeometry.maxY + (8 * scaleY);
        float moveZ = (float)labelGeometry.minZ * .0625f - 0.0025f;

        matrix.pushPose();

        alignRendering(matrix, side);
        matrix.translate(moveX / 16, 1 - moveY / 16, 1 - moveZ);
        matrix.mulPoseMatrix(Matrix4f.createScaleMatrix(scaleX, scaleY, 0.001f));

        try {
            BakedModel itemModel = itemRenderer.getModel(itemStack, null, null, 0);

            if (itemModel.isGui3d())
                matrix.last().normal().mul(ITEM_LIGHT_ROTATION_3D);
            else
                matrix.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);

            ItemTransforms.TransformType context = ItemTransforms.TransformType.GUI;
            if (itemModel.isCustomRenderer())
                context = ItemTransforms.TransformType.FIXED;

            itemRenderer.render(itemStack, context, false, matrix, buffer, combinedLight, combinedOverlay, itemModel);
        } catch (Exception e) {
            // Shrug
        }

        matrix.popPose();
    }

    private void alignRendering (PoseStack matrix, Direction side) {
        // Rotate to face the correct direction for the drawer's orientation.

        matrix.translate(.5f, 0, .5f);
        matrix.mulPoseMatrix(new Matrix4f(new Quaternion(Vector3f.YP, getRotationYForSide2D(side), true)));
        matrix.translate(-.5f, 0, -.5f);
    }

//    private void moveRendering (PoseStack matrix, float scaleX, float scaleY, float offsetX, float offsetY, float offsetZ) {
//        // NOTE: RenderItem expects to be called in a context where Y increases toward the bottom of the screen
//        // However, for in-world rendering the opposite is true. So we translate up by 1 along Y, and then flip
//        // along Y. Since the item is drawn at the back of the drawer, we also translate by `1-offsetZ` to move
//        // it to the front.
//
//        // The 0.00001 for the Z-scale both flattens the item and negates the 32.0 Z-scale done by RenderItem.
//
//        matrix.translate(0, 1, 1-offsetZ);
//        matrix.scale(1 / 16f, -1 / 16f, 0.00005f);
//
//        matrix.translate(offsetX, offsetY, 0);
//        matrix.scale(scaleX, scaleY, 1);
//    }

    private static final float[] sideRotationY2D = { 0, 0, 2, 0, 3, 1 };

    private float getRotationYForSide2D (Direction side) {
        return sideRotationY2D[side.ordinal()] * 90;
    }

    public static final ResourceLocation TEXTURE_IND_1 = StorageDrawers.rl("blocks/indicator/indicator_1_on");
    public static final ResourceLocation TEXTURE_IND_2 = StorageDrawers.rl("blocks/indicator/indicator_2_on");
    public static final ResourceLocation TEXTURE_IND_4 = StorageDrawers.rl("blocks/indicator/indicator_4_on");
    public static final ResourceLocation TEXTURE_IND_COMP = StorageDrawers.rl("blocks/indicator/indicator_comp_on");

    private void renderIndicator (BlockDrawers block, BlockEntityDrawers blockEntityDrawers, PoseStack matrixStack, MultiBufferSource buffer, Direction side, int combinedLight, int combinedOverlay) {
        int count = (blockEntityDrawers instanceof BlockEntityDrawersComp) ? 1 : block.getDrawerCount();

        ResourceLocation resource = TEXTURE_IND_1;
        if (blockEntityDrawers instanceof BlockEntityDrawersComp)
            resource = TEXTURE_IND_COMP;
        else if (count == 2)
            resource = TEXTURE_IND_2;
        else if (count == 4)
            resource = TEXTURE_IND_4;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(resource);
        float u1 = sprite.getU0();
        float u2 = sprite.getU1();
        float v1 = sprite.getV0();
        float v2 = sprite.getV1();
        float pxW = sprite.getWidth();
        float pxH = sprite.getHeight();

        float unit = 0.0625f;
        float divU = unit * (u2 - u1);
        float divV = unit * (v2 - v1);

        matrixStack.pushPose();

        alignRendering(matrixStack, side);

        for (int i = 0; i < count; i++) {
            IDrawer drawer = blockEntityDrawers.getGroup().getDrawer(i);
            if (drawer == Drawers.DISABLED || blockEntityDrawers.getDrawerAttributes().isConcealed())
                continue;

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

            float xCur = (stepX == 0) ? x2 : getIndEnd(blockEntityDrawers, i, x1, x2 - xb2, stepX);
            float xFrac = (x2 == xb2) ? 1 : (xCur - x1) / (x2 - xb2);
            float uCur = su1 + xFrac * (su2 - su1);

            float yCur = (stepY == 0) ? y2 : getIndEnd(blockEntityDrawers, i, y1, y2 - yb2, stepY);
            float yFrac = (y2 == yb2) ? 1 : (yCur - y1) / (y2 - yb2);
            float vCur = sv1 + yFrac * (sv2 - sv1);

            if (xCur > x1 && yCur > y1) {
                Matrix4f matrix = matrixStack.last().pose();
                Matrix3f normal = matrixStack.last().normal();
                VertexConsumer builder = buffer.getBuffer(RenderType.solid());
                addQuad(matrix, normal, builder, combinedLight, combinedOverlay, x1, xCur, y1, yCur, z, uCur, su1, sv1, vCur);
            }
        }

        matrixStack.popPose();
    }

    public static void addQuad(Matrix4f matrix, Matrix3f normal, VertexConsumer buffer, int combinedLight, int combinedOverlay, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
        addVertex(matrix, normal, buffer, combinedLight, combinedOverlay, x2, y1, z, u1, v1);
        addVertex(matrix, normal, buffer, combinedLight, combinedOverlay, x2, y2, z, u1, v2);
        addVertex(matrix, normal, buffer, combinedLight, combinedOverlay, x1, y2, z, u2, v2);
        addVertex(matrix, normal, buffer, combinedLight, combinedOverlay, x1, y1, z, u2, v1);
    }

    private static void addVertex(Matrix4f matrix, Matrix3f normal, VertexConsumer buffer, int combinedLight, int combinedOverlay, float x, float y, float z, float u, float v) {
        buffer.vertex(matrix, x, y, z).color(1f, 1f, 1f, 1f).uv(u, v).overlayCoords(combinedOverlay).uv2(combinedLight).normal(normal, 0, 1, 0).endVertex();
    }

    private float getIndEnd (BlockEntityDrawers blockEntityDrawers, int slot, float x, float w, int step) {
        IDrawer drawer = blockEntityDrawers.getGroup().getDrawer(slot);
        if (drawer == Drawers.DISABLED)
            return x;

        int cap = drawer.getMaxCapacity();
        int count = drawer.getStoredItemCount();
        if (cap == 0 || count == 0)
            return x;

        float fillAmt = (float)(step * count / cap) / step;

        return x + (w * fillAmt);
    }
}
