package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class InventoryUpgrade implements Container
{
    private static final int upgradeCapacity = 7;

    private TileEntityDrawers tile;

    public InventoryUpgrade (TileEntityDrawers tileEntity) {
        tile = tileEntity;
    }

    @Override
    public int getContainerSize () {
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
    public ItemStack getItem (int slot) {
        return tile.upgrades().getUpgrade(slot);
    }

    @Override
    @Nonnull
    public ItemStack removeItem (int slot, int count) {
        ItemStack stack = tile.upgrades().getUpgrade(slot);
        if (count > 0)
            tile.upgrades().setUpgrade(slot, ItemStack.EMPTY);

        return stack;
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate (int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem (int slot, @Nonnull ItemStack item) {
        tile.upgrades().setUpgrade(slot, item);
    }

    @Override
    public int getMaxStackSize () {
        return 1;
    }

    @Override
    public void setChanged () {
        tile.setChanged();
    }

    @Override
    public boolean stillValid (Player player) {
        BlockPos pos = tile.getBlockPos();
        if (tile.getLevel() == null || tile.getLevel().getBlockEntity(pos) != tile)
            return false;
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0)
            return false;

        return true;
    }

    @Override
    public void startOpen (Player player) { }

    @Override
    public void stopOpen (Player player) { }

    @Override
    public boolean canPlaceItem (int slot, @Nonnull ItemStack item) {
        return tile.upgrades().canAddUpgrade(item);
    }

    @Override
    public void clearContent () {

    }

    public boolean canAddUpgrade (@Nonnull ItemStack item) {
        return tile.upgrades().canAddUpgrade(item);
    }

    public boolean canRemoveStorageUpgrade (int slot) {
        return tile.upgrades().canRemoveUpgrade(slot);
    }
}
