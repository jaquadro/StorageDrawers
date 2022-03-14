package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.Drawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.config.ClientConfig;
import com.jaquadro.minecraft.storagedrawers.util.CountFormatter;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
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

        Direction side = state.getValue(BlockDrawers.FACING);
        if (playerBehindBlock(tile.getBlockPos(), side))
            return;

        Player player = Minecraft.getInstance().player;
        BlockPos blockPos = tile.getBlockPos().offset(.5, .5, .5);
        float distance = (float)Math.sqrt(blockPos.distToCenterSqr(player.position()));

        double renderDistance = ClientConfig.RENDER.labelRenderDistance.get();
        if (renderDistance > 0 && distance > renderDistance)
            return;

        renderItem = Minecraft.getInstance().getItemRenderer();

        if (tile.upgrades().hasIlluminationUpgrade()) {
            int blockLight = Math.max(combinedLight % 65536, 208);
            combinedLight = (combinedLight & 0xFFFF0000) | blockLight;
        }

        Minecraft mc = Minecraft.getInstance();
        GraphicsStatus cache = mc.options.graphicsMode;
        mc.options.graphicsMode = GraphicsStatus.FANCY;

        if (!tile.getDrawerAttributes().isConcealed())
            renderFastItemSet(tile, state, matrix, buffer, combinedLight, combinedOverlay, side, partialTickTime, distance);

        if (tile.getDrawerAttributes().hasFillLevel())
            renderIndicator((BlockDrawers)state.getBlock(), tile, matrix, buffer, state.getValue(BlockDrawers.FACING), combinedLight, combinedOverlay);

        mc.options.graphicsMode = cache;

        matrix.popPose();
        Lighting.setupLevel(matrix.last().pose());
        matrix.pushPose();
    }

    private boolean playerBehindBlock(BlockPos blockPos, Direction facing) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return false;

        BlockPos playerPos = player.blockPosition();
        switch (facing) {
            case NORTH:
                return playerPos.getZ() > blockPos.getZ();
            case SOUTH:
                return playerPos.getZ() < blockPos.getZ();
            case WEST:
                return playerPos.getX() > blockPos.getX();
            case EAST:
                return playerPos.getX() < blockPos.getX();
            default:
                return false;
        }
    }

    private void renderFastItemSet (TileEntityDrawers tile, BlockState state, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Direction side, float partialTickTime, float distance) {
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
            float alpha = 1;
            double fadeDistance = ClientConfig.RENDER.quantityFadeDistance.get();
            if (fadeDistance == 0 || distance > fadeDistance)
                alpha = Math.max(1f - ((distance - 4) / 6), 0.05f);

            double renderDistance = ClientConfig.RENDER.quantityRenderDistance.get();
            if (renderDistance == 0 || distance < renderDistance) {
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

    public static final ResourceLocation TEXTURE_IND_1 = new ResourceLocation(StorageDrawers.MOD_ID, "blocks/indicator/indicator_1_on");
    public static final ResourceLocation TEXTURE_IND_2 = new ResourceLocation(StorageDrawers.MOD_ID, "blocks/indicator/indicator_2_on");
    public static final ResourceLocation TEXTURE_IND_4 = new ResourceLocation(StorageDrawers.MOD_ID, "blocks/indicator/indicator_4_on");
    public static final ResourceLocation TEXTURE_IND_COMP = new ResourceLocation(StorageDrawers.MOD_ID, "blocks/indicator/indicator_comp_on");

    private void renderIndicator (BlockDrawers block, TileEntityDrawers tile, PoseStack matrixStack, MultiBufferSource buffer, Direction side, int combinedLight, int combinedOverlay) {
        int count = (tile instanceof TileEntityDrawersComp) ? 1 : block.getDrawerCount();

        ResourceLocation resource = TEXTURE_IND_1;
        if (tile instanceof TileEntityDrawersComp)
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
            IDrawer drawer = tile.getGroup().getDrawer(i);
            if (drawer == Drawers.DISABLED || tile.getDrawerAttributes().isConcealed())
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

            float xCur = (stepX == 0) ? x2 : getIndEnd(tile, i, x1, x2 - xb2, stepX);
            float xFrac = (x2 == xb2) ? 1 : (xCur - x1) / (x2 - xb2);
            float uCur = su1 + xFrac * (su2 - su1);

            float yCur = (stepY == 0) ? y2 : getIndEnd(tile, i, y1, y2 - yb2, stepY);
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

    private float getIndEnd (TileEntityDrawers tile, int slot, float x, float w, int step) {
        IDrawer drawer = tile.getGroup().getDrawer(slot);
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
