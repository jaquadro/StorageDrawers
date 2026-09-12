package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientKeyringTooltip implements ClientTooltipComponent
{
    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_highlight_front");
    private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_background");
    private static final int SLOT_MARGIN = 4;
    private static final int SLOT_SIZE = 24;
    private static final int GRID_WIDTH = 96;

    private final KeyringContents contents;

    public ClientKeyringTooltip (KeyringContents contents) {
        this.contents = contents;
    }

    @Override
    public int getHeight(Font font) {
        return backgroundHeight();
    }

    @Override
    public int getWidth(Font font) {
        return backgroundWidth();
    }

    private int backgroundWidth() {
        return GRID_WIDTH;
    }

    private int backgroundHeight() {
        return itemGridHeight() + 4;
    }

    private int getContentXOffset(int x) {
        return (x - GRID_WIDTH) / 2;
    }

    private int itemGridHeight() {
        return this.gridSizeY() * SLOT_SIZE;
    }

    private int gridSizeY() {
        return Mth.positiveCeilDiv(this.slotCount(), contents.getShowRowSize());
    }

    private int slotCount() {
        return Math.min(this.contents.getMaxShowSize(), this.contents.size());
    }

    @Override
    public void renderImage(Font font, int pX, int pY, int pW, int pH, GuiGraphics graphics) {
        if (this.contents.isEmpty()) {
            this.renderEmptyBundleTooltip(font, pX, pY, pW, pH, graphics);
        } else {
            this.renderBundleWithItemsTooltip(font, pX, pY, pW, pH, graphics);
        }
    }

    private void renderEmptyBundleTooltip(Font font, int pX, int pY, int pW, int pH, GuiGraphics graphics) {
        //drawEmptyBundleDescriptionText($$1 + this.getContentXOffset($$3), $$2, $$0, $$5);
        //this.drawProgressbar($$1 + this.getContentXOffset($$3), $$2 + getEmptyBundleDescriptionTextHeight($$0) + 4, $$0, $$5);
    }

    private void renderBundleWithItemsTooltip(Font font, int pX, int pY, int pW, int pH, GuiGraphics graphics) {
        boolean overflow = this.contents.size() > 16;
        List<ItemStack> items = this.getShownItems(this.contents.getNumberOfItemsToShow());
        int x = pX + this.getContentXOffset(pW) + GRID_WIDTH;
        int y = pY + this.gridSizeY() * SLOT_SIZE;
        int index = 1;

        for(int iy = 1; iy <= this.gridSizeY(); ++iy) {
            for(int ix = 1; ix <= this.contents.getShowRowSize(); ++ix) {
                int xOff = x - ix * SLOT_SIZE;
                int yOff = y - iy * SLOT_SIZE;
                if (shouldRenderSurplusText(overflow, ix, iy)) {
                    renderCount(xOff, yOff, this.getAmountOfHiddenItems(items), font, graphics);
                } else if (shouldRenderItemSlot(items, index)) {
                    this.renderSlot(index, xOff, yOff, items, index, font, graphics);
                    ++index;
                }
            }
        }

        this.drawSelectedItemTooltip(font, graphics, pX, pY, pW);
        //this.drawProgressbar(pX + this.getContentXOffset(pW), pY + this.itemGridHeight() + 4, font, graphics);
    }

    private List<ItemStack> getShownItems(int showCount) {
        int count = Math.min(this.contents.size(), showCount);
        return this.contents.itemCopyStream().toList().subList(0, count);
    }

    private static boolean shouldRenderSurplusText(boolean overflow, int ix, int iy) {
        return overflow && ix * iy == 1;
    }

    private int getAmountOfHiddenItems(List<ItemStack> items) {
        return this.contents.itemCopyStream().skip(items.size()).mapToInt(ItemStack::getCount).sum();
    }

    private static boolean shouldRenderItemSlot(List<ItemStack> items, int index) {
        return items.size() >= index;
    }

    private static void renderCount(int x, int y, int hiddenCount, Font font, GuiGraphics graphics) {
        graphics.drawCenteredString(font, "+" + hiddenCount, x + 12, y + 10, -1);
    }

    private void renderSlot(int index, int pX, int pY, List<ItemStack> items, int renderIndex, Font font, GuiGraphics graphics) {
        int itemIndex = items.size() - index;
        boolean selected = itemIndex == this.contents.getSelectedItem();
        ItemStack item = items.get(itemIndex);
        if (selected)
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, pX, pY, SLOT_SIZE, SLOT_SIZE);
        else
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, pX, pY, SLOT_SIZE, SLOT_SIZE);

        graphics.renderItem(item, pX + SLOT_MARGIN, pY + SLOT_MARGIN, renderIndex);
        graphics.renderItemDecorations(font, item, pX + SLOT_MARGIN, pY + SLOT_MARGIN);
        if (selected)
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, pX, pY, 24, 24);
    }

    private void drawSelectedItemTooltip(Font font, GuiGraphics graphics, int pX, int pY, int pW) {
        if (this.contents.hasSelectedItem()) {
            ItemStack item = this.contents.getItemUnsafe(this.contents.getSelectedItem());
            Component hoverComponent = item.getStyledHoverName();
            int textWidth = font.width(hoverComponent.getVisualOrderText());
            int textOffset = pX + pW / 2 - 12;
            ClientTooltipComponent tooltip = ClientTooltipComponent.create(hoverComponent.getVisualOrderText());
            graphics.renderTooltip(font, List.of(tooltip), textOffset - textWidth / 2, pY - 15, DefaultTooltipPositioner.INSTANCE, item.get(DataComponents.TOOLTIP_STYLE));
        }

    }
}
