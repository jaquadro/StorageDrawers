package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DrawerScreen extends AbstractContainerScreen<ContainerDrawers>
{
    private static final Identifier guiTextures1 = ModConstants.loc("textures/gui/drawers_1.png");
    private static final Identifier guiTextures2 = ModConstants.loc("textures/gui/drawers_2.png");
    private static final Identifier guiTextures4 = ModConstants.loc("textures/gui/drawers_4.png");
    private static final Identifier guiTexturesComp2 = ModConstants.loc("textures/gui/drawers_comp_2.png");
    private static final Identifier guiTexturesComp3 = ModConstants.loc("textures/gui/drawers_comp.png");

    private static final int smDisabledX = 176;
    private static final int smDisabledY = 0;
    private static final int smMissingY = 16;

    private final Identifier background;
    private final Inventory inventory;

    public DrawerScreen(ContainerDrawers container, Inventory playerInv, Component name, Identifier bg) {
        super(container, playerInv, name, 176, 199);

        background = bg;
        inventory = playerInv;
    }

    public static class Slot1 extends DrawerScreen {
        public Slot1(ContainerDrawers container, Inventory playerInv, Component name) {
            super(container, playerInv, name, guiTextures1);
        }
    }

    public static class Slot2 extends DrawerScreen {
        public Slot2(ContainerDrawers container, Inventory playerInv, Component name) {
            super(container, playerInv, name, guiTextures2);
        }
    }

    public static class Slot4 extends DrawerScreen {
        public Slot4(ContainerDrawers container, Inventory playerInv, Component name) {
            super(container, playerInv, name, guiTextures4);
        }
    }

    public static class Compacting2 extends DrawerScreen {
        public Compacting2(ContainerDrawers container, Inventory playerInv, Component name) {
            super(container, playerInv, name, guiTexturesComp2);
        }
    }

    public static class Compacting3 extends DrawerScreen {
        public Compacting3(ContainerDrawers container, Inventory playerInv, Component name) {
            super(container, playerInv, name, guiTexturesComp3);
        }
    }

    @Override
    protected void init () {
        super.init();
    }

    @Override
    protected void extractLabels (GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, 8, 6, 0xFF404040, false);
        graphics.text(this.font, I18n.get("container.storagedrawers.upgrades"), 8, 75, 0xFF404040, false);
        graphics.text(this.font, this.inventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 0xFF404040, false);

        String mult = Integer.toString(menu.getStackCapacity());
        graphics.text(this.font, mult, 161 - mult.length() * 6, 42, 0xFF404040, false);
    }

    @Override
    public void extractBackground (GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, background, guiX, guiY, 0, 0, imageWidth, imageHeight, 256, 256);

        List<Slot> storageSlots = menu.getStorageSlots();
        for (Slot slot : storageSlots) {
            if (slot instanceof SlotDrawer sd && sd.getDrawer().isMissing())
                graphics.blit(RenderPipelines.GUI_TEXTURED, background, guiX + slot.x, guiY + slot.y, smDisabledX, smMissingY, 16, 16, 256, 256);
            else
                graphics.blit(RenderPipelines.GUI_TEXTURED, background, guiX + slot.x, guiY + slot.y, smDisabledX, smDisabledY, 16, 16, 256, 256);
        }

        List<Slot> upgradeSlots = menu.getUpgradeSlots();
        for (Slot slot : upgradeSlots) {
            boolean locked = false;
            if (slot.container instanceof InventoryUpgrade ucontainer)
                locked = ucontainer.slotIsLocked(slot.getContainerSlot());

            if (locked)
                graphics.blit(RenderPipelines.GUI_TEXTURED, background, guiX + slot.x, guiY + slot.y, smDisabledX, smDisabledY, 16, 16, 256, 256);
        }
    }

    @Override
    protected void extractSlot (GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        if (!(slot instanceof SlotDrawer)) {
            super.extractSlot(graphics, slot, mouseX, mouseY);
            return;
        }

        ItemStack item = slot.getItem();
        if (item.isEmpty())
            return;

        graphics.item(item, slot.x, slot.y);

        graphics.pose().pushMatrix();
        renderDrawerBar(graphics, item, slot.x, slot.y);
        renderDrawerCooldown(graphics, item, slot.x, slot.y);
        renderDrawerCount(graphics, this.font, item, slot.x, slot.y);
        graphics.pose().popMatrix();
    }

    private void renderDrawerBar (GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        if (stack.isBarVisible()) {
            int offX = x + 2;
            int offY = y + 13;
            graphics.fill(RenderPipelines.GUI, offX, offY, offX + 13, offY + 2, -16777216);
            graphics.fill(RenderPipelines.GUI, offX, offY, offX + stack.getBarWidth(), offY + 1, ARGB.opaque(stack.getBarColor()));
        }
    }

    private void renderDrawerCooldown (GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        LocalPlayer player = this.minecraft.player;
        float f = player == null ? 0.0F : player.getCooldowns().getCooldownPercent(stack, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
        if (f > 0.0F) {
            int y1 = y + Mth.floor(16.0F * (1.0F - f));
            int y2 = y1 + Mth.ceil(16.0F * f);
            graphics.fill(RenderPipelines.GUI, x, y1, x + 16, y2, Integer.MAX_VALUE);
        }
    }

    private void renderDrawerCount (GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
        stack = ItemStackHelper.decodeItemStack(stack);

        int stackSize = stack.getCount();
        float scale = 0.5f;

        String text;
        if (stackSize >= 100000000)
            text = String.format("%.0fM", stackSize / 1000000f);
        else if (stackSize >= 1000000)
            text = String.format("%.1fM", stackSize / 1000000f);
        else if (stackSize >= 100000)
            text = String.format("%.0fK", stackSize / 1000f);
        else if (stackSize >= 10000)
            text = String.format("%.1fK", stackSize / 1000f);
        else
            text = String.valueOf(stackSize);

        int textX = (int)((x + 16 - font.width(text) * scale) / scale) - 1;
        int textY = (int)((y + 16 - 7 * scale) / scale) - 1;

        int color = 0xFFFFFFFF;
        if (stackSize == 0)
            color = (255 << 16) | (96 << 8) | (96);

        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.text(font, text, textX, textY, color, true);
        graphics.pose().popMatrix();
    }

    @Override
    protected boolean isHovering (int x, int y, int width, int height, double originX, double originY) {
        List<Slot> storageSlots = menu.getStorageSlots();
        for (Slot slot : storageSlots) {
            if (slot instanceof SlotStorage && slot.x == x && slot.y == y)
                return false;
        }

        /*List<Slot> upgradeSlots = container.getUpgradeSlots();
        for (Slot slot : upgradeSlots) {
            if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack() && slot.xPos == x && slot.yPos == y)
                return false;
        }*/

        return super.isHovering(x, y, width, height, originX, originY);
    }
}
