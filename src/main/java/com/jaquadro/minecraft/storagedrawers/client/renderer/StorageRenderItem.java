package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class StorageRenderItem extends ItemRenderer
{
    private ItemRenderer parent;

    @Nonnull
    public ItemStack overrideStack;

    public StorageRenderItem (TextureManager texManager, ModelManager modelManager, ItemColors colors) {
        super(texManager, modelManager, colors);
        parent = Minecraft.getInstance().getItemRenderer();
        overrideStack = ItemStack.EMPTY;
    }

    @Override
    public ItemModelMesher getItemModelMesher () {
        return parent.getItemModelMesher();
    }

    @Override
    public void renderItem(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn) {
        parent.renderItem(itemStackIn, transformTypeIn, leftHand, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, modelIn);
    }

    @Override
    public void renderItem(@Nullable LivingEntity livingEntityIn, ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, @Nullable World worldIn, int combinedLightIn, int combinedOverlayIn) {
        parent.renderItem(livingEntityIn, itemStackIn, transformTypeIn, leftHand, matrixStackIn, bufferIn, worldIn, combinedLightIn, combinedOverlayIn);
    }

    @Override
    public void renderItem(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn) {
        parent.renderItem(itemStackIn, transformTypeIn, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
    }

    public void renderQuads(MatrixStack matrixStackIn, IVertexBuilder bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn) {
        parent.renderQuads(matrixStackIn, bufferIn, quadsIn, itemStackIn, combinedLightIn, combinedOverlayIn);
    }

    @Override
    public IBakedModel getItemModelWithOverrides (@Nonnull ItemStack stack, World world, LivingEntity entity) {
        return parent.getItemModelWithOverrides(stack, world, entity);
    }

    @Override
    public void renderItemIntoGUI (@Nonnull ItemStack stack, int x, int y) {
        parent.renderItemIntoGUI(stack, x, y);
    }

    @Override
    public void renderItemAndEffectIntoGUI (@Nonnull ItemStack stack, int xPosition, int yPosition) {
        parent.renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
    }

    @Override
    public void renderItemAndEffectIntoGUI(@Nullable LivingEntity entityIn, ItemStack itemIn, int x, int y) {
        parent.renderItemAndEffectIntoGUI(entityIn, itemIn, x, y);
    }

    @Override
    public void renderItemOverlays (FontRenderer fr, @Nonnull ItemStack stack, int xPosition, int yPosition) {
        parent.renderItemOverlays(fr, stack, xPosition, yPosition);
    }

    @Override
    public void renderItemOverlayIntoGUI (FontRenderer font, @Nonnull ItemStack item, int x, int y, String text)
    {
        if (item != overrideStack) {
            super.renderItemOverlayIntoGUI(font, item, x, y, text);
            return;
        }

        item = ItemStackHelper.decodeItemStack(item);

        if (!item.isEmpty())
        {
            float scale = .5f;
            float xoff = 0;
            //if (font.getUnicodeFlag()) {
            //    scale = 1f;
            //    xoff = 1;
            //}

            int stackSize = item.getCount();
            if (ItemStackHelper.isStackEncoded(item))
                stackSize = 0;

            MatrixStack matrixstack = new MatrixStack();
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

                int textX = (int) ((x + 16 + xoff - font.getStringWidth(text) * scale) / scale) - 1;
                int textY = (int) ((y + 16 - 7 * scale) / scale) - 1;

                int color = 16777215;
                if (stackSize == 0)
                    color = (255 << 16) | (96 << 8) | (96);

                matrixstack.scale(scale, scale, 1);
                matrixstack.translate(0.0D, 0.0D, (double) (this.zLevel + 200.0F));
                IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
                font.renderString(text, textX, textY, color, true, matrixstack.getLast().getMatrix(), buffer, false, 0, 15728880);
                buffer.finish();
            }

            if (item.getItem().showDurabilityBar(item)) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                double health = item.getItem().getDurabilityForDisplay(item);
                int i = Math.round(13.0F - (float)health * 13.0F);
                int j = item.getItem().getRGBDurabilityForDisplay(item);
                this.draw(bufferbuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                this.draw(bufferbuilder, x + 2, y + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
            float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldownTracker().getCooldown(item.getItem(), Minecraft.getInstance().getRenderPartialTicks());
            if (f3 > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                this.draw(bufferbuilder1, x, y + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    @Override
    public void onResourceManagerReload (IResourceManager p_195410_1_) {
        parent.onResourceManagerReload(p_195410_1_);
    }

    private void draw (BufferBuilder tessellator, int x, int y, int w, int h, int r, int g, int b, int a)
    {
        tessellator.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        tessellator.pos(x + 0, y + 0, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + 0, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + 0, 0).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();
    }
}
