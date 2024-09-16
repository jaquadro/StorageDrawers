package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.capabilities.Capabilities;
import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import com.jaquadro.minecraft.storagedrawers.util.CompactingHelper;
import com.jaquadro.minecraft.storagedrawers.util.ItemStackMatcher;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;
import java.util.function.Predicate;

public class FractionalDrawerGroup extends BlockEntityDataShim implements IDrawerGroup
{
    private final FractionalStorage storage;
    private final FractionalDrawer[] slots;
    private final int[] order;

    public FractionalDrawerGroup (int slotCount) {

        storage = new FractionalStorage(this, slotCount);

        slots = new FractionalDrawer[slotCount];
        order = new int[slotCount];

        for (int i = 0; i < slotCount; i++) {
            slots[i] = new FractionalDrawer(storage, i);
            order[i] = i;
        }
    }

    @Override
    public int getDrawerCount () {
        return slots.length;
    }

    @NotNull
    @Override
    public IFractionalDrawer getDrawer (int slot) {
        if (slot < 0 || slot >= slots.length)
            return Drawers.DISABLED_FRACTIONAL;

        return slots[slot];
    }

    @Override
    public int[] getAccessibleDrawerSlots () {
        return order;
    }

    public int getPooledCount () {
        return storage.getPooledCount();
    }

    public void setPooledCount (int count) {
        storage.setPooledCount(count);
    }

