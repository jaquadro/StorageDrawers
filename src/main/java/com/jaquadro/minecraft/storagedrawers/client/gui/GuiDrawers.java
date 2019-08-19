/*package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.jaquadro.minecraft.storagedrawers.inventory.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiDrawers extends GuiContainer
{
    private static final ResourceLocation guiTextures1 = new ResourceLocation("storagedrawers", "textures/gui/drawers_1.png");
    private static final ResourceLocation guiTextures2 = new ResourceLocation("storagedrawers", "textures/gui/drawers_2.png");
    private static final ResourceLocation guiTextures4 = new ResourceLocation("storagedrawers", "textures/gui/drawers_4.png");
    private static final ResourceLocation guiTexturesComp = new ResourceLocation("storagedrawers", "textures/gui/drawers_comp.png");

    private static final int smDisabledX = 176;
    private static final int smDisabledY = 0;

    private static final Object[] empty = new Object[0];

    private static StorageRenderItem storageItemRender;

    private TileEntityDrawers tileDrawers;

    public GuiDrawers (InventoryPlayer inventory, TileEntityDrawers tileEntity) {
        super(getContainer(inventory, tileEntity));
        tileDrawers = tileEntity;

        xSize = 176;
        ySize = 199;

        if (storageItemRender == null) {
            Minecraft minecraft = Minecraft.getMinecraft();
            RenderItem defaultRenderItem = minecraft.getRenderItem();
            storageItemRender = new StorageRenderItem(minecraft.getTextureManager(), defaultRenderItem.getItemModelMesher().getModelManager(), minecraft.getItemColors());
        }

        itemRender = storageItemRender;
    }

    private static Container getContainer (InventoryPlayer inventory, TileEntityDrawers tile) {
        switch (tile.getDrawerCount()) {
            case 1:
                return new ContainerDrawers1(inventory, tile);
            case 2:
                return new ContainerDrawers2(inventory, tile);
            case 3:
                return new ContainerDrawersComp(inventory, tile);
            case 4:
                return new ContainerDrawers4(inventory, tile);
            default:
                return null;
        }
    }

    @Override
    public void drawScreen (int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        RenderItem ri = setItemRender(storageItemRender);

        if (inventorySlots instanceof ContainerDrawers) {
            ((ContainerDrawers) inventorySlots).activeRenderItem = storageItemRender;
        }

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);

        if (inventorySlots instanceof ContainerDrawers) {
            ((ContainerDrawers) inventorySlots).activeRenderItem = null;
            storageItemRender.overrideStack = ItemStack.EMPTY;
        }

        setItemRender(ri);
        this.renderHoveredToolTip(p_73863_1_, p_73863_2_);
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int p_146979_1_, int p_146979_2_) {
        String name = tileDrawers.hasCustomName() ? tileDrawers.getName() : I18n.format(tileDrawers.getName(), empty);
        fontRenderer.drawString(name, 8, 6, 4210752);
        fontRenderer.drawString(I18n.format("storagedrawers.container.upgrades", empty), 8, 75, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory",empty), 8, ySize - 96 + 2, 4210752);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer (float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        if (tileDrawers.getDrawerCount() == 1)
            mc.getTextureManager().bindTexture(guiTextures1);
        else if (tileDrawers.getDrawerCount() == 2)
            mc.getTextureManager().bindTexture(guiTextures2);
        else if (tileDrawers.getDrawerCount() == 4)
            mc.getTextureManager().bindTexture(guiTextures4);
        else
            mc.getTextureManager().bindTexture(guiTexturesComp);

        int guiX = (width - xSize) / 2;
        int guiY = (height - ySize) / 2;
        drawTexturedModalRect(guiX, guiY, 0, 0, xSize, ySize);

        if (inventorySlots instanceof ContainerDrawers) {
            ContainerDrawers container = (ContainerDrawers) inventorySlots;
            List<Slot> storageSlots = container.getStorageSlots();
            for (Slot slot : storageSlots) {
                drawTexturedModalRect(guiX + slot.xPos, guiY + slot.yPos, smDisabledX, smDisabledY, 16, 16);
            }

            List<Slot> upgradeSlots = container.getUpgradeSlots();
            for (Slot slot : upgradeSlots) {
                if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack())
                    drawTexturedModalRect(guiX + slot.xPos, guiY + slot.yPos, smDisabledX, smDisabledY, 16, 16);
            }
        }
    }

    @Override
    protected boolean isPointInRegion (int x, int y, int width, int height, int originX, int originY) {
        if (inventorySlots instanceof ContainerDrawers) {
            ContainerDrawers container = (ContainerDrawers) inventorySlots;
            List<Slot> storageSlots = container.getStorageSlots();
            for (Slot slot : storageSlots) {
                if (slot instanceof SlotStorage && slot.xPos == x && slot.yPos == y)
                    return false;
            }

            List<Slot> upgradeSlots = container.getUpgradeSlots();
            for (Slot slot : upgradeSlots) {
                if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack() && slot.xPos == x && slot.yPos == y)
                    return false;
            }
        }

        return super.isPointInRegion(x, y, width, height, originX, originY);
    }

    private RenderItem setItemRender (RenderItem renderItem) {
        RenderItem prev = itemRender;
        itemRender = renderItem;

        return prev;
    }
}
*/