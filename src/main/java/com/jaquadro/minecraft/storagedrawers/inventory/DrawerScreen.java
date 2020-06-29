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

    @Override // init
    protected void func_231160_c_ () {
        super.func_231160_c_();

        if (storageItemRender == null) {
            ItemRenderer defaultRenderItem = field_230706_i_.getItemRenderer(); // minecraft
            storageItemRender = new StorageRenderItem(field_230706_i_.getTextureManager(), defaultRenderItem.getItemModelMesher().getModelManager(), field_230706_i_.getItemColors());
        }
    }

    @Override // render
    public void func_230430_a_ (MatrixStack stack, int p_render_1_, int p_render_2_, float p_render_3_) {
        ItemRenderer ri = setItemRender(storageItemRender);
        container.activeRenderItem = storageItemRender;

        this.func_230446_a_(stack); // renderBackground
        super.func_230430_a_(stack, p_render_1_, p_render_2_, p_render_3_); // render
        this.func_230459_a_(stack, p_render_1_, p_render_2_); // renderHoveredTooltip

        container.activeRenderItem = null;
        storageItemRender.overrideStack = ItemStack.EMPTY;

        setItemRender(ri);
    }

    @Override
    protected void func_230451_b_ (MatrixStack stack, int mouseX, int mouseY) { // drawContainerForegroundLayer
        this.field_230712_o_.func_238422_b_(stack, this.field_230704_d_, 8.0F, 6.0F, 4210752); // drawString
        this.field_230712_o_.func_238421_b_(stack, I18n.format("container.storagedrawers.upgrades"), 8, 75, 4210752);
        this.field_230712_o_.func_238422_b_(stack, this.playerInventory.getDisplayName(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void func_230450_a_ (MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1, 1, 1, 1);

        field_230706_i_.getTextureManager().bindTexture(background);

        int guiX = (field_230708_k_ - xSize) / 2;
        int guiY = (field_230709_l_ - ySize) / 2;
        func_238474_b_(stack, guiX, guiY, 0, 0, xSize, ySize); // blit

        List<Slot> storageSlots = container.getStorageSlots();
        for (Slot slot : storageSlots) {
            func_238474_b_(stack, guiX + slot.xPos, guiY + slot.yPos, smDisabledX, smDisabledY, 16, 16); // blit
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
        ItemRenderer prev = field_230707_j_;
        field_230707_j_ = renderItem;

        return prev;
    }
}