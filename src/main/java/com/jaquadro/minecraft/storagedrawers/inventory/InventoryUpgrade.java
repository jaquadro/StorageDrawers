package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class InventoryUpgrade implements IInventory
{
    private static final int upgradeCapacity = 7;

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
            if (!tile.upgrades().getUpgrade(i).isEmpty())
                return false;
        }

        return true;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot (int slot) {
        return tile.upgrades().getUpgrade(slot);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize (int slot, int count) {
        ItemStack stack = tile.upgrades().getUpgrade(slot);
        if (count > 0)
            tile.upgrades().setUpgrade(slot, ItemStack.EMPTY);

        return stack;
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot (int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents (int slot, @Nonnull ItemStack item) {
        tile.upgrades().setUpgrade(slot, item);
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
    public boolean isUsableByPlayer (PlayerEntity player) {
        BlockPos pos = tile.getPos();
        if (tile.getWorld() == null || tile.getWorld().getTileEntity(pos) != tile)
            return false;
        if (player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0)
            return false;

        return true;
    }

    @Override
    public void openInventory (PlayerEntity player) { }

    @Override
    public void closeInventory (PlayerEntity player) { }

    @Override
    public boolean isItemValidForSlot (int slot, @Nonnull ItemStack item) {
        return tile.upgrades().canAddUpgrade(item);
    }

    @Override
    public void clear () {

    }

    public boolean canAddUpgrade (@Nonnull ItemStack item) {
        return tile.upgrades().canAddUpgrade(item);
    }

    public boolean canRemoveStorageUpgrade (int slot) {
        return tile.upgrades().canRemoveUpgrade(slot);
    }
}
