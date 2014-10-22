package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.CompTierRegistry;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;
import java.util.List;

public class TileEntityCompDrawers extends TileEntityDrawersBase implements IStorageProvider, ISidedInventory
{
    private static InventoryLookup lookup1 = new InventoryLookup(1, 1);
    private static InventoryLookup lookup2 = new InventoryLookup(2, 2);
    private static InventoryLookup lookup3 = new InventoryLookup(3, 3);

    private DrawerData[] data = new DrawerData[3];
    private int[] convRate = new int[3];

    int pooledCount;
    int lookupSizeResult;

    private ItemStack[] snapshotItems;
    private int[] snapshotCounts;

    private long lastClickTime;
    private UUID lastClickUUID;

    public TileEntityCompDrawers () {
        data = new DrawerData[3];
        for (int i = 0, n = data.length; i < n; i++)
            data[i] = new DrawerData(this, i);

        snapshotItems = new ItemStack[3];
        snapshotCounts = new int[3];
    }

    public int getDrawerCount () {
        return 3;
    }

    public int getItemCount (int slot) {
        if (convRate[slot] == 0)
            return 0;

        return pooledCount / convRate[slot];
    }

    public int getItemRemainderCount (int slot) {
        int count = getItemCount(slot);
        if (slot > 0)
            count -= getItemCount(slot - 1) * (convRate[slot - 1] / convRate[slot]);

        return count;
    }

    public int getItemCapacity (int slot) {
        if (!isSlotValid(slot))
            return 0;

        return data[slot].maxCapacity();
    }

    public int getItemStackSize (int slot) {
        return data[slot].itemStackMaxSize();
    }

    public ItemStack getSingleItemStack (int slot) {
        if (!isSlotValid(slot))
            return null;

        return data[slot].getReadOnlyItemStack();
    }

    private ItemStack getItemsFromSlot (int slot, int count) {
        if (!isSlotValid(slot))
            return null;

        int adjCount = count * convRate[slot];
        if (adjCount > pooledCount)
            count = pooledCount / convRate[slot];

        ItemStack stack = data[slot].getNewItemStack();
        stack.stackSize = Math.min(stack.getMaxStackSize(), count);

        return stack;
    }

    public ItemStack takeItemsFromSlot (int slot, int count) {
        ItemStack stack = getItemsFromSlot(slot, count);
        if (stack == null)
            return null;

        int adjCount = stack.stackSize * convRate[slot];
        pooledCount -= adjCount;
        if (pooledCount <= 0) {
            pooledCount = 0;
            for (DrawerData d : data)
                d.reset();
        }

        return stack;
    }

    public int putItemsIntoSlot (int slot, ItemStack stack, int count) {
        if (!isSlotValid(0)) {
            populateSlots(stack);
            if (isSlotValid(0) && data[0].areItemsEqual(stack))
                slot = 0;
            else if (isSlotValid(1) && data[1].areItemsEqual(stack))
                slot = 1;
            else if (isSlotValid(2) && data[2].areItemsEqual(stack))
                slot = 2;
        }

        if (!data[slot].areItemsEqual(stack))
            return 0;

        count = Math.min(count, stack.stackSize);
        int adjCount = count * convRate[slot];
        int adjCountAdded = Math.min(remainingCapacity(), adjCount);

        int countAdded = adjCountAdded / convRate[slot];

        pooledCount += countAdded * convRate[slot];
        stack.stackSize -= countAdded;

        return countAdded;
    }

    public int interactPutItemsIntoSlot (int slot, EntityPlayer player) {
        int count = 0;

        ItemStack currentStack = player.inventory.getCurrentItem();
        if (currentStack != null)
            count += putItemsIntoSlot(slot, currentStack, currentStack.stackSize);

        if (worldObj.getWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID)) {
            for (int i = 0, n = player.inventory.getSizeInventory(); i < n; i++) {
                ItemStack subStack = player.inventory.getStackInSlot(i);
                if (subStack != null) {
                    int subCount = putItemsIntoSlot(slot, subStack, subStack.stackSize);
                    if (subCount > 0 && subStack.stackSize == 0)
                        player.inventory.setInventorySlotContents(i, null);

                    count += subCount;
                }
            }
        }

