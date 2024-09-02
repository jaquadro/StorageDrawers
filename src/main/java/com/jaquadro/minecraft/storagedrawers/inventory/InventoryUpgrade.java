package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class InventoryUpgrade implements Container
{
    private static final int upgradeCapacity = 7;

    private final BlockEntityDrawers blockEntityDrawers;

    public InventoryUpgrade (BlockEntityDrawers blockEntityDrawers) {
        this.blockEntityDrawers = blockEntityDrawers;
    }

    @Override
    public int getContainerSize () {
        return upgradeCapacity;
    }

    @Override
    public boolean isEmpty () {
        for (int i = 0; i < upgradeCapacity; i++) {
            if (!blockEntityDrawers.upgrades().getUpgrade(i).isEmpty())
                return false;
        }

        return true;
    }

    @Override
    @NotNull
    public ItemStack getItem (int slot) {
        return blockEntityDrawers.upgrades().getUpgrade(slot);
    }

    @Override
    @NotNull
    public ItemStack removeItem (int slot, int count) {
        ItemStack stack = blockEntityDrawers.upgrades().getUpgrade(slot);
        if (count > 0)
            blockEntityDrawers.upgrades().setUpgrade(slot, ItemStack.EMPTY);

        return stack;
    }

    @Override
    @NotNull
    public ItemStack removeItemNoUpdate (int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem (int slot, @NotNull ItemStack item) {
        blockEntityDrawers.upgrades().setUpgrade(slot, item);
    }

    @Override
    public int getMaxStackSize () {
        return 1;
    }

    @Override
    public void setChanged () {
        blockEntityDrawers.setChanged();
    }

    @Override
    public boolean stillValid (@NotNull Player player) {
        BlockPos pos = blockEntityDrawers.getBlockPos();
        if (blockEntityDrawers.getLevel() == null || blockEntityDrawers.getLevel().getBlockEntity(pos) != blockEntityDrawers)
            return false;
        return !(player.distanceToSqr(Vec3.atCenterOf(pos)) > 64.0);
    }

    @Override
    public void startOpen (@NotNull Player player) { }

    @Override
    public void stopOpen (@NotNull Player player) { }

    @Override
    public boolean canPlaceItem (int slot, @NotNull ItemStack item) {
        if (blockEntityDrawers.hasMissingDrawers() && CommonConfig.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canAddUpgrade(item);
    }

    @Override
    public void clearContent () {

    }

    public int getStorageMultiplier () {
        return blockEntityDrawers.upgrades().getStorageMultiplier();
    }

    public int getStackCapacity () {
        try {
            return Math.multiplyExact(blockEntityDrawers.getEffectiveDrawerCapacity(), blockEntityDrawers.upgrades().getStorageMultiplier());
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE / 64;
        }
    }

    public boolean slotIsLocked (int slot) {
        if (blockEntityDrawers.hasMissingDrawers() && CommonConfig.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return true;

        if (!getItem(slot).isEmpty())
            return !canRemoveUpgrade(slot);

        return false;
    }

    public boolean canAddUpgrade (@NotNull ItemStack item) {
        if (blockEntityDrawers.hasMissingDrawers() && CommonConfig.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canAddUpgrade(item);
    }

    public boolean canRemoveUpgrade (int slot) {
        if (blockEntityDrawers.hasMissingDrawers() && CommonConfig.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canRemoveUpgrade(slot);
    }

    public boolean canRemoveStorageUpgrade (int slot) {
        if (blockEntityDrawers.hasMissingDrawers() && CommonConfig.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canRemoveUpgrade(slot);
    }

    public boolean canSwapUpgrade (int slot, @NotNull ItemStack item) {
        if (blockEntityDrawers.hasMissingDrawers() && CommonConfig.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canSwapUpgrade(slot, item);
    }
}
