package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockControllerIO;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemRepository;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import com.jaquadro.minecraft.storagedrawers.util.ItemCollectionRegistry;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class BlockEntityController extends BaseBlockEntity implements IDrawerGroup
{
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static final int PRI_LOCKED = 0;
    private static final int PRI_NORMAL = 1;
    private static final int PRI_LOCKED_VOID = 2;
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

    private final Queue<BlockPos> searchQueue = new LinkedList<>();
    private final Set<BlockPos> searchDiscovered = new HashSet<>();
    private final Comparator<SlotRecord> slotRecordComparator = Comparator.comparingInt(o -> o.priority);

    private IDrawerAttributes getAttributes (Object obj) {
        IDrawerAttributes attrs = EmptyDrawerAttributes.EMPTY;
        if (obj instanceof ICapabilityProvider)
            attrs = ((ICapabilityProvider) obj).getCapability(DRAWER_ATTRIBUTES_CAPABILITY, null).orElse(EmptyDrawerAttributes.EMPTY);

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

    private final Map<BlockPos, StorageRecord> storage = new HashMap<>();
    protected List<SlotRecord> drawerSlotList = new ArrayList<>();

    private final ItemCollectionRegistry<SlotRecord> drawerPrimaryLookup = new ItemCollectionRegistry<>();

    protected int[] drawerSlots = new int[0];
    private final int range;

    private long lastUpdateTime;
    private long lastClickTime;
    private UUID lastClickUUID;

    protected BlockEntityController(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
        range = CommonConfig.GENERAL.controllerRange.get();
    }

    public BlockEntityController(BlockPos pos, BlockState state) {
        this(ModBlockEntities.CONTROLLER.get(), pos, state);
    }

    public void printDebugInfo () {
        StorageDrawers.log.info("Controller at " + worldPosition);
        StorageDrawers.log.info("  Range: " + range + " blocks");
        StorageDrawers.log.info("  Stored records: " + storage.size() + ", slot list: " + drawerSlots.length);
        StorageDrawers.log.info("  Ticks since last update: " + (getLevel() == null ? "null" : (getLevel().getGameTime() - lastUpdateTime)));
    }

    @Override
    public void clearRemoved () {
        super.clearRemoved();
        if (getLevel() == null)
            return;

        if (!getLevel().getBlockTicks().hasScheduledTick(getBlockPos(), ModBlocks.CONTROLLER.get()))
            getLevel().scheduleTick(getBlockPos(), ModBlocks.CONTROLLER.get(), 1);
    }

    @Override
    public boolean isGroupValid () {
        return !isRemoved();
    }

    public int interactPutItemsIntoInventory (Player player) {
        if (getLevel() == null)
            return 0;

        boolean dumpInventory = getLevel().getGameTime() - lastClickTime < 10 && player.getUUID().equals(lastClickUUID);
        int count = 0;

        if (!dumpInventory) {
            ItemStack currentStack = player.getInventory().getSelected();
            if (!currentStack.isEmpty()) {
                count = insertItems(currentStack, player.getGameProfile());
                if (currentStack.getCount() == 0)
                    player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
            }
        }
        else {
            for (int i = 0, n = player.getInventory().getContainerSize(); i < n; i++) {
                ItemStack subStack = player.getInventory().getItem(i);
                if (!subStack.isEmpty()) {
                    count += insertItems(subStack, player.getGameProfile());
                    if (subStack.getCount() == 0)
                        player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }

        lastClickTime = getLevel().getGameTime();
        lastClickUUID = player.getUUID();

        return count;
    }

    protected int insertItems (@NotNull ItemStack stack, GameProfile profile) {
        int remainder = new ProtectedItemRepository(this, profile).insertItem(stack, false).getCount();
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

            if (record.storage instanceof IProtectable protectable) {
                if (!SecurityManager.hasOwnership(profile, protectable))
                    continue;

                if (template == null) {
                    template = protectable;

                    if (template.getOwner() == null)
                        state = profile.getId();
                    else {
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
            if (!(attrs instanceof IDrawerAttributesModifiable mattrs))
                continue;

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
            if (!(attrs instanceof IDrawerAttributesModifiable mattrs))
                continue;

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
            if (!(attrs instanceof IDrawerAttributesModifiable mattrs))
                continue;

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
        if (getLevel() == null)
            return;

        lastUpdateTime = getLevel().getGameTime();
        int preCount = drawerSlots.length;

        resetCache();

        populateNodes(getBlockPos());

        flattenLists();
        drawerSlots = sortSlotRecords(drawerSlotList);

        rebuildPrimaryLookup(drawerPrimaryLookup, drawerSlotList);

        if (preCount != drawerSlots.length && (preCount == 0 || drawerSlots.length == 0)) {
            if (!getLevel().isClientSide)
                setChanged();
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
        copied.sort(slotRecordComparator);

        int[] slotMap = new int[copied.size()];
        for (int i = 0; i < slotMap.length; i++)
            slotMap[i] = copied.get(i).index;

        return slotMap;
    }

    private void rebuildPrimaryLookup (ItemCollectionRegistry<SlotRecord> lookup, List<SlotRecord> records) {
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
            lookup.register(item.getItem(), record);
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

    private void updateRecordInfo (BlockPos coord, StorageRecord record, BlockEntity blockEntity) {
        if (blockEntity == null) {
            if (record.storage != null)
                clearRecordInfo(coord, record);

            return;
        }

        if (blockEntity instanceof BlockEntityController) {
            if (record.storage == null && record.invStorageSize > 0)
                return;

            if (record.storage != null)
                clearRecordInfo(coord, record);

            record.storage = null;
        }
        else if (blockEntity instanceof BlockEntityControllerIO) {
            if (record.storage == null && record.invStorageSize == 0) {
                if (((BlockEntityControllerIO) blockEntity).getController() == this)
                    return;
            }

            if (record.storage != null)
                clearRecordInfo(coord, record);

            record.storage = null;

            ((BlockEntityControllerIO) blockEntity).bindController(getBlockPos());
        }
        else if (blockEntity instanceof BlockEntityDrawers) {
            IDrawerGroup group = ((BlockEntityDrawers) blockEntity).getGroup();
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
            IDrawerGroup group = blockEntity.getCapability(DRAWER_GROUP_CAPABILITY, null).orElse(null);
            if (record.storage == group)
                return;

            if (record.storage != null)
                clearRecordInfo(coord, record);
            if (group == null)
                return;

            record.storage = group;
            record.drawerStorageSize = group.getDrawerCount();

            for (int i = 0, n = record.drawerStorageSize; i < n; i++)
                drawerSlotList.add(new SlotRecord(group, coord, i));
        }
    }

    private void populateNodes (BlockPos root) {
        if (getLevel() == null)
            return;

        searchQueue.clear();
        searchQueue.add(root);

        searchDiscovered.clear();
        searchDiscovered.add(root);

        while (!searchQueue.isEmpty()) {
            BlockPos coord = searchQueue.remove();
            int depth = Math.max(Math.max(Math.abs(coord.getX() - root.getX()), Math.abs(coord.getY() - root.getY())), Math.abs(coord.getZ() - root.getZ()));
            if (depth > range)
                continue;

            if (!getLevel().isLoaded(coord))
                continue;

            Block block = getLevel().getBlockState(coord).getBlock();
            if (!(block instanceof INetworked))
                continue;

            StorageRecord record = storage.get(coord);
            if (record == null) {
                record = new StorageRecord();
                storage.put(coord, record);
            }

            if (block instanceof BlockControllerIO) {
                WorldUtils.getBlockEntity(getLevel(), coord, BlockEntityControllerIO.class);
            }

            updateRecordInfo(coord, record, getLevel().getBlockEntity(coord));
            record.mark = true;
            record.distance = depth;

            BlockPos[] neighbors = new BlockPos[]{
                coord.west(), coord.east(), coord.south(), coord.north(), coord.above(), coord.below()
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
        if (group == null || !group.isGroupValid())
            return null;

        if (group instanceof BlockEntity tile) {
            if (tile.isRemoved() || !tile.getBlockPos().equals(record.coord)) {
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
    public void readFixed (HolderLookup.Provider provider, CompoundTag tag) {
        super.readFixed(provider, tag);

        if (getLevel() != null && !getLevel().isClientSide)
            updateCache();
    }

    @Override
    public boolean dataPacketRequiresRenderUpdate () {
        return true;
    }

    @Override
    public int getDrawerCount () {
        return drawerSlotList.size();
    }

    @Override
    @NotNull
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

    public IItemRepository getItemRepository () {
        return itemRepository;
    }

    static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final DrawerItemHandler itemHandler = new DrawerItemHandler(this);
    private final ItemRepository itemRepository = new ItemRepository(this);

    private final LazyOptional<IItemHandler> capabilityItemHandler = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IItemRepository> capabilityItemRepository = LazyOptional.of(() -> itemRepository);
    private final LazyOptional<IDrawerGroup> capabilityGroup = LazyOptional.of(() -> this);

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability (@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (capability == ITEM_HANDLER_CAPABILITY)
            return capabilityItemHandler.cast();
        if (capability == ITEM_REPOSITORY_CAPABILITY)
            return capabilityItemRepository.cast();
        if (capability == DRAWER_GROUP_CAPABILITY)
            return capabilityGroup.cast();

        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        capabilityItemHandler.invalidate();
        capabilityItemRepository.invalidate();
        capabilityGroup.invalidate();
    }

    private class ItemRepository extends DrawerItemRepository
    {
        public ItemRepository (IDrawerGroup group) {
            super(group);
        }

        @NotNull
        @Override
        public ItemStack insertItem (@NotNull ItemStack stack, boolean simulate, Predicate<ItemStack> predicate) {
            Collection<SlotRecord> primaryRecords = drawerPrimaryLookup.getEntries(stack.getItem());
            Set<Integer> checkedSlots = (simulate) ? new HashSet<>() : null;

            int amount = stack.getCount();
            if (primaryRecords != null) {
                // First test by strict remaining capacity
                for (SlotRecord record : primaryRecords) {
                    IDrawerGroup candidateGroup = getGroupForSlotRecord(record);
                    if (candidateGroup == null)
                        continue;

                    IDrawer drawer = candidateGroup.getDrawer(record.slot);
                    if (drawer.isEmpty())
                        continue;
                    if (!testPredicateInsert(drawer, stack, predicate))
                        continue;
                    if (!hasAccess(candidateGroup, drawer))
                        continue;

                    int adjusted = Math.min(amount, drawer.getRemainingCapacity());
                    amount = (simulate)
                        ? Math.max(amount - drawer.getRemainingCapacity(), 0)
                        : (amount - adjusted) + drawer.adjustStoredItemCount(adjusted);

                    if (amount == 0)
                        return ItemStack.EMPTY;

                    if (simulate)
                        checkedSlots.add(record.index);
                }

                // Then relax to available capacity
                for (SlotRecord record : primaryRecords) {
                    IDrawerGroup candidateGroup = getGroupForSlotRecord(record);
                    if (candidateGroup == null)
                        continue;

                    IDrawer drawer = candidateGroup.getDrawer(record.slot);
                    if (drawer.isEmpty())
                        continue;
                    if (!testPredicateInsert(drawer, stack, predicate))
                        continue;
                    if (!hasAccess(candidateGroup, drawer))
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
                if (!drawer.isEnabled())
                    continue;
                if (!testPredicateInsert(drawer, stack, predicate))
                    continue;
                if (!hasAccess(getGroupForDrawerSlot(slot), drawer))
                    continue;
                if (simulate && checkedSlots.contains(slot))
                    continue;

                boolean empty = drawer.isEmpty();
                if (empty && !simulate)
                    drawer = drawer.setStoredItem(stack);

                amount = (simulate)
                    ? Math.max(amount - (empty ? drawer.getAcceptingMaxCapacity(stack) : drawer.getAcceptingRemainingCapacity()), 0)
                    : drawer.adjustStoredItemCount(amount);

                if (amount == 0)
                    return ItemStack.EMPTY;
            }

            return stackResult(stack, amount);
        }

        @NotNull
        @Override
        public ItemStack extractItem (@NotNull ItemStack stack, int amount, boolean simulate, Predicate<ItemStack> predicate) {
            Collection<SlotRecord> primaryRecords = drawerPrimaryLookup.getEntries(stack.getItem());
            Set<Integer> checkedSlots = (simulate) ? new HashSet<>() : null;

            int remaining = amount;
            if (primaryRecords != null) {
                for (SlotRecord record : primaryRecords) {
                    IDrawerGroup candidateGroup = getGroupForSlotRecord(record);
                    if (candidateGroup == null)
                        continue;

                    IDrawer drawer = candidateGroup.getDrawer(record.slot);
                    if (!drawer.isEnabled())
                        continue;
                    if (!testPredicateExtract(drawer, stack, predicate))
                        continue;
                    if (!hasAccess(candidateGroup, drawer))
                        continue;

                    remaining = (simulate)
                        ? Math.max(remaining - drawer.getStoredItemCount(), 0)
                        : drawer.adjustStoredItemCount(-remaining);

                    if (remaining == 0)
                        return stackResult(stack, amount);

                    if (simulate)
                        checkedSlots.add(record.index);
                }
            }

            for (int slot : drawerSlots) {
                IDrawer drawer = getDrawer(slot);
                if (!drawer.isEnabled())
                    continue;
                if (!testPredicateExtract(drawer, stack, predicate))
                    continue;
                if (simulate && checkedSlots.contains(slot))
                    continue;

                remaining = (simulate)
                    ? Math.max(remaining - drawer.getStoredItemCount(), 0)
                    : drawer.adjustStoredItemCount(-remaining);

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
    }

    private class ProtectedItemRepository extends ItemRepository
    {
        private final GameProfile profile;

        public ProtectedItemRepository (IDrawerGroup group, GameProfile profile) {
            super(group);
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
