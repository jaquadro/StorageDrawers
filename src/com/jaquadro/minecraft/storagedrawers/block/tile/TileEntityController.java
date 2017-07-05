package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import com.jaquadro.minecraft.storagedrawers.util.ItemMetaCollectionRegistry;
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
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TileEntityController extends TileEntity implements IDrawerGroup, IPriorityGroup
{
    @CapabilityInject(IDrawerAttributes.class)
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = null;

    private static final IDrawerAttributes EMPTY_ATTRIBUTES = new EmptyDrawerAttributes();

    private static final int PRI_LOCKED = 0;
    private static final int PRI_LOCKED_VOID = 1;
    private static final int PRI_NORMAL = 2;
    private static final int PRI_VOID = 3;
    private static final int PRI_EMPTY = 4;
    private static final int PRI_LOCKED_EMPTY = 5;
    private static final int PRI_DISABLED = 6;

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

    protected static class SlotRecord implements Comparable<SlotRecord>
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

        @Override
        public int compareTo (SlotRecord other) {
            int diff = priority - other.priority;
            if (diff == 0) {
                diff = coord.compareTo(other.coord);
                if (diff == 0)
                    return index - other.index;

                return diff;
            }

            return diff;
        }
    }

    private Queue<BlockPos> searchQueue = new LinkedList<>();
    private Set<BlockPos> searchDiscovered = new HashSet<>();
    private Comparator<SlotRecord> slotRecordComparator = (o1, o2) -> o1.priority - o2.priority;

    private IDrawerAttributes getAttributes (Object obj) {
        IDrawerAttributes attrs = null;
        if (obj instanceof ICapabilityProvider)
            attrs = ((ICapabilityProvider) obj).getCapability(DRAWER_ATTRIBUTES_CAPABILITY, null);

        if (attrs == null)
            attrs = EMPTY_ATTRIBUTES;

        return attrs;
    }

    private int getSlotPriority (SlotRecord record) {
        IDrawerGroup group = getGroupForSlotRecord(record);
        if (group == null) {
            return PRI_DISABLED;
        }

        int drawerSlot = record.slot;
        IDrawer drawer = group.getDrawer(drawerSlot);
        if (!drawer.isEnabled()) {
            return PRI_DISABLED;
        }

        IDrawerAttributes attrs = getAttributes(group);

        if (drawer.isEmpty()) {
            if (attrs.isItemLocked(LockAttribute.LOCK_EMPTY))
                return PRI_LOCKED_EMPTY;
            else
                return PRI_EMPTY;
        }

        if (attrs.isVoid()) {
            if (attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
                return PRI_LOCKED_VOID;
            return PRI_VOID;
        }

        if (attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
            return PRI_LOCKED;

        return PRI_NORMAL;
    }

    private Map<BlockPos, StorageRecord> storage = new HashMap<>();
    protected List<SlotRecord> drawerSlotList = new ArrayList<>();

    private ItemMetaCollectionRegistry<SlotRecord> drawerPrimaryLookup = new ItemMetaCollectionRegistry<>();

    protected int[] drawerSlots = new int[0];
    private int range;

    private long lastUpdateTime;
    private long lastClickTime;
    private UUID lastClickUUID;

    public TileEntityController () {
        range = StorageDrawers.config.getControllerRange();
    }

    public void printDebugInfo () {
        StorageDrawers.log.info("Controller at " + pos.toString());
        StorageDrawers.log.info("  Range: " + range + " blocks");
        StorageDrawers.log.info("  Stored records: " + storage.size() + ", slot list: " + drawerSlots.length);
        StorageDrawers.log.info("  Ticks since last update: " + (getWorld().getTotalWorldTime() - lastUpdateTime));
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
        int remainder = new ProtectedItemRepository(profile).insertItem(stack, false).getCount();
        int added = stack.getCount() - remainder;

        stack.setCount(remainder);
        return added;
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
        Boolean template = null;
        boolean state = false;

        for (StorageRecord record : storage.values()) {
            if (record.storage == null)
                continue;

            if (record.storage instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable)record.storage))
                    continue;
            }

            IDrawerAttributes attrs = getAttributes(record.storage);
            if (!(attrs instanceof IDrawerAttributesModifiable))
                continue;

            IDrawerAttributesModifiable mattrs = (IDrawerAttributesModifiable)attrs;
            if (template == null) {
                template = mattrs.isConcealed();
                state = !template;
            }
            mattrs.setIsConcealed(state);
        }
    }

    public void toggleQuantified (GameProfile profile) {
        Boolean template = null;
        boolean state = false;

        for (StorageRecord record : storage.values()) {
            if (record.storage == null)
                continue;

            if (record.storage instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable)record.storage))
                    continue;
            }

            IDrawerAttributes attrs = getAttributes(record.storage);
            if (!(attrs instanceof IDrawerAttributesModifiable))
                continue;

            IDrawerAttributesModifiable mattrs = (IDrawerAttributesModifiable)attrs;
            if (template == null) {
                template = mattrs.isShowingQuantity();
                state = !template;
            }
            mattrs.setIsShowingQuantity(state);
        }
    }

    public void toggleLock (EnumSet<LockAttribute> attributes, LockAttribute key, GameProfile profile) {
        Boolean template = null;
        boolean state = false;

        for (StorageRecord record : storage.values()) {
            if (record.storage == null)
                continue;

            if (record.storage instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable)record.storage))
                    continue;
            }

            IDrawerAttributes attrs = getAttributes(record.storage);
            if (!(attrs instanceof IDrawerAttributesModifiable))
                continue;

            IDrawerAttributesModifiable mattrs = (IDrawerAttributesModifiable)attrs;
            if (template == null) {
                template = mattrs.isItemLocked(key);
                state = !template;
            }

            for (LockAttribute attr : attributes)
                mattrs.setItemLocked(attr, state);
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
        lastUpdateTime = getWorld().getTotalWorldTime();
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

    private void rebuildPrimaryLookup (ItemMetaCollectionRegistry<SlotRecord> lookup, List<SlotRecord> records) {
        lookup.clear();

        for (SlotRecord record : records) {
            IDrawerGroup group = getGroupForSlotRecord(record);
            if (group == null)
                continue;

            int drawerSlot = record.slot;
            IDrawer drawer = group.getDrawer(drawerSlot);
            if (!drawer.isEnabled() || drawer.isEmpty())
                continue;

            ItemStack item = drawer.getStoredItemPrototype();
            lookup.register(item.getItem(), item.getMetadata(), record);
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

            if (record.storage != null)
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
    @Nonnull
    public IDrawer getDrawer (int slot) {
        IDrawerGroup group = getGroupForDrawerSlot(slot);
        if (group == null)
            return Drawers.DISABLED;

        return group.getDrawer(getLocalDrawerSlot(slot));
    }

    @Override
    public int[] getAccessibleDrawerSlots () {
        return drawerSlots;
    }

    @CapabilityInject(IItemHandler.class)
    static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;
    @CapabilityInject(IItemRepository.class)
    static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = null;

    private DrawerItemHandler itemHandler = new DrawerItemHandler(this);
    private ItemRepository itemRepository = new ItemRepository();

    @Override
    public boolean hasCapability (@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ITEM_HANDLER_CAPABILITY)
            return true;
        if (capability == ITEM_REPOSITORY_CAPABILITY)
            return true;

        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability (@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        if (capability == ITEM_REPOSITORY_CAPABILITY)
            return (T) itemRepository;

        return super.getCapability(capability, facing);
    }

    private class ItemRepository implements IItemRepository
    {
        @Nonnull
        @Override
        public ItemStack insertItem (@Nonnull ItemStack stack, boolean simulate) {
            Collection<SlotRecord> primaryRecords = drawerPrimaryLookup.getEntries(stack.getItem(), stack.getMetadata());
            Set<Integer> checkedSlots = (simulate) ? new HashSet<>() : null;

            int amount = stack.getCount();
            if (primaryRecords != null) {
                for (SlotRecord record : primaryRecords) {
                    IDrawerGroup candidateGroup = getGroupForSlotRecord(record);
                    if (candidateGroup == null)
                        continue;

                    IDrawer drawer = candidateGroup.getDrawer(record.slot);
                    if (drawer.isEmpty() || !drawer.canItemBeStored(stack) || !hasAccess(candidateGroup, drawer))
                        continue;

                    amount = (simulate)
                        ? Math.max(amount - drawer.getAcceptingRemainingCapacity(), 0)
                        : drawer.adjustStoredItemCount(amount);

                    if (amount == 0)
                        return ItemStack.EMPTY;

                    if (simulate)
                        checkedSlots.add(record.index);
                }
            }

            for (int slot : drawerSlots) {
                IDrawer drawer = getDrawer(slot);
                if (!drawer.isEnabled() || !drawer.canItemBeStored(stack) || !hasAccess(getGroupForDrawerSlot(slot), drawer))
                    continue;
                if (simulate && checkedSlots.contains(slot))
                    continue;

                if (drawer.isEmpty())
                    drawer = drawer.setStoredItem(stack);

                amount = (simulate)
                    ? Math.max(amount - drawer.getAcceptingRemainingCapacity(), 0)
                    : drawer.adjustStoredItemCount(amount);

                if (amount == 0)
                    return ItemStack.EMPTY;
            }

            return stackResult(stack, amount);
        }

        @Nonnull
        @Override
        public ItemStack extractItem (@Nonnull ItemStack stack, int amount, boolean simulate) {
            Collection<SlotRecord> primaryRecords = drawerPrimaryLookup.getEntries(stack.getItem(), stack.getMetadata());
            Set<Integer> checkedSlots = (simulate) ? new HashSet<>() : null;

            int remaining = amount;
            for (SlotRecord record : primaryRecords) {
                IDrawerGroup candidateGroup = getGroupForSlotRecord(record);
                if (candidateGroup == null)
                    continue;

                IDrawer drawer = candidateGroup.getDrawer(record.slot);
                if (!drawer.canItemBeExtracted(stack) || !hasAccess(candidateGroup, drawer))
                    continue;

                remaining = (simulate)
                    ? Math.max(remaining - drawer.getStoredItemCount(), 0)
                    : drawer.adjustStoredItemCount(-remaining);

                if (remaining == 0)
                    return stackResult(stack, amount);

                if (simulate)
                    checkedSlots.add(record.index);
            }

            for (int slot : drawerSlots) {
                IDrawer drawer = getDrawer(slot);
                if (!drawer.canItemBeExtracted(stack) || !hasAccess(getGroupForDrawerSlot(slot), drawer))
                    continue;
                if (simulate && checkedSlots.contains(slot))
                    continue;

                if (remaining == 0)
                    return stackResult(stack, amount);
            }

            return (amount == remaining)
                ? ItemStack.EMPTY
                : stackResult(stack, amount - remaining);
        }

        protected boolean hasAccess (IDrawerGroup group, IDrawer drawer) {
            return true;
        }

        private ItemStack stackResult (@Nonnull ItemStack stack, int amount) {
            ItemStack result = stack.copy();
            result.setCount(amount);
            return result;
        }
    }

    private class ProtectedItemRepository extends ItemRepository
    {
        private GameProfile profile;

        public ProtectedItemRepository (GameProfile profile) {
            this.profile = profile;
        }

        @Override
        protected boolean hasAccess (IDrawerGroup group, IDrawer drawer) {
            if (drawer.isEmpty())
                return false;
            if (group instanceof IProtectable)
                return SecurityManager.hasAccess(profile, (IProtectable)group);

            return true;
        }
    }
}
