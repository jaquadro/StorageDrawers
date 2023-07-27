package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.gui.StorageGuiGraphics;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DrawerScreen extends AbstractContainerScreen<ContainerDrawers>
{
    private static final ResourceLocation guiTextures1 = StorageDrawers.rl("textures/gui/drawers_1.png");
    private static final ResourceLocation guiTextures2 = StorageDrawers.rl("textures/gui/drawers_2.png");
    private static final ResourceLocation guiTextures4 = StorageDrawers.rl("textures/gui/drawers_4.png");
    private static final ResourceLocation guiTexturesComp = StorageDrawers.rl("textures/gui/drawers_comp.png");

    private static final int smDisabledX = 176;
    private static final int smDisabledY = 0;

    private static StorageGuiGraphics storageGuiGraphics;

    private final ResourceLocation background;
    private final Inventory inventory;

    public DrawerScreen(ContainerDrawers container, Inventory playerInv, Component name, ResourceLocation bg) {
        super(container, playerInv, name);

        imageWidth = 176;
        imageHeight = 199;
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

    public static class Compacting extends DrawerScreen {
        public Compacting(ContainerDrawers container, Inventory playerInv, Component name) {
            super(container, playerInv, name, guiTexturesComp);
        }
    }

    @Override
    protected void init () {
        super.init();

        if (storageGuiGraphics == null && minecraft != null) {
            storageGuiGraphics = new StorageGuiGraphics(minecraft, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()));
        }
    }

    @Override
    public void render (GuiGraphics graphics, int x, int y, float f) {
        menu.activeGuiGraphics = storageGuiGraphics;

        super.render(storageGuiGraphics, x, y, f);

        menu.activeGuiGraphics = null;
        storageGuiGraphics.overrideStack = ItemStack.EMPTY;

        this.renderTooltip(graphics, x, y);
    }

    @Override
    protected void renderLabels (GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 6, 4210752, false);
        graphics.drawString(this.font, I18n.get("container.storagedrawers.upgrades"), 8, 75, 4210752, false);
        graphics.drawString(this.font, this.inventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg (GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;
        graphics.blit(background, guiX, guiY, 0, 0, imageWidth, imageHeight);

        List<Slot> storageSlots = menu.getStorageSlots();
        for (Slot slot : storageSlots) {
            graphics.blit(background, guiX + slot.x, guiY + slot.y, smDisabledX, smDisabledY, 16, 16);
        }

        List<Slot> upgradeSlots = menu.getUpgradeSlots();
        for (Slot slot : upgradeSlots) {
            if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack(Minecraft.getInstance().player))
                graphics.blit(background, guiX + slot.x, guiY + slot.y, smDisabledX, smDisabledY, 16, 16);
        }
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
