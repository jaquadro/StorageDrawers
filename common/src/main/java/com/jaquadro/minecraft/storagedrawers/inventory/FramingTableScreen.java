package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FramingTableScreen extends AbstractContainerScreen<ContainerFramingTable>
{
    private static final Identifier guiTextires = ModConstants.loc("textures/gui/framing.png");

    private final Identifier background;
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
        graphics.drawString(this.font, this.title, 8, 6, 0xFF404040, false);
        graphics.drawString(this.font, this.inventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 0xFF404040, false);
    }

    @Override
    protected void renderBg (GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, background, guiX, guiY, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}