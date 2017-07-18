package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.chameleon.block.tiledata.TileDataShim;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemRepository;
import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import com.jaquadro.minecraft.storagedrawers.util.ItemStackMatcher;
import com.jaquadro.minecraft.storagedrawers.util.ItemStackOreMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public abstract class StandardDrawerGroup extends TileDataShim implements IDrawerGroup
{
    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;
    @CapabilityInject(IItemRepository.class)
    public static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = null;

    private DrawerData[] slots;
    private int[] order;

    private final IItemHandler itemHandler;
    private final IItemRepository itemRepository;

    public StandardDrawerGroup (int slotCount) {
        itemHandler = new DrawerItemHandler(this);
        itemRepository = new DrawerItemRepository(this);

        slots = new DrawerData[slotCount];
        for (int i = 0; i < slotCount; i++)
            slots[i] = createDrawer(i);

        order = new int[slotCount];
        syncSlots();
    }

    public void setCapabilityProvider (ICapabilityProvider capProvider) {
        for (DrawerData slot : slots)
            slot.setCapabilityProvider(capProvider);
    }

    @Override
    public int getDrawerCount () {
        return slots.length;
    }

    @Nonnull
    @Override
    public IDrawer getDrawer (int slot) {
        if (slot < 0 || slot >= slots.length)
            return Drawers.DISABLED;

        return slots[slot];
    }

    @Nonnull
    @Override
    public int[] getAccessibleDrawerSlots () {
        return order;
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        if (!tag.hasKey("Drawers")) {
            if (tag.hasKey("Slots"))
                readFromLegacyNBT(tag);
            return;
        }

        NBTTagList itemList = tag.getTagList("Drawers", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < itemList.tagCount(); i++) {
            if (i >= 0 && i < slots.length)
                slots[i].deserializeNBT(itemList.getCompoundTagAt(i));
        }
    }

    public void readFromLegacyNBT (NBTTagCompound tag) {
        NBTTagList slotTags = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);

        DrawerData[] realSlots = new DrawerData[slotTags.tagCount()];
        for (int i = 0; i < realSlots.length && i < slots.length; i++)
            realSlots[i] = slots[i];

        slots = realSlots;

        for (int i = 0; i < slots.length; i++)
            slots[i].deserializeLegacyNBT(slotTags.getCompoundTagAt(i));
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound tag) {
        if (slots == null)
            return tag;

        NBTTagList itemList = new NBTTagList();
        for (DrawerData slot : slots)
            itemList.appendTag(slot.serializeNBT());

        tag.setTag("Drawers", itemList);

        return tag;
    }

    @Override
    public boolean hasCapability (@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == ITEM_HANDLER_CAPABILITY
            || capability == ITEM_REPOSITORY_CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability (@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        if (capability == ITEM_REPOSITORY_CAPABILITY)
            return (T) itemRepository;

        return null;
    }

    @Nonnull
    protected abstract DrawerData createDrawer (int slot);

    public void syncAttributes () {
        for (DrawerData drawer : slots)
            drawer.syncAttributes();

    }

    public void syncSlots () {
        int index = 0;
        for (int i = 0; i < slots.length; i++) {
            IDrawer drawer = getDrawer(i);
            if (!drawer.isEmpty())
                order[index++] = i;
        }

        if (index != slots.length) {
            for (int i = 0; i < slots.length; i++) {
                IDrawer drawer = getDrawer(i);
                if (drawer.isEnabled() && drawer.isEmpty())
                    order[index++] = i;
            }
        }

        if (index != slots.length) {
            for (int i = 0; i < slots.length; i++) {
                IDrawer drawer = getDrawer(i);
                if (!drawer.isEnabled())
                    order[index++] = i;
            }
        }
    }

    public static class DrawerData implements IDrawer, INBTSerializable<NBTTagCompound>
    {
        @CapabilityInject(IDrawerAttributes.class)
        static Capability<IDrawerAttributes> ATTR_CAPABILITY = null;

        IDrawerAttributes attrs;
        StandardDrawerGroup group;

        @Nonnull
        private ItemStack protoStack;
        private int count;
        private ItemStackMatcher matcher;

        public DrawerData (StandardDrawerGroup group) {
            this.group = group;
            attrs = new EmptyDrawerAttributes();
            protoStack = ItemStack.EMPTY;
            matcher = ItemStackMatcher.EMPTY;
        }

        public void setCapabilityProvider (ICapabilityProvider capProvider) {
            IDrawerAttributes capAttrs = capProvider.getCapability(ATTR_CAPABILITY, null);
            if (capAttrs != null)
                attrs = capAttrs;
        }

        @Override
        @Nonnull
        public ItemStack getStoredItemPrototype () {
            return protoStack;
        }

        @Override
        @Nonnull
        public IDrawer setStoredItem (@Nonnull ItemStack itemPrototype) {
            return setStoredItem(itemPrototype, true);
        }

        protected IDrawer setStoredItem (@Nonnull ItemStack itemPrototype, boolean notify) {
            if (matcher.matches(itemPrototype)) {
                return this;
            }

            itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
            if (itemPrototype.isEmpty()) {
                reset(notify);
                return this;
            }

            protoStack = itemPrototype;
            protoStack.setCount(1);
            count = 0;

            if (attrs.isDictConvertible())
                matcher = new ItemStackOreMatcher(protoStack);
            else
                matcher = new ItemStackMatcher(protoStack);

            group.syncSlots();
            if (notify)
                onItemChanged();

            return this;
        }

        protected IDrawer setStoredItemRaw (@Nonnull ItemStack itemPrototype) {
            itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
            protoStack = itemPrototype;
            protoStack.setCount(1);
            count = 0;

            if (attrs.isDictConvertible())
                matcher = new ItemStackOreMatcher(protoStack);
            else
                matcher = new ItemStackMatcher(protoStack);

            return this;
        }

        @Override
        public int getStoredItemCount () {
            if (protoStack.isEmpty())
                return 0;

            if (attrs.isUnlimitedVending())
                return Integer.MAX_VALUE;

            return count;
        }

        @Override
        public void setStoredItemCount (int amount) {
            setStoredItemCount(amount, true);
        }

        protected void setStoredItemCount (int amount, boolean notify) {
            if (protoStack.isEmpty() || count == amount)
                return;

            if (attrs.isUnlimitedVending())
                return;

            count = Math.min(amount, getMaxCapacity());
            count = Math.max(count, 0);

            if (count == 0 && !attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
                reset(notify);
            else {
                if (notify)
                    onAmountChanged();
            }
        }

        protected void setStoredItemCountRaw (int amount) {
            count = amount;
        }

        @Override
        public int adjustStoredItemCount (int amount) {
            return adjustStoredItemCount(amount, true);
        }

        protected int adjustStoredItemCount (int amount, boolean notify) {
            if (protoStack.isEmpty() || amount == 0)
                return Math.abs(amount);

            if (amount > 0) {
                if (attrs.isUnlimitedVending())
                    return 0;

                int originalCount = count;
                count = Math.min(count + amount, getMaxCapacity());

                if (count != originalCount && notify)
                    onAmountChanged();

                if (attrs.isVoid())
                    return 0;

                return amount - (count - originalCount);
            }
            else {
                int originalCount = count;
                setStoredItemCount(originalCount + amount, notify);

                return -amount - (originalCount - count);
            }
        }

        @Override
        public int getMaxCapacity (@Nonnull ItemStack itemPrototype) {
            if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
                return Integer.MAX_VALUE;

            if (itemPrototype.isEmpty())
                return 64 * getStackCapacity();

            return itemPrototype.getItem().getItemStackLimit(itemPrototype) * getStackCapacity();
        }

        @Override
        public int getAcceptingMaxCapacity (@Nonnull ItemStack itemPrototype) {
            if (attrs.isVoid())
                return Integer.MAX_VALUE;

            return getMaxCapacity(itemPrototype);
        }

        @Override
        public int getRemainingCapacity () {
            if (protoStack.isEmpty())
                return 0;

            if (attrs.isUnlimitedVending())
                return Integer.MAX_VALUE;

            return getMaxCapacity() - getStoredItemCount();
        }

        @Override
        public int getAcceptingRemainingCapacity () {
            if (protoStack.isEmpty())
                return 0;

            if (attrs.isUnlimitedVending() || attrs.isVoid())
                return Integer.MAX_VALUE;

            return getMaxCapacity() - getStoredItemCount();
        }

        @Override
        public boolean canItemBeStored (@Nonnull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            if (protoStack.isEmpty() && !attrs.isItemLocked(LockAttribute.LOCK_EMPTY))
                return true;

            if (matchPredicate == null)
                return matcher.matches(itemPrototype);
            return matchPredicate.test(protoStack);
        }

        @Override
        public boolean canItemBeExtracted (@Nonnull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            if (protoStack.isEmpty())
                return false;

            if (matchPredicate == null)
                return matcher.matches(itemPrototype);
            return matchPredicate.test(protoStack);
        }

        @Override
        public boolean isEmpty () {
            return protoStack.isEmpty();
        }

        protected void reset (boolean notify) {
            protoStack = ItemStack.EMPTY;
            count = 0;
            matcher = ItemStackMatcher.EMPTY;

            group.syncSlots();
            if (notify)
                onItemChanged();
        }

        @Override
        public NBTTagCompound serializeNBT () {
            NBTTagCompound tag = new NBTTagCompound();
            if (protoStack.isEmpty())
                return tag;

            NBTTagCompound item = new NBTTagCompound();
            protoStack.writeToNBT(item);

            tag.setTag("Item", item);
            tag.setInteger("Count", count);

            return tag;
        }

        @Override
        public void deserializeNBT (NBTTagCompound nbt) {
            ItemStack tagItem = ItemStack.EMPTY;
            int tagCount = 0;

            if (nbt.hasKey("Item"))
                tagItem = new ItemStack(nbt.getCompoundTag("Item"));
            if (nbt.hasKey("Count"))
                tagCount = nbt.getInteger("Count");

            setStoredItemRaw(tagItem);
            setStoredItemCountRaw(tagCount);
        }

        public void deserializeLegacyNBT (NBTTagCompound nbt) {
            ItemStack tagItem = ItemStack.EMPTY;
            int tagCount = 0;

            if (nbt.hasKey("Count"))
                tagCount = nbt.getInteger("Count");
            if (nbt.hasKey("Item")) {
                Item item = Item.getItemById(nbt.getShort("Item"));
                if (item != null) {
                    tagItem = new ItemStack(item);
                    tagItem.setItemDamage(nbt.getShort("Meta"));
                    if (nbt.hasKey("Tags"))
                        tagItem.setTagCompound(nbt.getCompoundTag("Tags"));
                }
            }

            setStoredItemRaw(tagItem);
            setStoredItemCountRaw(tagCount);
        }

        public void syncAttributes () {
            if (!protoStack.isEmpty()) {
                if (attrs.isDictConvertible())
                    matcher = new ItemStackOreMatcher(protoStack);
                else
                    matcher = new ItemStackMatcher(protoStack);
            }
        }

        protected int getStackCapacity() {
            return 0;
        }

        protected void onItemChanged() { }

        protected void onAmountChanged() { }
    }

}
