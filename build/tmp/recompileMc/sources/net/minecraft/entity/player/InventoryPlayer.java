package net.minecraft.entity.player;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InventoryPlayer implements IInventory
{
    /** An array of 36 item stacks indicating the main player inventory (including the visible bar). */
    public final ItemStack[] mainInventory = new ItemStack[36];
    /** An array of 4 item stacks containing the currently worn armor pieces. */
    public final ItemStack[] armorInventory = new ItemStack[4];
    public final ItemStack[] offHandInventory = new ItemStack[1];
    private final ItemStack[][] allInventories;
    /** The index of the currently held item (0-8). */
    public int currentItem;
    /** The player whose inventory this is. */
    public EntityPlayer player;
    private ItemStack itemStack;
    /**
     * Set true whenever the inventory changes. Nothing sets it false so you will have to write your own code to check
     * it and reset the value.
     */
    public boolean inventoryChanged;

    public InventoryPlayer(EntityPlayer playerIn)
    {
        this.allInventories = new ItemStack[][] {this.mainInventory, this.armorInventory, this.offHandInventory};
        this.player = playerIn;
    }

    /**
     * Returns the item stack currently held by the player.
     */
    @Nullable
    public ItemStack getCurrentItem()
    {
        return isHotbar(this.currentItem) ? this.mainInventory[this.currentItem] : null;
    }

    /**
     * Get the size of the player hotbar inventory
     */
    public static int getHotbarSize()
    {
        return 9;
    }

    private boolean canMergeStacks(@Nullable ItemStack stack1, ItemStack stack2)
    {
        return stack1 != null && this.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.stackSize < stack1.getMaxStackSize() && stack1.stackSize < this.getInventoryStackLimit();
    }

    /**
     * Checks item, NBT, and meta if the item is not damageable
     */
    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    /**
     * Returns the first item stack that is empty.
     */
    public int getFirstEmptyStack()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] == null)
            {
                return i;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    public void setPickedItemStack(ItemStack stack)
    {
        int i = this.getSlotFor(stack);

        if (isHotbar(i))
        {
            this.currentItem = i;
        }
        else
        {
            if (i == -1)
            {
                this.currentItem = this.getBestHotbarSlot();

                if (this.mainInventory[this.currentItem] != null)
                {
                    int j = this.getFirstEmptyStack();

                    if (j != -1)
                    {
                        this.mainInventory[j] = this.mainInventory[this.currentItem];
                    }
                }

                this.mainInventory[this.currentItem] = stack;
            }
            else
            {
                this.pickItem(i);
            }
        }
    }

    public void pickItem(int index)
    {
        this.currentItem = this.getBestHotbarSlot();
        ItemStack itemstack = this.mainInventory[this.currentItem];
        this.mainInventory[this.currentItem] = this.mainInventory[index];
        this.mainInventory[index] = itemstack;
    }

    public static boolean isHotbar(int index)
    {
        return index >= 0 && index < 9;
    }

    /**
     * Finds the stack or an equivalent one in the main inventory
     */
    @SideOnly(Side.CLIENT)
    public int getSlotFor(ItemStack stack)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.stackEqualExact(stack, this.mainInventory[i]))
            {
                return i;
            }
        }

        return -1;
    }

    public int getBestHotbarSlot()
    {
        for (int i = 0; i < 9; ++i)
        {
            int j = (this.currentItem + i) % 9;

            if (this.mainInventory[j] == null)
            {
                return j;
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            int l = (this.currentItem + k) % 9;

            if (!this.mainInventory[l].isItemEnchanted())
            {
                return l;
            }
        }

        return this.currentItem;
    }

    /**
     * Switch the current item to the next one or the previous one
     */
    @SideOnly(Side.CLIENT)
    public void changeCurrentItem(int direction)
    {
        if (direction > 0)
        {
            direction = 1;
        }

        if (direction < 0)
        {
            direction = -1;
        }

        for (this.currentItem -= direction; this.currentItem < 0; this.currentItem += 9)
        {
            ;
        }

        while (this.currentItem >= 9)
        {
            this.currentItem -= 9;
        }
    }

    /**
     * Removes matching items from the inventory.
     * @param itemIn The item to match, null ignores.
     * @param metadataIn The metadata to match, -1 ignores.
     * @param removeCount The number of items to remove. If less than 1, removes all matching items.
     * @param itemNBT The NBT data to match, null ignores.
     * @return The number of items removed from the inventory.
     */
    public int clearMatchingItems(@Nullable Item itemIn, int metadataIn, int removeCount, @Nullable NBTTagCompound itemNBT)
    {
        int i = 0;

        for (int j = 0; j < this.getSizeInventory(); ++j)
        {
            ItemStack itemstack = this.getStackInSlot(j);

            if (itemstack != null && (itemIn == null || itemstack.getItem() == itemIn) && (metadataIn <= -1 || itemstack.getMetadata() == metadataIn) && (itemNBT == null || NBTUtil.areNBTEquals(itemNBT, itemstack.getTagCompound(), true)))
            {
                int k = removeCount <= 0 ? itemstack.stackSize : Math.min(removeCount - i, itemstack.stackSize);
                i += k;

                if (removeCount != 0)
                {
                    itemstack.stackSize -= k;

                    if (itemstack.stackSize == 0)
                    {
                        this.setInventorySlotContents(j, (ItemStack)null);
                    }

                    if (removeCount > 0 && i >= removeCount)
                    {
                        return i;
                    }
                }
            }
        }

        if (this.itemStack != null)
        {
            if (itemIn != null && this.itemStack.getItem() != itemIn)
            {
                return i;
            }

            if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn)
            {
                return i;
            }

            if (itemNBT != null && !NBTUtil.areNBTEquals(itemNBT, this.itemStack.getTagCompound(), true))
            {
                return i;
            }

            int l = removeCount <= 0 ? this.itemStack.stackSize : Math.min(removeCount - i, this.itemStack.stackSize);
            i += l;

            if (removeCount != 0)
            {
                this.itemStack.stackSize -= l;

                if (this.itemStack.stackSize == 0)
                {
                    this.itemStack = null;
                }

                if (removeCount > 0 && i >= removeCount)
                {
                    return i;
                }
            }
        }

        return i;
    }

    /**
     * This function stores as many items of an ItemStack as possible in a matching slot and returns the quantity of
     * left over items.
     */
    private int storePartialItemStack(ItemStack itemStackIn)
    {
        Item item = itemStackIn.getItem();
        int i = itemStackIn.stackSize;
        int j = this.storeItemStack(itemStackIn);

        if (j == -1)
        {
            j = this.getFirstEmptyStack();
        }

        if (j == -1)
        {
            return i;
        }
        else
        {
            ItemStack itemstack = this.getStackInSlot(j);

            if (itemstack == null)
            {
                itemstack = itemStackIn.copy(); // Forge: Replace Item clone above to preserve item capabilities when picking the item up.
                itemstack.stackSize = 0;

                if (itemStackIn.hasTagCompound())
                {
                    itemstack.setTagCompound(itemStackIn.getTagCompound().copy());
                }

                this.setInventorySlotContents(j, itemstack);
            }

            int k = i;

            if (i > itemstack.getMaxStackSize() - itemstack.stackSize)
            {
                k = itemstack.getMaxStackSize() - itemstack.stackSize;
            }

            if (k > this.getInventoryStackLimit() - itemstack.stackSize)
            {
                k = this.getInventoryStackLimit() - itemstack.stackSize;
            }

            if (k == 0)
            {
                return i;
            }
            else
            {
                i = i - k;
                itemstack.stackSize += k;
                itemstack.animationsToGo = 5;
                return i;
            }
        }
    }

    /**
     * stores an itemstack in the users inventory
     */
    private int storeItemStack(ItemStack itemStackIn)
    {
        if (this.canMergeStacks(this.getStackInSlot(this.currentItem), itemStackIn))
        {
            return this.currentItem;
        }
        else if (this.canMergeStacks(this.getStackInSlot(40), itemStackIn))
        {
            return 40;
        }
        else
        {
            for (int i = 0; i < this.mainInventory.length; ++i)
            {
                if (this.canMergeStacks(this.mainInventory[i], itemStackIn))
                {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * Decrement the number of animations remaining. Only called on client side. This is used to handle the animation of
     * receiving a block.
     */
    public void decrementAnimations()
    {
        for (ItemStack[] aitemstack : this.allInventories)
        {
            for (int i = 0; i < aitemstack.length; ++i)
            {
                if (aitemstack[i] != null)
                {
                    aitemstack[i].updateAnimation(this.player.world, this.player, i, this.currentItem == i);
                }
            }
        }

        for (int i = 0; i < armorInventory.length; i++)
        {
            if (armorInventory[i] != null)
            {
                armorInventory[i].getItem().onArmorTick(player.world, player, armorInventory[i]);
            }
        }
    }

    /**
     * Adds the item stack to the inventory, returns false if it is impossible.
     */
    public boolean addItemStackToInventory(@Nullable final ItemStack itemStackIn)
    {
        if (itemStackIn != null && itemStackIn.stackSize != 0 && itemStackIn.getItem() != null)
        {
            try
            {
                if (itemStackIn.isItemDamaged())
                {
                    int j = this.getFirstEmptyStack();

                    if (j >= 0)
                    {
                        this.mainInventory[j] = ItemStack.copyItemStack(itemStackIn);
                        this.mainInventory[j].animationsToGo = 5;
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    else if (this.player.capabilities.isCreativeMode)
                    {
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    int i;

                    while (true)
                    {
                        i = itemStackIn.stackSize;
                        itemStackIn.stackSize = this.storePartialItemStack(itemStackIn);

                        if (itemStackIn.stackSize <= 0 || itemStackIn.stackSize >= i)
                        {
                            break;
                        }
                    }

                    if (itemStackIn.stackSize == i && this.player.capabilities.isCreativeMode)
                    {
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return itemStackIn.stackSize < i;
                    }
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Integer.valueOf(Item.getIdFromItem(itemStackIn.getItem())));
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(itemStackIn.getMetadata()));
                crashreportcategory.setDetail("Item name", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return itemStackIn.getDisplayName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Nullable
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack[] aitemstack = null;

        for (ItemStack[] aitemstack1 : this.allInventories)
        {
            if (index < aitemstack1.length)
            {
                aitemstack = aitemstack1;
                break;
            }

            index -= aitemstack1.length;
        }

        return aitemstack != null && aitemstack[index] != null ? ItemStackHelper.getAndSplit(aitemstack, index, count) : null;
    }

    public void deleteStack(ItemStack stack)
    {
        for (ItemStack[] aitemstack : this.allInventories)
        {
            for (int i = 0; i < aitemstack.length; ++i)
            {
                if (aitemstack[i] == stack)
                {
                    aitemstack[i] = null;
                    break;
                }
            }
        }
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Nullable
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack[] aitemstack = null;

        for (ItemStack[] aitemstack1 : this.allInventories)
        {
            if (index < aitemstack1.length)
            {
                aitemstack = aitemstack1;
                break;
            }

            index -= aitemstack1.length;
        }

        if (aitemstack != null && aitemstack[index] != null)
        {
            ItemStack itemstack = aitemstack[index];
            aitemstack[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, @Nullable ItemStack stack)
    {
        ItemStack[] aitemstack = null;

        for (ItemStack[] aitemstack1 : this.allInventories)
        {
            if (index < aitemstack1.length)
            {
                aitemstack = aitemstack1;
                break;
            }

            index -= aitemstack1.length;
        }

        if (aitemstack != null)
        {
            aitemstack[index] = stack;
        }
    }

    public float getStrVsBlock(IBlockState state)
    {
        float f = 1.0F;

        if (this.mainInventory[this.currentItem] != null)
        {
            f *= this.mainInventory[this.currentItem].getStrVsBlock(state);
        }

        return f;
    }

    /**
     * Writes the inventory out as a list of compound tags. This is where the slot indices are used (+100 for armor, +80
     * for crafting).
     */
    public NBTTagList writeToNBT(NBTTagList nbtTagListIn)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.mainInventory[i].writeToNBT(nbttagcompound);
                nbtTagListIn.appendTag(nbttagcompound);
            }
        }

        for (int j = 0; j < this.armorInventory.length; ++j)
        {
            if (this.armorInventory[j] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)(j + 100));
                this.armorInventory[j].writeToNBT(nbttagcompound1);
                nbtTagListIn.appendTag(nbttagcompound1);
            }
        }

        for (int k = 0; k < this.offHandInventory.length; ++k)
        {
            if (this.offHandInventory[k] != null)
            {
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                nbttagcompound2.setByte("Slot", (byte)(k + 150));
                this.offHandInventory[k].writeToNBT(nbttagcompound2);
                nbtTagListIn.appendTag(nbttagcompound2);
            }
        }

        return nbtTagListIn;
    }

    /**
     * Reads from the given tag list and fills the slots in the inventory with the correct items.
     */
    public void readFromNBT(NBTTagList nbtTagListIn)
    {
        Arrays.fill(this.mainInventory, (Object)null);
        Arrays.fill(this.armorInventory, (Object)null);
        Arrays.fill(this.offHandInventory, (Object)null);

        for (int i = 0; i < nbtTagListIn.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbtTagListIn.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                if (j >= 0 && j < this.mainInventory.length)
                {
                    this.mainInventory[j] = itemstack;
                }
                else if (j >= 100 && j < this.armorInventory.length + 100)
                {
                    this.armorInventory[j - 100] = itemstack;
                }
                else if (j >= 150 && j < this.offHandInventory.length + 150)
                {
                    this.offHandInventory[j - 150] = itemstack;
                }
            }
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.mainInventory.length + this.armorInventory.length + this.offHandInventory.length;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Nullable
    public ItemStack getStackInSlot(int index)
    {
        ItemStack[] aitemstack = null;

        for (ItemStack[] aitemstack1 : this.allInventories)
        {
            if (index < aitemstack1.length)
            {
                aitemstack = aitemstack1;
                break;
            }

            index -= aitemstack1.length;
        }

        return aitemstack == null ? null : aitemstack[index];
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return "container.inventory";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return false;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public ITextComponent getDisplayName()
    {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean canHarvestBlock(IBlockState state)
    {
        if (state.getMaterial().isToolNotRequired())
        {
            return true;
        }
        else
        {
            ItemStack itemstack = this.getStackInSlot(this.currentItem);
            return itemstack != null ? itemstack.canHarvestBlock(state) : false;
        }
    }

    /**
     * returns a player armor item (as itemstack) contained in specified armor slot.
     */
    @SideOnly(Side.CLIENT)
    public ItemStack armorItemInSlot(int slotIn)
    {
        return this.armorInventory[slotIn];
    }

    /**
     * Damages armor in each slot by the specified amount.
     */
    public void damageArmor(float damage)
    {
        damage = damage / 4.0F;

        if (damage < 1.0F)
        {
            damage = 1.0F;
        }

        for (int i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor)
            {
                this.armorInventory[i].damageItem((int)damage, this.player);

                if (this.armorInventory[i].stackSize == 0)
                {
                    this.armorInventory[i] = null;
                }
            }
        }
    }

    /**
     * Drop all armor and main inventory items.
     */
    public void dropAllItems()
    {
        for (ItemStack[] aitemstack : this.allInventories)
        {
            for (int i = 0; i < aitemstack.length; ++i)
            {
                if (aitemstack[i] != null)
                {
                    this.player.dropItem(aitemstack[i], true, false);
                    aitemstack[i] = null;
                }
            }
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        this.inventoryChanged = true;
    }

    /**
     * Set the stack helds by mouse, used in GUI/Container
     */
    public void setItemStack(@Nullable ItemStack itemStackIn)
    {
        this.itemStack = itemStackIn;
    }

    /**
     * Stack helds by mouse, used in GUI and Containers
     */
    @Nullable
    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return this.player.isDead ? false : player.getDistanceSqToEntity(this.player) <= 64.0D;
    }

    /**
     * Returns true if the specified ItemStack exists in the inventory.
     */
    public boolean hasItemStack(ItemStack itemStackIn)
    {
        for (ItemStack[] aitemstack : this.allInventories)
        {
            for (ItemStack itemstack : aitemstack)
            {
                if (itemstack != null && itemstack.isItemEqual(itemStackIn))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
     * guis use Slot.isItemValid
     */
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    /**
     * Copy the ItemStack contents from another InventoryPlayer instance
     */
    public void copyInventory(InventoryPlayer playerInventory)
    {
        for (int i = 0; i < this.getSizeInventory(); ++i)
        {
            this.setInventorySlotContents(i, playerInventory.getStackInSlot(i));
        }

        this.currentItem = playerInventory.currentItem;
    }

    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value)
    {
    }

    public int getFieldCount()
    {
        return 0;
    }

    public void clear()
    {
        for (ItemStack[] aitemstack : this.allInventories)
        {
            for (int i = 0; i < aitemstack.length; ++i)
            {
                aitemstack[i] = null;
            }
        }
    }
}