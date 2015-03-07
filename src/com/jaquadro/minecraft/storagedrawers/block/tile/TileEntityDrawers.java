package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroupInteractive;
import com.jaquadro.minecraft.storagedrawers.inventory.ISideManager;
import com.jaquadro.minecraft.storagedrawers.inventory.StorageInventory;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;

import java.util.Iterator;
import java.util.UUID;

public abstract class TileEntityDrawers extends TileEntity implements IDrawerGroupInteractive, ISidedInventory
{
    private IDrawer[] drawers;
    private IDrawerInventory inventory;

    private int[] autoSides = new int[] { 0, 1 };

    private int direction;
    private int drawerCapacity = 1;
    private int storageLevel = 1;
    private int statusLevel = 0;
    private boolean locked = false;

    private long lastClickTime;
    private UUID lastClickUUID;

    private NBTTagCompound failureSnapshot;

    protected TileEntityDrawers (int drawerCount) {
        initWithDrawerCount(drawerCount);
    }

    protected abstract IDrawer createDrawer (int slot);

    protected ISideManager getSideManager () {
        return new DefaultSideManager();
    }

    protected void initWithDrawerCount (int drawerCount) {
        drawers = new IDrawer[drawerCount];
        for (int i = 0; i < drawerCount; i++)
            drawers[i] = createDrawer(i);

        inventory = new StorageInventory(this, getSideManager());
    }

    public int getDirection () {
        return direction;
    }

    public void setDirection (int direction) {
        this.direction = direction % 6;

        autoSides = new int[] { 0, 1, ForgeDirection.OPPOSITES[direction], 2, 3 };

        if (direction == 2 || direction == 3) {
            autoSides[3] = 4;
            autoSides[4] = 5;
        }
    }

    public int getStorageLevel () {
        return storageLevel;
    }

    public void setStorageLevel (int level) {
        this.storageLevel = MathHelper.clamp_int(level, 1, 6);
    }

    public int getStatusLevel () {
        return statusLevel;
    }

    public void setStatusLevel (int level) {
        this.statusLevel = MathHelper.clamp_int(level, 1, 3);
    }

    public int getDrawerCapacity () {
        return drawerCapacity;
    }

    public void setDrawerCapacity (int stackCount) {
        drawerCapacity = stackCount;
    }

    public boolean isLocked () {
        if (!StorageDrawers.config.cache.enableLockUpgrades)
            return false;

        return locked;
    }

    public void setIsLocked (boolean locked) {
        this.locked = locked;

        if (!locked) {
            for (int i = 0; i < getDrawerCount(); i++) {
                if (!isDrawerEnabled(i))
                    continue;

                IDrawer drawer = getDrawer(i);
                if (drawer != null && drawer.getStoredItemCount() == 0)
                    drawer.setStoredItemCount(0);
            }
        }
    }

    public ItemStack takeItemsFromSlot (int slot, int count) {
        if (slot < 0 || slot >= getDrawerCount())
            return null;

        ItemStack stack = getItemsFromSlot(slot, count);
        if (stack == null)
            return null;

        IDrawer drawer = drawers[slot];
        drawer.setStoredItemCount(drawer.getStoredItemCount() - stack.stackSize);

        // TODO: Reset empty drawer in subclasses

        return stack;
    }

    protected ItemStack getItemsFromSlot (int slot, int count) {
        if (drawers[slot].isEmpty())
            return null;

        ItemStack stack = drawers[slot].getStoredItemCopy();
        stack.stackSize = Math.min(stack.getMaxStackSize(), count);
        stack.stackSize = Math.min(stack.stackSize, drawers[slot].getStoredItemCount());

        return stack;
    }

    public int putItemsIntoSlot (int slot, ItemStack stack, int count) {
        if (slot < 0 || slot >= getDrawerCount())
            return 0;

        IDrawer drawer = drawers[slot];
        if (drawer.isEmpty())
            drawer.setStoredItem(stack, 0);

        if (!drawer.canItemBeStored(stack))
            return 0;

        int countAdded = Math.min(drawer.getRemainingCapacity(), stack.stackSize);
        countAdded = Math.min(countAdded, count);

        drawer.setStoredItemCount(drawer.getStoredItemCount() + countAdded);
        stack.stackSize -= countAdded;

        return countAdded;
    }

    public int interactPutCurrentItemIntoSlot (int slot, EntityPlayer player) {
        ItemStack currentStack = player.inventory.getCurrentItem();
        if (currentStack != null)
            return putItemsIntoSlot(slot, currentStack, currentStack.stackSize);

        markDirty();
        return 0;
    }

    public int interactPutCurrentInventoryIntoSlot (int slot, EntityPlayer player) {
        int count = 0;

        if (!drawers[slot].isEmpty()) {
            for (int i = 0, n = player.inventory.getSizeInventory(); i < n; i++) {
                ItemStack subStack = player.inventory.getStackInSlot(i);
                if (subStack != null) {
                    int subCount = putItemsIntoSlot(slot, subStack, subStack.stackSize);
                    if (subCount > 0 && subStack.stackSize == 0)
                        player.inventory.setInventorySlotContents(i, null);

                    count += subCount;
                }
            }
        }

        markDirty();
        return count;
    }

