package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.*;
import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import com.jaquadro.minecraft.storagedrawers.util.ItemMetaListRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

public class TileEntityController extends TileEntity implements IDrawerGroup, IPriorityGroup, ISmartGroup
{
    private static final int PRI_VOID = 0;
    private static final int PRI_LOCKED = 1;
    private static final int PRI_NORMAL = 2;
    private static final int PRI_EMPTY = 3;
    private static final int PRI_LOCKED_EMPTY = 4;
    private static final int PRI_DISABLED = 5;

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

    protected static class SlotRecord
    {
        public BlockPos coord;
        public IDrawerGroup group;
        public int slot;

        public int index;
        public int priority;

        public SlotRecord (IDrawerGroup group, BlockPos coord, int slot) {
            this.group = group;
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

    private int getSlotPriority (SlotRecord record) {
        IDrawerGroup group = getGroupForSlotRecord(record);
        if (group == null) {
            return PRI_DISABLED;
        }

        int drawerSlot = record.slot;
        IDrawer drawer = group.getDrawerIfEnabled(drawerSlot);
        if (drawer == null) {
            return PRI_DISABLED;
        }

        if (drawer.isEmpty()) {
            if ((drawer instanceof IItemLockable && ((IItemLockable) drawer).isItemLocked(LockAttribute.LOCK_EMPTY)) ||
                (group instanceof IItemLockable && ((IItemLockable) group).isItemLocked(LockAttribute.LOCK_EMPTY))) {
                return PRI_LOCKED_EMPTY;
            }
            else
                return PRI_EMPTY;
        }

        if ((drawer instanceof IVoidable && ((IVoidable) drawer).isVoid()) ||
            (group instanceof IVoidable && ((IVoidable) group).isVoid())) {
            return PRI_VOID;
        }

        if ((drawer instanceof IItemLockable && ((IItemLockable) drawer).isItemLocked(LockAttribute.LOCK_POPULATED)) ||
            (group instanceof IItemLockable && ((IItemLockable) group).isItemLocked(LockAttribute.LOCK_POPULATED))) {
            return PRI_LOCKED;
        }

        return PRI_NORMAL;
    }

    private Map<BlockPos, StorageRecord> storage = new HashMap<BlockPos, StorageRecord>();
    protected List<SlotRecord> drawerSlotList = new ArrayList<SlotRecord>();

    private ItemMetaListRegistry<SlotRecord> drawerPrimaryLookup = new ItemMetaListRegistry<SlotRecord>();

    protected int[] drawerSlots = new int[0];
    private int range;

    private long lastClickTime;
    private UUID lastClickUUID;

    public TileEntityController () {
        range = StorageDrawers.config.getControllerRange();
    }

    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public int interactPutItemsIntoInventory (EntityPlayer player) {
        boolean dumpInventory = getWorld().getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID);
        int count = 0;

        if (!dumpInventory) {
            ItemStack currentStack = player.inventory.getCurrentItem();
            if (!currentStack.isEmpty()) {
                count = insertItems(currentStack, player.getGameProfile());
                if (currentStack.getCount() == 0)
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
            }
        }
        else {
            for (int i = 0, n = player.inventory.getSizeInventory(); i < n; i++) {
                ItemStack subStack = player.inventory.getStackInSlot(i);
                if (!subStack.isEmpty()) {
                    count += insertItems(subStack, player.getGameProfile());
                    if (subStack.getCount() == 0)
                        player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }

            if (count > 0)
                StorageDrawers.proxy.updatePlayerInventory(player);
        }

        lastClickTime = getWorld().getTotalWorldTime();
        lastClickUUID = player.getPersistentID();

        return count;
    }

    protected int insertItems (@Nonnull ItemStack stack, GameProfile profile) {
        int itemsLeft = stack.getCount();

        for (int slot : enumerateDrawersForInsertion(stack, false)) {
            IDrawerGroup group = getGroupForDrawerSlot(slot);
            if (group instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable)group))
                    continue;
            }

            IDrawer drawer = getDrawer(slot);
            if (drawer.isEmpty())
                break;

            itemsLeft = insertItemsIntoDrawer(drawer, itemsLeft);

