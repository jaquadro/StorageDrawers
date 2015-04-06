package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroupInteractive;
import com.jaquadro.minecraft.storagedrawers.network.ControllerUpdateMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.*;

public class TileEntityController extends TileEntity implements IDrawerGroup, ISidedInventory
{
    private static final int DEPTH_LIMIT = 12;
    private static final int[] emptySlots = new int[0];

    private Map<BlockPos, IDrawerGroup> storage = new HashMap<BlockPos, IDrawerGroup>();
    private Map<BlockPos, Boolean> mark = new HashMap<BlockPos, Boolean>();
    private Map<BlockPos, Integer> invStorageSize = new HashMap<BlockPos, Integer>();
    private Map<BlockPos, Integer> drawerStorageSize = new HashMap<BlockPos, Integer>();
    private List<BlockPos> invBlockList = new ArrayList<BlockPos>();
    private List<Integer> invSlotList = new ArrayList<Integer>();
    private List<BlockPos> drawerBlockList = new ArrayList<BlockPos>();
    private List<Integer> drawerSlotList = new ArrayList<Integer>();

    private int[] inventorySlots = new int[0];
    private int[] autoSides = new int[] { 0, 1 };
    private int direction;

    private int drawerSize = 0;

    private long lastClickTime;
    private UUID lastClickUUID;

