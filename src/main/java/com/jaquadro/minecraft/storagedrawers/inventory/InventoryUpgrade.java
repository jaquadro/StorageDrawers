package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class InventoryUpgrade implements Container
{
    private static final int upgradeCapacity = 7;

    private final TileEntityDrawers tileEntityDrawers;

    public InventoryUpgrade (TileEntityDrawers tileEntityDrawers) {
        this.tileEntityDrawers = tileEntityDrawers;
    }

    @Override
    public int getContainerSize () {
        return upgradeCapacity;
    }

    @Override
    public boolean isEmpty () {
        for (int i = 0; i < upgradeCapacity; i++) {
            if (!tileEntityDrawers.upgrades().getUpgrade(i).isEmpty())
                return false;
        }

        return true;
    }

    @Override
    @NotNull
    public ItemStack getItem (int slot) {
        return tileEntityDrawers.upgrades().getUpgrade(slot);
    }

    @Override
    @NotNull
    public ItemStack removeItem (int slot, int count) {
        ItemStack stack = tileEntityDrawers.upgrades().getUpgrade(slot);
        if (count > 0)
            tileEntityDrawers.upgrades().setUpgrade(slot, ItemStack.EMPTY);

        return stack;
    }

    @Override
    @NotNull
    public ItemStack removeItemNoUpdate (int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem (int slot, @NotNull ItemStack item) {
        tileEntityDrawers.upgrades().setUpgrade(slot, item);
    }

    @Override
    public int getMaxStackSize () {
        return 1;
    }

    @Override
    public void setChanged () {
        tileEntityDrawers.setChanged();
    }

    @Override
    public boolean stillValid (@NotNull Player player) {
        BlockPos pos = tileEntityDrawers.getBlockPos();
        if (tileEntityDrawers.getLevel() == null || tileEntityDrawers.getLevel().getBlockEntity(pos) != tileEntityDrawers)
            return false;
        return !(player.distanceToSqr(Vec3.atCenterOf(pos)) > 64.0);
    }

    @Override
    public void startOpen (@NotNull Player player) { }

    @Override
    public void stopOpen (@NotNull Player player) { }

    @Override
    public boolean canPlaceItem (int slot, @NotNull ItemStack item) {
        return tileEntityDrawers.upgrades().canAddUpgrade(item);
    }

    @Override
    public void clearContent () {

    }

    public boolean canAddUpgrade (@NotNull ItemStack item) {
        return tileEntityDrawers.upgrades().canAddUpgrade(item);
    }

    public boolean canRemoveStorageUpgrade (int slot) {
        return tileEntityDrawers.upgrades().canRemoveUpgrade(slot);
    }
}
