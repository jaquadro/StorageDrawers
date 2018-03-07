package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiShareToLan extends GuiScreen
{
    private final GuiScreen lastScreen;
    private GuiButton allowCheatsButton;
    private GuiButton gameModeButton;
    private String gameMode = "survival";
    private boolean allowCheats;

    public GuiShareToLan(GuiScreen p_i1055_1_)
    {
        this.lastScreen = p_i1055_1_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("lanServer.start", new Object[0])));
        this.buttonList.add(new GuiButton(102, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel", new Object[0])));
        this.gameModeButton = this.addButton(new GuiButton(104, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.gameMode", new Object[0])));
        this.allowCheatsButton = this.addButton(new GuiButton(103, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.allowCommands", new Object[0])));
        this.updateDisplayNames();
    }

    private void updateDisplayNames()
    {
        this.gameModeButton.displayString = I18n.format("selectWorld.gameMode", new Object[0]) + ": " + I18n.format("selectWorld.gameMode." + this.gameMode, new Object[0]);
        this.allowCheatsButton.displayString = I18n.format("selectWorld.allowCommands", new Object[0]) + " ";

        if (this.allowCheats)
        {
            this.allowCheatsButton.displayString = this.allowCheatsButton.displayString + I18n.format("options.on", new Object[0]);
        }
        else
        {
            this.allowCheatsButton.displayString = this.allowCheatsButton.displayString + I18n.format("options.off", new Object[0]);
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 102)
        {
            this.mc.displayGuiScreen(this.lastScreen);
        }
        else if (button.id == 104)
        {
            if ("spectator".equals(this.gameMode))
            {
                this.gameMode = "creative";
            }
            else if ("creative".equals(this.gameMode))
            {
                this.gameMode = "adventure";
            }
            else if ("adventure".equals(this.gameMode))
            {
                this.gameMode = "survival";
            }
            else
            {
                this.gameMode = "spectator";
            }

            this.updateDisplayNames();
        }
        else if (button.id == 103)
        {
            this.allowCheats = !this.allowCheats;
            this.updateDisplayNames();
        }
        else if (button.id == 101)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            String s = this.mc.getIntegratedServer().shareToLAN(GameType.getByName(this.gameMode), this.allowCheats);
            ITextComponent itextcomponent;

            if (s != null)
            {
                itextcomponent = new TextComponentTranslation("commands.publish.started", new Object[] {s});
            }
            else
            {
                itextcomponent = new TextComponentString("commands.publish.failed");
            }

            this.mc.ingameGUI.getChatGUI().printChatMessage(itextcomponent);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("lanServer.title", new Object[0]), this.width / 2, 50, 16777215);
        this.drawCenteredString(this.fontRendererObj, I18n.format("lanServer.otherPlayers", new Object[0]), this.width / 2, 82, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}