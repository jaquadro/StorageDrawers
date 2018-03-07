package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Slot
{
    /** The index of the slot in the inventory. */
    private final int slotIndex;
    /** The inventory we want to extract a slot from. */
    public final IInventory inventory;
    /** the id of the slot(also the index in the inventory arraylist) */
    public int slotNumber;
    /** display position of the inventory slot on the screen x axis */
    public int xPos;
    /** display position of the inventory slot on the screen y axis */
    public int yPos;

    public Slot(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        this.inventory = inventoryIn;
        this.slotIndex = index;
        this.xPos = xPosition;
        this.yPos = yPosition;
    }

    /**
     * if par2 has more items than par1, onCrafting(item,countIncrease) is called
     */
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_)
    {
        if (p_75220_1_ != null && p_75220_2_ != null)
        {
            if (p_75220_1_.getItem() == p_75220_2_.getItem())
            {
                int i = p_75220_2_.stackSize - p_75220_1_.stackSize;

                if (i > 0)
                {
                    this.onCrafting(p_75220_1_, i);
                }
            }
        }
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(ItemStack stack, int amount)
    {
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack stack)
    {
    }

    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
        this.onSlotChanged();
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean isItemValid(@Nullable ItemStack stack)
    {
        return true;
    }

    /**
     * Helper fnct to get the stack in the slot.
     */
    @Nullable
    public ItemStack getStack()
    {
        return this.inventory.getStackInSlot(this.slotIndex);
    }

    /**
     * Returns if this slot contains a stack.
     */
    public boolean getHasStack()
    {
        return this.getStack() != null;
    }

    /**
     * Helper method to put a stack in the slot.
     */
    public void putStack(@Nullable ItemStack stack)
    {
        this.inventory.setInventorySlotContents(this.slotIndex, stack);
        this.onSlotChanged();
    }

    /**
     * Called when the stack in a Slot changes
     */
    public void onSlotChanged()
    {
        this.inventory.markDirty();
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return this.inventory.getInventoryStackLimit();
    }

    public int getItemStackLimit(ItemStack stack)
    {
        return this.getSlotStackLimit();
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public String getSlotTexture()
    {
        return backgroundName;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int amount)
    {
        return this.inventory.decrStackSize(this.slotIndex, amount);
    }

    /**
     * returns true if the slot exists in the given inventory and location
     */
    public boolean isHere(IInventory inv, int slotIn)
    {
        return inv == this.inventory && slotIn == this.slotIndex;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return true;
    }

    /**
     * Actualy only call when we want to render the white square effect over the slots. Return always True, except for
     * the armor slot of the Donkey/Mule (we can't interact with the Undead and Skeleton horses)
     */
    @SideOnly(Side.CLIENT)
    public boolean canBeHovered()
    {
        return true;
    }

    /*========================================= FORGE START =====================================*/
    protected String backgroundName = null;
    protected net.minecraft.util.ResourceLocation backgroundLocation = null;
    protected Object backgroundMap;
    /**
     * Gets the path of the texture file to use for the background image of this slot when drawing the GUI.
     * @return The resource location for the background image
     */
    @SideOnly(Side.CLIENT)
    public net.minecraft.util.ResourceLocation getBackgroundLocation()
    {
        return (backgroundLocation == null ? net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE : backgroundLocation);
    }

    /**
     * Sets the texture file to use for the background image of the slot when it's empty.
     * @param texture the resourcelocation for the texture
     */
    @SideOnly(Side.CLIENT)
    public void setBackgroundLocation(net.minecraft.util.ResourceLocation texture)
    {
        this.backgroundLocation = texture;
    }

    /**
     * Sets which icon index to use as the background image of the slot when it's empty.
     * @param name The icon to use, null for none
     */
    public void setBackgroundName(String name)
    {
        this.backgroundName = name;
    }

    @SideOnly(Side.CLIENT)
    public net.minecraft.client.renderer.texture.TextureAtlasSprite getBackgroundSprite()
    {
        String name = getSlotTexture();
        return name == null ? null : getBackgroundMap().getAtlasSprite(name);
    }

    @SideOnly(Side.CLIENT)
    protected net.minecraft.client.renderer.texture.TextureMap getBackgroundMap()
    {
        if (backgroundMap == null) backgroundMap = net.minecraft.client.Minecraft.getMinecraft().getTextureMapBlocks();
        return (net.minecraft.client.renderer.texture.TextureMap)backgroundMap;
    }

    /**
     * Retrieves the index in the inventory for this slot, this value should typically not
     * be used, but can be useful for some occasions.
     *
     * @return Index in associated inventory for this slot.
     */
    public int getSlotIndex()
    {
        /** The index of the slot in the inventory. */
        return slotIndex;
    }

    /**
     * Checks if the other slot is in the same inventory, by comparing the inventory reference.
     * @param other
     * @return true if the other slot is in the same inventory
     */
    public boolean isSameInventory(Slot other)
    {
        return this.inventory == other.inventory;
    }
    /*========================================= FORGE END =====================================*/
}