package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class StorageRenderItem extends ItemRenderer
{
    private final ItemRenderer parent;

    @NotNull
    public ItemStack overrideStack;

    public StorageRenderItem (Minecraft mc, TextureManager texManager, ModelManager modelManager, ItemColors colors) {
        super(mc, texManager, modelManager, colors, null);
        parent = Minecraft.getInstance().getItemRenderer();
        overrideStack = ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemModelShaper getItemModelShaper () {
        return parent.getItemModelShaper();
    }

    @Override
    public void render(@NotNull ItemStack itemStackIn, ItemDisplayContext displayContext, boolean leftHand, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, @NotNull BakedModel modelIn) {
        parent.render(itemStackIn, displayContext, leftHand, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, modelIn);
    }

    @Override
    public void renderStatic(@Nullable LivingEntity livingEntityIn, @NotNull ItemStack itemStackIn, ItemDisplayContext displayContext, boolean leftHand, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, @Nullable Level worldIn, int combinedLightIn, int combinedOverlayIn, int p_174252_) {
        parent.renderStatic(livingEntityIn, itemStackIn, displayContext, leftHand, matrixStackIn, bufferIn, worldIn, combinedLightIn, combinedOverlayIn, p_174252_);
    }

    @Override
    public void renderStatic(@NotNull ItemStack itemStackIn, ItemDisplayContext displayContext, int combinedLightIn, int combinedOverlayIn, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, @Nullable Level worldIn, int p_174276_) {
        parent.renderStatic(itemStackIn, displayContext, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, worldIn, p_174276_);
    }

    public void renderQuadList(@NotNull PoseStack matrixStackIn, @NotNull VertexConsumer bufferIn, @NotNull List<BakedQuad> quadsIn, @NotNull ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn) {
        parent.renderQuadList(matrixStackIn, bufferIn, quadsIn, itemStackIn, combinedLightIn, combinedOverlayIn);
    }

    @Override
    @NotNull
    public BakedModel getModel (@NotNull ItemStack stack, Level world, LivingEntity entity, int p_174268_) {
        return parent.getModel(stack, world, entity, p_174268_);
    }

    @Override
    public void renderGuiItem (PoseStack poseStack, @NotNull ItemStack stack, int x, int y) {
        parent.renderGuiItem(poseStack, stack, x, y);
    }

    @Override
    public void renderAndDecorateItem (PoseStack poseStack, @NotNull ItemStack stack, int xPosition, int yPosition) {
        parent.renderAndDecorateItem(poseStack, stack, xPosition, yPosition);
    }

    @Override
    public void renderAndDecorateItem(PoseStack poseStack, @NotNull LivingEntity entityIn, @NotNull ItemStack itemIn, int x, int y, int p_174234_) {
        parent.renderAndDecorateItem(poseStack, entityIn, itemIn, x, y, p_174234_);
    }

    @Override
    public void renderGuiItemDecorations (PoseStack poseStack, @NotNull Font fr, @NotNull ItemStack stack, int xPosition, int yPosition) {
        parent.renderGuiItemDecorations(poseStack, fr, stack, xPosition, yPosition);
    }

    @Override
    public void renderGuiItemDecorations (PoseStack poseStack, @NotNull Font font, @NotNull ItemStack item, int x, int y, String text)
    {
        if (item != overrideStack) {
            super.renderGuiItemDecorations(poseStack, font, item, x, y, text);
            return;
        }

        if (!item.isEmpty())
        {
            item = ItemStackHelper.decodeItemStack(item);

            float scale = .5f;
            float xoff = 0;
            //if (font.getUnicodeFlag()) {
            //    scale = 1f;
            //    xoff = 1;
            //}

            int stackSize = item.getCount();
            if (ItemStackHelper.isStackEncoded(item))
                stackSize = 0;

            poseStack.pushPose();
            if (stackSize >= 0 || text != null) {
                if (stackSize >= 100000000)
                    text = (text == null) ? String.format("%.0fM", stackSize / 1000000f) : text;
                else if (stackSize >= 1000000)
                    text = (text == null) ? String.format("%.1fM", stackSize / 1000000f) : text;
                else if (stackSize >= 100000)
                    text = (text == null) ? String.format("%.0fK", stackSize / 1000f) : text;
                else if (stackSize >= 10000)
                    text = (text == null) ? String.format("%.1fK", stackSize / 1000f) : text;
                else
                    text = (text == null) ? String.valueOf(stackSize) : text;

                int textX = (int) ((x + 16 + xoff - font.width(text) * scale) / scale) - 1;
                int textY = (int) ((y + 16 - 7 * scale) / scale) - 1;

                int color = 16777215;
                if (stackSize == 0)
                    color = (255 << 16) | (96 << 8) | (96);

                poseStack.scale(scale, scale, 1);
                poseStack.translate(0.0D, 0.0D, 200.0F);
                MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                font.drawInBatch(text, textX, textY, color, true, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 15728880);
                buffer.endBatch();
            }

            if (item.getItem().isBarVisible(item)) {
                RenderSystem.disableDepthTest();
                int barWidth = item.getItem().getBarWidth(item);
                int j = item.getItem().getBarColor(item);
                int x1 = x + 2;
                int y1 = y + 13;
                GuiComponent.fill(poseStack, x1, y1, x1 + 13, y1 + 2, -16777216);
                GuiComponent.fill(poseStack, x1, y1, x1 + barWidth, y1 + 1, 1 | -16777216);
                RenderSystem.enableDepthTest();
            }

            LocalPlayer clientplayerentity = Minecraft.getInstance().player;
            float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldowns().getCooldownPercent(item.getItem(), Minecraft.getInstance().getFrameTime());
            if (f3 > 0.0F) {
                RenderSystem.disableDepthTest();
                int y1 = y + Mth.floor(16.0F * (1.0F - f3));
                int y2 = y1 + Mth.ceil(16.0f * f3);
                GuiComponent.fill(poseStack, x, y1, x + 16, y2, Integer.MAX_VALUE);
                RenderSystem.enableDepthTest();
            }

            poseStack.popPose();
        }
    }

    @Override
    public void onResourceManagerReload (@NotNull ResourceManager p_195410_1_) {
        parent.onResourceManagerReload(p_195410_1_);
    }

    private void draw (BufferBuilder tessellator, int x, int y, int w, int h, int r, int g, int b, int a)
    {
        tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        tessellator.vertex(x + 0, y + 0, 0).color(r, g, b, a).endVertex();
        tessellator.vertex(x + 0, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.vertex(x + w, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.vertex(x + w, y + 0, 0).color(r, g, b, a).endVertex();
        Tesselator.getInstance().end();
    }
}
