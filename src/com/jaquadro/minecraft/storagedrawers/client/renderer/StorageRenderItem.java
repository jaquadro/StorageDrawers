package com.jaquadro.minecraft.storagedrawers.client.renderer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class StorageRenderItem extends RenderItem
{
    public ItemStack overrideStack;

    @Override
    public void renderItemOverlayIntoGUI (FontRenderer font, TextureManager texManager, ItemStack item, int x, int y, String text)
    {
        if (item != overrideStack) {
            super.renderItemOverlayIntoGUI(font, texManager, item, x, y, text);
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

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPushMatrix();
                GL11.glScalef(scale, scale, scale);
                font.drawStringWithShadow(text, textX, textY, 16777215);
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            if (item.getItem().showDurabilityBar(item))
            {
                double health = item.getItem().getDurabilityForDisplay(item);
                int j1 = (int)Math.round(13.0D - health * 13.0D);
                int k = (int)Math.round(255.0D - health * 255.0D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_BLEND);
                Tessellator tessellator = Tessellator.instance;
                int l = 255 - k << 16 | k << 8;
                int i1 = (255 - k) / 4 << 16 | 16128;
                this.renderQuad(tessellator, x + 2, y + 13, 13, 2, 0);
                this.renderQuad(tessellator, x + 2, y + 13, 12, 1, i1);
                this.renderQuad(tessellator, x + 2, y + 13, j1, 1, l);
                //GL11.glEnable(GL11.GL_BLEND); // Forge: Disable Bled because it screws with a lot of things down the line.
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private void renderQuad (Tessellator tessellator, int x, int y, int w, int h, int color)
    {
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(color);
        tessellator.addVertex(x + 0, y + 0, 0);
        tessellator.addVertex(x + 0, y + h, 0);
        tessellator.addVertex(x + w, y + h, 0);
        tessellator.addVertex(x + w, y + 0, 0);
        tessellator.draw();
    }
}
