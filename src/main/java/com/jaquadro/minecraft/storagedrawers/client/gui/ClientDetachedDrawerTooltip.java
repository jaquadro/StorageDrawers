package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.DetachedDrawerTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientDetachedDrawerTooltip implements ClientTooltipComponent
{
    private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("container/bundle/background");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final ItemStack item;

    public ClientDetachedDrawerTooltip (DetachedDrawerTooltip tooltip) {
        this.item = tooltip.getItem();
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
        return 18 + 2 + 20;
    }

    private int backgroundHeight() {
        return 20 + 2;
    }

    @Override
    public void renderImage(Font font, int pX, int pY, GuiGraphics graphics) {
        //int i = gridSizeX();
        //int j = gridSizeY();
        //graphics.blit(BACKGROUND_SPRITE, pX, pY, this.backgroundWidth(), this.backgroundHeight());

        renderSlot(pX + 1 + 20, pY + 1, graphics, font);
    }

    private void renderSlot(int pX, int pY, GuiGraphics graphics, Font font) {
        //if (index >= this.contents.size()) {
        //    blit(graphics, pX, pY, isFull ? Texture.BLOCKED_SLOT : Texture.SLOT);
        //} else {
            ItemStack itemstack = this.item;
            //blit(graphics, pX, pY, Texture.SLOT);
            graphics.renderItem(itemstack, pX + 1, pY + 1, 0);
            graphics.renderItemDecorations(font, itemstack, pX + 1, pY + 1);
            //if (index == 0) {
            //    AbstractContainerScreen.renderSlotHighlight(graphics, pX + 1, pY + 1, 0);
            //}
        //}
    }

    //private void blit(GuiGraphics graphics, int x, int y, Texture texture) {
    //    graphics.blitSprite(texture.sprite, x, y, 0, texture.w, texture.h);
    //}

    //private int gridSizeX() {
    //    return Math.max(2, (int)Math.ceil(Math.sqrt((double)contents.size() + 1.0)));
    //}

    //private int gridSizeY() {
    //    return (int)Math.ceil(((double)contents.size() + 1.0) / (double)gridSizeX());
    //}

    @OnlyIn(Dist.CLIENT)
    private static enum Texture {
        SLOT(0, 0, 18, 20),
        BLOCKED_SLOT(0, 40, 18, 20),
        BORDER_VERTICAL(0, 18, 1, 20),
        BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
        BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
        BORDER_CORNER_TOP(0, 20, 1, 1),
        BORDER_CORNER_BOTTOM(0, 60, 1, 1);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        private Texture(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
