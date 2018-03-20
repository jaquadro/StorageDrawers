package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ServerListEntryLanDetected implements GuiListExtended.IGuiListEntry
{
    private final GuiMultiplayer screen;
    protected final Minecraft mc;
    protected final LanServerInfo serverData;
    private long lastClickTime;

    protected ServerListEntryLanDetected(GuiMultiplayer p_i47141_1_, LanServerInfo p_i47141_2_)
    {
        this.screen = p_i47141_1_;
        this.serverData = p_i47141_2_;
        this.mc = Minecraft.getMinecraft();
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
    {
        this.mc.fontRendererObj.drawString(I18n.format("lanServer.title", new Object[0]), x + 32 + 3, y + 1, 16777215);
        this.mc.fontRendererObj.drawString(this.serverData.getServerMotd(), x + 32 + 3, y + 12, 8421504);

        if (this.mc.gameSettings.hideServerAddress)
        {
            this.mc.fontRendererObj.drawString(I18n.format("selectServer.hiddenAddress", new Object[0]), x + 32 + 3, y + 12 + 11, 3158064);
        }
        else
        {
            this.mc.fontRendererObj.drawString(this.serverData.getServerIpPort(), x + 32 + 3, y + 12 + 11, 3158064);
        }
    }

    /**
     * Called when the mouse is clicked within this entry. Returning true means that something within this entry was
     * clicked and the list should not be dragged.
     */
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
    {
        this.screen.selectServer(slotIndex);

        if (Minecraft.getSystemTime() - this.lastClickTime < 250L)
        {
            this.screen.connectToSelected();
        }

        this.lastClickTime = Minecraft.getSystemTime();
        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
    {
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
    {
    }

    public LanServerInfo getServerData()
    {
        return this.serverData;
    }
}