    public TileEntityController () {
        invBlockList.add(null);
        invSlotList.add(0);
        inventorySlots = new int[] { 0 };
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

    public int interactPutItemsIntoInventory (EntityPlayer player) {
        if (inventorySlots.length == 0)
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
        drawerSize = 0;
    }

    private void rebuildCache () {
        resetCache();
        updateCache();
    }

    public void updateCache () {
        int preCount = inventorySlots.length;

        mark.clear();
        populateNode(getPos(), 0);

        for (BlockPos coord : storage.keySet()) {
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

        /*int validSlotCount = 0;
        for (int i = 0, n = invBlockList.size(); i < n; i++) {
            if (invBlockList.get(i) != null)
                validSlotCount++;
        }*/

        inventorySlots = new int[invBlockList.size()];
        for (int i = 0, j = 0, n = invBlockList.size(); i < n; i++) {
            //if (invBlockList.get(i) != null)
            inventorySlots[j++] = i;
        }

        if (!worldObj.isRemote)
            syncClient();

        if (preCount != inventorySlots.length && (preCount == 0 || inventorySlots.length == 0)) {
            if (!worldObj.isRemote)
                markDirty();
        }
    }

    private void populateNode (BlockPos pos, int depth) {
        TileEntity te = worldObj.getTileEntity(pos);
        if (te == null || !(te instanceof IDrawerGroup))
            return;

        //if (te instanceof TileEntityController && depth < DEPTH_LIMIT) {
        //    populateNeighborNodes(x, y, z, depth + 1);
        //    return;
        //}

        if (storage.containsKey(pos)) {
            if (storage.get(pos) != null) {
                if (!mark.containsKey(pos)) {
                    mark.put(pos, true);
                    populateNeighborNodes(pos, depth + 1);
                }
                return;
            }
        }

        mark.put(pos, true);

        if (te instanceof TileEntityController) {
            storage.put(pos, null);

            invStorageSize.put(pos, 1);
            invBlockList.add(null);
            invSlotList.add(0);
        }
        else {
            IDrawerGroup group = (IDrawerGroup)te;
            IDrawerInventory inventory = group.getDrawerInventory();
            if (inventory == null)
                return;

            storage.put(pos, group);

            invStorageSize.put(pos, inventory.getSizeInventory());
            drawerStorageSize.put(pos, group.getDrawerCount());

            for (int i = 0, n = invStorageSize.get(pos); i < n; i++) {
                invBlockList.add(pos);
                invSlotList.add(i);
            }

            for (int i = 0, n = drawerStorageSize.get(pos); i < n; i++) {
                drawerBlockList.add(pos);
                drawerSlotList.add(i);
            }

            drawerSize += drawerStorageSize.get(pos);
        }

        if (depth == DEPTH_LIMIT)
            return;

        populateNeighborNodes(pos, depth + 1);
    }

    private void populateNeighborNodes (BlockPos pos, int depth) {
        populateNode(pos.east(), depth);
        populateNode(pos.west(), depth);
        populateNode(pos.down(), depth);
        populateNode(pos.up(), depth);
        populateNode(pos.north(), depth);
        populateNode(pos.south(), depth);
    }

    private IDrawerGroup getDrawerBlockForInv (int slot) {
        if (slot >= invBlockList.size())
            return null;

        BlockPos pos = invBlockList.get(slot);
        if (pos == null)
            return null;

        if (!storage.containsKey(pos))
            return null;

        TileEntity te = worldObj.getTileEntity(pos);
        if (!(te instanceof IDrawerGroup)) {
            storage.remove(pos);
            return null;
        }

        return storage.get(pos);
    }

    private IDrawerGroup getDrawerBlockForGroup (int slot) {
        if (slot >= drawerBlockList.size())
            return null;

        BlockPos pos = drawerBlockList.get(slot);
        if (pos == null)
            return null;

        if (!storage.containsKey(pos))
            return null;

        TileEntity te = worldObj.getTileEntity(pos);
        if (!(te instanceof IDrawerGroup)) {
            storage.remove(pos);
            return null;
        }

        return storage.get(pos);
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

        return new S35PacketUpdateTileEntity(getPos(), 5, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (getWorld().isRemote)
            getWorld().markBlockForUpdate(getPos());
    }

    private void syncClient () {
        IMessage message = new ControllerUpdateMessage(getPos(), inventorySlots);
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(worldObj.provider.getDimensionId(), getPos().getX(), getPos().getY(), getPos().getZ(), 500);

        StorageDrawers.network.sendToAllAround(message, targetPoint);
        worldObj.notifyNeighborsOfStateChange(getPos(), worldObj.getBlockState(getPos()).getBlock());
    }

    public void clientUpdate (int[] inventorySlots) {
        this.inventorySlots = inventorySlots;
    }

    @Override
    public IDrawerInventory getDrawerInventory () {
        return null;
    }

    @Override
    public int getDrawerCount () {
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
    public void markDirty () {
        for (IDrawerGroup group : storage.values()) {
            if (group != null && group.getDrawerInventory() != null)
                group.markDirtyIfNeeded();
        }

        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded () {
        boolean synced = false;

        for (IDrawerGroup group : storage.values()) {
            if (group != null && group.getDrawerInventory() != null)
                synced |= group.markDirtyIfNeeded();
        }

        if (synced)
            super.markDirty();

        return synced;
    }

    @Override
    public int[] getSlotsForFace (EnumFacing side) {
        for (int aside : autoSides) {
            if (side == aside)
                return inventorySlots;
        }

        return emptySlots;
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, EnumFacing side) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return false;

        return inventory.canInsertItem(getDrawerSlotForInv(slot), stack);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, EnumFacing side) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return false;

        return inventory.canExtractItem(getDrawerSlotForInv(slot), stack);
    }

    @Override
    public int getSizeInventory () {
        return inventorySlots.length;
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
        inventory.markDirty();
    }

    @Override
    public String getName () {
        // TODO
        return null;
    }

    @Override
    public boolean hasCustomName () {
        // TODO
        return false;
    }

    @Override
    public IChatComponent getDisplayName () {
        // TODO
        return null;
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
    public void openInventory (EntityPlayer player) { }

    @Override
    public void closeInventory (EntityPlayer player) { }

    @Override
    public int getField (int id) {
        return 0;
    }

    @Override
    public void setField (int id, int value) {}

    @Override
    public int getFieldCount () {
        return 0;
    }

    @Override
    public void clear () { }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return false;

        return inventory.isItemValidForSlot(getDrawerSlotForInv(slot), stack);
    }
}