    @Override
    public void read (HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("Drawers"))
            storage.deserializeNBT(provider, tag.getCompound("Drawers"));
    }

    @Override
    public CompoundTag write (HolderLookup.Provider provider, CompoundTag tag) {
        tag.put("Drawers", storage.serializeNBT(provider));
        return tag;
    }

    public void syncAttributes () {
        storage.syncAttributes();
    }

    protected Level getWorld () { return null; }

    protected void log (String message) { }

    protected int getStackCapacity () {
        return 0;
    }

    protected void onItemChanged () { }

    protected void onAmountChanged () { }

    private static class FractionalStorage
    {
        private final FractionalDrawerGroup group;
        private final int slotCount;
        private final ItemStack[] protoStack;
        private final int[] convRate;
        private final ItemStackMatcher[] matchers;
        private int pooledCount;

        private ItemStack cacheKey;
        private final ItemStack[] cachedProtoStack;
        private final int[] cachedConvRate;
        private final ItemStackMatcher[] cachedMatchers;

        IDrawerAttributes cachedAttrs;

        public FractionalStorage (FractionalDrawerGroup group, int slotCount) {
            cacheKey = ItemStack.EMPTY;

            this.group = group;
            this.slotCount = slotCount;

            protoStack = new ItemStack[slotCount];
            matchers = new ItemStackMatcher[slotCount];

            cachedProtoStack = new ItemStack[slotCount];
            cachedMatchers = new ItemStackMatcher[slotCount];

            for (int i = 0; i < slotCount; i++) {
                protoStack[i] = ItemStack.EMPTY;
                matchers[i] = ItemStackMatcher.EMPTY;

                cachedProtoStack[i] = ItemStack.EMPTY;
                cachedMatchers[i] = ItemStackMatcher.EMPTY;
            }

            convRate = new int[slotCount];
            cachedConvRate = new int[slotCount];
        }

        @NotNull
        IDrawerAttributes getAttributes() {
            if (cachedAttrs != null)
                return cachedAttrs;

            cachedAttrs = group.getCapability(Capabilities.DRAWER_ATTRIBUTES);
            if (cachedAttrs != null)
                return cachedAttrs;

            return EmptyDrawerAttributes.EMPTY;
        }

        public int getPooledCount () {
            return pooledCount;
        }

        public void setPooledCount (int count) {
            if (pooledCount != count) {
                pooledCount = count;
                group.onAmountChanged();
            }
        }

        @NotNull
        public ItemStack getStack (int slot) {
            return protoStack[slot];
        }

        @NotNull
        public ItemStack baseStack () {
            return protoStack[0];
        }

        public int baseRate () {
            return convRate[0];
        }

        public IFractionalDrawer setStoredItem (int slot, @NotNull ItemStack itemPrototype) {
            itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
            if (itemPrototype.isEmpty()) {
                reset();
                return group.getDrawer(slot);
            }

            if (baseRate() == 0) {
                populateSlots(itemPrototype);
                for (int i = 0; i < slotCount; i++) {
                    if (ItemStackMatcher.areItemsEqual(protoStack[i], itemPrototype)) { // TODO: ItemStackOreMatcher
                        slot = i;
                        pooledCount = 0;
                    }
                }

                group.onItemChanged();
            }

            return group.getDrawer(slot);
        }

        public int getStoredCount (int slot) {
            if (convRate[slot] == 0)
                return 0;

            IDrawerAttributes attrs = getAttributes();
            if (attrs.isUnlimitedVending())
                return Integer.MAX_VALUE;

            return pooledCount / convRate[slot];
        }

        public void setStoredItemCount (int slot, int amount) {
            if (convRate[slot] == 0)
                return;

            IDrawerAttributes attrs = getAttributes();
            if (attrs.isUnlimitedVending())
                return;

            int oldCount = pooledCount;

            pooledCount = (pooledCount % convRate[slot]) + convRate[slot] * amount;
            pooledCount = Math.min(pooledCount, getMaxCapacity(0) * convRate[0]);
            pooledCount = Math.max(pooledCount, 0);

            if (pooledCount == oldCount)
                return;

            if (pooledCount == 0 && !attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
                reset();
            else
                group.onAmountChanged();
        }

        public int adjustStoredItemCount (int slot, int amount) {
            if (convRate[slot] == 0 || amount == 0)
                return Math.abs(amount);

            IDrawerAttributes attrs = getAttributes();
            if (amount > 0) {
                if (attrs.isUnlimitedVending())
                    return 0;

                int poolMax = getMaxCapacity(0) * convRate[0];
                if (poolMax < 0)
                    poolMax = Integer.MAX_VALUE;

                int canAdd = (poolMax - pooledCount) / convRate[slot];
                int willAdd = Math.min(amount, canAdd);
                if (willAdd > 0) {
                    pooledCount += convRate[slot] * willAdd;
                    group.onAmountChanged();
                }

                if (attrs.isVoid())
                    return 0;

                return amount - willAdd;
            }
            else {
                amount = -amount;

                int canRemove = pooledCount / convRate[slot];
                int willRemove = Math.min(amount, canRemove);
                if (willRemove == 0)
                    return amount;

                pooledCount -= willRemove * convRate[slot];

                if (pooledCount == 0 && !attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
                    reset();
                else
                    group.onAmountChanged();

                return amount - willRemove;
            }
        }

        public int getMaxCapacity (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return 0;

            IDrawerAttributes attrs = getAttributes();
            if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
                return Integer.MAX_VALUE / convRate[slot];

            int maxSize = ItemStackHelper.getMaxStackSize(baseStack());
            return maxSize * group.getStackCapacity() * (baseRate() / convRate[slot]);
        }

        public int getMaxCapacity (int slot, @NotNull ItemStack itemPrototype) {
            IDrawerAttributes attrs = getAttributes();
            if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending()) {
                if (convRate[slot] == 0)
                    return Integer.MAX_VALUE;
                return Integer.MAX_VALUE / convRate[slot];
            }

            if (baseStack().isEmpty()) {
                int itemStackLimit = 64;
                if (!itemPrototype.isEmpty())
                    itemStackLimit = ItemStackHelper.getMaxStackSize(itemPrototype);
                return itemStackLimit * group.getStackCapacity();
            }

            if (ItemStackMatcher.areItemsEqual(protoStack[slot], itemPrototype)) // TODO: ItemStackOreMatcher
                return getMaxCapacity(slot);

            return 0;
        }

        public int getAcceptingMaxCapacity (int slot, @NotNull ItemStack itemPrototype) {
            IDrawerAttributes attrs = getAttributes();
            if (attrs.isVoid())
                return Integer.MAX_VALUE;

            return getMaxCapacity(slot, itemPrototype);
        }

        public int getRemainingCapacity (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return 0;

            IDrawerAttributes attrs = getAttributes();
            if (attrs.isUnlimitedVending())
                return Integer.MAX_VALUE;

            int rawMaxCapacity = getMaxCapacity(0) * baseRate();
            int rawRemaining = rawMaxCapacity - pooledCount;

            return rawRemaining / convRate[slot];
        }

        public int getAcceptingRemainingCapacity (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return 0;

            IDrawerAttributes attrs = getAttributes();
            if (attrs.isUnlimitedVending() || attrs.isVoid())
                return Integer.MAX_VALUE;

            int rawMaxCapacity = getMaxCapacity(0) * baseRate();
            int rawRemaining = rawMaxCapacity - pooledCount;

            return rawRemaining / convRate[slot];
        }

        public boolean isEmpty (int slot) {
            return protoStack[slot].isEmpty();
        }

        public boolean isEnabled (int slot) {
            if (baseStack().isEmpty())
                return true;

            return !protoStack[slot].isEmpty();
        }

        public boolean canItemBeStored (int slot, @NotNull ItemStack itemPrototype, Predicate<ItemStack> predicate) {
            IDrawerAttributes attrs = getAttributes();
            if (protoStack[slot].isEmpty() && protoStack[0].isEmpty() && !attrs.isItemLocked(LockAttribute.LOCK_EMPTY))
                return true;

            if (predicate == null)
                return matchers[slot].matches(itemPrototype);
            return predicate.test(protoStack[slot]);
        }

        public boolean canItemBeExtracted (int slot, @NotNull ItemStack itemPrototype, Predicate<ItemStack> predicate) {
            if (protoStack[slot].isEmpty())
                return false;

            if (predicate == null)
                return matchers[slot].matches(itemPrototype);
            return predicate.test(protoStack[slot]);
        }

        public int getConversionRate (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return 0;

            return convRate[0] / convRate[slot];
        }

        public int getStoredItemRemainder (int slot) {
            if (convRate[slot] == 0)
                return 0;

            if (slot == 0)
                return pooledCount / baseRate();

            return (pooledCount / convRate[slot]) % (convRate[slot - 1] / convRate[slot]);
        }

        public boolean isSmallestUnit (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return false;

            return convRate[slot] == 1;
        }

        private void reset () {
            pooledCount = 0;

            for (int i = 0; i < slotCount; i++) {
                protoStack[i] = ItemStack.EMPTY;
                matchers[i] = ItemStackMatcher.EMPTY;
                convRate[i] = 0;
            }

            group.onItemChanged();
        }

        private void populateSlotsFromCache() {
            for (int slot = 0; slot < slotCount; slot++) {
                protoStack[slot] = cachedProtoStack[slot];
                convRate[slot] = cachedConvRate[slot];
                matchers[slot] = cachedMatchers[slot];
            }
        }

        private void populateSlots (@NotNull ItemStack itemPrototype) {
            Level world = group.getWorld();
            if (world == null) {
                protoStack[0] = itemPrototype;
                convRate[0] = 1;
                matchers[0] = new ItemStackMatcher(protoStack[0]);
                //matchers[0] = attrs.isDictConvertible()
                //    ? new ItemStackOreMatcher(protoStack[0])
                //    : new ItemStackMatcher(protoStack[0]);

                return;
            }

            // If a drawer cleared and is re-populated with the same initial item, restore that from memory
            // A drawer emptying and filling with the same item is a common expensive degenerate case
            if (ItemStackMatcher.areItemsEqual(itemPrototype, cacheKey)) {
                populateSlotsFromCache();
                return;
            }

            cacheKey = itemPrototype;

            CompactingHelper compacting = new CompactingHelper(world);
            Stack<CompactingHelper.Result> resultStack = new Stack<>();

            ItemStack lookupTarget = itemPrototype;
            for (int i = 0; i < slotCount - 1; i++) {
                CompactingHelper.Result lookup = compacting.findHigherTier(lookupTarget);
                if (lookup.getStack().isEmpty())
                    break;

                resultStack.push(lookup);
                lookupTarget = lookup.getStack();
            }

            int index = 0;
            for (int n = resultStack.size(); index < n; index++) {
                CompactingHelper.Result result = resultStack.pop();
                populateRawSlot(index, result.getStack(), result.getSize());
                group.log("Picked candidate " + result.getStack().toString() + " with conv=" + result.getSize());

                for (int i = 0; i < index; i++) {
                    convRate[i] *= result.getSize();
                    cachedConvRate[i] = convRate[i];
                }
            }

            if (index == slotCount)
                return;

            populateRawSlot(index++, itemPrototype, 1);

            lookupTarget = itemPrototype;
            for (; index < slotCount; index++) {
                CompactingHelper.Result lookup = compacting.findLowerTier(lookupTarget);
                ItemStack itemStack = lookup.getStack();
                if (!itemStack.isEmpty()) {
                    populateRawSlot(index, itemStack, 1);
                    group.log("Picked candidate " + itemStack + " with conv=" + lookup.getSize());

                    for (int i = 0; i < index; i++) {
                        convRate[i] *= lookup.getSize();
                        cachedConvRate[i] = convRate[i];
                    }
                } else {
                    populateRawSlot(index, ItemStack.EMPTY, 0);
                }
                lookupTarget = itemStack;
            }
        }

        private void populateRawSlot (int slot, @NotNull ItemStack itemPrototype, int rate) {
            protoStack[slot] = itemPrototype;
            convRate[slot] = rate;
            matchers[slot] = new ItemStackMatcher(protoStack[slot]);

            cachedProtoStack[slot] = itemPrototype;
            cachedConvRate[slot] = rate;
            cachedMatchers[slot] = matchers[slot];

            //matchers[slot] = attrs.isDictConvertible()
            //    ? new ItemStackOreMatcher(protoStack[slot])
            //    : new ItemStackMatcher(protoStack[slot]);
        }

        private void normalizeGroup () {
            for (int limit = slotCount - 1; limit > 0; limit--) {
                for (int i = 0; i < limit; i++) {
                    if (protoStack[i].isEmpty()) {
                        protoStack[i] = protoStack[i + 1];
                        matchers[i] = matchers[i + 1];
                        convRate[i] = convRate[i + 1];

                        protoStack[i + 1] = ItemStack.EMPTY;
                        matchers[i + 1] = ItemStackMatcher.EMPTY;
                        convRate[i + 1] = 0;
                    }
                }
            }

            int minConvRate = Integer.MAX_VALUE;
            for (int i = 0; i < slotCount; i++) {
                if (convRate[i] > 0)
                    minConvRate = Math.min(minConvRate, convRate[i]);
            }

            if (minConvRate > 1) {
                for (int i = 0; i < slotCount; i++)
                    convRate[i] /= minConvRate;

                pooledCount /= minConvRate;
            }
        }

        public CompoundTag serializeNBT (HolderLookup.Provider provider) {
            ListTag itemList = new ListTag();
            for (int i = 0; i < slotCount; i++) {
                if (protoStack[i].isEmpty())
                    continue;

                CompoundTag itemTag = new CompoundTag();
                itemTag = (CompoundTag)protoStack[i].save(provider, itemTag);

                CompoundTag slotTag = new CompoundTag();
                slotTag.putByte("Slot", (byte)i);
                slotTag.putInt("Conv", convRate[i]);
                slotTag.put("Item", itemTag);

                itemList.add(slotTag);
            }

            CompoundTag tag = new CompoundTag();
            tag.putInt("Count", pooledCount);
            tag.put("Items", itemList);

            return tag;
        }

        public void deserializeNBT (HolderLookup.Provider provider, CompoundTag tag) {
            for (int i = 0; i < slotCount; i++) {
                protoStack[i] = ItemStack.EMPTY;
                matchers[i] = ItemStackMatcher.EMPTY;
                convRate[i] = 0;
            }

            pooledCount = tag.getInt("Count");

            ListTag itemList = tag.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < itemList.size(); i++) {
                CompoundTag slotTag = itemList.getCompound(i);
                int slot = slotTag.getByte("Slot");

                protoStack[slot] = ItemStack.parseOptional(provider, slotTag.getCompound("Item"));
                convRate[slot] = slotTag.getByte("Conv");

                matchers[slot] = new ItemStackMatcher(protoStack[slot]);
                //matchers[slot] = attrs.isDictConvertible()
                //    ? new ItemStackOreMatcher(protoStack[slot])
                //    : new ItemStackMatcher(protoStack[slot]);
            }

            // TODO: We should only need to normalize if we had blank items with a conv rate, but this fixes blocks that were saved broken
            normalizeGroup();

            // Check if cache needs to be invalidated
            if (itemList.size() > 0) {
                boolean cacheMatch = true;
                for (int i = 0; i < slotCount; i++) {
                    cacheMatch &= ItemStackMatcher.areItemsEqual(protoStack[i], cachedProtoStack[i]);
                    cacheMatch &= convRate[i] == cachedConvRate[i];
                }

                if (!cacheMatch)
                    cacheKey = ItemStack.EMPTY;
            }
        }

        public void syncAttributes () {
            for (int i = 0; i < slotCount; i++) {
                if (!protoStack[i].isEmpty()) {
                    matchers[i] = new ItemStackMatcher(protoStack[i]);
                    //matchers[i] = attrs.isDictConvertible()
                    //    ? new ItemStackOreMatcher(protoStack[i])
                    //    : new ItemStackMatcher(protoStack[i]);
                }
            }
        }
    }

    private static class FractionalDrawer implements IFractionalDrawer {
        private final FractionalStorage storage;
        private final int slot;

        private FractionalDrawer(
                FractionalStorage storage,
                int slot) {
            this.storage = storage;
            this.slot = slot;
        }

        @NotNull
        @Override
        public ItemStack getStoredItemPrototype() {
            return storage.getStack(slot);
        }

        @NotNull
        @Override
        public IDrawer setStoredItem(@NotNull ItemStack itemPrototype) {
            if (ItemStackHelper.isStackEncoded(itemPrototype))
                itemPrototype = ItemStackHelper.decodeItemStackPrototype(itemPrototype);

            return storage.setStoredItem(slot, itemPrototype);
        }

        @Override
        public int getStoredItemCount() {
            return storage.getStoredCount(slot);
        }

        @Override
        public void setStoredItemCount(int amount) {
            storage.setStoredItemCount(slot, amount);
        }

        @Override
        public int adjustStoredItemCount(int amount) {
            return storage.adjustStoredItemCount(slot, amount);
        }

        @Override
        public int getMaxCapacity() {
            return storage.getMaxCapacity(slot);
        }

        @Override
        public int getMaxCapacity(@NotNull ItemStack itemPrototype) {
            return storage.getMaxCapacity(slot, itemPrototype);
        }

        @Override
        public int getAcceptingMaxCapacity(@NotNull ItemStack itemPrototype) {
            return storage.getAcceptingMaxCapacity(slot, itemPrototype);
        }

        @Override
        public int getRemainingCapacity() {
            return storage.getRemainingCapacity(slot);
        }

        @Override
        public int getAcceptingRemainingCapacity() {
            return storage.getAcceptingRemainingCapacity(slot);
        }

        @Override
        public boolean canItemBeStored(@NotNull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            return storage.canItemBeStored(slot, itemPrototype, matchPredicate);
        }

        @Override
        public boolean canItemBeExtracted(@NotNull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            return storage.canItemBeExtracted(slot, itemPrototype, matchPredicate);
        }

        @Override
        public boolean isEmpty() {
            return storage.isEmpty(slot);
        }

        @Override
        public boolean isEnabled() {
            return storage.isEnabled(slot);
        }

        @Override
        public int getConversionRate() {
            return storage.getConversionRate(slot);
        }

        @Override
        public int getStoredItemRemainder() {
            return storage.getStoredItemRemainder(slot);
        }

        @Override
        public boolean isSmallestUnit() {
            return storage.isSmallestUnit(slot);
        }
    }
}
