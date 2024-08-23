package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class DetachedDrawerData implements IDrawer, INBTSerializable<CompoundTag>
{
    private ItemStack protoStack;
    private int count;

    public DetachedDrawerData () {
        protoStack = ItemStack.EMPTY;
        count = 0;
    }

    public DetachedDrawerData (IDrawer sourceDrawer) {
        protoStack = sourceDrawer.getStoredItemPrototype();
        count = sourceDrawer.getStoredItemCount();
    }

    public DetachedDrawerData (CompoundTag serializedTag) {
        deserializeNBT(serializedTag);
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
    public CompoundTag serializeNBT () {
        CompoundTag tag = new CompoundTag();
        if (protoStack.isEmpty())
            return tag;

        CompoundTag item = new CompoundTag();
        protoStack.save(item);

        tag.put("Item", item);
        tag.putInt("Count", count);

        return tag;
    }

    @Override
    public void deserializeNBT (CompoundTag nbt) {
        ItemStack tagItem = ItemStack.EMPTY;
        int tagCount = 0;

        if (nbt.contains("Item"))
            tagItem = ItemStack.of(nbt.getCompound("Item"));
        if (nbt.contains("Count"))
            tagCount = nbt.getInt("Count");

        setStoredItemRaw(tagItem);
        setStoredItemCountRaw(tagCount);
    }
}
