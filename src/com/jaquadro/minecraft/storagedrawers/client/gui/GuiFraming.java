package com.jaquadro.minecraft.storagedrawers.client.gui;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerFramingTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiFraming extends GuiContainer
{
    private static final ResourceLocation guiTextires = new ResourceLocation(StorageDrawers.MOD_ID.toLowerCase(), "textures/gui/framing.png");
    private TileEntityFramingTable tileFramingTable;

    public GuiFraming (InventoryPlayer inventory, TileEntityFramingTable tileEntity) {
        super(new ContainerFramingTable(inventory, tileEntity));
        tileFramingTable = tileEntity;

        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY) {
        String name = tileFramingTable.hasCustomInventoryName() ? tileFramingTable.getInventoryName() : I18n.format(tileFramingTable.getInventoryName());
        fontRendererObj.drawString(name, 8, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer (float dt, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(guiTextires);

        int guiX = (width - xSize) / 2;
        int guiY = (height - ySize) / 2;
        drawTexturedModalRect(guiX, guiY, 0, 0, xSize, ySize);
    }
}
