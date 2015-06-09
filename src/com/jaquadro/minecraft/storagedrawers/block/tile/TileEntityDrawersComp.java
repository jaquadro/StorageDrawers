package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.registry.IIngredientHandler;
import com.jaquadro.minecraft.storagedrawers.api.registry.IRecipeHandler;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.config.CompTierRegistry;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.storage.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class TileEntityDrawersComp extends TileEntityDrawers
{
    private static InventoryLookup lookup1 = new InventoryLookup(1, 1);
    private static InventoryLookup lookup2 = new InventoryLookup(2, 2);
    private static InventoryLookup lookup3 = new InventoryLookup(3, 3);

    private ICentralInventory centralInventory;

    private int pooledCount;
    private int lookupSizeResult;

    private ItemStack[] protoStack;
    private int[] convRate;

    public TileEntityDrawersComp () {
        super(3);

        protoStack = new ItemStack[getDrawerCount()];
        convRate = new int[getDrawerCount()];
    }

    protected ICentralInventory getCentralInventory () {
        if (centralInventory == null)
            centralInventory = new CompCentralInventory();
        return centralInventory;
    }

    public int getStoredItemRemainder (int slot) {
        int count = centralInventory.getStoredItemCount(slot);
        if (slot > 0 && convRate[slot] > 0)
            count -= centralInventory.getStoredItemCount(slot - 1) * (convRate[slot - 1] / convRate[slot]);

        return count;
    }

    @Override
    protected IDrawer createDrawer (int slot) {
        return new CompDrawerData(getCentralInventory(), slot);
    }

    @Override
    public boolean isDrawerEnabled (int slot) {
        if (slot > 0 && convRate[slot] == 0)
            return false;

        return super.isDrawerEnabled(slot);
    }

    @Override
    public int putItemsIntoSlot (int slot, ItemStack stack, int count) {
        int added = 0;
        if (stack != null && convRate != null && convRate[0] == 0) {
            populateSlots(stack);

            for (int i = 0; i < getDrawerCount(); i++) {
                if (BaseDrawerData.areItemsEqual(protoStack[i], stack))
                    added = super.putItemsIntoSlot(i, stack, count);
            }

            for (int i = 0; i < getDrawerCount(); i++) {
                IDrawer drawer = getDrawer(i);
                if (drawer instanceof CompDrawerData)
                    ((CompDrawerData) drawer).refresh();
            }


        }

        return added + super.putItemsIntoSlot(slot, stack, count);
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        pooledCount = 0;

        for (int i = 0; i < getDrawerCount(); i++) {
            protoStack[i] = null;
            convRate[i] = 0;
        }

        super.readFromNBT(tag);

        try {
            pooledCount = tag.getInteger("Count");

            if (tag.hasKey("Conv0"))
                convRate[0] = tag.getByte("Conv0");
            if (tag.hasKey("Conv1"))
                convRate[1] = tag.getByte("Conv1");
            if (tag.hasKey("Conv2"))
                convRate[2] = tag.getByte("Conv2");

            for (int i = 0; i < getDrawerCount(); i++) {
                IDrawer drawer = getDrawer(i);
                if (drawer instanceof CompDrawerData)
                    ((CompDrawerData) drawer).refresh();
            }

            if (worldObj != null && !worldObj.isRemote) {
                //TileEntityDrawersComp.this.markDirty();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
        catch (Throwable t) {
            trapLoadFailure(t, tag);
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (loadDidFail())
            return;

        tag.setInteger("Count", pooledCount);

        if (convRate[0] > 0)
            tag.setByte("Conv0", (byte)convRate[0]);
        if (convRate[1] > 0)
            tag.setByte("Conv1", (byte)convRate[1]);
        if (convRate[2] > 0)
            tag.setByte("Conv2", (byte)convRate[2]);
    }

    @Override
    public void clientUpdateCount (int slot, int count) {
        if (count != pooledCount) {
            pooledCount = count;
            getWorldObj().func_147479_m(xCoord, yCoord, zCoord); // markBlockForRenderUpdate
        }
    }

    @Override
    public String getInventoryName () {
        return hasCustomInventoryName() ? super.getInventoryName() : "storageDrawers.container.compDrawers";
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
        convRate[slot] = conversion;
        protoStack[slot] = stack.copy();
        //centralInventory.setStoredItem(slot, stack, 0);
        //getDrawer(slot).setStoredItem(stack, 0);
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

            IRecipeHandler handler = StorageDrawers.recipeHandlerRegistry.getRecipeHandler(recipe.getClass());
            while (handler != null) {
                Object[] itemArr = handler.getInputAsArray(recipe);
                if (itemArr != null) {
                    match = tryMatch(stack, itemArr);
                    break;
                }

                List itemList = handler.getInputAsList(recipe);
                if (itemList != null) {
                    match = tryMatch(stack, itemList);
                    break;
                }

                break;
            }

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
        if (item == null)
            return null;

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
        else {
            IIngredientHandler handler = StorageDrawers.recipeHandlerRegistry.getIngredientHandler(item.getClass());
            if (handler == null)
                return null;

            ItemStack item1 = handler.getItemStack(item);
            if (item1 == null)
                return null;

            for (int i = 1, n = list.length; i < n; i++) {
                Object item2 = list[i];
                if (item2 == null || item.getClass() != item2.getClass())
                    return null;

                item2 = handler.getItemStack(item2);
                if (item2 == null || item2.getClass() != ItemStack.class)
                    return null;
                if (!item1.isItemEqual((ItemStack)item2))
                    return null;
            }

            return item1;
        }

        return null;
    }

    private void setupLookup (InventoryLookup inv, ItemStack stack) {
        for (int i = 0, n = inv.getSizeInventory(); i < n; i++)
            inv.setInventorySlotContents(i, stack);

        lookupSizeResult = inv.getSizeInventory();
    }

    private class CompCentralInventory implements ICentralInventory
    {
        @Override
        public ItemStack getStoredItemPrototype (int slot) {
            return protoStack[slot];
        }

        @Override
        public void setStoredItem (int slot, ItemStack itemPrototype, int amount) {
            if (itemPrototype != null && convRate != null && convRate[0] == 0) {
                populateSlots(itemPrototype);
                for (int i = 0; i < getDrawerCount(); i++) {
                    if (BaseDrawerData.areItemsEqual(protoStack[i], itemPrototype))
                        pooledCount = (pooledCount % convRate[i]) + convRate[i] * amount;
                }

                for (int i = 0; i < getDrawerCount(); i++) {
                    if (i == slot)
                        continue;

                    IDrawer drawer = getDrawer(i);
                    if (drawer instanceof CompDrawerData)
                        ((CompDrawerData) drawer).refresh();
                }

                if (worldObj != null && !worldObj.isRemote) {
                    //TileEntityDrawersComp.this.markDirty();
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }
            else if (itemPrototype == null) {
                setStoredItemCount(slot, 0);
            }
        }

        @Override
        public int getStoredItemCount (int slot) {
            if (convRate == null || convRate[slot] == 0)
                return 0;

            return pooledCount / convRate[slot];
        }

        @Override
        public void setStoredItemCount (int slot, int amount) {
            if (convRate == null || convRate[slot] == 0)
                return;

            int oldCount = pooledCount;
            pooledCount = (pooledCount % convRate[slot]) + convRate[slot] * amount;

            int poolMax = getMaxCapacity(0) * convRate[0];
            if (pooledCount > poolMax)
                pooledCount = poolMax;

            if (pooledCount != oldCount) {
                if (pooledCount != 0 || TileEntityDrawersComp.this.isLocked(LockAttribute.LOCK_POPULATED))
                    markAmountDirty();
                else {
                    clear();
                    if (worldObj != null && !worldObj.isRemote) {
                        //TileEntityDrawersComp.this.markDirty();
                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    }
                }
            }
        }

        @Override
        public int getMaxCapacity (int slot) {
            if (protoStack[slot] == null || convRate == null || convRate[slot] == 0)
                return 0;

            return protoStack[slot].getItem().getItemStackLimit(protoStack[slot]) * getStackCapacity(slot);
        }

        @Override
        public int getMaxCapacity (int slot, ItemStack itemPrototype) {
            if (itemPrototype == null || itemPrototype.getItem() == null)
                return 0;

            if (convRate == null || protoStack[0] == null || convRate[0] == 0)
                return itemPrototype.getItem().getItemStackLimit(itemPrototype) * getBaseStackCapacity();

            if (BaseDrawerData.areItemsEqual(protoStack[slot], itemPrototype))
                return getMaxCapacity(slot);

            return 0;
        }

        @Override
        public int getRemainingCapacity (int slot) {
            return getMaxCapacity(slot) - getStoredItemCount(slot);
        }

        @Override
        public int getStoredItemStackSize (int slot) {
            if (protoStack[slot] == null || convRate == null || convRate[slot] == 0)
                return 0;

            return protoStack[slot].getItem().getItemStackLimit(protoStack[slot]);
        }

        @Override
        public int getItemCapacityForInventoryStack (int slot) {
            if (isVoid())
                return Integer.MAX_VALUE;
            else
                return getMaxCapacity(slot);
        }

        @Override
        public int getConversionRate (int slot) {
            if (protoStack[slot] == null || convRate == null || convRate[slot] == 0)
                return 0;

            return convRate[0] / convRate[slot];
        }

        @Override
        public int getStoredItemRemainder (int slot) {
            return TileEntityDrawersComp.this.getStoredItemRemainder(slot);
        }

        @Override
        public boolean isSmallestUnit (int slot) {
            if (protoStack[slot] == null || convRate == null || convRate[slot] == 0)
                return false;

            return convRate[slot] == 1;
        }

        @Override
        public boolean isVoidSlot (int slot) {
            return isVoid();
        }

        @Override
        public boolean isShroudedSlot (int slot) {
            return isShrouded();
        }

        @Override
        public boolean setIsSlotShrouded (int slot, boolean state) {
            setIsShrouded(state);
            return true;
        }

        @Override
        public boolean isLocked (int slot, LockAttribute attr) {
            return TileEntityDrawersComp.this.isLocked(attr);
        }

        @Override
        public void writeToNBT (int slot, NBTTagCompound tag) {
            ItemStack protoStack = getStoredItemPrototype(slot);
            if (protoStack != null && protoStack.getItem() != null) {
                tag.setShort("Item", (short) Item.getIdFromItem(protoStack.getItem()));
                tag.setShort("Meta", (short) protoStack.getItemDamage());
                tag.setInteger("Count", 0); // TODO: Remove when ready to break 1.1.7 compat

                if (protoStack.getTagCompound() != null)
                    tag.setTag("Tags", protoStack.getTagCompound());
            }
        }

        @Override
        public void readFromNBT (int slot, NBTTagCompound tag) {
            if (tag.hasKey("Item")) {
                Item item = Item.getItemById(tag.getShort("Item"));
                if (item != null) {
                    ItemStack stack = new ItemStack(item);
                    stack.setItemDamage(tag.getShort("Meta"));
                    if (tag.hasKey("Tags"))
                        stack.setTagCompound(tag.getCompoundTag("Tags"));

                    protoStack[slot] = stack;
                }
            }
        }

        private void clear () {
            for (int i = 0; i < getDrawerCount(); i++) {
                protoStack[i] = null;
                convRate[i] = 0;
            }

            refresh();
            TileEntityDrawersComp.this.markDirty();
        }

        public void refresh () {
            for (int i = 0; i < getDrawerCount(); i++) {
                IDrawer drawer = getDrawer(i);
                if (drawer instanceof CompDrawerData)
                    ((CompDrawerData) drawer).refresh();
            }
        }

        private int getStackCapacity (int slot) {
            if (convRate == null || convRate[slot] == 0)
                return 0;

            int slotStacks = getBaseStackCapacity();

            int stackLimit = convRate[0] * slotStacks;
            return stackLimit / convRate[slot];
        }

        private int getBaseStackCapacity () {
            ConfigManager config = StorageDrawers.config;
            return TileEntityDrawersComp.this.getEffectiveStorageMultiplier() * TileEntityDrawersComp.this.getDrawerCapacity();
        }

        public void markAmountDirty () {
            if (getWorldObj().isRemote)
                return;

            IMessage message = new CountUpdateMessage(xCoord, yCoord, zCoord, 0, pooledCount);
            NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(getWorldObj().provider.dimensionId, xCoord, yCoord, zCoord, 500);

            StorageDrawers.network.sendToAllAround(message, targetPoint);
        }

        public void markDirty (int slot) {
            if (getWorldObj().isRemote)
                return;

            getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    private static class InventoryLookup extends InventoryCrafting
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
}
