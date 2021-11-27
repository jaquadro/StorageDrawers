package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.entity.ItemRenderer;

@OnlyIn(Dist.CLIENT)
public class StorageRenderItem extends ItemRenderer
{
    private ItemRenderer parent;

    @Nonnull
    public ItemStack overrideStack;

    public StorageRenderItem (TextureManager texManager, ModelManager modelManager, ItemColors colors) {
        super(texManager, modelManager, colors, null);
        parent = Minecraft.getInstance().getItemRenderer();
        overrideStack = ItemStack.EMPTY;
    }

    @Override
    public ItemModelShaper getItemModelShaper () {
        return parent.getItemModelShaper();
    }

    @Override
    public void render(ItemStack itemStackIn, ItemTransforms.TransformType transformTypeIn, boolean leftHand, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BakedModel modelIn) {
        parent.render(itemStackIn, transformTypeIn, leftHand, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, modelIn);
    }

    @Override
    public void renderStatic(@Nullable LivingEntity livingEntityIn, ItemStack itemStackIn, ItemTransforms.TransformType transformTypeIn, boolean leftHand, PoseStack matrixStackIn, MultiBufferSource bufferIn, @Nullable Level worldIn, int combinedLightIn, int combinedOverlayIn, int p_174252_) {
        parent.renderStatic(livingEntityIn, itemStackIn, transformTypeIn, leftHand, matrixStackIn, bufferIn, worldIn, combinedLightIn, combinedOverlayIn, p_174252_);
    }

    @Override
    public void renderStatic(ItemStack itemStackIn, ItemTransforms.TransformType transformTypeIn, int combinedLightIn, int combinedOverlayIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int p_174276_) {
        parent.renderStatic(itemStackIn, transformTypeIn, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, p_174276_);
    }

    public void renderQuadList(PoseStack matrixStackIn, VertexConsumer bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn) {
        parent.renderQuadList(matrixStackIn, bufferIn, quadsIn, itemStackIn, combinedLightIn, combinedOverlayIn);
    }

    @Override
    public BakedModel getModel (@Nonnull ItemStack stack, Level world, LivingEntity entity, int p_174268_) {
        return parent.getModel(stack, world, entity, p_174268_);
    }

    @Override
    public void renderGuiItem (@Nonnull ItemStack stack, int x, int y) {
        parent.renderGuiItem(stack, x, y);
    }

    @Override
    public void renderAndDecorateItem (@Nonnull ItemStack stack, int xPosition, int yPosition) {
        parent.renderAndDecorateItem(stack, xPosition, yPosition);
    }

    @Override
    public void renderAndDecorateItem(@Nullable LivingEntity entityIn, ItemStack itemIn, int x, int y, int p_174234_) {
        parent.renderAndDecorateItem(entityIn, itemIn, x, y, p_174234_);
    }

    @Override
    public void renderGuiItemDecorations (Font fr, @Nonnull ItemStack stack, int xPosition, int yPosition) {
        parent.renderGuiItemDecorations(fr, stack, xPosition, yPosition);
    }

    @Override
    public void renderGuiItemDecorations (Font font, @Nonnull ItemStack item, int x, int y, String text)
    {
        if (item != overrideStack) {
            super.renderGuiItemDecorations(font, item, x, y, text);
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

            PoseStack matrixstack = new PoseStack();
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

                matrixstack.scale(scale, scale, 1);
                matrixstack.translate(0.0D, 0.0D, (double) (this.blitOffset + 200.0F));
                MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                font.drawInBatch(text, textX, textY, color, true, matrixstack.last().pose(), buffer, false, 0, 15728880);
                buffer.endBatch();
            }

            if (item.getItem().isBarVisible(item)) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuilder();
                int barWidth = item.getItem().getBarWidth(item);
                int j = item.getItem().getBarColor(item);
                this.draw(bufferbuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                this.draw(bufferbuilder, x + 2, y + 13, barWidth, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            LocalPlayer clientplayerentity = Minecraft.getInstance().player;
            float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldowns().getCooldownPercent(item.getItem(), Minecraft.getInstance().getFrameTime());
            if (f3 > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tesselator tessellator1 = Tesselator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuilder();
                this.draw(bufferbuilder1, x, y + Mth.floor(16.0F * (1.0F - f3)), 16, Mth.ceil(16.0F * f3), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    @Override
    public void onResourceManagerReload (ResourceManager p_195410_1_) {
        parent.onResourceManagerReload(p_195410_1_);
    }

    private void draw (BufferBuilder tessellator, int x, int y, int w, int h, int r, int g, int b, int a)
    {
        tessellator.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
        tessellator.vertex(x + 0, y + 0, 0).color(r, g, b, a).endVertex();
        tessellator.vertex(x + 0, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.vertex(x + w, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.vertex(x + w, y + 0, 0).color(r, g, b, a).endVertex();
        Tesselator.getInstance().end();
    }
}
