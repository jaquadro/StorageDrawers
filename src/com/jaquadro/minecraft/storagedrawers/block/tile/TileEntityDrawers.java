package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.inventory.InventoryStack;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
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
    private class DrawerInventoryStack extends InventoryStack
    {
        private int slot;

        public DrawerInventoryStack (int slot) {
            this.slot = slot;
            init();
        }

        @Override
        protected ItemStack getNewItemStack () {
            return data[slot].getNewItemStack();
        }

        @Override
        protected int getItemStackSize () {
            return data[slot].itemStackMaxSize();
        }

        @Override
        protected int getItemCount () {
            return data[slot].count;
        }

        @Override
        protected int getItemCapacity () {
            return data[slot].maxCapacity();
        }

        @Override
        protected void applyDiff (int diff) {
            if (diff != 0) {
                if (diff > 0)
                    putItemsIntoSlot(slot, getNativeStack(), diff);
                else
                    takeItemsFromSlot(slot, -diff);
            }
        }
    }

    private int drawerCount = 2;

    private DrawerData[] data;
    //private ItemStack[] snapshotItems;
    //private int[] snapshotCounts;

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

        inventoryStacks = new InventoryStack[count];
        //snapshotItems = new ItemStack[count];
        //snapshotCounts = new int[count];
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
        if (data[slot].count <= 0) {
            data[slot].reset();
            inventoryStacks[slot] = null;
        }

        if (!worldObj.isRemote)
            syncClientCount(slot);

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
        if (data[slot].getItem() == null) {
            data[slot].setItem(stack);
            inventoryStacks[slot] = new DrawerInventoryStack(slot);
            inventoryStacks[slot].markDirty();
        }

        if (!data[slot].areItemsEqual(stack))
            return 0;

        int countAdded = Math.min(data[slot].remainingCapacity(), stack.stackSize);
        countAdded = Math.min(countAdded, count);

        data[slot].count += countAdded;
        stack.stackSize -= countAdded;

        if (!worldObj.isRemote)
            syncClientCount(slot);

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

        inventoryStacks = new InventoryStack[drawerCount];

        for (int i = 0, n = data.length; i < n; i++) {
            NBTTagCompound slot = slots.getCompoundTagAt(i);
            data[i] = new DrawerData(this, i);
            data[i].readFromNBT(slot);
            if (data[i].getItem() != null)
                inventoryStacks[i] = new DrawerInventoryStack(i);
        }


        //snapshotItems = new ItemStack[drawerCount];
        //snapshotCounts = new int[drawerCount];
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
    public void clientUpdateCount (int slot, int count) {
        if (data[slot].count != count) {
            data[slot].count = count;
            getWorldObj().func_147479_m(xCoord, yCoord, zCoord); // markBlockForRenderUpdate
        }
    }

    protected void syncClientCount (int slot) {
        IMessage message = new CountUpdateMessage(xCoord, yCoord, zCoord, slot, data[slot].count);
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 500);

        StorageDrawers.network.sendToAllAround(message, targetPoint);
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
            if (inventoryStacks[i] != null)
                inventoryStacks[i].markDirty();

            /*if (snapshotItems[i] != null && snapshotItems[i].stackSize != snapshotCounts[i]) {
                int diff = snapshotItems[i].stackSize - snapshotCounts[i];
                if (diff > 0)
                    putItemsIntoSlot(i, snapshotItems[i], diff);
                else
                    takeItemsFromSlot(i, -diff);

                int itemStackLimit = getItemStackSize(i);
                snapshotItems[i].stackSize = itemStackLimit - Math.min(itemStackLimit, data[i].remainingCapacity());
                snapshotCounts[i] = snapshotItems[i].stackSize;
            }*/
        }

        super.markDirty();
    }

    @Override
    public int getSizeInventory () {
        return drawerCount * 3;
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        if (slot < 0 || slot >= getSizeInventory())
            return null;

        int baseSlot = slot % drawerCount;
        InventoryStack invStack = inventoryStacks[baseSlot];
        if (invStack == null)
            return null;

        invStack.markDirty();

        int groupSlot = slot / drawerCount;
        switch (groupSlot) {
            case 0: return invStack.getInStack();
            case 1: return invStack.getOutStack();
            case 2: return invStack.getNativeStack();
        }

        return null;

        /*int itemStackLimit = getItemStackSize(baseSlot);
        ItemStack stack = getItemsFromSlot(slot, itemStackLimit);
        if (stack != null) {
            stack.stackSize = itemStackLimit - Math.min(itemStackLimit, data[slot].remainingCapacity());
            snapshotItems[slot] = stack;
            snapshotCounts[slot] = stack.stackSize;
        }
        else {
            snapshotItems[slot] = null;
            snapshotCounts[slot] = 0;
        }

        return stack;*/
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        if (slot < 0 || slot >= getSizeInventory())
            return null;

        int baseSlot = slot % drawerCount;
        ItemStack stack = takeItemsFromSlot(baseSlot, count);
        if (stack != null && !worldObj.isRemote)
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

        if (inventoryStacks[baseSlot] != null)
            inventoryStacks[baseSlot].markDirty();

        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemStack) {
        if (slot < 0 || slot >= getSizeInventory())
            return;

        int baseSlot = slot % drawerCount;
        int slotGroup = slot / drawerCount;
        int insertCount = 0;

        if (slotGroup == 0) {
            insertCount = (itemStack != null) ? itemStack.stackSize : 0;
            if (inventoryStacks[baseSlot] != null)
                insertCount -= inventoryStacks[baseSlot].getInStack().stackSize;
        }
        else if (slotGroup == 1) {
            insertCount = (itemStack != null) ? itemStack.stackSize : 0;
            if (inventoryStacks[baseSlot] != null)
                insertCount -= inventoryStacks[baseSlot].getOutStack().stackSize;
        }

        //if (snapshotItems[slot] != null)
        //    insertCount = itemStack.stackSize - snapshotCounts[slot];

        if (insertCount > 0) {
            int count = putItemsIntoSlot(baseSlot, itemStack, insertCount);
            if (count > 0 && !worldObj.isRemote)
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        else if (insertCount < 0) {
            ItemStack rmStack = takeItemsFromSlot(baseSlot, -insertCount);
            if (rmStack != null && rmStack.stackSize > 0 && !worldObj.isRemote)
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        if (inventoryStacks[baseSlot] != null)
            inventoryStacks[baseSlot].markDirty();

        /*if (snapshotItems[slot] != null) {
            int itemStackLimit = getItemStackSize(slot);
            snapshotItems[slot].stackSize = itemStackLimit - Math.min(itemStackLimit, data[slot].remainingCapacity());
            snapshotCounts[slot] = snapshotItems[slot].stackSize;
        }*/
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
        if (slot < 0 || slot >= getSizeInventory())
            return false;

        int baseSlot = slot % drawerCount;
        if (data[baseSlot].getItem() == null)
            return true;

        if (!data[baseSlot].areItemsEqual(itemStack))
            return false;

        if (data[baseSlot].remainingCapacity() < itemStack.stackSize)
            return false;

        return true;
    }

    private static final int[] drawerSlots0 = new int[0];
    private static final int[] drawerSlots2 = new int[] { 0, 1, 2, 3 };
    private static final int[] drawerSlots4 = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };

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
        if (slot < 0 || slot >= getSizeInventory() / 3)
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
        int baseUnit = getSizeInventory() / 3;
        if (slot < baseUnit || slot >= 2 * baseUnit)
            return false;

        return true;
    }
}
