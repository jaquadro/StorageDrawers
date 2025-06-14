package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import com.jaquadro.minecraft.storagedrawers.capabilities.Capabilities;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public class DrawerStackStorage extends SingleStackStorage
{
    DrawerStorageImpl storage;
    int slot;
    ItemStack lastReleasedSnapshot = null;

    DrawerStackStorage (DrawerStorageImpl storage, int slot) {
        this.storage = storage;
        this.slot = slot;
    }

    void updateSlot (int slot) {
        this.slot = slot;
    }

    @Override
    protected ItemStack getStack () {
        IDrawer drawer = storage.getDrawer(slot);
        return drawer.getStoredItemPrototype().copyWithCount(drawer.getStoredItemCount());
    }

    @Override
    protected void setStack (ItemStack stack) {
        if (stack.getCount() > 0)
            storage.getDrawer(slot).setStoredItem(stack, stack.getCount());
        else
            storage.getDrawer(slot).setStoredItemCount(0);
    }

    @Override
    protected int getCapacity (ItemVariant itemVariant) {
        return storage.getDrawer(slot).getMaxCapacity(itemVariant.toStack());
    }

    /**
     * Gets drawer attributes from a drawer group, trying capability first, then direct access
     */
    private IDrawerAttributes getDrawerAttributes(IDrawerGroup group) {
        if (group == null) return null;
        
        // Try capability first
        IDrawerAttributes attr = group.getCapability(Capabilities.DRAWER_ATTRIBUTES);
        // If that fails and it's a BlockEntityDrawers, get attributes directly
        if (attr == null && group instanceof BlockEntityDrawers) {
            attr = ((BlockEntityDrawers) group).getDrawerAttributes();
        }
        
        return attr;
    }
    
    /**
     * Checks if a drawer from a controller has void attribute
     */
    private boolean checkControllerVoid(BlockEntityController controller) {
        if (controller == null) return false;
        
        IDrawer drawer = storage.getDrawer(slot);
        if (drawer == null || !drawer.isEnabled()) return false;
        
        // Get the underlying drawer group that this drawer belongs to
        IDrawerGroup drawerGroup = controller.getGroupForDrawerSlot(slot);
        if (drawerGroup == null) return false;
        
        // Get attributes and check void
        IDrawerAttributes attrs = getDrawerAttributes(drawerGroup);
        return attrs != null && attrs.isVoid();
    }
    
    @Override
    public long insert (ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        if (!storage.getDrawer(slot).canItemBeStored(insertedVariant.toStack()))
            return 0;

        long inserted = super.insert(insertedVariant, maxAmount, transaction);

        if (inserted < maxAmount) {
            boolean isVoid;

            if (storage.group instanceof BlockEntityController) {
                // Handle controller case
                isVoid = checkControllerVoid((BlockEntityController) storage.group);
            }
            else if (storage.group instanceof BlockEntitySlave) {
                // Handle slave case by getting its controller
                BlockEntityController controller =
                    ((BlockEntitySlave) storage.group).getController();
                isVoid = checkControllerVoid(controller);
            }
            else {
                // Handle normal drawer case
                IDrawerAttributes attr = getDrawerAttributes(storage.group);
                isVoid = attr != null && attr.isVoid();
            }

            // If we found the void attribute, accept the full amount
            if (isVoid) {
                inserted = maxAmount;
            }
        }

        return inserted;
    }

    @Override
    public long extract (ItemVariant variant, long maxAmount, TransactionContext transaction) {
        if (!storage.getDrawer(slot).canItemBeExtracted(variant.toStack()))
            return 0;

        return super.extract(variant, maxAmount, transaction);
    }

    @Override
    protected void releaseSnapshot (ItemStack snapshot) {
        lastReleasedSnapshot = snapshot;
    }

    @Override
    protected void onFinalCommit () {
        ItemStack original = lastReleasedSnapshot;
        ItemStack currentStack = getStack();

        if (!original.isEmpty() && original.getItem() == currentStack.getItem()) {
            original.setTag(currentStack.getTag());
            original.setCount(currentStack.getCount());
            setStack(original);
        } else
            original.setCount(0);
    }
}