        lastClickTime = worldObj.getWorldTime();
        lastClickUUID = player.getPersistentID();

        markDirty();

        return count;
    }

    @Override
    public boolean canUpdate () {
        return false;
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);
        data = new DrawerData[slots.tagCount()];

        for (int i = 0, n = data.length; i < n; i++) {
            NBTTagCompound slot = slots.getCompoundTagAt(i);
            data[i] = new DrawerData(this, i);
            data[i].readFromNBT(slot);
        }

        pooledCount = tag.getInteger("Count");

        for (int i = 0, n = convRate.length; i < n; i++)
            convRate[i] = 0;

        if (tag.hasKey("Conv0"))
            convRate[0] = tag.getByte("Conv0");
        if (tag.hasKey("Conv1"))
            convRate[1] = tag.getByte("Conv1");
        if (tag.hasKey("Conv2"))
            convRate[2] = tag.getByte("Conv2");

        snapshotItems = new ItemStack[3];
        snapshotCounts = new int[3];
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagList slots = new NBTTagList();
        for (DrawerData drawer : data) {
            NBTTagCompound slot = new NBTTagCompound();
            drawer.writeToNBT(slot);
            slots.appendTag(slot);
        }

        tag.setInteger("Count", pooledCount);

        if (convRate[0] > 0)
            tag.setByte("Conv0", (byte)convRate[0]);
        if (convRate[1] > 0)
            tag.setByte("Conv1", (byte)convRate[1]);
        if (convRate[2] > 0)
            tag.setByte("Conv2", (byte)convRate[2]);

        tag.setTag("Slots", slots);
    }

    @Override
    public void markDirty () {
        for (int i = 0; i < 3; i++) {
            if (snapshotItems[i] != null && snapshotItems[i].stackSize != snapshotCounts[i]) {
                int diff = snapshotItems[i].stackSize - snapshotCounts[i];
                if (diff > 0)
                    putItemsIntoSlot(i, snapshotItems[i], diff);
                else
                    takeItemsFromSlot(i, -diff);

                int itemStackLimit = getItemStackSize(i);
                snapshotItems[i].stackSize = itemStackLimit - Math.min(itemStackLimit, data[i].remainingCapacity());
                snapshotCounts[i] = snapshotItems[i].stackSize;
            }
        }

        super.markDirty();
    }

    @Override
    public int getSlotCapacity (int slot) {
        ConfigManager config = StorageDrawers.config;
        return getDrawerCapacity() * config.getStorageUpgradeMultiplier(getLevel());
    }

    private int remainingCapacity () {
        if (!isSlotValid(0))
            return 0;

        return data[0].maxCapacity() * convRate[0] - pooledCount;
    }

    private int remainingCapacity (int slot) {
        if (!isSlotValid(slot))
            return 0;

        int adjCapacity = data[slot].maxCapacity() * convRate[slot] - pooledCount;
        return adjCapacity / convRate[slot];
    }

    private boolean isSlotValid (int slot) {
        return data[slot] != null && data[slot].getItem() != null;
    }

    private void populateSlots (ItemStack stack) {
        int index = 0;

        ItemStack uTier1 = findHigherTier(stack);
        if (uTier1 != null) {
            int uCount1 = lookupSizeResult;
            ItemStack uTier2 = findHigherTier(uTier1);
            if (uTier2 != null)
                populateSlot(index++, uTier2, lookupSizeResult * uCount1);

            populateSlot(index++, uTier1, uCount1);
        }

        populateSlot(index++, stack, 1);

        if (index == 3)
            return;

        ItemStack lTier1 = findLowerTier(stack);
        if (lTier1 != null) {
            populateSlot(index++, lTier1, 1);
            for (int i = 0; i < index - 1; i++)
                convRate[i] *= lookupSizeResult;
        }

        if (index == 3 || lTier1 == null)
            return;

        ItemStack lTier2 = findLowerTier(lTier1);
        if (lTier2 != null) {
            populateSlot(index++, lTier2, 1);
            for (int i = 0; i < index - 1; i++)
                convRate[i] *= lookupSizeResult;
        }
    }

    private void populateSlot (int slot, ItemStack stack, int conversion) {
        data[slot].setItem(stack);
        convRate[slot] = conversion;
    }

    private ItemStack findHigherTier (ItemStack stack) {
        CompTierRegistry.Record record = StorageDrawers.compRegistry.findHigherTier(stack);
        if (record != null) {
            lookupSizeResult = record.convRate;
            return record.upper;
        }

        CraftingManager cm = CraftingManager.getInstance();

        setupLookup(lookup3, stack);
        ItemStack match = cm.findMatchingRecipe(lookup3, worldObj);

        if (match == null || match.getItem() == null) {
            setupLookup(lookup2, stack);
            match = cm.findMatchingRecipe(lookup2, worldObj);
        }

        if (match != null && match.getItem() != null) {
            int size = lookupSizeResult;

            setupLookup(lookup1, match);
            ItemStack comp = cm.findMatchingRecipe(lookup1, worldObj);
            if (!DrawerData.areItemsEqual(comp, stack) || comp.stackSize != size)
                return null;

            lookupSizeResult = size;
        }

        return match;
    }

    private ItemStack findLowerTier (ItemStack stack) {
        CompTierRegistry.Record record = StorageDrawers.compRegistry.findLowerTier(stack);
        if (record != null) {
            lookupSizeResult = record.convRate;
            return record.lower;
        }

        CraftingManager cm = CraftingManager.getInstance();
        List recipeList = cm.getRecipeList();

        for (int i = 0, n = recipeList.size(); i < n; i++) {
            IRecipe recipe = (IRecipe) recipeList.get(i);
            ItemStack match = null;

            ItemStack output = recipe.getRecipeOutput();
            if (!DrawerData.areItemsEqual(stack, output))
                continue;

            if (recipe instanceof ShapelessOreRecipe)
                match = tryMatch(stack, ((ShapelessOreRecipe) recipe).getInput());
            else if (recipe instanceof ShapelessRecipes)
                match = tryMatch(stack, ((ShapelessRecipes) recipe).recipeItems);
            else if (recipe instanceof ShapedOreRecipe)
                match = tryMatch(stack, ((ShapedOreRecipe) recipe).getInput());
            else if (recipe instanceof ShapedRecipes)
                match = tryMatch(stack, ((ShapedRecipes) recipe).recipeItems);

            if (match != null) {
                setupLookup(lookup1, stack);
                ItemStack comp = cm.findMatchingRecipe(lookup1, worldObj);
                if (DrawerData.areItemsEqual(match, comp) && comp.stackSize == recipe.getRecipeSize()) {
                    lookupSizeResult = recipe.getRecipeSize();
                    return match;
                }
            }
        }

        return null;
    }

    private ItemStack tryMatch (ItemStack stack, List list) {
        if (list.size() != 9 && list.size() != 4)
            return null;

        Object item = list.get(0);
        if (item instanceof ItemStack) {
            ItemStack item1 = (ItemStack)item;
            for (int i = 1, n = list.size(); i < n; i++) {
                Object item2 = list.get(i);
                if (item2.getClass() != ItemStack.class)
                    return null;
                if (!item1.isItemEqual((ItemStack)item2))
                    return null;
            }
            return item1;
        }
        else if (item instanceof ArrayList) {
            for (int i = 1, n = list.size(); i < n; i++) {
                if (item != list.get(i))
                    return null;
            }

            ArrayList itemList = (ArrayList)item;
            if (itemList.size() > 0) {
                Object item1 = itemList.get(0);
                if (item1 instanceof ItemStack)
                    return (ItemStack)item1;
            }
        }

        return null;
    }

    private ItemStack tryMatch (ItemStack stack, Object[] list) {
        if (list.length != 9 && list.length != 4)
            return null;

        Object item = list[0];
        if (item instanceof ItemStack) {
            ItemStack item1 = (ItemStack)item;
            for (int i = 1, n = list.length; i < n; i++) {
                Object item2 = list[i];
                if (item2 == null || item2.getClass() != ItemStack.class)
                    return null;
                if (!item1.isItemEqual((ItemStack)item2))
                    return null;
            }
            return item1;
        }
        else if (item instanceof ArrayList) {
            for (int i = 1, n = list.length; i < n; i++) {
                if (item != list[i])
                    return null;
            }

            ArrayList itemList = (ArrayList)item;
            if (itemList.size() > 0) {
                Object item1 = itemList.get(0);
                if (item1 instanceof ItemStack)
                    return (ItemStack)item1;
            }
        }

        return null;
    }

    private void setupLookup (InventoryLookup inv, ItemStack stack) {
        for (int i = 0, n = inv.getSizeInventory(); i < n; i++)
            inv.setInventorySlotContents(i, stack);

        lookupSizeResult = inv.getSizeInventory();
    }

    @Override
    public int getSizeInventory () {
        return 3;
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        if (slot < 0 || slot >= getSizeInventory())
            return null;

        int itemStackLimit = getItemStackSize(slot);
        ItemStack stack = getItemsFromSlot(slot, itemStackLimit);
        if (stack != null) {
            stack.stackSize = itemStackLimit - Math.min(itemStackLimit, remainingCapacity(slot));
            snapshotItems[slot] = stack;
            snapshotCounts[slot] = stack.stackSize;
        }
        else {
            snapshotItems[slot] = null;
            snapshotCounts[slot] = 0;
        }

        return stack;
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        if (slot < 0 || slot >= getSizeInventory())
            return null;

        ItemStack stack = takeItemsFromSlot(slot, count);
        if (stack != null && !worldObj.isRemote)
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemStack) {
        if (slot < 0 || slot >= getSizeInventory())
            return;

        int insertCount = itemStack.stackSize;
        if (snapshotItems[slot] != null)
            insertCount = itemStack.stackSize - snapshotCounts[slot];

        if (insertCount > 0) {
            int count = putItemsIntoSlot(slot, itemStack, insertCount);
            if (count > 0 && !worldObj.isRemote)
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        else if (insertCount < 0) {
            ItemStack rmStack = takeItemsFromSlot(slot, -insertCount);
            if (rmStack != null && rmStack.stackSize > 0 && !worldObj.isRemote)
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        if (snapshotItems[slot] != null) {
            int itemStackLimit = getItemStackSize(slot);
            snapshotItems[slot].stackSize = itemStackLimit - Math.min(itemStackLimit, remainingCapacity(slot));
            snapshotCounts[slot] = snapshotItems[slot].stackSize;
        }
    }

    @Override
    public String getInventoryName () {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName () {
        return false;
    }

    @Override
    public int getInventoryStackLimit () {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory () {

    }

    @Override
    public void closeInventory () {

    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack itemStack) {
        if (slot < 0 || slot >= getSizeInventory())
            return false;

        if (!isSlotValid(0))
            return true;

        if (!data[slot].areItemsEqual(itemStack))
            return false;

        if (remainingCapacity(slot) < itemStack.stackSize)
            return false;

        return true;
    }

    private static final int[] drawerSlots0 = new int[0];
    private static final int[] drawerSlots3 = new int[] { 0, 1, 2 };

    @Override
    public int[] getAccessibleSlotsFromSide (int side) {
        for (int aside : autoSides) {
            if (side == aside)
                return drawerSlots3;
        }

        return drawerSlots0;
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, int side) {
        if (slot < 0 || slot >= getSizeInventory())
            return false;

        for (int aside : autoSides) {
            if (side == aside) {
                return isItemValidForSlot(slot, stack);
            }
        }

        return false;
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, int side) {
        return false;
    }
}

class InventoryLookup extends InventoryCrafting
{
    private ItemStack[] stackList;

    public InventoryLookup (int width, int height) {
        super(null, width, height);
        stackList = new ItemStack[width * height];
    }

    @Override
    public int getSizeInventory ()
    {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return slot >= this.getSizeInventory() ? null : this.stackList[slot];
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        return null;
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        stackList[slot] = stack;
    }
}

