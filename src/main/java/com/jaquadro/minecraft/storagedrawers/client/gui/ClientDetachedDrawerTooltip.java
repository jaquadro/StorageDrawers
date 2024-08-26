package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.DetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.util.CountFormatter;
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
    private static final ResourceLocation BACKGROUND_SPRITE = StorageDrawers.rl("textures/gui/detached_tooltip.png");

    private final IDrawer drawer;
    private final ItemStack item;
    private final int stackLimit;

    public ClientDetachedDrawerTooltip (DetachedDrawerTooltip tooltip) {
        this.drawer = tooltip.getDrawer();
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
        graphics.blit(BACKGROUND_SPRITE, pX, pY, 0, 0, this.backgroundWidth(), this.backgroundHeight(), 128, 64);

        renderSlot(pX + 3, pY + 3, graphics, font);

        String count = CountFormatter.formatApprox(font, drawer);
        graphics.drawString(font, count, pX + 22, pY + 8, 0x808080, false);
        graphics.drawString(font, Integer.toString(stackLimit), pX + 83, pY + 8, 0x808080, false);
    }

    private void renderSlot(int pX, int pY, GuiGraphics graphics, Font font) {
        ItemStack itemstack = this.item;
        graphics.renderItem(itemstack, pX + 1, pY + 1, 0);
    }
}
