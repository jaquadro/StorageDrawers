package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientKeyringTooltip implements ClientTooltipComponent
{
    private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("container/bundle/background");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final KeyringContents contents;

    public ClientKeyringTooltip (KeyringContents contents) {
        this.contents = contents;
    }

    @Override
    public int getHeight() {
        return backgroundHeight() + 4;
    }

    @Override
    public int getWidth(Font font) {
        return backgroundWidth();
    }

    private int backgroundWidth() {
        return gridSizeX() * 18 + 2;
    }

    private int backgroundHeight() {
        return gridSizeY() * 20 + 2;
    }

    @Override
    public void renderImage(Font font, int pX, int pY, GuiGraphics graphics) {
        int i = gridSizeX();
        int j = gridSizeY();
        graphics.blitSprite(BACKGROUND_SPRITE, pX, pY, this.backgroundWidth(), this.backgroundHeight());
        boolean isFull = contents.size() >= 64;
        int k = 0;

        for (int l = 0; l < j; l++) {
            for (int i1 = 0; i1 < i; i1++) {
                int j1 = pX + i1 * 18 + 1;
                int k1 = pY + l * 20 + 1;
                renderSlot(j1, k1, k++, isFull, graphics, font);
            }
        }
    }

    private void renderSlot(int pX, int pY, int index, boolean isFull, GuiGraphics graphics, Font font) {
        if (index >= this.contents.size()) {
            blit(graphics, pX, pY, isFull ? Texture.BLOCKED_SLOT : Texture.SLOT);
        } else {
            ItemStack itemstack = this.contents.getItemUnsafe(index);
            blit(graphics, pX, pY, Texture.SLOT);
            graphics.renderItem(itemstack, pX + 1, pY + 1, index);
            graphics.renderItemDecorations(font, itemstack, pX + 1, pY + 1);
            if (index == 0) {
                AbstractContainerScreen.renderSlotHighlight(graphics, pX + 1, pY + 1, 0);
            }
        }
    }

    private void blit(GuiGraphics graphics, int x, int y, Texture texture) {
        graphics.blitSprite(texture.sprite, x, y, 0, texture.w, texture.h);
    }

    private int gridSizeX() {
        return Math.max(2, (int)Math.ceil(Math.sqrt((double)contents.size() + 1.0)));
    }

    private int gridSizeY() {
        return (int)Math.ceil(((double)contents.size() + 1.0) / (double)gridSizeX());
    }

    @OnlyIn(Dist.CLIENT)
    static enum Texture {
        BLOCKED_SLOT(new ResourceLocation("container/bundle/blocked_slot"), 18, 20),
        SLOT(new ResourceLocation("container/bundle/slot"), 18, 20);

        public final ResourceLocation sprite;
        public final int w;
        public final int h;

        private Texture(ResourceLocation sprite, int w, int h) {
            this.sprite = sprite;
            this.w = w;
            this.h = h;
        }
    }
}
