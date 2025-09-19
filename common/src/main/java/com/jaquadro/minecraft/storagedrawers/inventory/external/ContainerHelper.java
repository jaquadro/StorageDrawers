package com.jaquadro.minecraft.storagedrawers.inventory.external;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ContainerHelper
{
    private static Container EMPTY_CONTAINER = new SimpleContainer(1);

    public static Container getContainerAt (Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        Container container = null;

        if (block instanceof WorldlyContainerHolder holder)
            container = holder.getContainer(state, level, pos);
        else if (state.hasBlockEntity()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof Container c) {
                container = c;
                if (container instanceof ChestBlockEntity && block instanceof ChestBlock chestBlock)
                    container = ChestBlock.getContainer(chestBlock, state, level, pos, true);
            }
        }

        return container;
    }

    public static boolean canPlaceItemInContainer (Container container, ItemStack stack, int slot, Direction dir) {
        if (!container.canPlaceItem(slot, stack))
            return false;

        if (container instanceof WorldlyContainer worldlyContainer) {
            if (!worldlyContainer.canPlaceItemThroughFace(slot, stack, dir))
                return false;
        }

        return true;
    }

    public static boolean canTakeItemFromContainer (Container container, ItemStack stack, int slot, Direction dir) {
        if (!container.canTakeItem(EMPTY_CONTAINER, slot, stack))
            return false;

        if (container instanceof WorldlyContainer worldlyContainer) {
            if (!worldlyContainer.canTakeItemThroughFace(slot, stack, dir))
                return false;
        }

        return true;
    }

    public static boolean addItemFromDrawer (IDrawer drawer, Container container, Direction dir) {
        if (!drawer.isEnabled() || drawer.isEmpty() || drawer.getAttributes().isSuspended())
            return false;

        int pushCount = Math.min(drawer.getStoredItemCount(), drawer.getStoredItemPrototype().getMaxStackSize());
        pushCount = Math.min(pushCount, 1);

        ItemStack stack = drawer.getStoredItemPrototype().copyWithCount(pushCount);
        stack = ContainerHelper.addItem(container, stack, dir);

        int addedCount = pushCount - stack.getCount();
        if (addedCount > 0) {
            drawer.setStoredItemCount(drawer.getStoredItemCount() - addedCount);
            return true;
        }

        return false;
    }

    // Potentially modifies input stack
    public static ItemStack addItem (Container container, ItemStack stack, Direction dir) {
        if (container instanceof WorldlyContainer worldlycontainer) {
            if (dir != null) {
                int[] slots = worldlycontainer.getSlotsForFace(dir);

                for (int i = 0; i < slots.length && !stack.isEmpty(); ++i)
                    stack = addItem(container, stack, slots[i], dir);

                return stack;
            }
        }

        int slotCount = container.getContainerSize();
        for (int j = 0; j < slotCount && !stack.isEmpty(); ++j)
            stack = addItem(container, stack, j, dir);

        return stack;
    }

    // Potentially modifies input stack
    private static ItemStack addItem (Container container, ItemStack stack, int slot, Direction dir) {
        if (!canPlaceItemInContainer(container, stack, slot, dir))
            return stack;

        ItemStack existingItem = container.getItem(slot);
        if (existingItem.isEmpty()) {
            container.setItem(slot, stack);
            container.setChanged();
            return ItemStack.EMPTY;
        }

        if (canMergeItems(existingItem, stack)) {
            int availCap = stack.getMaxStackSize() - existingItem.getCount();
            int amount = Math.min(stack.getCount(), availCap);

            if (amount > 0) {
                stack.shrink(amount);
                existingItem.grow(amount);
                container.setChanged();
            }
        }

        return stack;
    }

    public static boolean takeItemIntoDrawer (IDrawer drawer, Container container, Direction dir) {
        if (!drawer.isEnabled() || drawer.isEmpty() || drawer.getAttributes().isSuspended())
            return false;

        int pullCount = Math.min(drawer.getAcceptingRemainingCapacity(), drawer.getStoredItemPrototype().getMaxStackSize());
        pullCount = Math.min(pullCount, 1);

        ItemStack stack = drawer.getStoredItemPrototype().copyWithCount(pullCount);
        stack = ContainerHelper.takeItem(container, stack, dir);

        int takenCount = stack.getCount();
        if (takenCount > 0) {
            drawer.setStoredItemCount(drawer.getStoredItemCount() + takenCount);
            return true;
        }

        return false;
    }

    // Potentially modifies input stack
    public static ItemStack takeItem (Container container, ItemStack stack, Direction dir) {
        if (container instanceof WorldlyContainer worldlycontainer) {
            if (dir != null) {
                int[] slots = worldlycontainer.getSlotsForFace(dir);

                ItemStack result = ItemStack.EMPTY;
                for (int i = 0; i < slots.length && !stack.isEmpty(); ++i) {
                    ItemStack take = takeItem(container, stack, slots[i], dir);
                    stack.shrink(take.getCount());

                    if (result.isEmpty())
                        result = take;
                    else
                        result.grow(take.getCount());
                }

                return result;
            }
        }

        int slotCount = container.getContainerSize();
        ItemStack result = ItemStack.EMPTY;

        for (int j = 0; j < slotCount && !stack.isEmpty(); ++j) {
            ItemStack take = takeItem(container, stack, j, dir);
            stack.shrink(take.getCount());

            if (result.isEmpty())
                result = take;
            else
                result.grow(take.getCount());
        }

        return result;
    }

    private static ItemStack takeItem (Container container, ItemStack stack, int slot, Direction dir) {
        if (!canTakeItemFromContainer(container, stack, slot, dir))
            return ItemStack.EMPTY;

        ItemStack existingItem = container.getItem(slot);
        if (!stack.isEmpty() && !ItemStack.isSameItemSameTags(stack, existingItem))
            return ItemStack.EMPTY;

        int amount = existingItem.getCount();
        if (!stack.isEmpty())
            amount = Math.min(stack.getCount(), amount);

        ItemStack result = container.removeItem(slot, amount);
        if (!result.isEmpty())
            container.setChanged();

        return result;
    }

    private static boolean canMergeItems (ItemStack stack1, ItemStack stack2) {
        return stack1.getCount() <= stack1.getMaxStackSize() && ItemStack.isSameItemSameTags(stack1, stack2);
    }
}
