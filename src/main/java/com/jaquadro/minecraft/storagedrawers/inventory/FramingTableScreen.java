package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FramingTableScreen extends AbstractContainerScreen<ContainerFramingTable>
{
    private static final ResourceLocation guiTextires = StorageDrawers.rl("textures/gui/framing.png");

    private final ResourceLocation background;
    private final Inventory inventory;

    public FramingTableScreen (ContainerFramingTable container, Inventory playerInv, Component name) {
        super(container, playerInv, name);

        imageWidth = 176;
        imageHeight = 166;
        background = guiTextires;
        inventory = playerInv;
    }

    @Override
    public void render (@NotNull GuiGraphics graphics, int x, int y, float f) {
        super.render(graphics, x, y, f);
        this.renderTooltip(graphics, x, y);
    }

    @Override
    protected void renderLabels (GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, 4210752, false);
        graphics.drawString(this.font, this.inventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg (GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;
        graphics.blit(background, guiX, guiY, 0, 0, imageWidth, imageHeight);
    }
}
