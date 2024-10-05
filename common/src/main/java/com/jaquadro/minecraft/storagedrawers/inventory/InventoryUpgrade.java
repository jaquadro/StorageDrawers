package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
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
        if (blockEntityDrawers == null)
            return true;

        for (int i = 0; i < upgradeCapacity; i++) {
            if (!blockEntityDrawers.upgrades().getUpgrade(i).isEmpty())
                return false;
        }

        return true;
    }

    @Override
    @NotNull
    public ItemStack getItem (int slot) {
        if (blockEntityDrawers == null)
            return ItemStack.EMPTY;

        return blockEntityDrawers.upgrades().getUpgrade(slot);
    }

    @Override
    @NotNull
    public ItemStack removeItem (int slot, int count) {
        if (blockEntityDrawers == null)
            return ItemStack.EMPTY;

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
        if (blockEntityDrawers == null)
            return;

        blockEntityDrawers.upgrades().setUpgrade(slot, item);
    }

    @Override
    public int getMaxStackSize () {
        return 1;
    }

    @Override
    public void setChanged () {
        if (blockEntityDrawers == null)
            return;

        blockEntityDrawers.setChanged();
    }

    @Override
    public boolean stillValid (@NotNull Player player) {
        if (blockEntityDrawers == null)
            return false;

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
        if (blockEntityDrawers == null)
            return false;

        if (blockEntityDrawers.hasMissingDrawers() && ModCommonConfig.INSTANCE.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canAddUpgrade(item);
    }

    @Override
    public void clearContent () {

    }

    public int getStorageMultiplier () {
        if (blockEntityDrawers == null)
            return 1;

        return blockEntityDrawers.upgrades().getStorageMultiplier();
    }

    public int getStackCapacity () {
        if (blockEntityDrawers == null)
            return 1;

        try {
            return Math.multiplyExact(blockEntityDrawers.getEffectiveDrawerCapacity(), blockEntityDrawers.upgrades().getStorageMultiplier());
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE / 64;
        }
    }

    public boolean slotIsLocked (int slot) {
        if (blockEntityDrawers == null)
            return false;

        if (blockEntityDrawers.hasMissingDrawers() && ModCommonConfig.INSTANCE.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return true;

        if (!getItem(slot).isEmpty())
            return !canRemoveUpgrade(slot);

        return false;
    }

    public boolean canAddUpgrade (@NotNull ItemStack item) {
        if (blockEntityDrawers == null)
            return false;

        if (blockEntityDrawers.hasMissingDrawers() && ModCommonConfig.INSTANCE.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canAddUpgrade(item);
    }

    public boolean canRemoveUpgrade (int slot) {
        if (blockEntityDrawers == null)
            return false;

        if (blockEntityDrawers.hasMissingDrawers() && ModCommonConfig.INSTANCE.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canRemoveUpgrade(slot);
    }

    public boolean canRemoveStorageUpgrade (int slot) {
        if (blockEntityDrawers == null)
            return false;

        if (blockEntityDrawers.hasMissingDrawers() && ModCommonConfig.INSTANCE.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canRemoveUpgrade(slot);
    }

    public boolean canSwapUpgrade (int slot, @NotNull ItemStack item) {
        if (blockEntityDrawers == null)
            return false;

        if (blockEntityDrawers.hasMissingDrawers() && ModCommonConfig.INSTANCE.GENERAL.forceDetachedDrawersMaxCapacityCheck.get())
            return false;

        return blockEntityDrawers.upgrades().canSwapUpgrade(slot, item);
    }
}