    public int interactPutItemsIntoSlot (int slot, EntityPlayer player) {
        int count = 0;
        if (worldObj.getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID))
            count = interactPutCurrentInventoryIntoSlot(slot, player);
        else
            count = interactPutCurrentItemIntoSlot(slot, player);

        lastClickTime = worldObj.getTotalWorldTime();
        lastClickUUID = player.getPersistentID();

        return count;
    }

    protected void trapLoadFailure (Throwable t, NBTTagCompound tag) {
        failureSnapshot = (NBTTagCompound)tag.copy();
        FMLLog.log(StorageDrawers.MOD_ID, Level.ERROR, t, "Tile Load Failure.");
    }

    protected void restoreLoadFailure (NBTTagCompound tag) {
        Iterator<String> iter = failureSnapshot.func_150296_c().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (!tag.hasKey(key))
                tag.setTag(key, failureSnapshot.getTag(key));
        }
    }

    protected boolean loadDidFail () {
        return failureSnapshot != null;
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        failureSnapshot = null;

        try {
            setDirection(tag.getByte("Dir"));

            drawerCapacity = tag.getByte("Cap");
            storageLevel = tag.getByte("Lev");

            statusLevel = 0;
            if (tag.hasKey("Stat"))
                statusLevel = tag.getByte("Stat");

            locked = false;
            if (tag.hasKey("Lock"))
                locked = tag.getBoolean("Lock");

            NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);
            int drawerCount = slots.tagCount();
            drawers = new IDrawer[slots.tagCount()];

            for (int i = 0, n = drawers.length; i < n; i++) {
                NBTTagCompound slot = slots.getCompoundTagAt(i);
                drawers[i] = createDrawer(i);
                drawers[i].readFromNBT(slot);
            }

            inventory = new StorageInventory(this, getSideManager());
        }
        catch (Throwable t) {
            trapLoadFailure(t, tag);
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (failureSnapshot != null) {
            restoreLoadFailure(tag);
            return;
        }

        tag.setByte("Dir", (byte)direction);
        tag.setByte("Cap", (byte)drawerCapacity);
        tag.setByte("Lev", (byte) storageLevel);

        if (statusLevel > 0)
            tag.setByte("Stat", (byte)statusLevel);

        if (locked)
            tag.setBoolean("Lock", locked);

        NBTTagList slots = new NBTTagList();
        for (IDrawer drawer : drawers) {
            NBTTagCompound slot = new NBTTagCompound();
            drawer.writeToNBT(slot);
            slots.appendTag(slot);
        }

        tag.setTag("Slots", slots);
    }

    @Override
    public boolean canUpdate () {
        return false;
    }

    @Override
    public void markDirty () {
        inventory.markDirty();
        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded () {
        if (inventory.syncInventoryIfNeeded()) {
            super.markDirty();
            return true;
        }

        return false;
    }

    public void clientUpdateCount (int slot, int count) {
        IDrawer drawer = getDrawer(slot);
        if (drawer.getStoredItemCount() != count) {
            drawer.setStoredItemCount(count);
            getWorldObj().func_147479_m(xCoord, yCoord, zCoord); // markBlockForRenderUpdate
        }
    }

    private void syncClientCount (int slot) {
        IMessage message = new CountUpdateMessage(xCoord, yCoord, zCoord, slot, drawers[slot].getStoredItemCount());
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 500);

        StorageDrawers.network.sendToAllAround(message, targetPoint);
    }

    // TODO: Eventually eliminate these expensive network updates
    @Override
    public Packet getDescriptionPacket () {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        getWorldObj().func_147479_m(xCoord, yCoord, zCoord); // markBlockForRenderUpdate
    }

    @Override
    public int getDrawerCount () {
        return drawers.length;
    }

    @Override
    public IDrawer getDrawer (int slot) {
        if (slot < 0 || slot >= drawers.length)
            return null;

        return drawers[slot];
    }

    @Override
    public IDrawerInventory getDrawerInventory () {
        return inventory;
    }

    @Override
    public boolean isDrawerEnabled (int slot) {
        return getDrawer(slot) != null;
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int side) {
        return inventory.getAccessibleSlotsFromSide(side);
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, int side) {
        return inventory.canInsertItem(slot, stack, side);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, int side) {
        return inventory.canExtractItem(slot, stack, side);
    }

    @Override
    public int getSizeInventory () {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        return inventory.decrStackSize(slot, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        return inventory.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName () {
        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName () {
        return inventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit () {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory () {
        inventory.openInventory();
    }

    @Override
    public void closeInventory () {
        inventory.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        return inventory.isItemValidForSlot(slot, stack);
    }

    private class DefaultSideManager implements ISideManager
    {
        @Override
        public int[] getSlotsForSide (int side) {
            return autoSides;
        }
    }
}
