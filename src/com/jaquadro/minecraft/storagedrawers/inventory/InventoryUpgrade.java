package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class InventoryUpgrade implements IInventory
{
    private static final int upgradeCapacity = 5;

    private TileEntityDrawers tile;

    public InventoryUpgrade (TileEntityDrawers tileEntity) {
        tile = tileEntity;
    }

    @Override
    public int getSizeInventory () {
        return upgradeCapacity;
    }

    @Override
    public boolean isEmpty () {
        for (int i = 0; i < upgradeCapacity; i++) {
            if (!tile.getUpgrade(i).isEmpty())
                return false;
        }

        return true;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot (int slot) {
        return tile.getUpgrade(slot);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize (int slot, int count) {
        ItemStack stack = tile.getUpgrade(slot);
        if (count > 0)
            tile.setUpgrade(slot, ItemStack.EMPTY);

        return stack;
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot (int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents (int slot, @Nonnull ItemStack item) {
        //if (item != null && item.stackSize > getInventoryStackLimit())
        //    item.stackSize = getInventoryStackLimit();

        tile.setUpgrade(slot, item);
    }

    @Override
    public String getName () {
        return tile.getName();
    }

    @Override
    public boolean hasCustomName () {
        return tile.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName () {
        return tile.getDisplayName();
    }

    @Override
    public int getInventoryStackLimit () {
        return 1;
    }

    @Override
    public void markDirty () {
        tile.markDirty();
    }

    @Override
    public boolean isUsableByPlayer (EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory (EntityPlayer player) { }

    @Override
    public void closeInventory (EntityPlayer player) { }

    @Override
    public boolean isItemValidForSlot (int slot, @Nonnull ItemStack item) {
        if (item.getItem() == ModItems.upgradeOneStack)
            return tile.canAddOneStackUpgrade();

        return item.getItem() instanceof ItemUpgrade;
    }

    @Override
    public int getField (int id) {
        return 0;
    }

    @Override
    public void setField (int id, int value) {

    }

    @Override
    public int getFieldCount () {
        return 0;
    }

    @Override
    public void clear () {

    }

    public boolean canAddOneStackUpgrade () {
        return tile.canAddOneStackUpgrade();
    }

    public boolean canAddUpgrade (@Nonnull ItemStack item) {
        return tile.canAddUpgrade(item);
    }

    public boolean canRemoveStorageUpgrade (int storageLevel) {
        return canRemoveStorageUpgrade(tile, storageLevel);
    }

    private boolean canRemoveStorageUpgrade (TileEntityDrawers tile, int storageLevel) {
        int storageMult = StorageDrawers.config.getStorageUpgradeMultiplier(storageLevel);
        int effectiveStorageMult = tile.getEffectiveStorageMultiplier();
        if (effectiveStorageMult == storageMult)
            storageMult--;

        int addedStackCapacity = storageMult * tile.getEffectiveDrawerCapacity();

        for (int i = 0; i < tile.getDrawerCount(); i++) {
            IDrawer drawer = tile.getDrawerIfEnabled(i);
            if (drawer == null || drawer.isEmpty())
                continue;

            int addedItemCapacity = addedStackCapacity * drawer.getStoredItemStackSize();
            if (drawer.getMaxCapacity() - addedItemCapacity < drawer.getStoredItemCount())
                return false;
        }

        return true;
    }
}
