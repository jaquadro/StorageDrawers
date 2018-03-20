package net.minecraft.client.gui.achievement;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiStats extends GuiScreen implements IProgressMeter
{
    protected GuiScreen parentScreen;
    protected String screenTitle = "Select world";
    private GuiStats.StatsGeneral generalStats;
    private GuiStats.StatsItem itemStats;
    private GuiStats.StatsBlock blockStats;
    private GuiStats.StatsMobsList mobStats;
    private final StatisticsManager stats;
    private GuiSlot displaySlot;
    /** When true, the game will be paused when the gui is shown */
    private boolean doesGuiPauseGame = true;

    public GuiStats(GuiScreen p_i1071_1_, StatisticsManager p_i1071_2_)
    {
        this.parentScreen = p_i1071_1_;
        this.stats = p_i1071_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.screenTitle = I18n.format("gui.stats", new Object[0]);
        this.doesGuiPauseGame = true;
        this.mc.getConnection().sendPacket(new CPacketClientStatus(CPacketClientStatus.State.REQUEST_STATS));
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        if (this.displaySlot != null)
        {
            this.displaySlot.handleMouseInput();
        }
    }

    public void initLists()
    {
        this.generalStats = new GuiStats.StatsGeneral(this.mc);
        this.generalStats.registerScrollButtons(1, 1);
        this.itemStats = new GuiStats.StatsItem(this.mc);
        this.itemStats.registerScrollButtons(1, 1);
        this.blockStats = new GuiStats.StatsBlock(this.mc);
        this.blockStats.registerScrollButtons(1, 1);
        this.mobStats = new GuiStats.StatsMobsList(this.mc);
        this.mobStats.registerScrollButtons(1, 1);
    }

    public void createButtons()
    {
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4, this.height - 28, 150, 20, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 160, this.height - 52, 80, 20, I18n.format("stat.generalButton", new Object[0])));
        GuiButton guibutton = this.addButton(new GuiButton(2, this.width / 2 - 80, this.height - 52, 80, 20, I18n.format("stat.blocksButton", new Object[0])));
        GuiButton guibutton1 = this.addButton(new GuiButton(3, this.width / 2, this.height - 52, 80, 20, I18n.format("stat.itemsButton", new Object[0])));
        GuiButton guibutton2 = this.addButton(new GuiButton(4, this.width / 2 + 80, this.height - 52, 80, 20, I18n.format("stat.mobsButton", new Object[0])));

        if (this.blockStats.getSize() == 0)
        {
            guibutton.enabled = false;
        }

        if (this.itemStats.getSize() == 0)
        {
            guibutton1.enabled = false;
        }

        if (this.mobStats.getSize() == 0)
        {
            guibutton2.enabled = false;
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (button.id == 1)
            {
                this.displaySlot = this.generalStats;
            }
            else if (button.id == 3)
            {
                this.displaySlot = this.itemStats;
            }
            else if (button.id == 2)
            {
                this.displaySlot = this.blockStats;
            }
            else if (button.id == 4)
            {
                this.displaySlot = this.mobStats;
            }
            else
            {
                this.displaySlot.actionPerformed(button);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.doesGuiPauseGame)
        {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats", new Object[0]), this.width / 2, this.height / 2, 16777215);
            this.drawCenteredString(this.fontRendererObj, LOADING_STRINGS[(int)(Minecraft.getSystemTime() / 150L % (long)LOADING_STRINGS.length)], this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2, 16777215);
        }
        else
        {
            this.displaySlot.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    public void doneLoading()
    {
        if (this.doesGuiPauseGame)
        {
            this.initLists();
            this.createButtons();
            this.displaySlot = this.generalStats;
            this.doesGuiPauseGame = false;
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return !this.doesGuiPauseGame;
    }

    private void drawStatsScreen(int p_146521_1_, int p_146521_2_, Item p_146521_3_)
    {
        this.drawButtonBackground(p_146521_1_ + 1, p_146521_2_ + 1);
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.renderItemIntoGUI(new ItemStack(p_146521_3_), p_146521_1_ + 2, p_146521_2_ + 2);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }

    /**
     * Draws a gray box that serves as a button background.
     */
    private void drawButtonBackground(int p_146531_1_, int p_146531_2_)
    {
        this.drawSprite(p_146531_1_, p_146531_2_, 0, 0);
    }

    /**
     * Draws a sprite from assets/textures/gui/container/stats_icons.png
     */
    private void drawSprite(int p_146527_1_, int p_146527_2_, int p_146527_3_, int p_146527_4_)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(STAT_ICONS);
        float f = 0.0078125F;
        float f1 = 0.0078125F;
        int i = 18;
        int j = 18;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)(p_146527_1_ + 0), (double)(p_146527_2_ + 18), (double)this.zLevel).tex((double)((float)(p_146527_3_ + 0) * 0.0078125F), (double)((float)(p_146527_4_ + 18) * 0.0078125F)).endVertex();
        vertexbuffer.pos((double)(p_146527_1_ + 18), (double)(p_146527_2_ + 18), (double)this.zLevel).tex((double)((float)(p_146527_3_ + 18) * 0.0078125F), (double)((float)(p_146527_4_ + 18) * 0.0078125F)).endVertex();
        vertexbuffer.pos((double)(p_146527_1_ + 18), (double)(p_146527_2_ + 0), (double)this.zLevel).tex((double)((float)(p_146527_3_ + 18) * 0.0078125F), (double)((float)(p_146527_4_ + 0) * 0.0078125F)).endVertex();
        vertexbuffer.pos((double)(p_146527_1_ + 0), (double)(p_146527_2_ + 0), (double)this.zLevel).tex((double)((float)(p_146527_3_ + 0) * 0.0078125F), (double)((float)(p_146527_4_ + 0) * 0.0078125F)).endVertex();
        tessellator.draw();
    }

    @SideOnly(Side.CLIENT)
    abstract class Stats extends GuiSlot
    {
        protected int headerPressed = -1;
        protected List<StatCrafting> statsHolder;
        protected Comparator<StatCrafting> statSorter;
        protected int sortColumn = -1;
        protected int sortOrder;

        protected Stats(Minecraft mcIn)
        {
            super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 20);
            this.setShowSelectionBox(false);
            this.setHasListHeader(true, 20);
        }

        /**
         * The element in the slot that was clicked, boolean for whether it was double clicked or not
         */
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int slotIndex)
        {
            return false;
        }

        /**
         * Gets the width of the list
         */
        public int getListWidth()
        {
            return 375;
        }

        protected int getScrollBarX()
        {
            return this.width / 2 + 140;
        }

        protected void drawBackground()
        {
            GuiStats.this.drawDefaultBackground();
        }

        /**
         * Handles drawing a list's header row.
         */
        protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
        {
            if (!Mouse.isButtonDown(0))
            {
                this.headerPressed = -1;
            }

            if (this.headerPressed == 0)
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 0, 18);
            }

            if (this.headerPressed == 1)
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 0, 18);
            }

            if (this.headerPressed == 2)
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 0, 18);
            }

            if (this.headerPressed == 3)
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 0, 18);
            }

            if (this.headerPressed == 4)
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 0, 0);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 0, 18);
            }

            if (this.sortColumn != -1)
            {
                int i = 79;
                int j = 18;

                if (this.sortColumn == 1)
                {
                    i = 129;
                }
                else if (this.sortColumn == 2)
                {
                    i = 179;
                }
                else if (this.sortColumn == 3)
                {
                    i = 229;
                }
                else if (this.sortColumn == 4)
                {
                    i = 279;
                }

                if (this.sortOrder == 1)
                {
                    j = 36;
                }

                GuiStats.this.drawSprite(insideLeft + i, insideTop + 1, j, 0);
            }
        }

        protected void clickedHeader(int p_148132_1_, int p_148132_2_)
        {
            this.headerPressed = -1;

            if (p_148132_1_ >= 79 && p_148132_1_ < 115)
            {
                this.headerPressed = 0;
            }
            else if (p_148132_1_ >= 129 && p_148132_1_ < 165)
            {
                this.headerPressed = 1;
            }
            else if (p_148132_1_ >= 179 && p_148132_1_ < 215)
            {
                this.headerPressed = 2;
            }
            else if (p_148132_1_ >= 229 && p_148132_1_ < 265)
            {
                this.headerPressed = 3;
            }
            else if (p_148132_1_ >= 279 && p_148132_1_ < 315)
            {
                this.headerPressed = 4;
            }

            if (this.headerPressed >= 0)
            {
                this.sortByColumn(this.headerPressed);
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }

        protected final int getSize()
        {
            return this.statsHolder.size();
        }

        protected final StatCrafting getSlotStat(int p_148211_1_)
        {
            return (StatCrafting)this.statsHolder.get(p_148211_1_);
        }

        protected abstract String getHeaderDescriptionId(int p_148210_1_);

        protected void renderStat(StatBase p_148209_1_, int p_148209_2_, int p_148209_3_, boolean p_148209_4_)
        {
            if (p_148209_1_ != null)
            {
                String s = p_148209_1_.format(GuiStats.this.stats.readStat(p_148209_1_));
                GuiStats.this.drawString(GuiStats.this.fontRendererObj, s, p_148209_2_ - GuiStats.this.fontRendererObj.getStringWidth(s), p_148209_3_ + 5, p_148209_4_ ? 16777215 : 9474192);
            }
            else
            {
                String s1 = "-";
                GuiStats.this.drawString(GuiStats.this.fontRendererObj, "-", p_148209_2_ - GuiStats.this.fontRendererObj.getStringWidth("-"), p_148209_3_ + 5, p_148209_4_ ? 16777215 : 9474192);
            }
        }

        protected void renderDecorations(int mouseXIn, int mouseYIn)
        {
            if (mouseYIn >= this.top && mouseYIn <= this.bottom)
            {
                int i = this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn);
                int j = (this.width - this.getListWidth()) / 2;

                if (i >= 0)
                {
                    if (mouseXIn < j + 40 || mouseXIn > j + 40 + 20)
                    {
                        return;
                    }

                    StatCrafting statcrafting = this.getSlotStat(i);
                    this.renderMouseHoverToolTip(statcrafting, mouseXIn, mouseYIn);
                }
                else
                {
                    String s;

                    if (mouseXIn >= j + 115 - 18 && mouseXIn <= j + 115)
                    {
                        s = this.getHeaderDescriptionId(0);
                    }
                    else if (mouseXIn >= j + 165 - 18 && mouseXIn <= j + 165)
                    {
                        s = this.getHeaderDescriptionId(1);
                    }
                    else if (mouseXIn >= j + 215 - 18 && mouseXIn <= j + 215)
                    {
                        s = this.getHeaderDescriptionId(2);
                    }
                    else if (mouseXIn >= j + 265 - 18 && mouseXIn <= j + 265)
                    {
                        s = this.getHeaderDescriptionId(3);
                    }
                    else
                    {
                        if (mouseXIn < j + 315 - 18 || mouseXIn > j + 315)
                        {
                            return;
                        }

                        s = this.getHeaderDescriptionId(4);
                    }

                    s = ("" + I18n.format(s, new Object[0])).trim();

                    if (!s.isEmpty())
                    {
                        int k = mouseXIn + 12;
                        int l = mouseYIn - 12;
                        int i1 = GuiStats.this.fontRendererObj.getStringWidth(s);
                        GuiStats.this.drawGradientRect(k - 3, l - 3, k + i1 + 3, l + 8 + 3, -1073741824, -1073741824);
                        GuiStats.this.fontRendererObj.drawStringWithShadow(s, (float)k, (float)l, -1);
                    }
                }
            }
        }

        protected void renderMouseHoverToolTip(StatCrafting p_148213_1_, int p_148213_2_, int p_148213_3_)
        {
            if (p_148213_1_ != null)
            {
                Item item = p_148213_1_.getItem();
                ItemStack itemstack = new ItemStack(item);
                String s = itemstack.getUnlocalizedName();
                String s1 = ("" + I18n.format(s + ".name", new Object[0])).trim();

                if (!s1.isEmpty())
                {
                    int i = p_148213_2_ + 12;
                    int j = p_148213_3_ - 12;
                    int k = GuiStats.this.fontRendererObj.getStringWidth(s1);
                    GuiStats.this.drawGradientRect(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
                    GuiStats.this.fontRendererObj.drawStringWithShadow(s1, (float)i, (float)j, -1);
                }
            }
        }

        protected void sortByColumn(int p_148212_1_)
        {
            if (p_148212_1_ != this.sortColumn)
            {
                this.sortColumn = p_148212_1_;
                this.sortOrder = -1;
            }
            else if (this.sortOrder == -1)
            {
                this.sortOrder = 1;
            }
            else
            {
                this.sortColumn = -1;
                this.sortOrder = 0;
            }

            Collections.sort(this.statsHolder, this.statSorter);
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsBlock extends GuiStats.Stats
    {
        public StatsBlock(Minecraft mcIn)
        {
            super(mcIn);
            this.statsHolder = Lists.<StatCrafting>newArrayList();

            for (StatCrafting statcrafting : StatList.MINE_BLOCK_STATS)
            {
                boolean flag = false;
                Item item = statcrafting.getItem();

                if (GuiStats.this.stats.readStat(statcrafting) > 0)
                {
                    flag = true;
                }
                else if (StatList.getObjectUseStats(item) != null && GuiStats.this.stats.readStat(StatList.getObjectUseStats(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.getCraftStats(item) != null && GuiStats.this.stats.readStat(StatList.getCraftStats(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.getObjectsPickedUpStats(item) != null && GuiStats.this.stats.readStat(StatList.getObjectsPickedUpStats(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.getDroppedObjectStats(item) != null && GuiStats.this.stats.readStat(StatList.getDroppedObjectStats(item)) > 0)
                {
                    flag = true;
                }

                if (flag)
                {
                    this.statsHolder.add(statcrafting);
                }
            }

            this.statSorter = new Comparator<StatCrafting>()
            {
                public int compare(StatCrafting p_compare_1_, StatCrafting p_compare_2_)
                {
                    Item item1 = p_compare_1_.getItem();
                    Item item2 = p_compare_2_.getItem();
                    StatBase statbase = null;
                    StatBase statbase1 = null;

                    if (StatsBlock.this.sortColumn == 2)
                    {
                        statbase = StatList.getBlockStats(Block.getBlockFromItem(item1));
                        statbase1 = StatList.getBlockStats(Block.getBlockFromItem(item2));
                    }
                    else if (StatsBlock.this.sortColumn == 0)
                    {
                        statbase = StatList.getCraftStats(item1);
                        statbase1 = StatList.getCraftStats(item2);
                    }
                    else if (StatsBlock.this.sortColumn == 1)
                    {
                        statbase = StatList.getObjectUseStats(item1);
                        statbase1 = StatList.getObjectUseStats(item2);
                    }
                    else if (StatsBlock.this.sortColumn == 3)
                    {
                        statbase = StatList.getObjectsPickedUpStats(item1);
                        statbase1 = StatList.getObjectsPickedUpStats(item2);
                    }
                    else if (StatsBlock.this.sortColumn == 4)
                    {
                        statbase = StatList.getDroppedObjectStats(item1);
                        statbase1 = StatList.getDroppedObjectStats(item2);
                    }

                    if (statbase != null || statbase1 != null)
                    {
                        if (statbase == null)
                        {
                            return 1;
                        }

                        if (statbase1 == null)
                        {
                            return -1;
                        }

                        int i = GuiStats.this.stats.readStat(statbase);
                        int j = GuiStats.this.stats.readStat(statbase1);

                        if (i != j)
                        {
                            return (i - j) * StatsBlock.this.sortOrder;
                        }
                    }

                    return Item.getIdFromItem(item1) - Item.getIdFromItem(item2);
                }
            };
        }

        /**
         * Handles drawing a list's header row.
         */
        protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
        {
            super.drawListHeader(insideLeft, insideTop, tessellatorIn);

            if (this.headerPressed == 0)
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18 + 1, insideTop + 1 + 1, 18, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 18, 18);
            }

            if (this.headerPressed == 1)
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18 + 1, insideTop + 1 + 1, 36, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 36, 18);
            }

            if (this.headerPressed == 2)
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18 + 1, insideTop + 1 + 1, 54, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 54, 18);
            }

            if (this.headerPressed == 3)
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18 + 1, insideTop + 1 + 1, 90, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 90, 18);
            }

            if (this.headerPressed == 4)
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18 + 1, insideTop + 1 + 1, 108, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 108, 18);
            }
        }

        protected void drawSlot(int entryID, int insideLeft, int yPos, int insideSlotHeight, int mouseXIn, int mouseYIn)
        {
            StatCrafting statcrafting = this.getSlotStat(entryID);
            Item item = statcrafting.getItem();
            GuiStats.this.drawStatsScreen(insideLeft + 40, yPos, item);
            this.renderStat(StatList.getCraftStats(item), insideLeft + 115, yPos, entryID % 2 == 0);
            this.renderStat(StatList.getObjectUseStats(item), insideLeft + 165, yPos, entryID % 2 == 0);
            this.renderStat(statcrafting, insideLeft + 215, yPos, entryID % 2 == 0);
            this.renderStat(StatList.getObjectsPickedUpStats(item), insideLeft + 265, yPos, entryID % 2 == 0);
            this.renderStat(StatList.getDroppedObjectStats(item), insideLeft + 315, yPos, entryID % 2 == 0);
        }

        protected String getHeaderDescriptionId(int p_148210_1_)
        {
            return p_148210_1_ == 0 ? "stat.crafted" : (p_148210_1_ == 1 ? "stat.used" : (p_148210_1_ == 3 ? "stat.pickup" : (p_148210_1_ == 4 ? "stat.dropped" : "stat.mined")));
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsGeneral extends GuiSlot
    {
        public StatsGeneral(Minecraft mcIn)
        {
            super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 10);
            this.setShowSelectionBox(false);
        }

        protected int getSize()
        {
            return StatList.BASIC_STATS.size();
        }

        /**
         * The element in the slot that was clicked, boolean for whether it was double clicked or not
         */
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int slotIndex)
        {
            return false;
        }

        /**
         * Return the height of the content being scrolled
         */
        protected int getContentHeight()
        {
            return this.getSize() * 10;
        }

        protected void drawBackground()
        {
            GuiStats.this.drawDefaultBackground();
        }

        protected void drawSlot(int entryID, int insideLeft, int yPos, int insideSlotHeight, int mouseXIn, int mouseYIn)
        {
            StatBase statbase = (StatBase)StatList.BASIC_STATS.get(entryID);
            GuiStats.this.drawString(GuiStats.this.fontRendererObj, statbase.getStatName().getUnformattedText(), insideLeft + 2, yPos + 1, entryID % 2 == 0 ? 16777215 : 9474192);
            String s = statbase.format(GuiStats.this.stats.readStat(statbase));
            GuiStats.this.drawString(GuiStats.this.fontRendererObj, s, insideLeft + 2 + 213 - GuiStats.this.fontRendererObj.getStringWidth(s), yPos + 1, entryID % 2 == 0 ? 16777215 : 9474192);
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsItem extends GuiStats.Stats
    {
        public StatsItem(Minecraft mcIn)
        {
            super(mcIn);
            this.statsHolder = Lists.<StatCrafting>newArrayList();

            for (StatCrafting statcrafting : StatList.USE_ITEM_STATS)
            {
                boolean flag = false;
                Item item = statcrafting.getItem();

                if (GuiStats.this.stats.readStat(statcrafting) > 0)
                {
                    flag = true;
                }
                else if (StatList.getObjectBreakStats(item) != null && GuiStats.this.stats.readStat(StatList.getObjectBreakStats(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.getCraftStats(item) != null && GuiStats.this.stats.readStat(StatList.getCraftStats(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.getObjectsPickedUpStats(item) != null && GuiStats.this.stats.readStat(StatList.getObjectsPickedUpStats(item)) > 0)
                {
                    flag = true;
                }
                else if (StatList.getDroppedObjectStats(item) != null && GuiStats.this.stats.readStat(StatList.getDroppedObjectStats(item)) > 0)
                {
                    flag = true;
                }

                if (flag)
                {
                    this.statsHolder.add(statcrafting);
                }
            }

            this.statSorter = new Comparator<StatCrafting>()
            {
                public int compare(StatCrafting p_compare_1_, StatCrafting p_compare_2_)
                {
                    Item item1 = p_compare_1_.getItem();
                    Item item2 = p_compare_2_.getItem();
                    int i = Item.getIdFromItem(item1);
                    int j = Item.getIdFromItem(item2);
                    StatBase statbase = null;
                    StatBase statbase1 = null;

                    if (StatsItem.this.sortColumn == 0)
                    {
                        statbase = StatList.getObjectBreakStats(item1);
                        statbase1 = StatList.getObjectBreakStats(item2);
                    }
                    else if (StatsItem.this.sortColumn == 1)
                    {
                        statbase = StatList.getCraftStats(item1);
                        statbase1 = StatList.getCraftStats(item2);
                    }
                    else if (StatsItem.this.sortColumn == 2)
                    {
                        statbase = StatList.getObjectUseStats(item1);
                        statbase1 = StatList.getObjectUseStats(item2);
                    }
                    else if (StatsItem.this.sortColumn == 3)
                    {
                        statbase = StatList.getObjectsPickedUpStats(item1);
                        statbase1 = StatList.getObjectsPickedUpStats(item2);
                    }
                    else if (StatsItem.this.sortColumn == 4)
                    {
                        statbase = StatList.getDroppedObjectStats(item1);
                        statbase1 = StatList.getDroppedObjectStats(item2);
                    }

                    if (statbase != null || statbase1 != null)
                    {
                        if (statbase == null)
                        {
                            return 1;
                        }

                        if (statbase1 == null)
                        {
                            return -1;
                        }

                        int k = GuiStats.this.stats.readStat(statbase);
                        int l = GuiStats.this.stats.readStat(statbase1);

                        if (k != l)
                        {
                            return (k - l) * StatsItem.this.sortOrder;
                        }
                    }

                    return i - j;
                }
            };
        }

        /**
         * Handles drawing a list's header row.
         */
        protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
        {
            super.drawListHeader(insideLeft, insideTop, tessellatorIn);

            if (this.headerPressed == 0)
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18 + 1, insideTop + 1 + 1, 72, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 72, 18);
            }

            if (this.headerPressed == 1)
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18 + 1, insideTop + 1 + 1, 18, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 18, 18);
            }

            if (this.headerPressed == 2)
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18 + 1, insideTop + 1 + 1, 36, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 36, 18);
            }

            if (this.headerPressed == 3)
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18 + 1, insideTop + 1 + 1, 90, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 90, 18);
            }

            if (this.headerPressed == 4)
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18 + 1, insideTop + 1 + 1, 108, 18);
            }
            else
            {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 108, 18);
            }
        }

        protected void drawSlot(int entryID, int insideLeft, int yPos, int insideSlotHeight, int mouseXIn, int mouseYIn)
        {
            StatCrafting statcrafting = this.getSlotStat(entryID);
            Item item = statcrafting.getItem();
            GuiStats.this.drawStatsScreen(insideLeft + 40, yPos, item);
            this.renderStat(StatList.getObjectBreakStats(item), insideLeft + 115, yPos, entryID % 2 == 0);
            this.renderStat(StatList.getCraftStats(item), insideLeft + 165, yPos, entryID % 2 == 0);
            this.renderStat(statcrafting, insideLeft + 215, yPos, entryID % 2 == 0);
            this.renderStat(StatList.getObjectsPickedUpStats(item), insideLeft + 265, yPos, entryID % 2 == 0);
            this.renderStat(StatList.getDroppedObjectStats(item), insideLeft + 315, yPos, entryID % 2 == 0);
        }

        protected String getHeaderDescriptionId(int p_148210_1_)
        {
            return p_148210_1_ == 1 ? "stat.crafted" : (p_148210_1_ == 2 ? "stat.used" : (p_148210_1_ == 3 ? "stat.pickup" : (p_148210_1_ == 4 ? "stat.dropped" : "stat.depleted")));
        }
    }

    @SideOnly(Side.CLIENT)
    class StatsMobsList extends GuiSlot
    {
        private final List<EntityList.EntityEggInfo> mobs = Lists.<EntityList.EntityEggInfo>newArrayList();

        public StatsMobsList(Minecraft mcIn)
        {
            super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, GuiStats.this.fontRendererObj.FONT_HEIGHT * 4);
            this.setShowSelectionBox(false);

            for (EntityList.EntityEggInfo entitylist$entityegginfo : EntityList.ENTITY_EGGS.values())
            {
                if (GuiStats.this.stats.readStat(entitylist$entityegginfo.killEntityStat) > 0 || GuiStats.this.stats.readStat(entitylist$entityegginfo.entityKilledByStat) > 0)
                {
                    this.mobs.add(entitylist$entityegginfo);
                }
            }
        }

        protected int getSize()
        {
            return this.mobs.size();
        }

        /**
         * The element in the slot that was clicked, boolean for whether it was double clicked or not
         */
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int slotIndex)
        {
            return false;
        }

        /**
         * Return the height of the content being scrolled
         */
        protected int getContentHeight()
        {
            return this.getSize() * GuiStats.this.fontRendererObj.FONT_HEIGHT * 4;
        }

        protected void drawBackground()
        {
            GuiStats.this.drawDefaultBackground();
        }

        protected void drawSlot(int entryID, int insideLeft, int yPos, int insideSlotHeight, int mouseXIn, int mouseYIn)
        {
            EntityList.EntityEggInfo entitylist$entityegginfo = (EntityList.EntityEggInfo)this.mobs.get(entryID);
            String s = I18n.format("entity." + entitylist$entityegginfo.spawnedID + ".name", new Object[0]);
            int i = GuiStats.this.stats.readStat(entitylist$entityegginfo.killEntityStat);
            int j = GuiStats.this.stats.readStat(entitylist$entityegginfo.entityKilledByStat);
            String s1 = I18n.format("stat.entityKills", new Object[] {Integer.valueOf(i), s});
            String s2 = I18n.format("stat.entityKilledBy", new Object[] {s, Integer.valueOf(j)});

            if (i == 0)
            {
                s1 = I18n.format("stat.entityKills.none", new Object[] {s});
            }

            if (j == 0)
            {
                s2 = I18n.format("stat.entityKilledBy.none", new Object[] {s});
            }

            GuiStats.this.drawString(GuiStats.this.fontRendererObj, s, insideLeft + 2 - 10, yPos + 1, 16777215);
            GuiStats.this.drawString(GuiStats.this.fontRendererObj, s1, insideLeft + 2, yPos + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT, i == 0 ? 6316128 : 9474192);
            GuiStats.this.drawString(GuiStats.this.fontRendererObj, s2, insideLeft + 2, yPos + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT * 2, j == 0 ? 6316128 : 9474192);
        }
    }
}