package com.jaquadro.minecraft.storagedrawers.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class StorageRenderItem extends RenderItem
{
    private RenderItem parent;

    public ItemStack overrideStack;

    public StorageRenderItem (TextureManager texManager, ModelManager modelManager) {
        super(texManager, modelManager);
        parent = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public ItemModelMesher getItemModelMesher () {
        return parent.getItemModelMesher();
    }

    @Override
    public void renderItem (ItemStack stack, IBakedModel model) {
        parent.renderItem(stack, model);
    }

    @Override
    public boolean shouldRenderItemIn3D (ItemStack stack) {
        return parent.shouldRenderItemIn3D(stack);
    }

    @Override
    public void func_181564_a (ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        parent.func_181564_a(stack, transformType);
    }

    @Override
    public void renderItemModelForEntity (ItemStack stack, EntityLivingBase entityToRenderFor, ItemCameraTransforms.TransformType cameraTransformType) {
        parent.renderItemModelForEntity(stack, entityToRenderFor, cameraTransformType);
    }

    @Override
    public void renderItemIntoGUI (ItemStack stack, int x, int y) {
        parent.renderItemIntoGUI(stack, x, y);
    }

    @Override
    public void renderItemAndEffectIntoGUI (ItemStack stack, int xPosition, int yPosition) {
        parent.renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
    }

    @Override
    public void renderItemOverlays (FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
        parent.renderItemOverlays(fr, stack, xPosition, yPosition);
    }

    @Override
    public void renderItemOverlayIntoGUI (FontRenderer font, ItemStack item, int x, int y, String text)
    {
        if (item != overrideStack) {
            super.renderItemOverlayIntoGUI(font, item, x, y, text);
            return;
        }

        if (item != null)
        {
            float scale = .5f;
            float xoff = 0;
            if (font.getUnicodeFlag()) {
                scale = 1f;
                xoff = 1;
            }

            if (item.stackSize > 1 || text != null)
            {
                if (item.stackSize >= 100000000 || (item.stackSize >= 1000000 && font.getUnicodeFlag()))
                    text = (text == null) ? String.format("%.0fM", item.stackSize / 1000000f) : text;
                else if (item.stackSize >= 1000000)
                    text = (text == null) ? String.format("%.1fM", item.stackSize / 1000000f) : text;
                else if (item.stackSize >= 100000 || (item.stackSize >= 10000 && font.getUnicodeFlag()))
                    text = (text == null) ? String.format("%.0fK", item.stackSize / 1000f) : text;
                else if (item.stackSize >= 10000)
                    text = (text == null) ? String.format("%.1fK", item.stackSize / 1000f) : text;
                else
                    text = (text == null) ? String.valueOf(item.stackSize) : text;

                int textX = (int)((x + 16 + xoff - font.getStringWidth(text) * scale) / scale) - 1;
                int textY = (int)((y + 16 - 7 * scale) / scale) - 1;

                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, scale);
                font.drawStringWithShadow(text, textX, textY, 16777215);
                GlStateManager.popMatrix();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            if (item.getItem().showDurabilityBar(item))
            {
                double health = item.getItem().getDurabilityForDisplay(item);
                int j1 = (int)Math.round(13.0D - health * 13.0D);
                int k = (int)Math.round(255.0D - health * 255.0D);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                int l = 255 - k << 16 | k << 8;
                int i1 = (255 - k) / 4 << 16 | 16128;
                this.renderQuad(worldrenderer, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                this.renderQuad(worldrenderer, x + 2, y + 13, 12, 1, (255 - k) / 4, 64, 0, 255);
                this.renderQuad(worldrenderer, x + 2, y + 13, j1, 1, 255 - k, k, 0, 255);
                //GL11.glEnable(GL11.GL_BLEND); // Forge: Disable Bled because it screws with a lot of things down the line.
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    private void renderQuad (WorldRenderer tessellator, int x, int y, int w, int h, int r, int g, int b, int a)
    {
        tessellator.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        tessellator.pos(x + 0, y + 0, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + 0, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + h, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + 0, 0).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();
    }
}
