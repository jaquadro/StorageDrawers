package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.components.item.DetachedDrawerContents;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.util.CountFormatter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ClientDetachedDrawerTooltip implements ClientTooltipComponent
{
    private static final ResourceLocation BACKGROUND_SPRITE = ModConstants.loc("textures/gui/detached_tooltip.png");

    private final ItemStack item;
    private final int stackLimit;

    public ClientDetachedDrawerTooltip (DetachedDrawerContents tooltip) {
        this.item = tooltip.getItem();
        this.stackLimit = tooltip.getStackLimit();
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
        return 128;
    }

    private int backgroundHeight() {
        return 20 + 4;
    }

    @Override
    public void renderImage(Font font, int pX, int pY, GuiGraphics graphics) {
        boolean forceCapCheck = ModCommonConfig.INSTANCE.GENERAL.forceDetachedDrawersMaxCapacityCheck.get();
        int bgY = forceCapCheck ? 0 : 24;
        graphics.blit(BACKGROUND_SPRITE, pX, pY, 0, bgY, this.backgroundWidth(), this.backgroundHeight(), 128, 64);

        renderSlot(pX + 3, pY + 3, graphics, font);

        String count = CountFormatter.formatApprox(font, item.getCount());
        graphics.drawString(font, count, pX + 22, pY + 8, 0x808080, false);

        if (forceCapCheck)
            graphics.drawString(font, Integer.toString(stackLimit), pX + 83, pY + 8, 0x808080, false);
    }

    private void renderSlot(int pX, int pY, GuiGraphics graphics, Font font) {
        ItemStack itemstack = this.item;
        graphics.renderItem(itemstack, pX + 1, pY + 1, 0);
    }
}
