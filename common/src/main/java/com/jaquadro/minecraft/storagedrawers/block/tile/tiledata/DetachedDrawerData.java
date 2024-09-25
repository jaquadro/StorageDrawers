package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class DetachedDrawerData implements IDrawer
{
    private ItemStack protoStack;
    private int count;
    private int storageMult;
    private boolean heavy;

    public DetachedDrawerData () {
        protoStack = ItemStack.EMPTY;
        count = 0;
        storageMult = 1;
        heavy = false;
    }

    public DetachedDrawerData (IDrawer sourceDrawer) {
        this(sourceDrawer, 1);
    }

    public DetachedDrawerData (IDrawer sourceDrawer, int storageMult) {
        protoStack = sourceDrawer.getStoredItemPrototype();
        count = sourceDrawer.getStoredItemCount();
        this.storageMult = storageMult;
    }

    public DetachedDrawerData (HolderLookup.Provider provider, CompoundTag serializedTag) {
        deserializeNBT(provider, serializedTag);
    }

    protected DetachedDrawerData (DetachedDrawerData data) {
        protoStack = data.protoStack;
        count = data.count;
        storageMult = data.storageMult;
        heavy = data.heavy;
    }

    public int getStorageMultiplier () {
        return storageMult;
    }

    public void setStorageMultiplier (int storageMult) {
        this.storageMult = storageMult;
    }

    public boolean isHeavy () {
        return heavy;
    }

    public void setIsHeavy (boolean state) {
        heavy = state;
    }

    @Override
    public @NotNull ItemStack getStoredItemPrototype () {
        return protoStack;
    }

    @Override
    public @NotNull IDrawer setStoredItem (@NotNull ItemStack itemPrototype) {
        return this;
    }

    protected IDrawer setStoredItemRaw (@NotNull ItemStack itemPrototype) {
        itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
        protoStack = itemPrototype;
        protoStack.setCount(1);
        count = 0;

        return this;
    }

    @Override
    public int getStoredItemCount () {
        return count;
    }

    @Override
    public void setStoredItemCount (int amount) {

    }

    protected void setStoredItemCountRaw (int amount) {
        count = amount;
    }

    @Override
    public int getMaxCapacity (@NotNull ItemStack itemPrototype) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getRemainingCapacity () {
        return getMaxCapacity() - getStoredItemCount();
    }

    @Override
    public boolean canItemBeStored (@NotNull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
        return false;
    }

    @Override
    public boolean canItemBeExtracted (@NotNull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
        return false;
    }

    @Override
    public boolean isEmpty () {
        return protoStack.isEmpty();
    }

    @Override
    public IDrawer copy () {
        return new DetachedDrawerData(this);
    }

    public CompoundTag serializeNBT (HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (storageMult > 1)
            tag.putInt("StorageMult", storageMult);

        if (protoStack.isEmpty())
            return tag;

        CompoundTag item = new CompoundTag();
        item = (CompoundTag)protoStack.save(provider, item);

        tag.put("Item", item);
        tag.putInt("Count", count);

        if (heavy)
            tag.putBoolean("Heavy", true);

        return tag;
    }

    public void deserializeNBT (HolderLookup.Provider provider, CompoundTag nbt) {
        ItemStack tagItem = ItemStack.EMPTY;
        int tagCount = 0;

        if (nbt == null)
            return;

        if (nbt.contains("Item"))
            tagItem = ItemStack.parseOptional(provider, nbt.getCompound("Item"));
        if (nbt.contains("Count"))
            tagCount = nbt.getInt("Count");

        if (nbt.contains("StorageMult"))
            storageMult = nbt.getInt("StorageMult");
        else
            storageMult = ModCommonConfig.INSTANCE.GENERAL.baseStackStorage.get() * 8;

        if (nbt.contains("Heavy"))
            setIsHeavy(nbt.getBoolean("Heavy"));

        setStoredItemRaw(tagItem);
        setStoredItemCountRaw(tagCount);
    }
}
