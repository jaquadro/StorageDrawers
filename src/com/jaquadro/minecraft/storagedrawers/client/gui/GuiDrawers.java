package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersBase;
import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import scala.tools.nsc.doc.model.Object;

public class GuiDrawers extends GuiContainer
{
    private static final ResourceLocation guiTextures2 = new ResourceLocation("storagedrawers", "textures/gui/drawers_2.png");
    private static final ResourceLocation guiTextures4 = new ResourceLocation("storagedrawers", "textures/gui/drawers_4.png");
    private static final ResourceLocation guiTexturesComp = new ResourceLocation("storagedrawers", "textures/gui/drawers_comp.png");

    private static final Object[] empty = new Object[0];

    private static RenderItem storageItemRender = new StorageRenderItem();

    private TileEntityDrawersBase tileDrawers;

    public GuiDrawers (InventoryPlayer inventory, TileEntityDrawersBase tileEntity) {
        super(getContainer(inventory, tileEntity));
        tileDrawers = tileEntity;

        xSize = 176;
        ySize = 199;

        itemRender = storageItemRender;
    }

    private static Container getContainer (InventoryPlayer inventory, TileEntityDrawersBase tile) {
        switch (tile.getDrawerCount()) {
            case 2:
                return new ContainerDrawers2(inventory, tile);
            case 4:
                return new ContainerDrawers4(inventory, tile);
            default:
                return null;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int p_146979_1_, int p_146979_2_) {
        String name = tileDrawers.hasCustomInventoryName() ? tileDrawers.getInventoryName() : I18n.format(tileDrawers.getInventoryName(), empty);
        fontRendererObj.drawString(name, 8, 6, 4210752);
        fontRendererObj.drawString(I18n.format("storageDrawers.container.upgrades", empty), 8, 75, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory",empty), 8, ySize - 96 + 2, 4210752);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer (float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        if (tileDrawers.getDrawerCount() == 2)
            mc.getTextureManager().bindTexture(guiTextures2);
        else if (tileDrawers.getDrawerCount() == 4)
            mc.getTextureManager().bindTexture(guiTextures4);
        else
            mc.getTextureManager().bindTexture(guiTexturesComp);

        int guiX = (width - xSize) / 2;
        int guiY = (height - ySize) / 2;
        drawTexturedModalRect(guiX, guiY, 0, 0, xSize, ySize);
    }

    private RenderItem setItemRender (RenderItem renderItem) {
        if (StorageDrawers.integration.NEI.isLoaded())
            return StorageDrawers.integration.NEI.setItemRender(renderItem);
        else {
            RenderItem prev = itemRender;
            itemRender = renderItem;

            return prev;
        }
    }

    @Override
    public void func_146977_a (Slot slot) {
        RenderItem ri = setItemRender(storageItemRender);

        super.func_146977_a(slot);

        setItemRender(ri);
    }
}
