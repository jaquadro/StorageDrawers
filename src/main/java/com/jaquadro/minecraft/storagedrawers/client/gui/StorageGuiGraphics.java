package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StorageGuiGraphics extends GuiGraphics
{
    private final Minecraft minecraft;

    @NotNull
    public ItemStack overrideStack;

    public StorageGuiGraphics (Minecraft p_283406_, MultiBufferSource.BufferSource p_282238_) {
        super(p_283406_, p_282238_);

        minecraft = p_283406_;
        overrideStack = ItemStack.EMPTY;
    }

    public void renderItemDecorations(Font font, ItemStack item, int x, int y, @Nullable String text) {
        if (item != overrideStack) {
            super.renderItemDecorations(font, item, x, y, text);
            return;
        }

        if (!item.isEmpty()) {
            item = ItemStackHelper.decodeItemStack(item);

            float scale = .5f;
            float xoff = 0;

            int stackSize = item.getCount();
            if (ItemStackHelper.isStackEncoded(item))
                stackSize = 0;

            pose().pushPose();
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

                int textX = (int) ((x + 16 + xoff - font.width(text) * scale) / scale) - 1;
                int textY = (int) ((y + 16 - 7 * scale) / scale) - 1;

                int color = 16777215;
                if (stackSize == 0)
                    color = (255 << 16) | (96 << 8) | (96);

                pose().scale(scale, scale, 1);
                pose().translate(0.0D, 0.0D, 200.0F);
                this.drawString(font, text, textX, textY, color, true);
            }

            if (item.getItem().isBarVisible(item)) {
                int barWidth = item.getItem().getBarWidth(item);
                int color = item.getItem().getBarColor(item);
                int x1 = x + 2;
                int y1 = y + 13;
                this.fill(RenderType.guiOverlay(), x1, y1, x1 + 13, y1 + 2, -16777216);
                this.fill(RenderType.guiOverlay(), x1, y1, x1 + barWidth, y1 + 1, color | -16777216);
            }

            LocalPlayer localplayer = this.minecraft.player;
            float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(item.getItem(), this.minecraft.getTimer().getGameTimeDeltaPartialTick(true));
            if (f > 0.0F) {
                int y1 = y + Mth.floor(16.0F * (1.0F - f));
                int y2 = y1 + Mth.ceil(16.0f * f);
                this.fill(RenderType.guiOverlay(), x, y1, x + 16, y2, Integer.MAX_VALUE);
            }

            this.pose().popPose();
            net.neoforged.neoforge.client.ItemDecoratorHandler.of(item).render(this, font, item, x, y);
        }
    }
}
