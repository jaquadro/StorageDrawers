package net.minecraft.client.gui.inventory;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiContainerCreative extends InventoryEffectRenderer
{
    /** The location of the creative inventory tabs texture */
    private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static final InventoryBasic basicInventory = new InventoryBasic("tmp", true, 45);
    /** Currently selected creative inventory tab index. */
    private static int selectedTabIndex = CreativeTabs.BUILDING_BLOCKS.getTabIndex();
    /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
    private float currentScroll;
    /** True if the scrollbar is being dragged */
    private boolean isScrolling;
    /** True if the left mouse button was held down last time drawScreen was called. */
    private boolean wasClicking;
    private GuiTextField searchField;
    private List<Slot> originalSlots;
    private Slot destroyItemSlot;
    private boolean clearSearch;
    private CreativeCrafting listener;
    private static int tabPage = 0;
    private int maxPages = 0;

    public GuiContainerCreative(EntityPlayer player)
    {
        super(new GuiContainerCreative.ContainerCreative(player));
        player.openContainer = this.inventorySlots;
        this.allowUserInput = true;
        this.ySize = 136;
        this.xSize = 195;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (!this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.player));
        }
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        this.clearSearch = true;
        boolean flag = type == ClickType.QUICK_MOVE;
        type = slotId == -999 && type == ClickType.PICKUP ? ClickType.THROW : type;

        if (slotIn == null && selectedTabIndex != CreativeTabs.INVENTORY.getTabIndex() && type != ClickType.QUICK_CRAFT)
        {
            InventoryPlayer inventoryplayer1 = this.mc.player.inventory;

            if (inventoryplayer1.getItemStack() != null)
            {
                if (mouseButton == 0)
                {
                    this.mc.player.dropItem(inventoryplayer1.getItemStack(), true);
                    this.mc.playerController.sendPacketDropItem(inventoryplayer1.getItemStack());
                    inventoryplayer1.setItemStack((ItemStack)null);
                }

                if (mouseButton == 1)
                {
                    ItemStack itemstack5 = inventoryplayer1.getItemStack().splitStack(1);
                    this.mc.player.dropItem(itemstack5, true);
                    this.mc.playerController.sendPacketDropItem(itemstack5);

                    if (inventoryplayer1.getItemStack().stackSize == 0)
                    {
                        inventoryplayer1.setItemStack((ItemStack)null);
                    }
                }
            }
        }
        else if (slotIn == this.destroyItemSlot && flag)
        {
            for (int j = 0; j < this.mc.player.inventoryContainer.getInventory().size(); ++j)
            {
                this.mc.playerController.sendSlotPacket((ItemStack)null, j);
            }
        }
        else if (selectedTabIndex == CreativeTabs.INVENTORY.getTabIndex())
        {
            if (slotIn == this.destroyItemSlot)
            {
                this.mc.player.inventory.setItemStack((ItemStack)null);
            }
            else if (type == ClickType.THROW && slotIn != null && slotIn.getHasStack())
            {
                ItemStack itemstack = slotIn.decrStackSize(mouseButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
                this.mc.player.dropItem(itemstack, true);
                this.mc.playerController.sendPacketDropItem(itemstack);
            }
            else if (type == ClickType.THROW && this.mc.player.inventory.getItemStack() != null)
            {
                this.mc.player.dropItem(this.mc.player.inventory.getItemStack(), true);
                this.mc.playerController.sendPacketDropItem(this.mc.player.inventory.getItemStack());
                this.mc.player.inventory.setItemStack((ItemStack)null);
            }
            else
            {
                this.mc.player.inventoryContainer.slotClick(slotIn == null ? slotId : ((GuiContainerCreative.CreativeSlot)slotIn).slot.slotNumber, mouseButton, type, this.mc.player);
                this.mc.player.inventoryContainer.detectAndSendChanges();
            }
        }
        else if (type != ClickType.QUICK_CRAFT && slotIn.inventory == basicInventory)
        {
            InventoryPlayer inventoryplayer = this.mc.player.inventory;
            ItemStack itemstack1 = inventoryplayer.getItemStack();
            ItemStack itemstack2 = slotIn.getStack();

            if (type == ClickType.SWAP)
            {
                if (itemstack2 != null && mouseButton >= 0 && mouseButton < 9)
                {
                    ItemStack itemstack7 = itemstack2.copy();
                    itemstack7.stackSize = itemstack7.getMaxStackSize();
                    this.mc.player.inventory.setInventorySlotContents(mouseButton, itemstack7);
                    this.mc.player.inventoryContainer.detectAndSendChanges();
                }

                return;
            }

            if (type == ClickType.CLONE)
            {
                if (inventoryplayer.getItemStack() == null && slotIn.getHasStack())
                {
                    ItemStack itemstack6 = slotIn.getStack().copy();
                    itemstack6.stackSize = itemstack6.getMaxStackSize();
                    inventoryplayer.setItemStack(itemstack6);
                }

                return;
            }

            if (type == ClickType.THROW)
            {
                if (itemstack2 != null)
                {
                    ItemStack itemstack3 = itemstack2.copy();
                    itemstack3.stackSize = mouseButton == 0 ? 1 : itemstack3.getMaxStackSize();
                    this.mc.player.dropItem(itemstack3, true);
                    this.mc.playerController.sendPacketDropItem(itemstack3);
                }

                return;
            }

            if (itemstack1 != null && itemstack2 != null && itemstack1.isItemEqual(itemstack2) && ItemStack.areItemStackTagsEqual(itemstack1, itemstack2))
            {
                if (mouseButton == 0)
                {
                    if (flag)
                    {
                        itemstack1.stackSize = itemstack1.getMaxStackSize();
                    }
                    else if (itemstack1.stackSize < itemstack1.getMaxStackSize())
                    {
                        ++itemstack1.stackSize;
                    }
                }
                else if (itemstack1.stackSize <= 1)
                {
                    inventoryplayer.setItemStack((ItemStack)null);
                }
                else
                {
                    --itemstack1.stackSize;
                }
            }
            else if (itemstack2 != null && itemstack1 == null)
            {
                inventoryplayer.setItemStack(ItemStack.copyItemStack(itemstack2));
                itemstack1 = inventoryplayer.getItemStack();

                if (flag)
                {
                    itemstack1.stackSize = itemstack1.getMaxStackSize();
                }
            }
            else
            {
                inventoryplayer.setItemStack((ItemStack)null);
            }
        }
        else
        {
            this.inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, mouseButton, type, this.mc.player);

            if (Container.getDragEvent(mouseButton) == 2)
            {
                for (int i = 0; i < 9; ++i)
                {
                    this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + i).getStack(), 36 + i);
                }
            }
            else if (slotIn != null)
            {
                ItemStack itemstack4 = this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
                this.mc.playerController.sendSlotPacket(itemstack4, slotIn.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
            }
        }
    }

    protected void updateActivePotionEffects()
    {
        int i = this.guiLeft;
        super.updateActivePotionEffects();

        if (this.searchField != null && this.guiLeft != i)
        {
            this.searchField.xPosition = this.guiLeft + 82;
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        if (this.mc.playerController.isInCreativeMode())
        {
            super.initGui();
            this.buttonList.clear();
            Keyboard.enableRepeatEvents(true);
            this.searchField = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRendererObj.FONT_HEIGHT);
            this.searchField.setMaxStringLength(15);
            this.searchField.setEnableBackgroundDrawing(false);
            this.searchField.setVisible(false);
            this.searchField.setTextColor(16777215);
            int i = selectedTabIndex;
            selectedTabIndex = -1;
            this.setCurrentCreativeTab(CreativeTabs.CREATIVE_TAB_ARRAY[i]);
            this.listener = new CreativeCrafting(this.mc);
            this.mc.player.inventoryContainer.addListener(this.listener);
            int tabCount = CreativeTabs.CREATIVE_TAB_ARRAY.length;
            if (tabCount > 12)
            {
                buttonList.add(new GuiButton(101, guiLeft,              guiTop - 50, 20, 20, "<"));
                buttonList.add(new GuiButton(102, guiLeft + xSize - 20, guiTop - 50, 20, 20, ">"));
                maxPages = ((tabCount - 12) / 10) + 1;
            }
        }
        else
        {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.player));
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (this.mc.player != null && this.mc.player.inventory != null)
        {
            this.mc.player.inventoryContainer.removeListener(this.listener);
        }

        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex].hasSearchBar())
        {
            if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat))
            {
                this.setCurrentCreativeTab(CreativeTabs.SEARCH);
            }
            else
            {
                super.keyTyped(typedChar, keyCode);
            }
        }
        else
        {
            if (this.clearSearch)
            {
                this.clearSearch = false;
                this.searchField.setText("");
            }

            if (!this.checkHotbarKeys(keyCode))
            {
                if (this.searchField.textboxKeyTyped(typedChar, keyCode))
                {
                    this.updateCreativeSearch();
                }
                else
                {
                    super.keyTyped(typedChar, keyCode);
                }
            }
        }
    }

    private void updateCreativeSearch()
    {
        GuiContainerCreative.ContainerCreative guicontainercreative$containercreative = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
        guicontainercreative$containercreative.itemList.clear();

        CreativeTabs tab = CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex];
        if (tab.hasSearchBar() && tab != CreativeTabs.SEARCH)
        {
            tab.displayAllRelevantItems(guicontainercreative$containercreative.itemList);
            updateFilteredItems(guicontainercreative$containercreative);
            return;
        }

        for (Item item : Item.REGISTRY)
        {
            if (item != null && item.getCreativeTab() != null)
            {
                item.getSubItems(item, (CreativeTabs)null, guicontainercreative$containercreative.itemList);
            }
        }
        updateFilteredItems(guicontainercreative$containercreative);
    }

    //split from above for custom search tabs
    private void updateFilteredItems(GuiContainerCreative.ContainerCreative guicontainercreative$containercreative)
    {
        if (CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex] == CreativeTabs.SEARCH) // FORGE: Only add enchanted books to the regular search
        for (Enchantment enchantment : Enchantment.REGISTRY)
        {
            if (enchantment != null && enchantment.type != null)
            {
                Items.ENCHANTED_BOOK.getAll(enchantment, guicontainercreative$containercreative.itemList);
            }
        }
        Iterator<ItemStack> iterator = guicontainercreative$containercreative.itemList.iterator();
        String s1 = this.searchField.getText().toLowerCase();

        while (iterator.hasNext())
        {
            ItemStack itemstack = (ItemStack)iterator.next();
            boolean flag = false;

            for (String s : itemstack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips))
            {
                if (TextFormatting.getTextWithoutFormattingCodes(s).toLowerCase().contains(s1))
                {
                    flag = true;
                    break;
                }
            }

            if (!flag)
            {
                iterator.remove();
            }
        }

        this.currentScroll = 0.0F;
        guicontainercreative$containercreative.scrollTo(0.0F);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        CreativeTabs creativetabs = CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex];

        if (creativetabs != null && creativetabs.drawInForegroundOfTab())
        {
            GlStateManager.disableBlend();
            this.fontRendererObj.drawString(I18n.format(creativetabs.getTranslatedTabLabel(), new Object[0]), 8, 6, 4210752);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

            for (CreativeTabs creativetabs : CreativeTabs.CREATIVE_TAB_ARRAY)
            {
                if (this.isMouseOverTab(creativetabs, i, j))
                {
                    return;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (state == 0)
        {
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

            for (CreativeTabs creativetabs : CreativeTabs.CREATIVE_TAB_ARRAY)
            {
                if (creativetabs != null && this.isMouseOverTab(creativetabs, i, j))
                {
                    this.setCurrentCreativeTab(creativetabs);
                    return;
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * returns (if you are not on the inventoryTab) and (the flag isn't set) and (you have more than 1 page of items)
     */
    private boolean needsScrollBars()
    {
        if (CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex] == null) return false;
        return selectedTabIndex != CreativeTabs.INVENTORY.getTabIndex() && CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex].shouldHidePlayerInventory() && ((GuiContainerCreative.ContainerCreative)this.inventorySlots).canScroll();
    }

    /**
     * Sets the current creative tab, restructuring the GUI as needed.
     */
    private void setCurrentCreativeTab(CreativeTabs tab)
    {
        if (tab == null) return;
        int i = selectedTabIndex;
        selectedTabIndex = tab.getTabIndex();
        GuiContainerCreative.ContainerCreative guicontainercreative$containercreative = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
        this.dragSplittingSlots.clear();
        guicontainercreative$containercreative.itemList.clear();
        tab.displayAllRelevantItems(guicontainercreative$containercreative.itemList);

        if (tab == CreativeTabs.INVENTORY)
        {
            Container container = this.mc.player.inventoryContainer;

            if (this.originalSlots == null)
            {
                this.originalSlots = guicontainercreative$containercreative.inventorySlots;
            }

            guicontainercreative$containercreative.inventorySlots = Lists.<Slot>newArrayList();

            for (int j = 0; j < container.inventorySlots.size(); ++j)
            {
                Slot slot = new GuiContainerCreative.CreativeSlot((Slot)container.inventorySlots.get(j), j);
                guicontainercreative$containercreative.inventorySlots.add(slot);

                if (j >= 5 && j < 9)
                {
                    int j1 = j - 5;
                    int k1 = j1 / 2;
                    int l1 = j1 % 2;
                    slot.xPos = 54 + k1 * 54;
                    slot.yPos = 6 + l1 * 27;
                }
                else if (j >= 0 && j < 5)
                {
                    slot.xPos = -2000;
                    slot.yPos = -2000;
                }
                else if (j == 45)
                {
                    slot.xPos = 35;
                    slot.yPos = 20;
                }
                else if (j < container.inventorySlots.size())
                {
                    int k = j - 9;
                    int l = k % 9;
                    int i1 = k / 9;
                    slot.xPos = 9 + l * 18;

                    if (j >= 36)
                    {
                        slot.yPos = 112;
                    }
                    else
                    {
                        slot.yPos = 54 + i1 * 18;
                    }
                }
            }

            this.destroyItemSlot = new Slot(basicInventory, 0, 173, 112);
            guicontainercreative$containercreative.inventorySlots.add(this.destroyItemSlot);
        }
        else if (i == CreativeTabs.INVENTORY.getTabIndex())
        {
            guicontainercreative$containercreative.inventorySlots = this.originalSlots;
            this.originalSlots = null;
        }

        if (this.searchField != null)
        {
            if (tab.hasSearchBar())
            {
                this.searchField.setVisible(true);
                this.searchField.setCanLoseFocus(false);
                this.searchField.setFocused(true);
                this.searchField.setText("");
                this.searchField.width = tab.getSearchbarWidth();
                this.searchField.xPosition = this.guiLeft + (82 /*default left*/ + 89 /*default width*/) - this.searchField.width;
                this.updateCreativeSearch();
            }
            else
            {
                this.searchField.setVisible(false);
                this.searchField.setCanLoseFocus(true);
                this.searchField.setFocused(false);
            }
        }

        this.currentScroll = 0.0F;
        guicontainercreative$containercreative.scrollTo(0.0F);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0 && this.needsScrollBars())
        {
            int j = (((GuiContainerCreative.ContainerCreative)this.inventorySlots).itemList.size() + 9 - 1) / 9 - 5;

            if (i > 0)
            {
                i = 1;
            }

            if (i < 0)
            {
                i = -1;
            }

            this.currentScroll = (float)((double)this.currentScroll - (double)i / (double)j);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        boolean flag = Mouse.isButtonDown(0);
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;

        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
        {
            this.isScrolling = this.needsScrollBars();
        }

        if (!flag)
        {
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling)
        {
            this.currentScroll = ((float)(mouseY - l) - 7.5F) / ((float)(j1 - l) - 15.0F);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        int start = tabPage * 10;
        int end = Math.min(CreativeTabs.CREATIVE_TAB_ARRAY.length, ((tabPage + 1) * 10) + 2);
        if (tabPage != 0) start += 2;
        boolean rendered = false;

        for (CreativeTabs creativetabs : java.util.Arrays.copyOfRange(CreativeTabs.CREATIVE_TAB_ARRAY,start,end))
        {
            if (creativetabs == null) continue;
            if (this.renderCreativeInventoryHoveringText(creativetabs, mouseX, mouseY))
            {
                rendered = true;
                break;
            }
        }

        if (!rendered && !renderCreativeInventoryHoveringText(CreativeTabs.SEARCH, mouseX, mouseY))
        {
            renderCreativeInventoryHoveringText(CreativeTabs.INVENTORY, mouseX, mouseY);
        }

        if (this.destroyItemSlot != null && selectedTabIndex == CreativeTabs.INVENTORY.getTabIndex() && this.isPointInRegion(this.destroyItemSlot.xPos, this.destroyItemSlot.yPos, 16, 16, mouseX, mouseY))
        {
            this.drawCreativeTabHoveringText(I18n.format("inventory.binSlot", new Object[0]), mouseX, mouseY);
        }

        if (maxPages != 0)
        {
            String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
            int width = fontRendererObj.getStringWidth(page);
            GlStateManager.disableLighting();
            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            fontRendererObj.drawString(page, guiLeft + (xSize / 2) - (width / 2), guiTop - 44, -1);
            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
    }

    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        if (selectedTabIndex == CreativeTabs.SEARCH.getTabIndex())
        {
            List<String> list = stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips);
            CreativeTabs creativetabs = stack.getItem().getCreativeTab();

            if (creativetabs == null && stack.getItem() == Items.ENCHANTED_BOOK)
            {
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);

                if (map.size() == 1)
                {
                    Enchantment enchantment = (Enchantment)map.keySet().iterator().next();

                    for (CreativeTabs creativetabs1 : CreativeTabs.CREATIVE_TAB_ARRAY)
                    {
                        if (creativetabs1.hasRelevantEnchantmentType(enchantment.type))
                        {
                            creativetabs = creativetabs1;
                            break;
                        }
                    }
                }
            }

            if (creativetabs != null)
            {
                list.add(1, "" + TextFormatting.BOLD + TextFormatting.BLUE + I18n.format(creativetabs.getTranslatedTabLabel(), new Object[0]));
            }

            for (int i = 0; i < list.size(); ++i)
            {
                if (i == 0)
                {
                    list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
                }
                else
                {
                    list.set(i, TextFormatting.GRAY + (String)list.get(i));
                }
            }

            net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
            this.drawHoveringText(list, x, y);
            net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
        }
        else
        {
            super.renderToolTip(stack, x, y);
        }
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        CreativeTabs creativetabs = CreativeTabs.CREATIVE_TAB_ARRAY[selectedTabIndex];

        int start = tabPage * 10;
        int end = Math.min(CreativeTabs.CREATIVE_TAB_ARRAY.length, ((tabPage + 1) * 10 + 2));
        if (tabPage != 0) start += 2;

        for (CreativeTabs creativetabs1 : java.util.Arrays.copyOfRange(CreativeTabs.CREATIVE_TAB_ARRAY,start,end))
        {
            this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);

            if (creativetabs1 == null) continue;
            if (creativetabs1.getTabIndex() != selectedTabIndex)
            {
                this.drawTab(creativetabs1);
            }
        }

        if (tabPage != 0)
        {
            if (creativetabs != CreativeTabs.SEARCH)
            {
                this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
                drawTab(CreativeTabs.SEARCH);
            }
            if (creativetabs != CreativeTabs.INVENTORY)
            {
                this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
                drawTab(CreativeTabs.INVENTORY);
            }
        }

        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + creativetabs.getBackgroundImageName()));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.searchField.drawTextBox();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.guiLeft + 175;
        int j = this.guiTop + 18;
        int k = j + 112;
        this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);

        if (creativetabs.shouldHidePlayerInventory())
        {
            this.drawTexturedModalRect(i, j + (int)((float)(k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        }

        if (creativetabs == null || creativetabs.getTabPage() != tabPage)
        {
            if (creativetabs != CreativeTabs.SEARCH && creativetabs != CreativeTabs.INVENTORY)
            {
                return;
            }
        }

        this.drawTab(creativetabs);

        if (creativetabs == CreativeTabs.INVENTORY)
        {
            GuiInventory.drawEntityOnScreen(this.guiLeft + 88, this.guiTop + 45, 20, (float)(this.guiLeft + 88 - mouseX), (float)(this.guiTop + 45 - 30 - mouseY), this.mc.player);
        }
    }

    /**
     * Checks if the mouse is over the given tab. Returns true if so.
     */
    protected boolean isMouseOverTab(CreativeTabs tab, int mouseX, int mouseY)
    {
        if (tab.getTabPage() != tabPage)
        {
            if (tab != CreativeTabs.SEARCH && tab != CreativeTabs.INVENTORY)
            {
                return false;
            }
        }

        int i = tab.getTabColumn();
        int j = 28 * i;
        int k = 0;

        if (i == 5)
        {
            j = this.xSize - 28 + 2;
        }
        else if (i > 0)
        {
            j += i;
        }

        if (tab.isTabInFirstRow())
        {
            k = k - 32;
        }
        else
        {
            k = k + this.ySize;
        }

        return mouseX >= j && mouseX <= j + 28 && mouseY >= k && mouseY <= k + 32;
    }

    /**
     * Renders the creative inventory hovering text if mouse is over it. Returns true if did render or false otherwise.
     * Params: current creative tab to be checked, current mouse x position, current mouse y position.
     */
    protected boolean renderCreativeInventoryHoveringText(CreativeTabs tab, int mouseX, int mouseY)
    {
        int i = tab.getTabColumn();
        int j = 28 * i;
        int k = 0;

        if (i == 5)
        {
            j = this.xSize - 28 + 2;
        }
        else if (i > 0)
        {
            j += i;
        }

        if (tab.isTabInFirstRow())
        {
            k = k - 32;
        }
        else
        {
            k = k + this.ySize;
        }

        if (this.isPointInRegion(j + 3, k + 3, 23, 27, mouseX, mouseY))
        {
            this.drawCreativeTabHoveringText(I18n.format(tab.getTranslatedTabLabel(), new Object[0]), mouseX, mouseY);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Draws the given tab and its background, deciding whether to highlight the tab or not based off of the selected
     * index.
     */
    protected void drawTab(CreativeTabs tab)
    {
        boolean flag = tab.getTabIndex() == selectedTabIndex;
        boolean flag1 = tab.isTabInFirstRow();
        int i = tab.getTabColumn();
        int j = i * 28;
        int k = 0;
        int l = this.guiLeft + 28 * i;
        int i1 = this.guiTop;
        int j1 = 32;

        if (flag)
        {
            k += 32;
        }

        if (i == 5)
        {
            l = this.guiLeft + this.xSize - 28;
        }
        else if (i > 0)
        {
            l += i;
        }

        if (flag1)
        {
            i1 = i1 - 28;
        }
        else
        {
            k += 64;
            i1 = i1 + (this.ySize - 4);
        }

        GlStateManager.disableLighting();
        GlStateManager.color(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        GlStateManager.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        this.drawTexturedModalRect(l, i1, j, k, 28, 32);
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + (flag1 ? 1 : -1);
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        ItemStack itemstack = tab.getIconItemStack();
        this.itemRender.renderItemAndEffectIntoGUI(itemstack, l, i1);
        this.itemRender.renderItemOverlays(this.fontRendererObj, itemstack, l, i1);
        GlStateManager.disableLighting();
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.player.getStatFileWriter()));
        }

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
        }

        if (button.id == 101)
        {
            tabPage = Math.max(tabPage - 1, 0);
        }
        else if (button.id == 102)
        {
            tabPage = Math.min(tabPage + 1, maxPages);
        }
    }

    /**
     * Returns the index of the currently selected tab.
     */
    public int getSelectedTabIndex()
    {
        /** Currently selected creative inventory tab index. */
        return selectedTabIndex;
    }

    @SideOnly(Side.CLIENT)
    static class ContainerCreative extends Container
        {
            /** the list of items in this container */
            public List<ItemStack> itemList = Lists.<ItemStack>newArrayList();

            public ContainerCreative(EntityPlayer player)
            {
                InventoryPlayer inventoryplayer = player.inventory;

                for (int i = 0; i < 5; ++i)
                {
                    for (int j = 0; j < 9; ++j)
                    {
                        this.addSlotToContainer(new Slot(GuiContainerCreative.basicInventory, i * 9 + j, 9 + j * 18, 18 + i * 18));
                    }
                }

                for (int k = 0; k < 9; ++k)
                {
                    this.addSlotToContainer(new Slot(inventoryplayer, k, 9 + k * 18, 112));
                }

                this.scrollTo(0.0F);
            }

            /**
             * Determines whether supplied player can use this container
             */
            public boolean canInteractWith(EntityPlayer playerIn)
            {
                return true;
            }

            /**
             * Updates the gui slots ItemStack's based on scroll position.
             */
            public void scrollTo(float p_148329_1_)
            {
                int i = (this.itemList.size() + 9 - 1) / 9 - 5;
                int j = (int)((double)(p_148329_1_ * (float)i) + 0.5D);

                if (j < 0)
                {
                    j = 0;
                }

                for (int k = 0; k < 5; ++k)
                {
                    for (int l = 0; l < 9; ++l)
                    {
                        int i1 = l + (k + j) * 9;

                        if (i1 >= 0 && i1 < this.itemList.size())
                        {
                            GuiContainerCreative.basicInventory.setInventorySlotContents(l + k * 9, (ItemStack)this.itemList.get(i1));
                        }
                        else
                        {
                            GuiContainerCreative.basicInventory.setInventorySlotContents(l + k * 9, (ItemStack)null);
                        }
                    }
                }
            }

            public boolean canScroll()
            {
                return this.itemList.size() > 45;
            }

            /**
             * Retries slotClick() in case of failure
             */
            protected void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn)
            {
            }

            /**
             * Take a stack from the specified inventory slot.
             */
            @Nullable
            public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
            {
                if (index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size())
                {
                    Slot slot = (Slot)this.inventorySlots.get(index);

                    if (slot != null && slot.getHasStack())
                    {
                        slot.putStack((ItemStack)null);
                    }
                }

                return null;
            }

            /**
             * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack
             * passed in is null for the initial slot that was double-clicked.
             */
            public boolean canMergeSlot(ItemStack stack, Slot slotIn)
            {
                return slotIn.yPos > 90;
            }

            /**
             * Returns true if the player can "drag-spilt" items into this slot,. returns true by default. Called to
             * check if the slot can be added to a list of Slots to split the held ItemStack across.
             */
            public boolean canDragIntoSlot(Slot slotIn)
            {
                return slotIn.inventory instanceof InventoryPlayer || slotIn.yPos > 90 && slotIn.xPos <= 162;
            }
        }

    @SideOnly(Side.CLIENT)
    class CreativeSlot extends Slot
    {
        private final Slot slot;

        public CreativeSlot(Slot p_i46313_2_, int p_i46313_3_)
        {
            super(p_i46313_2_.inventory, p_i46313_3_, 0, 0);
            this.slot = p_i46313_2_;
        }

        public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
        {
            this.slot.onPickupFromSlot(playerIn, stack);
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        public boolean isItemValid(@Nullable ItemStack stack)
        {
            return this.slot.isItemValid(stack);
        }

        /**
         * Helper fnct to get the stack in the slot.
         */
        public ItemStack getStack()
        {
            return this.slot.getStack();
        }

        /**
         * Returns if this slot contains a stack.
         */
        public boolean getHasStack()
        {
            return this.slot.getHasStack();
        }

        /**
         * Helper method to put a stack in the slot.
         */
        public void putStack(@Nullable ItemStack stack)
        {
            this.slot.putStack(stack);
        }

        /**
         * Called when the stack in a Slot changes
         */
        public void onSlotChanged()
        {
            this.slot.onSlotChanged();
        }

        /**
         * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
         * case of armor slots)
         */
        public int getSlotStackLimit()
        {
            return this.slot.getSlotStackLimit();
        }

        public int getItemStackLimit(ItemStack stack)
        {
            return this.slot.getItemStackLimit(stack);
        }

        @Nullable
        public String getSlotTexture()
        {
            return this.slot.getSlotTexture();
        }

        /**
         * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
         * stack.
         */
        public ItemStack decrStackSize(int amount)
        {
            return this.slot.decrStackSize(amount);
        }

        /**
         * returns true if the slot exists in the given inventory and location
         */
        public boolean isHere(IInventory inv, int slotIn)
        {
            return this.slot.isHere(inv, slotIn);
        }

        /**
         * Actualy only call when we want to render the white square effect over the slots. Return always True, except
         * for the armor slot of the Donkey/Mule (we can't interact with the Undead and Skeleton horses)
         */
        public boolean canBeHovered()
        {
            return this.slot.canBeHovered();
        }

        /**
         * Return whether this slot's stack can be taken from this slot.
         */
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            return this.slot.canTakeStack(playerIn);
        }

        /**
         * if par2 has more items than par1, onCrafting(item,countIncrease) is called
         */
        public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_)
        {
            super.onSlotChange(p_75220_1_, p_75220_2_);
        }
    }
}