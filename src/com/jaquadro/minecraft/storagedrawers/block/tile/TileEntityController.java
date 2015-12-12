package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroupInteractive;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ILockable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IShroudable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class TileEntityController extends TileEntity implements IDrawerGroup, ISidedInventory
{
    private static final int PRI_VOID = 0;
    private static final int PRI_LOCKED = 1;
    private static final int PRI_NORMAL = 2;
    private static final int PRI_EMPTY = 3;
    private static final int PRI_DISABLED = 4;

    private static final int DEPTH_LIMIT = 12;
    private static final int[] emptySlots = new int[0];

    private static class StorageRecord
    {
        public IDrawerGroup storage;
        public boolean mark;
        public int invStorageSize;
        public int drawerStorageSize;
        public int distance = Integer.MAX_VALUE;

        public void clear () {
            storage = null;
            mark = false;
            invStorageSize = 0;
            drawerStorageSize = 0;
            distance = Integer.MAX_VALUE;
        }
    }

    private static class SlotRecord
    {
        public BlockPos coord;
        public int slot;

        public int index;
        public int priority;

        public SlotRecord (BlockPos coord, int slot) {
            this.coord = coord;
            this.slot = slot;
        }
    }

    private Queue<BlockPos> searchQueue = new LinkedList<BlockPos>();
    private Set<BlockPos> searchDiscovered = new HashSet<BlockPos>();
    private Comparator<SlotRecord> slotRecordComparator = new Comparator<SlotRecord>()
    {
        @Override
        public int compare (SlotRecord o1, SlotRecord o2) {
            return o1.priority - o2.priority;
        }
    };

    private int getSlotPriority (SlotRecord record, boolean invBased) {
        IDrawerGroup group = getGroupForCoord(record.coord);
        if (group == null) {
            return PRI_DISABLED;
        }

        int drawerSlot = (invBased) ? group.getDrawerInventory().getDrawerSlot(record.slot) : record.slot;
        if (!group.isDrawerEnabled(drawerSlot)) {
            return PRI_DISABLED;
        }

        IDrawer drawer = group.getDrawer(drawerSlot);
        if (drawer.isEmpty()) {
            return PRI_EMPTY;
        }

        if ((drawer instanceof IVoidable && ((IVoidable) drawer).isVoid()) ||
            (group instanceof IVoidable && ((IVoidable) group).isVoid())) {
            return PRI_VOID;
        }

        if ((drawer instanceof ILockable && ((ILockable) drawer).isLocked(LockAttribute.LOCK_POPULATED)) ||
            (group instanceof ILockable && ((ILockable) group).isLocked(LockAttribute.LOCK_POPULATED))) {
            return PRI_LOCKED;
        }

        return PRI_NORMAL;
    }

    private Map<BlockPos, StorageRecord> storage = new HashMap<BlockPos, StorageRecord>();
    private List<SlotRecord> invSlotList = new ArrayList<SlotRecord>();
    private List<SlotRecord> drawerSlotList = new ArrayList<SlotRecord>();

    private int[] inventorySlots = new int[0];
    private int[] drawerSlots = new int[0];
    private int[] autoSides = new int[] { 0, 1, 2, 3, 4, 5 };

    private int drawerSize = 0;

    private long lastClickTime;
    private UUID lastClickUUID;

    private String customName;

    public TileEntityController () {
        invSlotList.add(new SlotRecord(null, 0));
        inventorySlots = new int[] { 0 };
    }

    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public int interactPutItemsIntoInventory (EntityPlayer player) {
        if (inventorySlots.length <= 1)
            updateCache();

        boolean dumpInventory = worldObj.getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID);
        int count = 0;

        for (int i = 0, n = drawerSlots.length; i < n; i++) {
            int slotIndex = drawerSlots[i];

            IDrawerGroup group = getGroupForDrawerSlot(slotIndex);
            if (group == null || !(group instanceof IDrawerGroupInteractive))
                continue;

            IDrawerGroupInteractive intGroup = (IDrawerGroupInteractive)group;

            int slot = getLocalDrawerSlot(slotIndex);
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

    public void toggleShroud () {
        IShroudable template = null;
        boolean state = false;

        for (StorageRecord record : storage.values()) {
            if (record.storage == null)
                continue;

            for (int i = 0, n = record.storage.getDrawerCount(); i < n; i++) {
                if (!record.storage.isDrawerEnabled(i))
                    continue;

                IDrawer drawer = record.storage.getDrawer(i);
                if (!(drawer instanceof IShroudable))
                    continue;

                IShroudable shroudableStorage = (IShroudable)drawer;
                if (template == null) {
                    template = shroudableStorage;
                    state = !template.isShrouded();
                }

                shroudableStorage.setIsShrouded(state);
            }
        }
    }

    public void toggleLock (EnumSet<LockAttribute> attributes, LockAttribute key) {
        ILockable template = null;
        boolean state = false;

        for (StorageRecord record : storage.values()) {
            if (record.storage == null)
                continue;

            if (record.storage instanceof ILockable) {
                ILockable lockableStorage = (ILockable)record.storage;
                if (template == null) {
                    template = lockableStorage;
                    state = !template.isLocked(key);
                }

                for (LockAttribute attr : attributes)
                    lockableStorage.setLocked(attr, state);
            }
            else {
                for (int i = 0, n = record.storage.getDrawerCount(); i < n; i++) {
                    if (!record.storage.isDrawerEnabled(i))
                        continue;

                    IDrawer drawer = record.storage.getDrawer(i);
                    if (!(drawer instanceof IShroudable))
                        continue;

                    ILockable lockableStorage = (ILockable)drawer;
                    if (template == null) {
                        template = lockableStorage;
                        state = !template.isLocked(key);
                    }

                    for (LockAttribute attr : attributes)
                        lockableStorage.setLocked(attr, state);
                }
            }
        }
    }

    private void resetCache () {
        storage.clear();
        invSlotList.clear();
        drawerSlotList.clear();
        drawerSize = 0;
    }

    public boolean isValidSlave (BlockPos coord) {
        StorageRecord record = storage.get(coord);
        if (record == null || !record.mark)
            return false;

        return record.storage == null;
    }

    public void updateCache () {
        int preCount = inventorySlots.length;

        resetCache();

        populateNodes(getPos());

        flattenLists();
        inventorySlots = sortSlotRecords(invSlotList, true); //sortInventorySlots();
        drawerSlots = sortSlotRecords(drawerSlotList, false);

        if (preCount != inventorySlots.length && (preCount == 0 || inventorySlots.length == 0)) {
            if (!worldObj.isRemote)
                markDirty();
        }
    }

    private void indexSlotRecords (List<SlotRecord> records, boolean invBased) {
        for (int i = 0, n = records.size(); i < n; i++) {
            SlotRecord record = records.get(i);
            if (record != null) {
                record.index = i;
                record.priority = getSlotPriority(record, invBased);
            }
        }
    }

    private int[] sortSlotRecords (List<SlotRecord> records, boolean invBased) {
        indexSlotRecords(records, invBased);

        List<SlotRecord> copied = new ArrayList<SlotRecord>(records);
        Collections.sort(copied, slotRecordComparator);

        int[] slotMap = new int[copied.size()];
        for (int i = 0; i < slotMap.length; i++)
            slotMap[i] = copied.get(i).index;

        return slotMap;
    }

    private boolean containsNullEntries (List<SlotRecord> list) {
        int nullCount = 0;
        for (int i = 0, n = list.size(); i < n; i++) {
            if (list.get(i) == null)
                nullCount++;
        }

        return nullCount > 0;
    }

    private void flattenLists () {
        if (containsNullEntries(invSlotList)) {
            List<SlotRecord> newInvSlotList = new ArrayList<SlotRecord>();

            for (int i = 0, n = invSlotList.size(); i < n; i++) {
                SlotRecord record = invSlotList.get(i);
                if (record != null)
                    newInvSlotList.add(record);
            }

            invSlotList = newInvSlotList;
        }

        if (containsNullEntries(drawerSlotList)) {
            List<SlotRecord> newDrawerSlotList = new ArrayList<SlotRecord>();

            for (int i = 0, n = drawerSlotList.size(); i < n; i++) {
                SlotRecord record = drawerSlotList.get(i);
                if (record != null)
                    newDrawerSlotList.add(record);
            }

            drawerSlotList = newDrawerSlotList;
        }
    }

    private void clearRecordInfo (BlockPos coord, StorageRecord record) {
        record.clear();

        for (int i = 0; i < invSlotList.size(); i++) {
            SlotRecord slotRecord = invSlotList.get(i);
            if (slotRecord != null && coord.equals(slotRecord.coord))
                invSlotList.set(i, null);
        }

        for (int i = 0; i < drawerSlotList.size(); i++) {
            SlotRecord slotRecord = drawerSlotList.get(i);
            if (slotRecord != null && coord.equals(slotRecord.coord))
                drawerSlotList.set(i, null);
        }
    }

    private void updateRecordInfo (BlockPos coord, StorageRecord record, TileEntity te) {
        if (te == null) {
            if (record.storage != null)
                clearRecordInfo(coord, record);

            return;
        }

        if (te instanceof TileEntityController) {
            if (record.storage == null && record.invStorageSize > 0)
                return;

            if (record.storage != null)
                clearRecordInfo(coord, record);

            record.storage = null;
            record.invStorageSize = 1;

            invSlotList.add(new SlotRecord(null, 0));
        }
        else if (te instanceof TileEntitySlave) {
            if (record.storage == null && record.invStorageSize == 0) {
                if (((TileEntitySlave) te).getController() == this)
                    return;
            }

            if (record.storage != null)
                clearRecordInfo(coord, record);

            record.storage = null;
            record.invStorageSize = 0;

            ((TileEntitySlave) te).bindController(getPos());
        }
        else if (te instanceof IDrawerGroup) {
            IDrawerGroup group = (IDrawerGroup)te;
            if (record.storage == group)
                return;

            if (record.storage != null && record.storage != group)
                clearRecordInfo(coord, record);

            IDrawerInventory inventory = group.getDrawerInventory();
            if (inventory == null)
                return;

            record.storage = group;
            record.invStorageSize = inventory.getSizeInventory();
            record.drawerStorageSize = group.getDrawerCount();

            for (int i = 0, n = record.invStorageSize; i < n; i++)
                invSlotList.add(new SlotRecord(coord, i));

            for (int i = 0, n = record.drawerStorageSize; i < n; i++)
                drawerSlotList.add(new SlotRecord(coord, i));

            drawerSize += record.drawerStorageSize;
        }
        else {
            if (record.storage != null)
                clearRecordInfo(coord, record);
        }
    }

    private void populateNodes (BlockPos root) {

        searchQueue.clear();
        searchQueue.add(root);

        searchDiscovered.clear();
        searchDiscovered.add(root);

        while (!searchQueue.isEmpty()) {
            BlockPos coord = searchQueue.remove();
            int depth = Math.max(Math.max(Math.abs(coord.getX() - root.getX()), Math.abs(coord.getY() - root.getY())), Math.abs(coord.getZ() - root.getZ()));
            if (depth > DEPTH_LIMIT)
                continue;

            Block block = worldObj.getBlockState(coord).getBlock();
            if (!(block instanceof INetworked))
                continue;

            StorageRecord record = storage.get(coord);
            if (record == null) {
                record = new StorageRecord();
                storage.put(coord, record);
            }

            if (block instanceof BlockSlave) {
                ((BlockSlave) block).getTileEntitySafe(worldObj, coord);
            }

            updateRecordInfo(coord, record, worldObj.getTileEntity(coord));
            record.mark = true;
            record.distance = depth;

            BlockPos[] neighbors = new BlockPos[]{
                coord.west(), coord.east(), coord.south(), coord.north(), coord.up(), coord.down()
            };

            for (BlockPos n : neighbors) {
                if (!searchDiscovered.contains(n)) {
                    searchQueue.add(n);
                    searchDiscovered.add(n);
                }
            }
        }
    }

    private IDrawerGroup getGroupForInvSlot (int invSlot) {
        if (invSlot >= invSlotList.size())
            return null;

        SlotRecord record = invSlotList.get(invSlot);
        if (record == null)
            return null;

        return getGroupForCoord(record.coord);
    }

    private IDrawerGroup getGroupForDrawerSlot (int drawerSlot) {
        if (drawerSlot >= drawerSlotList.size())
            return null;

        SlotRecord record = drawerSlotList.get(drawerSlot);
        if (record == null)
            return null;

        return getGroupForCoord(record.coord);
    }

    private IDrawerGroup getGroupForCoord (BlockPos coord) {
        if (coord == null || !storage.containsKey(coord))
            return null;

        TileEntity te = worldObj.getTileEntity(coord);
        if (!(te instanceof IDrawerGroup)) {
            storage.remove(coord);
            return null;
        }

        return storage.get(coord).storage;
    }

    private int getLocalInvSlot (int invSlot) {
        if (invSlot >= invSlotList.size())
            return 0;

        SlotRecord record = invSlotList.get(invSlot);
        if (record == null)
            return 0;

        return record.slot;
    }

    private int getLocalDrawerSlot (int drawerSlot) {
        if (drawerSlot >= drawerSlotList.size())
            return 0;

        SlotRecord record = drawerSlotList.get(drawerSlot);
        if (record == null)
            return 0;

        return record.slot;
    }

    private IDrawerInventory getDrawerInventory (int invSlot) {
        IDrawerGroup group = getGroupForInvSlot(invSlot);
        if (group == null)
            return null;

        return group.getDrawerInventory();
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("CustomName", Constants.NBT.TAG_STRING))
            customName = tag.getString("CustomName");

        if (worldObj != null && !worldObj.isRemote)
            updateCache();
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (hasCustomName())
            tag.setString("CustomName", customName);
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
        if (worldObj.isRemote)
            worldObj.markBlockForUpdate(getPos());
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
        IDrawerGroup group = getGroupForDrawerSlot(slot);
        if (group == null)
            return null;

        return group.getDrawer(getLocalDrawerSlot(slot));
    }

    @Override
    public boolean isDrawerEnabled (int slot) {
        IDrawerGroup group = getGroupForDrawerSlot(slot);
        if (group == null)
            return false;

        return group.isDrawerEnabled(getLocalDrawerSlot(slot));
    }

    @Override
    public void markDirty () {
        for (StorageRecord record : storage.values()) {
            IDrawerGroup group = record.storage;
            if (group != null && group.getDrawerInventory() != null)
                group.markDirtyIfNeeded();
        }

        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded () {
        boolean synced = false;

        for (StorageRecord record : storage.values()) {
            IDrawerGroup group = record.storage;
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
            if (side.ordinal() == aside)
                return inventorySlots;
        }

        return emptySlots;
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, EnumFacing side) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return false;

        return inventory.canInsertItem(getLocalInvSlot(slot), stack);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, EnumFacing side) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return false;

        return inventory.canExtractItem(getLocalInvSlot(slot), stack);
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

        return inventory.getStackInSlot(getLocalInvSlot(slot));
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return null;

        return inventory.decrStackSize(getLocalInvSlot(slot), count);
    }

    @Override
    public ItemStack removeStackFromSlot (int slot) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return null;

        return inventory.removeStackFromSlot(getLocalInvSlot(slot));
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return;

        inventory.setInventorySlotContents(getLocalInvSlot(slot), stack);
        inventory.markDirty();
    }

    public void setInventoryName (String name) {
        customName = name;
    }

    @Override
    public String getName () {
        return hasCustomName() ? customName : "storageDrawers.container.controller";
    }

    @Override
    public boolean hasCustomName () {
        return customName != null && customName.length() > 0;
    }

    @Override
    public IChatComponent getDisplayName () {
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
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        IDrawerInventory inventory = getDrawerInventory(slot);
        if (inventory == null)
            return false;

        return inventory.isItemValidForSlot(getLocalInvSlot(slot), stack);
    }

    @Override
    public int getField (int id) {
        return 0;
    }

    @Override
    public void setField (int id, int value) {

    }

    @Override
    public int getFieldCount () {
        return 0;
    }

    @Override
    public void clear () {

    }
}
