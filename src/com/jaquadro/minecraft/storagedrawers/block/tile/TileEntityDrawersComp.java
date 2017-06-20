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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreIngredient;
import org.apache.logging.log4j.Level;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
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

    private int capacity = 0;
    private int pooledCount;
    private int lookupSizeResult;

    private ItemStack[] protoStack;
    private int[] convRate;

    public TileEntityDrawersComp () {
        super(3);

        protoStack = new ItemStack[getDrawerCount()];
        for (int i = 0; i < protoStack.length; i++)
            protoStack[i] = ItemStack.EMPTY;

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
    public int getDrawerCapacity () {
        if (getWorld() == null || getWorld().isRemote)
            return super.getDrawerCapacity();

        if (capacity == 0) {
            ConfigManager config = StorageDrawers.config;
            capacity = config.getBlockBaseStorage("compdrawers");

            if (capacity <= 0)
                capacity = 1;

            attributeChanged();
        }

        return capacity;
    }

    @Override
    public IDrawer getDrawerIfEnabled (int slot) {
        if (slot > 0 && slot < convRate.length && convRate[slot] == 0)
            return null;

        return super.getDrawerIfEnabled(slot);
    }

    @Override
    public int putItemsIntoSlot (int slot, @Nonnull ItemStack stack, int count) {
        int added = 0;
        if (!stack.isEmpty() && convRate != null && convRate[0] == 0) {
            populateSlots(stack);

            for (int i = 0; i < getDrawerCount(); i++) {
                if (convRate[i] != 0 && BaseDrawerData.areItemsEqual(protoStack[i], stack))
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
    public boolean dataPacketRequiresRenderUpdate () {
        return true;
    }

    @Override
    public void readFromPortableNBT (NBTTagCompound tag) {
        super.readFromPortableNBT(tag);

        pooledCount = 0;

        for (int i = 0; i < getDrawerCount(); i++) {
            protoStack[i] = ItemStack.EMPTY;
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
    }

    @Override
    public NBTTagCompound writeToPortableNBT (NBTTagCompound tag) {
        tag = super.writeToPortableNBT(tag);

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
    @SideOnly(Side.CLIENT)
    public void clientUpdateCount (final int slot, final int count) {
        if (!getWorld().isRemote)
            return;

        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run () {
                TileEntityDrawersComp.this.clientUpdateCountAsync(slot, count);
            }
        });
    }

    @SideOnly(Side.CLIENT)
    private void clientUpdateCountAsync (int slot, int count) {
        if (count != pooledCount) {
            pooledCount = count;
        }
    }

    @Override
    public String getName () {
        return hasCustomName() ? super.getName() : "storagedrawers.container.compDrawers";
    }

    private void populateSlots (@Nonnull ItemStack stack) {
        int index = 0;

        ItemStack uTier1 = findHigherTier(stack);
        if (!uTier1.isEmpty()) {
            if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Picked candidate " + uTier1.toString() + " with conv=" + lookupSizeResult);

            int uCount1 = lookupSizeResult;
            ItemStack uTier2 = findHigherTier(uTier1);
            if (!uTier2.isEmpty()) {
                if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                    FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Picked candidate " + uTier2.toString() + " with conv=" + lookupSizeResult);

                populateSlot(index++, uTier2, lookupSizeResult * uCount1);
            }

            populateSlot(index++, uTier1, uCount1);
        }

        populateSlot(index++, stack, 1);

        if (index == 3)
            return;

        ItemStack lTier1 = findLowerTier(stack);
        if (!lTier1.isEmpty()) {
            if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Picked candidate " + lTier1.toString() + " with conv=" + lookupSizeResult);

            populateSlot(index++, lTier1, 1);
            for (int i = 0; i < index - 1; i++)
                convRate[i] *= lookupSizeResult;
        }

        if (index == 3 || lTier1.isEmpty())
            return;

        ItemStack lTier2 = findLowerTier(lTier1);
        if (!lTier2.isEmpty()) {
            if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Picked candidate " + lTier2.toString() + " with conv=" + lookupSizeResult);

            populateSlot(index++, lTier2, 1);
            for (int i = 0; i < index - 1; i++)
                convRate[i] *= lookupSizeResult;
        }
    }

    private void populateSlot (int slot, @Nonnull ItemStack stack, int conversion) {
        convRate[slot] = conversion;
        protoStack[slot] = stack.copy();
        //centralInventory.setStoredItem(slot, stack, 0);
        //getDrawer(slot).setStoredItem(stack, 0);

        markBlockForUpdate();
    }

    @Nonnull
    private ItemStack findHigherTier (@Nonnull ItemStack stack) {
        if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Finding ascending candidates for " + stack.toString());

        CompTierRegistry.Record record = StorageDrawers.compRegistry.findHigherTier(stack);
        if (record != null) {
            if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Found " + record.upper.toString() + " in registry with conv=" + record.convRate);

            lookupSizeResult = record.convRate;
            return record.upper;
        }

        List<ItemStack> candidates = new ArrayList<>();

        setupLookup(lookup3, stack);
        List<ItemStack> fwdCandidates = findAllMatchingRecipes(lookup3);

        if (fwdCandidates.size() == 0) {
            setupLookup(lookup2, stack);
            fwdCandidates = findAllMatchingRecipes(lookup2);
        }

        if (fwdCandidates.size() > 0) {
            int size = lookupSizeResult;

            for (ItemStack match : fwdCandidates) {
                setupLookup(lookup1, match);
                List<ItemStack> backCandidates = findAllMatchingRecipes(lookup1);

                for (ItemStack comp : backCandidates) {
                    if (comp.getCount() != size)
                        continue;

                    if (!DrawerData.areItemsEqual(comp, stack, false))
                        continue;

                    candidates.add(match);
                    if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                        FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Found ascending candidate for " + stack.toString() + ": " + match.toString() + " size=" + lookupSizeResult + ", inverse=" + comp.toString());

                    break;
                }
            }

            lookupSizeResult = size;
        }

        ItemStack modMatch = findMatchingModCandidate(stack, candidates);
        if (!modMatch.isEmpty())
            return modMatch;

        if (candidates.size() > 0)
            return candidates.get(0);

        if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "No candidates found");

        return ItemStack.EMPTY;
    }

    private List<ItemStack> findAllMatchingRecipes (InventoryCrafting crafting) {
        List<ItemStack> candidates = new ArrayList<>();

        for (Object aRecipeList : CraftingManager.REGISTRY) {
            IRecipe recipe = (IRecipe) aRecipeList;
            if (recipe.matches(crafting, getWorld())) {
                ItemStack result = recipe.getCraftingResult(crafting);
                if (!result.isEmpty())
                    candidates.add(result);
            }
        }

        return candidates;
    }

    @Nonnull
    private ItemStack findLowerTier (@Nonnull ItemStack stack) {
        if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Finding descending candidates for " + stack.toString());

        CompTierRegistry.Record record = StorageDrawers.compRegistry.findLowerTier(stack);
        if (record != null) {
            if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Found " + record.lower.toString() + " in registry with conv=" + record.convRate);

            lookupSizeResult = record.convRate;
            return record.lower;
        }

        List<ItemStack> candidates = new ArrayList<>();
        Map<ItemStack, Integer> candidatesRate = new HashMap<>();

        for (Object aRecipeList : CraftingManager.REGISTRY) {
            IRecipe recipe = (IRecipe) aRecipeList;
            ItemStack match = ItemStack.EMPTY;

            ItemStack output = recipe.getRecipeOutput();
            if (!DrawerData.areItemsEqual(stack, output, true))
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

            if (!match.isEmpty()) {
                setupLookup(lookup1, output);
                List<ItemStack> compMatches = findAllMatchingRecipes(lookup1);
                for (ItemStack comp : compMatches) {
                    int recipeSize = recipe.getIngredients().size();
                    if (DrawerData.areItemsEqual(match, comp, true) && comp.getCount() == recipeSize) {
                        lookupSizeResult = recipeSize;
                        candidates.add(match);
                        candidatesRate.put(match, lookupSizeResult);

                        if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Found descending candidate for " + stack.toString() + ": " + match.toString() + " size=" + lookupSizeResult + ", inverse=" + comp.toString());
                    } else if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                        FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "Back-check failed for " + match.toString() + " size=" + lookupSizeResult + ", inverse=" + comp.toString());
                }
            }
        }

        ItemStack modMatch = findMatchingModCandidate(stack, candidates);
        if (!modMatch.isEmpty()) {
            lookupSizeResult = candidatesRate.get(modMatch);
            return modMatch;
        }

        if (candidates.size() > 0) {
            ItemStack match = candidates.get(0);
            lookupSizeResult = candidatesRate.get(match);

            return match;
        }

        if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "No candidates found");

        return ItemStack.EMPTY;
    }

    @Nonnull
    private ItemStack findMatchingModCandidate (@Nonnull ItemStack reference, List<ItemStack> candidates) {
        ResourceLocation referenceName = reference.getItem().getRegistryName();
        if (referenceName != null) {
            for (ItemStack candidate : candidates) {
                ResourceLocation matchName = candidate.getItem().getRegistryName();
                if (matchName != null) {
                    if (referenceName.getResourceDomain().equals(matchName.getResourceDomain()))
                        return candidate;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    private ItemStack tryMatch (@Nonnull ItemStack stack, List list) {
        if (list.size() != 9 && list.size() != 4)
            return ItemStack.EMPTY;

        Object item = list.get(0);
        if (item instanceof ItemStack) {
            ItemStack item1 = (ItemStack)item;
            for (int i = 1, n = list.size(); i < n; i++) {
                Object item2 = list.get(i);
                if (item2.getClass() != ItemStack.class)
                    return ItemStack.EMPTY;
                if (!item1.isItemEqual((ItemStack)item2))
                    return ItemStack.EMPTY;
            }
            return item1;
        }
        else if (item instanceof List) {
            for (int i = 1, n = list.size(); i < n; i++) {
                if (item != list.get(i))
                    return ItemStack.EMPTY;
            }

            List itemList = (List)item;
            if (itemList.size() > 0) {
                Object item1 = itemList.get(0);
                if (item1 instanceof ItemStack)
                    return (ItemStack)item1;
            }
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    private ItemStack tryMatch (@Nonnull ItemStack stack, Object[] list) {
        if (list.length != 9 && list.length != 4)
            return ItemStack.EMPTY;

        Object item = list[0];
        if (item == null)
            return ItemStack.EMPTY;

        if (item instanceof ItemStack) {
            ItemStack item1 = (ItemStack)item;
            for (int i = 1, n = list.length; i < n; i++) {
                Object item2 = list[i];
                if (item2 == null || item2.getClass() != ItemStack.class)
                    return ItemStack.EMPTY;
                if (!item1.isItemEqual((ItemStack)item2))
                    return ItemStack.EMPTY;
            }
            return item1;
        }
        else if (item instanceof Ingredient) {
            Ingredient ingItem = (Ingredient)item;
            ItemStack[] ingItemMatchingStacks = ingItem.getMatchingStacks();
            if (ingItemMatchingStacks.length == 0)
                return ItemStack.EMPTY;

            for (int i = 1, n = list.length; i < n; i++) {
                if (item.getClass() != list[i].getClass())
                    return ItemStack.EMPTY;

                Ingredient ingredient = (Ingredient)list[i];
                ItemStack match = ItemStack.EMPTY;
                for (ItemStack ingItemMatch : ingItemMatchingStacks) {
                    if (ingredient.apply(ingItemMatch)) {
                        match = ingItemMatch;
                        break;
                    }
                }

                if (match.isEmpty())
                    return ItemStack.EMPTY;
            }

            ItemStack match = findMatchingModCandidate(stack, Arrays.asList(ingItemMatchingStacks));
            if (match.isEmpty())
                match = ingItemMatchingStacks[0];

            return match;
        }
        else if (item instanceof List) {
            for (int i = 1, n = list.length; i < n; i++) {
                if (item != list[i])
                    return ItemStack.EMPTY;
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
                return ItemStack.EMPTY;

            ItemStack item1 = handler.getItemStack(item);
            if (item1.isEmpty())
                return ItemStack.EMPTY;

            for (int i = 1, n = list.length; i < n; i++) {
                Object item2 = list[i];
                if (item2 == null || item.getClass() != item2.getClass())
                    return ItemStack.EMPTY;

                item2 = handler.getItemStack(item2);
                if (item2 == null || item2.getClass() != ItemStack.class)
                    return ItemStack.EMPTY;
                if (!item1.isItemEqual((ItemStack)item2))
                    return ItemStack.EMPTY;
            }

            return item1;
        }

        return ItemStack.EMPTY;
    }

    private void setupLookup (InventoryLookup inv, @Nonnull ItemStack stack) {
        for (int i = 0, n = inv.getSizeInventory(); i < n; i++)
            inv.setInventorySlotContents(i, stack);

        lookupSizeResult = inv.getSizeInventory();
    }

    private class CompCentralInventory implements ICentralInventory
    {
        // TODO: More consistent handling of index 0/slot and some once-only logging
        
        @Override
        @Nonnull
        public ItemStack getStoredItemPrototype (int slot) {
            return protoStack[slot];
        }

        @Override
        public IDrawer setStoredItem (int slot, @Nonnull ItemStack itemPrototype, int amount) {
            boolean itemValid = !itemPrototype.isEmpty();
            if (itemValid && convRate != null && convRate[0] == 0) {
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

                markBlockForUpdate();
                return target;
            }
            else if (!itemValid && isDrawerEnabled(slot)) {
                pooledCount = 0;
                clear();
                markBlockForUpdate();
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
                    markBlockForUpdate();
                }

                if (!getWorld().isRemote && isRedstone()) {
                    IBlockState state = getWorld().getBlockState(getPos());
                    getWorld().notifyNeighborsOfStateChange(getPos(), state.getBlock(), false);
                    getWorld().notifyNeighborsOfStateChange(getPos().down(), state.getBlock(), false);
                }
            }
        }

        @Override
        public int getMaxCapacity (int slot) {
            if (protoStack[0].isEmpty() || protoStack[slot].isEmpty() || convRate == null || convRate[slot] == 0)
                return 0;

            if (TileEntityDrawersComp.this.isUnlimited() || TileEntityDrawersComp.this.isVending()) {
                if (convRate == null || protoStack[slot].isEmpty() || convRate[slot] == 0)
                    return Integer.MAX_VALUE;
                return Integer.MAX_VALUE / convRate[slot];
            }

            return protoStack[0].getItem().getItemStackLimit(protoStack[0]) * getStackCapacity(0) * getConversionRate(slot);
        }

        @Override
        public int getMaxCapacity (int slot, @Nonnull ItemStack itemPrototype) {
            if (itemPrototype.isEmpty())
                return 0;

            if (TileEntityDrawersComp.this.isUnlimited() || TileEntityDrawersComp.this.isVending()) {
                if (convRate == null || protoStack[slot].isEmpty() || convRate[slot] == 0)
                    return Integer.MAX_VALUE;
                return Integer.MAX_VALUE / convRate[slot];
            }

            if (convRate == null || protoStack[0].isEmpty() || convRate[0] == 0)
                return itemPrototype.getItem().getItemStackLimit(itemPrototype) * getBaseStackCapacity();

            if (BaseDrawerData.areItemsEqual(protoStack[slot], itemPrototype))
                return getMaxCapacity(slot);

            return 0;
        }

        @Override
        public int getDefaultMaxCapacity (int slot) {
            if (!isDrawerEnabled(slot))
                return 0;

            if (TileEntityDrawersComp.this.isUnlimited() || TileEntityDrawersComp.this.isVending())
                return Integer.MAX_VALUE;

            return 64 * getBaseStackCapacity();
        }

        @Override
        public int getRemainingCapacity (int slot) {
            if (protoStack[0].isEmpty() || protoStack[slot].isEmpty() || convRate == null || convRate[slot] == 0)
                return 0;
            if (TileEntityDrawersComp.this.isVending())
                return Integer.MAX_VALUE;

            int rawMaxCapacity = protoStack[0].getItem().getItemStackLimit(protoStack[0]) * getStackCapacity(0) * convRate[0];
            int rawRemaining = rawMaxCapacity - pooledCount;

            return rawRemaining / convRate[slot];
        }

        @Override
        public int getStoredItemStackSize (int slot) {
            if (protoStack[slot].isEmpty() || convRate == null || convRate[slot] == 0)
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
            if (protoStack[0].isEmpty() || protoStack[slot].isEmpty() || convRate == null || convRate[slot] == 0)
                return 0;

            return convRate[0] / convRate[slot];
        }

        @Override
        public int getStoredItemRemainder (int slot) {
            return TileEntityDrawersComp.this.getStoredItemRemainder(slot);
        }

        @Override
        public boolean isSmallestUnit (int slot) {
            if (protoStack[slot].isEmpty() || convRate == null || convRate[slot] == 0)
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
        public boolean isSlotShowingQuantity (int slot) {
            return isShowingQuantity();
        }

        @Override
        public boolean setIsSlotShowingQuantity (int slot, boolean state) {
            return setIsShowingQuantity(state);
        }

        @Override
        public boolean isLocked (int slot, LockAttribute attr) {
            return TileEntityDrawersComp.this.isItemLocked(attr);
        }

        @Override
        public void writeToNBT (int slot, NBTTagCompound tag) {
            ItemStack protoStack = getStoredItemPrototype(slot);
            if (!protoStack.isEmpty()) {
                tag.setShort("Item", (short) Item.getIdFromItem(protoStack.getItem()));
                tag.setShort("Meta", (short) protoStack.getItemDamage());

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
                protoStack[i] = ItemStack.EMPTY;
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
            return TileEntityDrawersComp.this.getEffectiveStorageMultiplier() * TileEntityDrawersComp.this.getEffectiveDrawerCapacity();
        }

        public void markAmountDirty () {
            if (getWorld().isRemote)
                return;

            IMessage message = new CountUpdateMessage(getPos(), 0, pooledCount);
            NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 500);

            StorageDrawers.network.sendToAllAround(message, targetPoint);

            TileEntityDrawersComp.this.markDirty();
        }

        public void markDirty (int slot) {
            markBlockForUpdate();
        }
    }

    private static class InventoryLookup extends InventoryCrafting
    {
        private ItemStack[] stackList;

        public InventoryLookup (int width, int height) {
            super(null, width, height);

            stackList = new ItemStack[width * height];
            for (int i = 0; i < stackList.length; i++)
                stackList[i] = ItemStack.EMPTY;
        }

        @Override
        public int getSizeInventory ()
        {
            return this.stackList.length;
        }

        @Override
        @Nonnull
        public ItemStack getStackInSlot (int slot)
        {
            return slot >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList[slot];
        }

        @Override
        @Nonnull
        public ItemStack removeStackFromSlot (int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        @Nonnull
        public ItemStack decrStackSize (int slot, int count) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setInventorySlotContents (int slot, @Nonnull ItemStack stack) {
            stackList[slot] = stack;
        }
    }
}
