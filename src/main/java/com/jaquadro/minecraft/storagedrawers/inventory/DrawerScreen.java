package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.List;

public class DrawerScreen extends AbstractContainerScreen<ContainerDrawers>
{
    private static final ResourceLocation guiTextures1 = new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_1.png");
    private static final ResourceLocation guiTextures2 = new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_2.png");
    private static final ResourceLocation guiTextures4 = new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_4.png");
    private static final ResourceLocation guiTexturesComp = new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_comp.png");

    private static final int smDisabledX = 176;
    private static final int smDisabledY = 0;

    private static StorageRenderItem storageItemRender;

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

        if (storageItemRender == null) {
            ItemRenderer defaultRenderItem = minecraft.getItemRenderer();
            storageItemRender = new StorageRenderItem(minecraft.getTextureManager(), defaultRenderItem.getItemModelShaper().getModelManager(), minecraft.getItemColors());
        }
    }

    @Override
    public void render (PoseStack stack, int p_render_1_, int p_render_2_, float p_render_3_) {
        ItemRenderer ri = setItemRender(storageItemRender);
        menu.activeRenderItem = storageItemRender;

        this.renderBackground(stack);
        super.render(stack, p_render_1_, p_render_2_, p_render_3_);
        this.renderTooltip(stack, p_render_1_, p_render_2_);

        menu.activeRenderItem = null;
        storageItemRender.overrideStack = ItemStack.EMPTY;

        setItemRender(ri);
    }

    @Override
    protected void renderLabels (PoseStack stack, int mouseX, int mouseY) {
        this.font.draw(stack, this.title.getString(), 8.0F, 6.0F, 4210752);
        this.font.draw(stack, I18n.get("container.storagedrawers.upgrades"), 8, 75, 4210752);
        this.font.draw(stack, this.inventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    protected void renderBg (PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager._color4f(1, 1, 1, 1);

        minecraft.getTextureManager().bind(background);

        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;
        blit(stack, guiX, guiY, 0, 0, imageWidth, imageHeight);

        List<Slot> storageSlots = menu.getStorageSlots();
        for (Slot slot : storageSlots) {
            blit(stack, guiX + slot.x, guiY + slot.y, smDisabledX, smDisabledY, 16, 16);
        }

        /*List<Slot> upgradeSlots = container.getUpgradeSlots();
        for (Slot slot : upgradeSlots) {
            if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack())
                blit(guiX + slot.xPos, guiY + slot.yPos, smDisabledX, smDisabledY, 16, 16);
        }*/
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

    private ItemRenderer setItemRender (ItemRenderer renderItem) {
        ItemRenderer prev = itemRenderer;
        itemRenderer = renderItem;

        return prev;
    }
}
