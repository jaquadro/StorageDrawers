package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class DrawerScreen extends ContainerScreen<ContainerDrawers>
{
    private static final ResourceLocation guiTextures1 = new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_1.png");
    private static final ResourceLocation guiTextures2 = new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_2.png");
    private static final ResourceLocation guiTextures4 = new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_4.png");
    private static final ResourceLocation guiTexturesComp = new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_comp.png");

    private static final int smDisabledX = 176;
    private static final int smDisabledY = 0;

    private static StorageRenderItem storageItemRender;

    private final ResourceLocation background;

    public DrawerScreen(ContainerDrawers container, PlayerInventory playerInv, ITextComponent name, ResourceLocation bg) {
        super(container, playerInv, name);

        xSize = 176;
        ySize = 199;
        background = bg;
    }

    public static class Slot1 extends DrawerScreen {
        public Slot1(ContainerDrawers container, PlayerInventory playerInv, ITextComponent name) {
            super(container, playerInv, name, guiTextures1);
        }
    }

    public static class Slot2 extends DrawerScreen {
        public Slot2(ContainerDrawers container, PlayerInventory playerInv, ITextComponent name) {
            super(container, playerInv, name, guiTextures2);
        }
    }

    public static class Slot4 extends DrawerScreen {
        public Slot4(ContainerDrawers container, PlayerInventory playerInv, ITextComponent name) {
            super(container, playerInv, name, guiTextures4);
        }
    }

    public static class Compacting extends DrawerScreen {
        public Compacting(ContainerDrawers container, PlayerInventory playerInv, ITextComponent name) {
            super(container, playerInv, name, guiTexturesComp);
        }
    }

    @Override
    protected void init () {
        super.init();

        if (storageItemRender == null) {
            ItemRenderer defaultRenderItem = minecraft.getItemRenderer();
            storageItemRender = new StorageRenderItem(minecraft.getTextureManager(), defaultRenderItem.getItemModelMesher().getModelManager(), minecraft.getItemColors());
        }
    }

    @Override
    public void render (MatrixStack stack, int p_render_1_, int p_render_2_, float p_render_3_) {
        ItemRenderer ri = setItemRender(storageItemRender);
        container.activeRenderItem = storageItemRender;

        this.renderBackground(stack);
        super.render(stack, p_render_1_, p_render_2_, p_render_3_);
        this.func_230459_a_(stack, p_render_1_, p_render_2_);

        container.activeRenderItem = null;
        storageItemRender.overrideStack = ItemStack.EMPTY;

        setItemRender(ri);
    }

    @Override
    protected void drawGuiContainerForegroundLayer (MatrixStack stack, int mouseX, int mouseY) {
        this.font.drawString(stack, this.title.getString(), 8.0F, 6.0F, 4210752);
        this.font.drawString(stack, I18n.format("container.storagedrawers.upgrades"), 8, 75, 4210752);
        this.font.drawString(stack, this.playerInventory.getDisplayName().getString(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer (MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1, 1, 1, 1);

        minecraft.getTextureManager().bindTexture(background);

        int guiX = (width - xSize) / 2;
        int guiY = (height - ySize) / 2;
        blit(stack, guiX, guiY, 0, 0, xSize, ySize);

        List<Slot> storageSlots = container.getStorageSlots();
        for (Slot slot : storageSlots) {
            blit(stack, guiX + slot.xPos, guiY + slot.yPos, smDisabledX, smDisabledY, 16, 16);
        }

        /*List<Slot> upgradeSlots = container.getUpgradeSlots();
        for (Slot slot : upgradeSlots) {
            if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack())
                blit(guiX + slot.xPos, guiY + slot.yPos, smDisabledX, smDisabledY, 16, 16);
        }*/
    }



    @Override
    protected boolean isPointInRegion (int x, int y, int width, int height, double originX, double originY) {
        List<Slot> storageSlots = container.getStorageSlots();
        for (Slot slot : storageSlots) {
            if (slot instanceof SlotStorage && slot.xPos == x && slot.yPos == y)
                return false;
        }

        /*List<Slot> upgradeSlots = container.getUpgradeSlots();
        for (Slot slot : upgradeSlots) {
            if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack() && slot.xPos == x && slot.yPos == y)
                return false;
        }*/

        return super.isPointInRegion(x, y, width, height, originX, originY);
    }

    private ItemRenderer setItemRender (ItemRenderer renderItem) {
        ItemRenderer prev = itemRenderer;
        itemRenderer = renderItem;

        return prev;
    }
}