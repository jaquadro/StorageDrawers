package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersBase;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryUpgrade implements IInventory
{
    private static final int upgradeCapacity = 5;

    private TileEntityDrawersBase tile;

    public InventoryUpgrade (TileEntityDrawersBase tileEntity) {
        tile = tileEntity;
    }

    @Override
    public int getSizeInventory () {
        return upgradeCapacity;
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        return tile.getUpgrade(slot);
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        if (count > 0)
            tile.setUpgrade(slot, null);

        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack item) {
        tile.setUpgrade(slot, item);
    }

    @Override
    public String getInventoryName () {
        return tile.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName () {
        return tile.hasCustomInventoryName();
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
    public boolean isUseableByPlayer (EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public void openInventory () {

    }

    @Override
    public void closeInventory () {

    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack item) {
        if (item.getItem() instanceof ItemUpgrade || item.getItem() instanceof ItemUpgradeStatus)
            return true;

        return false;
    }
}
