package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenRealmsProxy extends GuiScreen
{
    private final RealmsScreen proxy;

    public GuiScreenRealmsProxy(RealmsScreen proxyIn)
    {
        this.proxy = proxyIn;
        super.buttonList = Collections.<GuiButton>synchronizedList(Lists.<GuiButton>newArrayList());
    }

    public RealmsScreen getProxy()
    {
        return this.proxy;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.proxy.init();
        super.initGui();
    }

    public void drawCenteredString(String p_154325_1_, int p_154325_2_, int p_154325_3_, int p_154325_4_)
    {
        super.drawCenteredString(this.fontRendererObj, p_154325_1_, p_154325_2_, p_154325_3_, p_154325_4_);
    }

    public void drawString(String p_154322_1_, int p_154322_2_, int p_154322_3_, int p_154322_4_, boolean p_154322_5_)
    {
        if (p_154322_5_)
        {
            super.drawString(this.fontRendererObj, p_154322_1_, p_154322_2_, p_154322_3_, p_154322_4_);
        }
        else
        {
            this.fontRendererObj.drawString(p_154322_1_, p_154322_2_, p_154322_3_, p_154322_4_);
        }
    }

    /**
     * Draws a textured rectangle at the current z-value.
     */
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        this.proxy.blit(x, y, textureX, textureY, width, height);
        super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    public void drawDefaultBackground()
    {
        super.drawDefaultBackground();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return super.doesGuiPauseGame();
    }

    public void drawWorldBackground(int tint)
    {
        super.drawWorldBackground(tint);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.proxy.render(mouseX, mouseY, partialTicks);
    }

    public void renderToolTip(ItemStack stack, int x, int y)
    {
        super.renderToolTip(stack, x, y);
    }

    /**
     * Draws the text when mouse is over creative inventory tab. Params: current creative tab to be checked, current
     * mouse x position, current mouse y position.
     */
    public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY)
    {
        super.drawCreativeTabHoveringText(tabName, mouseX, mouseY);
    }

    /**
     * Draws a List of strings as a tooltip. Every entry is drawn on a seperate line.
     */
    public void drawHoveringText(List<String> textLines, int x, int y)
    {
        super.drawHoveringText(textLines, x, y);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.proxy.tick();
        super.updateScreen();
    }

    public int getFontHeight()
    {
        return this.fontRendererObj.FONT_HEIGHT;
    }

    public int getStringWidth(String p_154326_1_)
    {
        return this.fontRendererObj.getStringWidth(p_154326_1_);
    }

    public void fontDrawShadow(String p_154319_1_, int p_154319_2_, int p_154319_3_, int p_154319_4_)
    {
        this.fontRendererObj.drawStringWithShadow(p_154319_1_, (float)p_154319_2_, (float)p_154319_3_, p_154319_4_);
    }

    public List<String> fontSplit(String p_154323_1_, int p_154323_2_)
    {
        return this.fontRendererObj.listFormattedStringToWidth(p_154323_1_, p_154323_2_);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    public final void actionPerformed(GuiButton button) throws IOException
    {
        this.proxy.buttonClicked(((GuiButtonRealmsProxy)button).getRealmsButton());
    }

    public void buttonsClear()
    {
        super.buttonList.clear();
    }

    public void buttonsAdd(RealmsButton button)
    {
        super.buttonList.add(button.getProxy());
    }

    public List<RealmsButton> buttons()
    {
        List<RealmsButton> list = Lists.<RealmsButton>newArrayListWithExpectedSize(super.buttonList.size());

        for (GuiButton guibutton : super.buttonList)
        {
            list.add(((GuiButtonRealmsProxy)guibutton).getRealmsButton());
        }

        return list;
    }

    public void buttonsRemove(RealmsButton button)
    {
        super.buttonList.remove(button.getProxy());
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        this.proxy.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        this.proxy.mouseEvent();
        super.handleMouseInput();
    }

    /**
     * Handles keyboard input.
     */
    public void handleKeyboardInput() throws IOException
    {
        this.proxy.keyboardEvent();
        super.handleKeyboardInput();
    }

    /**
     * Called when a mouse button is released.
     */
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.proxy.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        this.proxy.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    public void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.proxy.keyPressed(typedChar, keyCode);
    }

    public void confirmClicked(boolean result, int id)
    {
        this.proxy.confirmResult(result, id);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        this.proxy.removed();
        super.onGuiClosed();
    }
}