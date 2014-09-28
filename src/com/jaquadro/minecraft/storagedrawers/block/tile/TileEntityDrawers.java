package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
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

import java.util.UUID;

public class TileEntityDrawers extends TileEntityDrawersBase implements IStorageProvider, ISidedInventory
{
    private int drawerCount = 2;

    private DrawerData[] data;
    private ItemStack[] snapshotItems;
    private int[] snapshotCounts;

    private long lastClickTime;
    private UUID lastClickUUID;

    public TileEntityDrawers () {
        setDrawerCount(2);
    }

    public int getDrawerCount () {
        return drawerCount;
    }

    public void setDrawerCount (int count) {
        drawerCount = count;

        data = new DrawerData[drawerCount];
        for (int i = 0, n = data.length; i < n; i++)
            data[i] = new DrawerData(this, i);

        snapshotItems = new ItemStack[count];
        snapshotCounts = new int[count];
    }

    public int getItemCount (int slot) {
        return data[slot].count;
    }

    public int getItemCapacity (int slot) {
        return data[slot].maxCapacity();
    }

    public int getItemStackSize (int slot) {
        return data[slot].itemStackMaxSize();
    }

    public ItemStack getSingleItemStack (int slot) {
        if (data[slot].getItem() == null)
            return null;

        return data[slot].getReadOnlyItemStack();
    }

    public NBTTagCompound getItemAttrs (int slot) {
        return data[slot].getAttrs();
    }

    public ItemStack takeItemsFromSlot (int slot, int count) {
        ItemStack stack = getItemsFromSlot(slot, count);
        if (stack == null)
            return null;

        data[slot].count -= stack.stackSize;
        if (data[slot].count <= 0)
            data[slot].reset();

        return stack;
    }

    public int interactPutItemsIntoSlot (int slot, EntityPlayer player) {
        int count = 0;

        ItemStack currentStack = player.inventory.getCurrentItem();
        if (currentStack != null)
            count += putItemsIntoSlot(slot, currentStack, currentStack.stackSize);

        if (worldObj.getWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID)) {
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

        lastClickTime = worldObj.getWorldTime();
        lastClickUUID = player.getPersistentID();

        return count;
    }

    public int putItemsIntoSlot (int slot, ItemStack stack, int count) {
        if (data[slot].getItem() == null)
            data[slot].setItem(stack);

        if (!data[slot].areItemsEqual(stack))
            return 0;

        int countAdded = Math.min(data[slot].remainingCapacity(), stack.stackSize);
        countAdded = Math.min(countAdded, count);

        data[slot].count += countAdded;
        stack.stackSize -= countAdded;

        return countAdded;
    }

    private ItemStack getItemsFromSlot (int slot, int count) {
        if (data[slot].getItem() == null)
            return null;

        ItemStack stack = data[slot].getNewItemStack();
        stack.stackSize = Math.min(stack.getMaxStackSize(), count);
        stack.stackSize = Math.min(stack.stackSize, data[slot].count);

        return stack;
    }

    @Override
    public int getSlotCapacity (int slot) {
        ConfigManager config = StorageDrawers.config;
        return config.getStorageUpgradeMultiplier(getLevel()) * getDrawerCapacity();
    }

    @Override
    public boolean canUpdate () {
        return false;
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);
        drawerCount = slots.tagCount();
        data = new DrawerData[slots.tagCount()];

        for (int i = 0, n = data.length; i < n; i++) {
            NBTTagCompound slot = slots.getCompoundTagAt(i);
            data[i] = new DrawerData(this, i);
            data[i].readFromNBT(slot);
        }

        snapshotItems = new ItemStack[drawerCount];
        snapshotCounts = new int[drawerCount];
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagList slots = new NBTTagList();
        for (DrawerData drawer : data) {
            NBTTagCompound slot = new NBTTagCompound();
            drawer.writeToNBT(slot);
            slots.appendTag(slot);
        }

        tag.setTag("Slots", slots);
    }

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
    public void markDirty () {
        for (int i = 0; i < drawerCount; i++) {
            if (snapshotItems[i] != null && snapshotItems[i].stackSize != snapshotCounts[i]) {
                int diff = snapshotItems[i].stackSize - snapshotCounts[i];
                if (diff > 0)
                    putItemsIntoSlot(i, snapshotItems[i], diff);
                else
                    takeItemsFromSlot(i, -diff);

                snapshotItems[i].stackSize = 64 - Math.min(64, data[i].remainingCapacity());
                snapshotCounts[i] = snapshotItems[i].stackSize;
            }
        }

        super.markDirty();
    }

    @Override
    public int getSizeInventory () {
        return drawerCount;
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        if (slot >= getSizeInventory())
            return null;

        ItemStack stack = getItemsFromSlot(slot, getItemStackSize(slot));
        if (stack != null) {
            stack.stackSize = 64 - Math.min(64, data[slot].remainingCapacity());
            snapshotItems[slot] = stack;
            snapshotCounts[slot] = stack.stackSize;
        }
        else {
            snapshotItems[slot] = null;
            snapshotCounts[slot] = 0;
        }

        return stack;
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        if (slot >= getSizeInventory())
            return null;

        ItemStack stack = takeItemsFromSlot(slot, count);
        if (stack != null && !worldObj.isRemote)
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemStack) {
        if (slot >= getSizeInventory())
            return;

        int insertCount = itemStack.stackSize;
        if (snapshotItems[slot] != null)
            insertCount = itemStack.stackSize - snapshotCounts[slot];

        if (insertCount > 0) {
            int count = putItemsIntoSlot(slot, itemStack, insertCount);
            if (count > 0 && !worldObj.isRemote)
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        else if (insertCount < 0) {
            ItemStack rmStack = takeItemsFromSlot(slot, -insertCount);
            if (rmStack != null && rmStack.stackSize > 0 && !worldObj.isRemote)
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        if (snapshotItems[slot] != null) {
            snapshotItems[slot].stackSize = 64 - Math.min(64, data[slot].remainingCapacity());
            snapshotCounts[slot] = snapshotItems[slot].stackSize;
        }
    }

    @Override
    public String getInventoryName () {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName () {
        return false;
    }

    @Override
    public int getInventoryStackLimit () {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory () {

    }

    @Override
    public void closeInventory () {

    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack itemStack) {
        if (slot >= getSizeInventory())
            return false;

        if (data[slot].getItem() == null)
            return true;

        if (!data[slot].areItemsEqual(itemStack))
            return false;

        if (data[slot].remainingCapacity() < itemStack.stackSize)
            return false;

        return true;
    }

    private static final int[] drawerSlots0 = new int[0];
    private static final int[] drawerSlots2 = new int[] { 0, 1 };
    private static final int[] drawerSlots4 = new int[] { 0, 1, 2, 3 };

    @Override
    public int[] getAccessibleSlotsFromSide (int side) {
        for (int aside : autoSides) {
            if (side == aside)
                return (drawerCount == 2) ? drawerSlots2 : drawerSlots4;
        }

        return drawerSlots0;
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, int side) {
        if (slot >= getSizeInventory())
            return false;

        for (int aside : autoSides) {
            if (side == aside) {
                return isItemValidForSlot(slot, stack);
            }
        }

        return false;
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, int side) {
        return false;
    }
}
