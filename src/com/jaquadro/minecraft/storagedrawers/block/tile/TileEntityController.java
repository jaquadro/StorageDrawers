package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroupInteractive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class TileEntityController extends TileEntity implements IDrawerGroup, ISidedInventory
{
    private static final int DEPTH_LIMIT = 12;
    private static final int[] emptySlots = new int[0];

    private static class BlockCoord {
        private int x;
        private int y;
        private int z;

        public BlockCoord (int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals (Object obj) {
            if (obj == null || obj.getClass() != getClass())
                return false;

            BlockCoord that = (BlockCoord)obj;
            return x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode () {
            int hash = 23;
            hash = hash * 31 + x;
            hash = hash * 31 + y;
            hash = hash * 31 + z;

            return hash;
        }
    }

    private Map<BlockCoord, IDrawerGroup> storage = new HashMap<BlockCoord, IDrawerGroup>();
    private Map<BlockCoord, Boolean> mark = new HashMap<BlockCoord, Boolean>();
    private Map<BlockCoord, Integer> invStorageSize = new HashMap<BlockCoord, Integer>();
    private Map<BlockCoord, Integer> drawerStorageSize = new HashMap<BlockCoord, Integer>();
    private List<BlockCoord> invBlockList = new ArrayList<BlockCoord>();
    private List<Integer> invSlotList = new ArrayList<Integer>();
    private List<BlockCoord> drawerBlockList = new ArrayList<BlockCoord>();
    private List<Integer> drawerSlotList = new ArrayList<Integer>();

    private int[] inventorySlots = new int[0];
    private int[] autoSides = new int[] { 0, 1 };
    private int direction;

    private int inventorySize = 0;
    private int drawerSize = 0;

    private long lastClickTime;
    private UUID lastClickUUID;

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

    public int interactPutItemsIntoInventory (EntityPlayer player) {
        if (inventorySize == 0)
            updateCache();

        boolean dumpInventory = worldObj.getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID);
        int count = 0;

        for (int i = 0, n = drawerSlotList.size(); i < n; i++) {
            IDrawerGroup group = getDrawerBlockForGroup(i);
            if (group == null || !(group instanceof IDrawerGroupInteractive))
                continue;

            IDrawerGroupInteractive intGroup = (IDrawerGroupInteractive)group;

            int slot = getDrawerSlotForGroup(i);
            if (!group.isDrawerEnabled(slot))
                continue;

            IDrawer drawer = group.getDrawer(slot);
            if (drawer == null || drawer.isEmpty())
                continue;

            if (dumpInventory)
                count += intGroup.interactPutCurrentInventoryIntoSlot(slot, player);
            else
                count += intGroup.interactPutCurrentItemIntoSlot(slot, player);
        }

        lastClickTime = worldObj.getTotalWorldTime();
        lastClickUUID = player.getPersistentID();

        return count;
    }

    private void resetCache () {
        storage.clear();
        invStorageSize.clear();
        drawerStorageSize.clear();
        invBlockList.clear();
        invSlotList.clear();
        drawerBlockList.clear();
        drawerSlotList.clear();
        inventorySize = 0;
        drawerSize = 0;
    }

    //private void buildCache () {
    //    populateNode(xCoord, yCoord, zCoord, 0);
    //}

    private void rebuildCache () {
        resetCache();
        updateCache();
    }

    public void updateCache () {
        mark.clear();
        populateNode(xCoord, yCoord, zCoord, 0);

        for (BlockCoord coord : storage.keySet()) {
            if (!mark.containsKey(coord)) {
                storage.put(coord, null);

                for (int i = 0, n = invBlockList.size(); i < n; i++) {
                    if (coord.equals(invBlockList.get(i)))
                        invBlockList.set(i, null);
                }

                for (int i = 0, n = drawerBlockList.size(); i < n; i++) {
                    if (coord.equals(drawerBlockList.get(i)))
                        drawerBlockList.set(i, null);
                }
            }
        }

        int validSlotCount = 0;
        for (int i = 0, n = invBlockList.size(); i < n; i++) {
            if (invBlockList.get(i) != null)
                validSlotCount++;
        }

        inventorySlots = new int[validSlotCount];
        for (int i = 0, j = 0, n = invBlockList.size(); i < n; i++) {
            if (invBlockList.get(i) != null)
                inventorySlots[j++] = i;
        }
    }

    private void populateNode (int x, int y, int z, int depth) {
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te == null || !(te instanceof IDrawerGroup))
            return;

        if (te instanceof TileEntityController) {
            populateNeighborNodes(x, y, z, depth + 1);
            return;
        }

        IDrawerGroup group = (IDrawerGroup)te;
        IDrawerInventory inventory = group.getDrawerInventory();
        if (inventory == null)
            return;

        BlockCoord coord = new BlockCoord(x, y, z);
        if (storage.containsKey(coord)) {
            if (storage.get(coord) != null) {
                if (!mark.containsKey(coord)) {
                    mark.put(coord, true);
                    populateNeighborNodes(x, y, z, depth + 1);
                }
                return;
            }
        }

        storage.put(coord, group);
        mark.put(coord, true);

        invStorageSize.put(coord, inventory.getSizeInventory());
        drawerStorageSize.put(coord, group.getDrawerCount());

        for (int i = 0, n = invStorageSize.get(coord); i < n; i++) {
            invBlockList.add(coord);
            invSlotList.add(i);
        }

        for (int i = 0, n = drawerStorageSize.get(coord); i < n; i++) {
            drawerBlockList.add(coord);
            drawerSlotList.add(i);
        }

        inventorySize += invStorageSize.get(coord);
        drawerSize += drawerStorageSize.get(coord);

        if (depth == DEPTH_LIMIT)
            return;

        populateNeighborNodes(x, y, z, depth + 1);
    }

    private void populateNeighborNodes (int x, int y, int z, int depth) {
        populateNode(x - 1, y, z, depth);
        populateNode(x + 1, y, z, depth);
        populateNode(x, y - 1, z, depth);
        populateNode(x, y + 1, z, depth);
        populateNode(x, y, z - 1, depth);
        populateNode(x, y, z + 1, depth);
    }

    private IDrawerGroup getDrawerBlockForInv (int slot) {
        if (slot >= invBlockList.size())
            return null;

        BlockCoord coord = invBlockList.get(slot);
        if (coord == null)
            return null;

        if (!storage.containsKey(coord))
            return null;

        return storage.get(coord);
    }

    private IDrawerGroup getDrawerBlockForGroup (int slot) {
        if (slot >= drawerBlockList.size())
            return null;

        BlockCoord coord = drawerBlockList.get(slot);
        if (coord == null)
            return null;

        if (!storage.containsKey(coord))
            return null;

        return storage.get(coord);
    }

    private int getDrawerSlotForInv (int slot) {
        if (slot >= invSlotList.size())
            return 0;

        return invSlotList.get(slot);
    }

    private int getDrawerSlotForGroup (int slot) {
        if (slot >= drawerSlotList.size())
            return 0;

        return drawerSlotList.get(slot);
    }

    private IDrawerInventory getDrawerInventory (int slot) {
        IDrawerGroup group = getDrawerBlockForInv(slot);
        if (group == null)
            return null;

        return group.getDrawerInventory();
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        setDirection(tag.getByte("Dir"));

        if (worldObj != null && !worldObj.isRemote)
            rebuildCache();
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setByte("Dir", (byte)direction);
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
    public IDrawerInventory getDrawerInventory () {
        return null;
    }

    @Override
    public int getDrawerCount () {
        if (inventorySize == 0)
            updateCache();

        return drawerSlotList.size();
    }

    @Override
    public IDrawer getDrawer (int slot) {
        IDrawerGroup group = getDrawerBlockForGroup(slot);
        if (group == null)
            return null;

        return group.getDrawer(getDrawerSlotForGroup(slot));
    }

    @Override
    public boolean isDrawerEnabled (int slot) {
        IDrawerGroup group = getDrawerBlockForGroup(slot);
        if (group == null)
            return false;

        return group.isDrawerEnabled(getDrawerSlotForGroup(slot));
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int side) {
        for (int aside : autoSides) {
            if (side == aside)
                return inventorySlots;
        }

        return emptySlots;
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, int side) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return false;

        return inventory.canInsertItem(getDrawerSlotForInv(slot), stack);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, int side) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return false;

        return inventory.canExtractItem(getDrawerSlotForInv(slot), stack);
    }

    @Override
    public int getSizeInventory () {
        return inventorySize;
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return null;

        return inventory.getStackInSlot(getDrawerSlotForInv(slot));
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return null;

        return inventory.decrStackSize(getDrawerSlotForInv(slot), count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return null;

        return inventory.getStackInSlotOnClosing(getDrawerSlotForInv(slot));
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return;

        inventory.setInventorySlotContents(getDrawerSlotForInv(slot), stack);
    }

    @Override
    public String getInventoryName () {
        // TODO
        return null;
    }

    @Override
    public boolean hasCustomInventoryName () {
        // TODO
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
    public void openInventory () { }

    @Override
    public void closeInventory () { }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return false;

        return inventory.isItemValidForSlot(getDrawerSlotForInv(slot), stack);
    }
}
