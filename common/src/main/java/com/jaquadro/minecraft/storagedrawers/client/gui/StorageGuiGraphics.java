package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StorageGuiGraphics extends GuiGraphics
{
    private final Minecraft minecraft;
    private GuiGraphics baseGraphics;

    @NotNull
    public ItemStack overrideStack;

    public StorageGuiGraphics (Minecraft minecraft, GuiRenderState renderState, int mouseX, int mouseY) {
        super(minecraft, renderState, mouseX, mouseY);

        this.minecraft = minecraft;
        overrideStack = ItemStack.EMPTY;
    }

    public StorageGuiGraphics (Minecraft minecraft, GuiGraphics graphics, int mouseX, int mouseY) {
        super(minecraft, graphics.pose(), minecraft.gameRenderer.guiRenderState, mouseX, mouseY);

        this.baseGraphics = graphics;
        this.minecraft = minecraft;
        this.overrideStack = ItemStack.EMPTY;
    }

    public GuiGraphics baseGraphics () {
        return baseGraphics;
    }

    public void renderItemDecorations(Font font, ItemStack item, int x, int y, @Nullable String text) {
        if (item != overrideStack) {
            super.renderItemDecorations(font, item, x, y, text);
            return;
        }

        if (!item.isEmpty()) {
            pose().pushMatrix();
            renderItemBar(item, x, y);
            renderItemCooldown(item, x, y);
            renderItemCount(font, item, x, y, text);
            pose().popMatrix();
        }
    }

    private void renderItemBar(ItemStack stack, int x, int y) {
        if (stack.isBarVisible()) {
            int offX = x + 2;
            int offY = y + 13;
            this.fill(RenderPipelines.GUI, offX, offY, offX + 13, offY + 2, -16777216);
            this.fill(RenderPipelines.GUI, offX, offY, offX + stack.getBarWidth(), offY + 1, ARGB.opaque(stack.getBarColor()));
        }
    }

    private void renderItemCooldown(ItemStack stack, int x, int y) {
        LocalPlayer player = this.minecraft.player;
        float f = player == null ? 0.0F : player.getCooldowns().getCooldownPercent(stack, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
        if (f > 0.0F) {
            int y1 = y + Mth.floor(16.0F * (1.0F - f));
            int y2 = y1 + Mth.ceil(16.0F * f);
            this.fill(RenderPipelines.GUI, x, y1, x + 16, y2, Integer.MAX_VALUE);
        }
    }

    private void renderItemCount(Font font, ItemStack stack, int x, int y, String text) {
        stack = ItemStackHelper.decodeItemStack(stack);

        int stackSize = stack.getCount();
        float scale = 0.5f;

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

            int textX = (int)((x + 16 - font.width(text) * scale) / scale) - 1;
            int textY = (int) ((y + 16 - 7 * scale) / scale) - 1;

            int color = 0xFFFFFFFF;
            if (stackSize == 0)
                color = (255 << 16) | (96 << 8) | (96);

            pose().pushMatrix();
            pose().scale(scale, scale);

            this.drawString(font, text, textX, textY, color, true);
            pose().popMatrix();
        }

    }
}
