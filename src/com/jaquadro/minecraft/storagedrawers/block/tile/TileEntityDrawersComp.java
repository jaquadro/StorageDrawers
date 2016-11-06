package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.registry.IIngredientHandler;
import com.jaquadro.minecraft.storagedrawers.api.registry.IRecipeHandler;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.config.CompTierRegistry;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawersComp;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.storage.*;
import com.jaquadro.minecraft.storagedrawers.util.UniqueMetaIdentifier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerDrawersComp(playerInventory, this);
    }

    @Override
    public String getGuiID () {
        return StorageDrawers.MOD_ID + ":compDrawers";
    }

    @Override
    public IDrawer getDrawerIfEnabled (int slot) {
        if (slot > 0 && slot < convRate.length && convRate[slot] == 0)
            return null;

        return super.getDrawerIfEnabled(slot);
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
    public void readFromPortableNBT (NBTTagCompound tag) {
        pooledCount = 0;

        for (int i = 0; i < getDrawerCount(); i++) {
            protoStack[i] = null;
            convRate[i] = 0;
        }

        super.readFromPortableNBT(tag);

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
            IBlockState state = worldObj.getBlockState(getPos());
            worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public NBTTagCompound writeToPortableNBT (NBTTagCompound tag) {
        super.writeToPortableNBT(tag);

        tag.setInteger("Count", pooledCount);

        if (convRate[0] > 0)
            tag.setByte("Conv0", (byte)convRate[0]);
        if (convRate[1] > 0)
            tag.setByte("Conv1", (byte)convRate[1]);
        if (convRate[2] > 0)
            tag.setByte("Conv2", (byte)convRate[2]);

        return tag;
    }

    @Override
    public void clientUpdateCount (int slot, int count) {
        if (count != pooledCount) {
            pooledCount = count;
            IBlockState state = worldObj.getBlockState(getPos());
            worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public String getName () {
        return hasCustomName() ? super.getName() : "storageDrawers.container.compDrawers";
    }

    private void populateSlots (ItemStack stack) {
        int index = 0;

        ItemStack uTier1 = findHigherTier(stack);
        if (uTier1 != null) {
            if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Picked candidate " + uTier1.toString() + " with conv=" + lookupSizeResult);

            int uCount1 = lookupSizeResult;
            ItemStack uTier2 = findHigherTier(uTier1);
            if (uTier2 != null) {
                if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
                    FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Picked candidate " + uTier2.toString() + " with conv=" + lookupSizeResult);

                populateSlot(index++, uTier2, lookupSizeResult * uCount1);
            }

            populateSlot(index++, uTier1, uCount1);
        }

        populateSlot(index++, stack, 1);

        if (index == 3)
            return;

        ItemStack lTier1 = findLowerTier(stack);
        if (lTier1 != null) {
            if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Picked candidate " + lTier1.toString() + " with conv=" + lookupSizeResult);

            populateSlot(index++, lTier1, 1);
            for (int i = 0; i < index - 1; i++)
                convRate[i] *= lookupSizeResult;
        }

        if (index == 3 || lTier1 == null)
            return;

        ItemStack lTier2 = findLowerTier(lTier1);
        if (lTier2 != null) {
            if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Picked candidate " + lTier2.toString() + " with conv=" + lookupSizeResult);

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

        if (worldObj != null && !worldObj.isRemote) {
            IBlockState state = worldObj.getBlockState(getPos());
            worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    private ItemStack findHigherTier (ItemStack stack) {
        if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Finding ascending candidates for " + stack.toString());

        CompTierRegistry.Record record = StorageDrawers.compRegistry.findHigherTier(stack);
        if (record != null) {
            if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Found " + record.upper.toString() + " in registry with conv=" + record.convRate);

            lookupSizeResult = record.convRate;
            return record.upper;
        }

        CraftingManager cm = CraftingManager.getInstance();
        List<ItemStack> candidates = new ArrayList<ItemStack>();

        setupLookup(lookup3, stack);
        List<ItemStack> fwdCandidates = findAllMatchingRecipes(lookup3);

        if (fwdCandidates.size() == 0) {
            setupLookup(lookup2, stack);
            fwdCandidates = findAllMatchingRecipes(lookup2);
        }

        if (fwdCandidates.size() > 0) {
            int size = lookupSizeResult;

            for (int i = 0, n1 = fwdCandidates.size(); i < n1; i++) {
                ItemStack match = fwdCandidates.get(i);
                setupLookup(lookup1, match);
                List<ItemStack> backCandidates = findAllMatchingRecipes(lookup1);

                for (int j = 0, n2 = backCandidates.size(); j < n2; j++) {
                    ItemStack comp = backCandidates.get(j);
                    if (comp.stackSize != size)
                        continue;

                    if (!DrawerData.areItemsEqual(comp, stack, false))
                        continue;

                    candidates.add(match);
                    if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
                        FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Found ascending candidate for " + stack.toString() + ": " + match.toString() + " size=" + lookupSizeResult + ", inverse=" + comp.toString());

                    break;
                }
            }

            lookupSizeResult = size;
        }

        ItemStack modMatch = findMatchingModCandidate(stack, candidates);
        if (modMatch != null)
            return modMatch;

        if (candidates.size() > 0)
            return candidates.get(0);

        if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "No candidates found");

        return null;
    }

    private List<ItemStack> findAllMatchingRecipes (InventoryCrafting crafting) {
        List<ItemStack> candidates = new ArrayList<ItemStack>();

        CraftingManager cm = CraftingManager.getInstance();
        List recipeList = cm.getRecipeList();

        for (int i = 0, n = recipeList.size(); i < n; i++) {
            IRecipe recipe = (IRecipe) recipeList.get(i);
            if (recipe.matches(crafting, worldObj)) {
                ItemStack result = recipe.getCraftingResult(crafting);
                if (result != null && result.getItem() != null)
                    candidates.add(result);
            }
        }

        return candidates;
    }

    private ItemStack findLowerTier (ItemStack stack) {
        if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Finding descending candidates for " + stack.toString());

        CompTierRegistry.Record record = StorageDrawers.compRegistry.findLowerTier(stack);
        if (record != null) {
            if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Found " + record.lower.toString() + " in registry with conv=" + record.convRate);

            lookupSizeResult = record.convRate;
            return record.lower;
        }

        CraftingManager cm = CraftingManager.getInstance();
        List recipeList = cm.getRecipeList();

        List<ItemStack> candidates = new ArrayList<ItemStack>();
        Map<ItemStack, Integer> candidatesRate = new HashMap<ItemStack, Integer>();

        for (int i = 0, n = recipeList.size(); i < n; i++) {
            IRecipe recipe = (IRecipe) recipeList.get(i);
            ItemStack match = null;

            ItemStack output = recipe.getRecipeOutput();
            if (!DrawerData.areItemsEqual(stack, output, false))
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
                List<ItemStack> compMatches = findAllMatchingRecipes(lookup1);
                for (ItemStack comp : compMatches) {
                    if (DrawerData.areItemsEqual(match, comp, true) && comp.stackSize == recipe.getRecipeSize()) {
                        lookupSizeResult = recipe.getRecipeSize();
                        candidates.add(match);
                        candidatesRate.put(match, lookupSizeResult);

                        if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
                            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Found descending candidate for " + stack.toString() + ": " + match.toString() + " size=" + lookupSizeResult + ", inverse=" + comp.toString());
                    } else if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
                        FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Back-check failed for " + match.toString() + " size=" + lookupSizeResult + ", inverse=" + comp.toString());
                }
            }
        }

        ItemStack modMatch = findMatchingModCandidate(stack, candidates);
        if (modMatch != null) {
            lookupSizeResult = candidatesRate.get(modMatch);
            return modMatch;
        }

        if (candidates.size() > 0) {
            ItemStack match = candidates.get(0);
            lookupSizeResult = candidatesRate.get(match);

            return match;
        }

        if (!worldObj.isRemote && StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "No candidates found");

        return null;
    }

    private ItemStack findMatchingModCandidate (ItemStack reference, List<ItemStack> candidates) {
        String referenceName = GameData.getItemRegistry().getNameForObject(reference.getItem()).toString();
        if (referenceName != null) {
           UniqueMetaIdentifier referneceID = new UniqueMetaIdentifier(referenceName);
            for (ItemStack candidate : candidates) {
                String matchName = GameData.getItemRegistry().getNameForObject(candidate.getItem()).toString();
                if (matchName != null) {
                    UniqueMetaIdentifier matchID = new UniqueMetaIdentifier(matchName);
                    if (referneceID.getModID().equals(matchID.getModID()))
                        return candidate;
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
        else if (item instanceof List) {
            for (int i = 1, n = list.size(); i < n; i++) {
                if (item != list.get(i))
                    return null;
            }

            List itemList = (List)item;
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
        else if (item instanceof List) {
            for (int i = 1, n = list.length; i < n; i++) {
                if (item != list[i])
                    return null;
            }

            List itemList = (List)item;
            if (itemList.size() > 0) {
                Object item1 = findMatchingModCandidate(stack, itemList);
                if (item1 == null)
                    item1 = itemList.get(0);

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
        public IDrawer setStoredItem (int slot, ItemStack itemPrototype, int amount) {
            if (itemPrototype != null && convRate != null && convRate[0] == 0) {
                IDrawer target = null;
                populateSlots(itemPrototype);
                for (int i = 0; i < getDrawerCount(); i++) {
                    if (BaseDrawerData.areItemsEqual(protoStack[i], itemPrototype)) {
                        target = getDrawer(i);
                        pooledCount = (pooledCount % convRate[i]) + convRate[i] * amount;
                    }
                }

                for (int i = 0; i < getDrawerCount(); i++) {
                    if (i == slot)
                        continue;

                    IDrawer drawer = getDrawer(i);
                    if (drawer instanceof CompDrawerData)
                        ((CompDrawerData) drawer).refresh();
                }

                if (worldObj != null && !worldObj.isRemote) {
                    IBlockState state = worldObj.getBlockState(getPos());
                    worldObj.notifyBlockUpdate(getPos(), state, state, 3);
                }

                return target;
            }
            else if (itemPrototype == null) {
                pooledCount = 0;
                clear();
                if (worldObj != null && !worldObj.isRemote) {
                    IBlockState state = worldObj.getBlockState(getPos());
                    worldObj.notifyBlockUpdate(getPos(), state, state, 3);
                }
            }

            return getDrawer(slot);
        }

        @Override
        public int getStoredItemCount (int slot) {
            if (convRate == null || convRate[slot] == 0)
                return 0;

            if (TileEntityDrawersComp.this.isVending())
                return Integer.MAX_VALUE;

            return pooledCount / convRate[slot];
        }

        @Override
        public void setStoredItemCount (int slot, int amount) {
            if (convRate == null || convRate[slot] == 0)
                return;

            if (TileEntityDrawersComp.this.isVending())
                return;

            int oldCount = pooledCount;
            pooledCount = (pooledCount % convRate[slot]) + convRate[slot] * amount;

            int poolMax = getMaxCapacity(0) * convRate[0];
            if (pooledCount > poolMax)
                pooledCount = poolMax;

            if (pooledCount != oldCount) {
                if (pooledCount != 0 || TileEntityDrawersComp.this.isItemLocked(LockAttribute.LOCK_POPULATED))
                    markAmountDirty();
                else {
                    clear();
                    if (worldObj != null && !worldObj.isRemote) {
                        IBlockState state = worldObj.getBlockState(getPos());
                        worldObj.notifyBlockUpdate(getPos(), state, state, 3);
                    }
                }
            }
        }

        @Override
        public int getMaxCapacity (int slot) {
            if (protoStack[slot] == null || convRate == null || convRate[slot] == 0)
                return 0;

            if (TileEntityDrawersComp.this.isUnlimited() || TileEntityDrawersComp.this.isVending()) {
                if (convRate == null || protoStack[slot] == null || convRate[slot] == 0)
                    return Integer.MAX_VALUE;
                return Integer.MAX_VALUE / convRate[slot];
            }

            return protoStack[slot].getItem().getItemStackLimit(protoStack[slot]) * getStackCapacity(slot);
        }

        @Override
        public int getMaxCapacity (int slot, ItemStack itemPrototype) {
            if (itemPrototype == null || itemPrototype.getItem() == null)
                return 0;

            if (TileEntityDrawersComp.this.isUnlimited() || TileEntityDrawersComp.this.isVending()) {
                if (convRate == null || protoStack[slot] == null || convRate[slot] == 0)
                    return Integer.MAX_VALUE;
                return Integer.MAX_VALUE / convRate[slot];
            }

            if (convRate == null || protoStack[0] == null || convRate[0] == 0)
                return itemPrototype.getItem().getItemStackLimit(itemPrototype) * getBaseStackCapacity();

            if (BaseDrawerData.areItemsEqual(protoStack[slot], itemPrototype))
                return getMaxCapacity(slot);

            return 0;
        }

        @Override
        public int getRemainingCapacity (int slot) {
            if (TileEntityDrawersComp.this.isVending())
                return Integer.MAX_VALUE;

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
            return TileEntityDrawersComp.this.isItemLocked(attr);
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
            if (getWorld().isRemote)
                return;

            IMessage message = new CountUpdateMessage(getPos(), 0, pooledCount);
            NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 500);

            StorageDrawers.network.sendToAllAround(message, targetPoint);
        }

        public void markDirty (int slot) {
            if (getWorld().isRemote)
                return;

            IBlockState state = worldObj.getBlockState(getPos());
            worldObj.notifyBlockUpdate(getPos(), state, state, 3);
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
        public ItemStack removeStackFromSlot (int slot) {
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