            if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid())
                itemsLeft = 0;
            if (itemsLeft == 0)
                break;
        }

        int count = stack.getCount() - itemsLeft;
        stack.setCount(itemsLeft);

        return count;
    }

    protected int insertItemsIntoDrawer (IDrawer drawer, int itemCount) {
        int capacity = drawer.getMaxCapacity();
        int storedItems = drawer.getStoredItemCount();

        int storableItems = capacity - storedItems;
        if (drawer instanceof IFractionalDrawer) {
            IFractionalDrawer fracDrawer = (IFractionalDrawer)drawer;
            if (!fracDrawer.isSmallestUnit() && fracDrawer.getStoredItemRemainder() > 0)
                storableItems--;
        }

        if (storableItems == 0)
            return itemCount;

        int remainder = Math.max(itemCount - storableItems, 0);
        storedItems += Math.min(itemCount, storableItems);
        drawer.setStoredItemCount(storedItems);

        return remainder;
    }

    public void toggleProtection (GameProfile profile, ISecurityProvider provider) {
        IProtectable template = null;
        UUID state = null;

        for (StorageRecord record : storage.values()) {
            if (record.storage == null)
                continue;

            if (record.storage instanceof IProtectable) {
                IProtectable protectable = (IProtectable)record.storage;
                if (!SecurityManager.hasOwnership(profile, protectable))
                    continue;

                if (template == null) {
                    template = protectable;

                    if (template.getOwner() == null)
                        state = profile.getId();
                    else {
                        state = null;
                        provider = null;
                    }
                }

                protectable.setOwner(state);
                protectable.setSecurityProvider(provider);
            }
        }
    }

    public void toggleShroud (GameProfile profile) {
        IShroudable template = null;
        boolean state = false;

        for (StorageRecord record : storage.values()) {
            if (record.storage == null)
                continue;

            if (record.storage instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable)record.storage))
                    continue;
            }

            for (int i = 0, n = record.storage.getDrawerCount(); i < n; i++) {
                IDrawer drawer = record.storage.getDrawerIfEnabled(i);
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

    public void toggleLock (EnumSet<LockAttribute> attributes, LockAttribute key, GameProfile profile) {
        IItemLockable template = null;
        boolean state = false;

        for (StorageRecord record : storage.values()) {
            if (record.storage == null)
                continue;

            if (record.storage instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable)record.storage))
                    continue;
            }

            if (record.storage instanceof IItemLockable) {
                IItemLockable lockableStorage = (IItemLockable)record.storage;
                if (template == null) {
                    template = lockableStorage;
                    state = !template.isItemLocked(key);
                }

                for (LockAttribute attr : attributes)
                    lockableStorage.setItemLocked(attr, state);
            }
            else {
                for (int i = 0, n = record.storage.getDrawerCount(); i < n; i++) {
                    IDrawer drawer = record.storage.getDrawerIfEnabled(i);
                    if (!(drawer instanceof IShroudable))
                        continue;

                    IItemLockable lockableStorage = (IItemLockable)drawer;
                    if (template == null) {
                        template = lockableStorage;
                        state = !template.isItemLocked(key);
                    }

                    for (LockAttribute attr : attributes)
                        lockableStorage.setItemLocked(attr, state);
                }
            }
        }
    }

    protected void resetCache () {
        storage.clear();
        drawerSlotList.clear();
    }

    public boolean isValidSlave (BlockPos coord) {
        StorageRecord record = storage.get(coord);
        if (record == null || !record.mark)
            return false;

        return record.storage == null;
    }

    public void updateCache () {
        int preCount = drawerSlots.length;

        resetCache();

        populateNodes(getPos());

        flattenLists();
        drawerSlots = sortSlotRecords(drawerSlotList);

        rebuildPrimaryLookup(drawerPrimaryLookup, drawerSlotList);

        if (preCount != drawerSlots.length && (preCount == 0 || drawerSlots.length == 0)) {
            if (!getWorld().isRemote)
                markDirty();
        }
    }

    private void indexSlotRecords (List<SlotRecord> records) {
        for (int i = 0, n = records.size(); i < n; i++) {
            SlotRecord record = records.get(i);
            if (record != null) {
                record.index = i;
                record.priority = getSlotPriority(record);
            }
        }
    }

    private int[] sortSlotRecords (List<SlotRecord> records) {
        indexSlotRecords(records);

        List<SlotRecord> copied = new ArrayList<>(records);
        Collections.sort(copied, slotRecordComparator);

        int[] slotMap = new int[copied.size()];
        for (int i = 0; i < slotMap.length; i++)
            slotMap[i] = copied.get(i).index;

        return slotMap;
    }

    private void rebuildPrimaryLookup (ItemMetaListRegistry<SlotRecord> lookup, List<SlotRecord> records) {
        lookup.clear();

        for (SlotRecord record : records) {
            IDrawerGroup group = getGroupForSlotRecord(record);
            if (group == null)
                continue;

            int drawerSlot = record.slot;
            IDrawer drawer = group.getDrawerIfEnabled(drawerSlot);
            if (drawer == null)
                continue;

            if (drawer.isEmpty())
                continue;

            ItemStack item = drawer.getStoredItemPrototype();
            lookup.register(item.getItem(), item.getItemDamage(), record);
        }
    }

    private boolean containsNullEntries (List<SlotRecord> list) {
        int nullCount = 0;
        for (SlotRecord aList : list) {
            if (aList == null)
                nullCount++;
        }

        return nullCount > 0;
    }

    private void flattenLists () {
        if (containsNullEntries(drawerSlotList)) {
            List<SlotRecord> newDrawerSlotList = new ArrayList<>();

            for (SlotRecord record : drawerSlotList) {
                if (record != null)
                    newDrawerSlotList.add(record);
            }

            drawerSlotList = newDrawerSlotList;
        }
    }

    private void clearRecordInfo (BlockPos coord, StorageRecord record) {
        record.clear();

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
        }
        else if (te instanceof TileEntitySlave) {
            if (record.storage == null && record.invStorageSize == 0) {
                if (((TileEntitySlave) te).getController() == this)
                    return;
            }

            if (record.storage != null)
                clearRecordInfo(coord, record);

            record.storage = null;

            ((TileEntitySlave) te).bindController(getPos());
        }
        else if (te instanceof IDrawerGroup) {
            IDrawerGroup group = (IDrawerGroup)te;
            if (record.storage == group)
                return;

            if (record.storage != null && record.storage != group)
                clearRecordInfo(coord, record);

            record.storage = group;
            record.drawerStorageSize = group.getDrawerCount();

            for (int i = 0, n = record.drawerStorageSize; i < n; i++)
                drawerSlotList.add(new SlotRecord(group, coord, i));
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
            if (depth > range)
                continue;

            Block block = getWorld().getBlockState(coord).getBlock();
            if (!(block instanceof INetworked))
                continue;

            StorageRecord record = storage.get(coord);
            if (record == null) {
                record = new StorageRecord();
                storage.put(coord, record);
            }

            if (block instanceof BlockSlave) {
                ((BlockSlave) block).getTileEntitySafe(getWorld(), coord);
            }

            updateRecordInfo(coord, record, getWorld().getTileEntity(coord));
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

    protected IDrawerGroup getGroupForDrawerSlot (int drawerSlot) {
        if (drawerSlot < 0 || drawerSlot >= drawerSlotList.size())
            return null;

        SlotRecord record = drawerSlotList.get(drawerSlot);
        if (record == null)
            return null;

        return getGroupForSlotRecord(record);
    }

    protected IDrawerGroup getGroupForSlotRecord (SlotRecord record) {
        IDrawerGroup group = record.group;
        if (group == null)
            return null;

        if (group instanceof TileEntity) {
            TileEntity tile = (TileEntity)group;
            if (tile.isInvalid() || !tile.getPos().equals(record.coord)) {
                record.group = null;
                return null;
            }
        }

        return group;
    }

    private int getLocalDrawerSlot (int drawerSlot) {
        if (drawerSlot >= drawerSlotList.size())
            return 0;

        SlotRecord record = drawerSlotList.get(drawerSlot);
        if (record == null)
            return 0;

        return record.slot;
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (getWorld() != null && !getWorld().isRemote)
            updateCache();
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        return tag;
    }

    @Override
    public NBTTagCompound getUpdateTag () {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return tag;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket () {
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
    }

    @Override
    public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (getWorld().isRemote) {
            IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
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
    public IDrawer getDrawerIfEnabled (int slot) {
        IDrawerGroup group = getGroupForDrawerSlot(slot);
        if (group == null)
            return null;

        int localSlot = getLocalDrawerSlot(slot);
        return group.getDrawerIfEnabled(localSlot);
    }

    @Override
    public int[] getAccessibleDrawerSlots () {
        return drawerSlots;
    }

    @Override
    public void markDirty () {
        for (StorageRecord record : storage.values()) {
            IDrawerGroup group = record.storage;
            if (group != null)
                group.markDirtyIfNeeded();
        }

        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded () {
        boolean synced = false;

        for (StorageRecord record : storage.values()) {
            IDrawerGroup group = record.storage;
            if (group != null)
                synced |= group.markDirtyIfNeeded();
        }

        if (synced)
            super.markDirty();

        return synced;
    }

    private DrawerItemHandler itemHandler = new DrawerItemHandler(this);

    @Override
    public boolean hasCapability (Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        return super.getCapability(capability, facing);
    }

    private class DrawerStackIterator implements Iterable<Integer>
    {
        @Nonnull
        private ItemStack stack;
        private boolean strict;
        private boolean insert;

        public DrawerStackIterator (@Nonnull ItemStack stack, boolean strict, boolean insert) {
            this.stack = stack;
            this.strict = strict;
            this.insert = insert;
        }

        @Override
        public Iterator<Integer> iterator () {
            return new Iterator<Integer> ()
            {
                List<SlotRecord> primaryRecords = drawerPrimaryLookup.getEntries(stack.getItem(), stack.getItemDamage());
                Iterator<SlotRecord> iter1;
                int index2;
                Integer nextSlot = null;

                @Override
                public boolean hasNext () {
                    if (nextSlot == null)
                        advance();
                    return nextSlot != null;
                }

                @Override
                public Integer next () {
                    if (nextSlot == null)
                        advance();

                    Integer slot = nextSlot;
                    nextSlot = null;
                    return slot;
                }

                private void advance () {
                    if (iter1 == null && primaryRecords != null && primaryRecords.size() > 0)
                        iter1 = primaryRecords.iterator();

                    if (iter1 != null) {
                        while (iter1.hasNext()) {
                            SlotRecord candidate = iter1.next();
                            IDrawerGroup candidateGroup = getGroupForSlotRecord(candidate);
                            if (candidateGroup == null)
                                continue;

                            IDrawer drawer = candidateGroup.getDrawer(candidate.slot);

                            if (insert) {
                                boolean voiding = (drawer instanceof IVoidable) ? ((IVoidable) drawer).isVoid() : false;
                                if (!(drawer.canItemBeStored(stack) && (drawer.isEmpty() || drawer.getRemainingCapacity() > 0 || voiding)))
                                    continue;
                            }
                            else {
                                if (!(drawer.canItemBeExtracted(stack) && drawer.getStoredItemCount() > 0))
                                    continue;
                            }

                            int slot = drawerSlotList.indexOf(candidate);
                            if (slot > -1) {
                                nextSlot = slot;
                                return;
                            }
                        }
                    }

                    for (; index2 < drawerSlots.length; index2++) {
                        int slot = drawerSlots[index2];
                        IDrawer drawer = getDrawerIfEnabled(slot);
                        if (drawer == null)
                            continue;

                        if (strict) {
                            ItemStack proto = drawer.getStoredItemPrototype();
                            if (!proto.isItemEqual(stack))
                                continue;
                        }

                        if (insert) {
                            boolean voiding = (drawer instanceof IVoidable) ? ((IVoidable) drawer).isVoid() : false;
                            if (!(drawer.canItemBeStored(stack) && (drawer.isEmpty() || drawer.getRemainingCapacity() > 0 || voiding)))
                                continue;
                        }
                        else {
                            if (!(drawer.canItemBeExtracted(stack) && drawer.getStoredItemCount() > 0))
                                continue;
                        }

                        SlotRecord record = drawerSlotList.get(slot);
                        if (primaryRecords != null && primaryRecords.contains(record))
                            continue;

                        nextSlot = slot;
                        index2++;
                        return;
                    }
                }
            };
        }
    };

    public Iterable<Integer> enumerateDrawersForInsertion (@Nonnull ItemStack stack, boolean strict) {
        return new DrawerStackIterator(stack, strict, true);
    }

    public Iterable<Integer> enumerateDrawersForExtraction (@Nonnull ItemStack stack, boolean strict) {
        return new DrawerStackIterator(stack, strict, false);
    }
}
