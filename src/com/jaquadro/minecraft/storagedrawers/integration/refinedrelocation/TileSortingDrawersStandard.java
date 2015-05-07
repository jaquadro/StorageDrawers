package com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import net.minecraft.item.ItemStack;

public class TileSortingDrawersStandard extends TileEntityDrawersStandard //implements ISpecialSortingInventory
{
    /*
    private DrawerSortingInventory sortingInventory;

    public TileSortingDrawersStandard () {
        sortingInventory = new DrawerSortingInventory(this, this, this, this);
    }

    @Override
    public boolean isSorting () {
        return true;
    }

    @Override
    public boolean canUpdate () {
        return super.canUpdate() || !sortingInventory.isAttached();
    }

    @Override
    public void updateEntity () {
        if (!sortingInventory.isAttached())
            sortingInventory.attach();
        super.updateEntity();
    }

    @Override
    public void invalidate () {
        sortingInventory.detach();
        super.invalidate();
    }

    @Override
    public void onChunkUnload () {
        sortingInventory.detach();
        super.onChunkUnload();
    }

    @Override
    public void markDirty () {
        super.markDirty();
        sortingInventory.markDirty();
    }

    @Override
    public boolean putStackInSlot (ItemStack itemStack, int slotIndex) {
        return sortingInventory.putStackInSlot(itemStack, slotIndex);
    }

    @Override
    public ItemStack putInInventory (ItemStack itemStack, boolean simulate) {
        return sortingInventory.putInInventory(itemStack, simulate);
    }

    @Override
    public SpecialLocalizedStack getLocalizedStackInSlot (int slot) {
        return sortingInventory.getLocalizedStackInSlot(slot);
    }

    @Override
    public void alterStackSize (int slot, int alteration) {
        sortingInventory.alterStackSize(slot, alteration);
    }

    @Override
    public ISortingInventory.Priority getPriority () {
        return sortingInventory.getPriority();
    }

    @Override
    public void setPriority (ISortingInventory.Priority priority) {
        sortingInventory.setPriority(priority);
    }

    @Override
    public IFilter getFilter () {
        return sortingInventory.getFilter();
    }

    @Override
    public ISortingInventoryHandler getHandler () {
        return sortingInventory.getHandler();
    }*/
}
