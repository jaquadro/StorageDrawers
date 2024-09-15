package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IDrawerCapability;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.capabilities.BasicDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.capabilities.Capabilities;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeRedstone;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.network.MessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public abstract class BlockEntityDrawers extends BaseBlockEntity implements IDrawerGroup /* IProtectable, INameable */
{
    public static final ModelProperty<IDrawerAttributes> ATTRIBUTES = new ModelProperty<>();
    //public static final ModelProperty<Boolean> ITEM_LOCKED = new ModelProperty<>();
    //public static final ModelProperty<Boolean> SHROUDED = new ModelProperty<>();
    //public static final ModelProperty<Boolean> VOIDING = new ModelProperty<>();

    //private CustomNameData customNameData = new CustomNameData("storagedrawers.container.drawers");
    //private MaterialData materialData = new MaterialData();
    private final UpgradeData upgradeData = new DrawerUpgradeData();

    //public final ControllerData controllerData = new ControllerData();

    //private int direction;
    //private String material;
    //private boolean taped = false;
    //private UUID owner;
    //private String securityKey;

    private final IDrawerAttributesModifiable drawerAttributes;

    private long lastClickTime;
    private UUID lastClickUUID;
    private boolean loading;

    private class DrawerAttributes extends BasicDrawerAttributes
    {
        @Override
        protected void onAttributeChanged () {
            if (!loading && !BlockEntityDrawers.this.drawerAttributes.isItemLocked(LockAttribute.LOCK_POPULATED)) {
                for (int slot = 0; slot < BlockEntityDrawers.this.getGroup().getDrawerCount(); slot++) {
                    if (BlockEntityDrawers.this.emptySlotCanBeCleared(slot)) {
                        IDrawer drawer = BlockEntityDrawers.this.getGroup().getDrawer(slot);
                        drawer.setStoredItem(ItemStack.EMPTY);
                    }
                }
            }

            BlockEntityDrawers.this.onAttributeChanged();
            if (getLevel() != null && !getLevel().isClientSide) {
                setChanged();
                markBlockForUpdate();
            }
        }
    }

    private class DrawerUpgradeData extends UpgradeData
    {
        DrawerUpgradeData () {
            super(7);
        }

        @Override
        public boolean canAddUpgrade (@NotNull ItemStack upgrade) {
            if (!super.canAddUpgrade(upgrade))
                return false;

            if (upgrade.getItem() == ModItems.ONE_STACK_UPGRADE.get()) {
                int lostStackCapacity = upgradeData.getStorageMultiplier() * (getEffectiveDrawerCapacity() - 1);
                return stackCapacityCheck(lostStackCapacity);
            }

            return true;
        }

        @Override
        public boolean canRemoveUpgrade (int slot) {
            if (!super.canRemoveUpgrade(slot))
                return false;

            ItemStack upgrade = getUpgrade(slot);
            if (upgrade.getItem() instanceof ItemUpgradeStorage) {
                int storageLevel = ((ItemUpgradeStorage) upgrade.getItem()).level.getLevel();
                int storageMult = ModCommonConfig.INSTANCE.UPGRADES.getLevelMult(storageLevel);
                int effectiveStorageMult = upgradeData.getStorageMultiplier();
                if (effectiveStorageMult == storageMult)
                    storageMult--;

                int addedStackCapacity = storageMult * getEffectiveDrawerCapacity();
                return stackCapacityCheck(addedStackCapacity);
            }

            return true;
        }

        @Override
        public boolean canSwapUpgrade(int slot, @NotNull ItemStack add) {
            if (!(add.getItem() instanceof ItemUpgradeStorage))
                return false;

            ItemStack upgrade = getUpgrade(slot);
            if (upgrade.getItem() == ModItems.ONE_STACK_UPGRADE.get())
                return true;

            if (!(upgrade.getItem() instanceof ItemUpgradeStorage))
                return false;

            if (!canAddUpgrade(add))
                return false;

            if (((ItemUpgradeStorage) add.getItem()).level.getLevel() > ((ItemUpgradeStorage) upgrade.getItem()).level.getLevel())
                return true;

            // New item is a downgrade
            int currentUpgradeMult = upgradeData.getStorageMultiplier();
            int storageLevel = ((ItemUpgradeStorage) upgrade.getItem()).level.getLevel();
            int storageMult = ModCommonConfig.INSTANCE.UPGRADES.getLevelMult(storageLevel);

            // The below first calculates the amount of stacks to remove if the multiplier stayed the same, then adds the removed multiplier,
            // which results in the amount of stacks (storage) to remove. The addition would be multiplied by
            // the stacks to scale to, but in this case, that is 1.

            // We need the below removed stacks calculation to be less than or equal to
            // currentUpgradeMult * getEffectiveDrawerCapacity - 1, as otherwise, the calculated stacks to remove will be equal
            // to the current max stack size of the drawer, which will result in a calculation of 0 stacks.

            int removedStacks = Math.min(currentUpgradeMult * getEffectiveDrawerCapacity() - 1,
                currentUpgradeMult * (getEffectiveDrawerCapacity() - 1) + storageMult);
            return stackCapacityCheck(removedStacks);
        }

        @Override
        protected void onUpgradeChanged (ItemStack oldUpgrade, ItemStack newUpgrade) {

            if (getLevel() != null && !getLevel().isClientSide) {
                setChanged();
                markBlockForUpdate();
            }
        }

        private boolean stackCapacityCheck (int stackCapacity) {
            for (int i = 0; i < getDrawerCount(); i++) {
                IDrawer drawer = getDrawer(i);
                if (!drawer.isEnabled() || drawer.isEmpty())
                    continue;

                int addedItemCapacity = stackCapacity * drawer.getStoredItemStackSize();
                if (drawer.getMaxCapacity() - addedItemCapacity < drawer.getStoredItemCount())
                    return false;
            }

            return true;
        }
    }

    protected BlockEntityDrawers(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);

        drawerAttributes = new DrawerAttributes();

        upgradeData.setDrawerAttributes(drawerAttributes);

        //injectPortableData(customNameData);
        injectPortableData(upgradeData);
        //injectPortableData(materialData);
        //injectData(controllerData);
    }

    @NotNull
    public abstract IDrawerGroup getGroup ();

    @NotNull
    public IDrawerAttributes getDrawerAttributes () {
        return drawerAttributes;
    }

    public UpgradeData upgrades () {
        return upgradeData;
    }

    //public MaterialData material () {
    //    return materialData;
    //}


    @Override
    public boolean isGroupValid () {
        return !isRemoved();
    }

    public int getDrawerCapacity () {
        Block block = getBlockState().getBlock();
        if (!(block instanceof BlockDrawers))
            return 0;

        return ((BlockDrawers)block).getStorageUnits();
    }

    public int getEffectiveDrawerCapacity () {
        if (upgradeData.hasOneStackUpgrade())
            return 1;

        return getDrawerCapacity() * ModCommonConfig.INSTANCE.GENERAL.baseStackStorage.get();
    }

    protected boolean emptySlotCanBeCleared (int slot) {
        IDrawer drawer = BlockEntityDrawers.this.getGroup().getDrawer(slot);
        return !drawer.isEmpty() && drawer.getStoredItemCount() == 0;
    }

    /*@Override
    public UUID getOwner () {
        if (!StorageDrawers.config.cache.enablePersonalUpgrades)
            return null;

        return owner;
    }

    @Override
    public boolean setOwner (UUID owner) {
        if (!StorageDrawers.config.cache.enablePersonalUpgrades)
            return false;

        if ((this.owner != null && !this.owner.equals(owner)) || (owner != null && !owner.equals(this.owner))) {
            this.owner = owner;

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }

        return true;
    }

    @Override
    public ISecurityProvider getSecurityProvider () {
        return StorageDrawers.securityRegistry.getProvider(securityKey);
    }

    @Override
    public boolean setSecurityProvider (ISecurityProvider provider) {
        if (!StorageDrawers.config.cache.enablePersonalUpgrades)
            return false;

        String newKey = (provider == null) ? null : provider.getProviderID();
        if ((newKey != null && !newKey.equals(securityKey)) || (securityKey != null && !securityKey.equals(newKey))) {
            securityKey = newKey;

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }

        return true;
    }*/

    protected void onAttributeChanged () {
        requestModelDataUpdate();
        //refreshModelData();
    }

    /*public boolean isSealed () {
        if (!StorageDrawers.config.cache.enableTape)
            return false;

        return taped;
    }

    public boolean setIsSealed (boolean sealed) {
        if (!StorageDrawers.config.cache.enableTape)
            return false;

        if (this.taped != sealed) {
            this.taped = sealed;

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }

        return true;
    }*/

    public boolean isRedstone () {
        //if (!StorageDrawers.config.cache.enableRedstoneUpgrades)
        //    return false;

        return upgradeData.getRedstoneType() != null;
    }

    public int getRedstoneLevel () {
        EnumUpgradeRedstone type = upgradeData.getRedstoneType();
        if (type == null)
            return 0;

        return switch (type) {
            case COMBINED -> getCombinedRedstoneLevel();
            case MAX -> getMaxRedstoneLevel();
            case MIN -> getMinRedstoneLevel();
        };
    }

    protected int getCombinedRedstoneLevel () {
        int active = 0;
        float fillRatio = 0;

        for (int i = 0; i < getDrawerCount(); i++) {
            IDrawer drawer = getDrawer(i);
            if (!drawer.isEnabled())
                continue;

            if (drawer.getMaxCapacity() > 0)
                fillRatio += ((float)drawer.getStoredItemCount() / drawer.getMaxCapacity());

            active++;
        }

        if (active == 0)
            return 0;

        if (fillRatio == active)
            return 15;

        return (int)Math.ceil((fillRatio / active) * 14);
    }

    protected int getMinRedstoneLevel () {
        float minRatio = 2;

        for (int i = 0; i < getDrawerCount(); i++) {
            IDrawer drawer = getDrawer(i);
            if (!drawer.isEnabled())
                continue;

            if (drawer.getMaxCapacity() > 0)
                minRatio = Math.min(minRatio, (float)drawer.getStoredItemCount() / drawer.getMaxCapacity());
            else
                minRatio = 0;
        }

        if (minRatio > 1)
            return 0;
        if (minRatio == 1)
            return 15;

        return (int)Math.ceil(minRatio * 14);
    }

    protected int getMaxRedstoneLevel () {
        float maxRatio = 0;

        for (int i = 0; i < getDrawerCount(); i++) {
            IDrawer drawer = getDrawer(i);
            if (!drawer.isEnabled())
                continue;

            if (drawer.getMaxCapacity() > 0)
                maxRatio = Math.max(maxRatio, (float)drawer.getStoredItemCount() / drawer.getMaxCapacity());
        }

        if (maxRatio == 1)
            return 15;

        return (int)Math.ceil(maxRatio * 14);
    }

    @NotNull
    public ItemStack takeItemsFromSlot (int slot, int count) {
        IDrawer drawer = getGroup().getDrawer(slot);
        if (!drawer.isEnabled() || drawer.isEmpty())
            return ItemStack.EMPTY;

        ItemStack stack = drawer.getStoredItemPrototype().copy();
        stack.setCount(Math.min(count, drawer.getStoredItemCount()));

        drawer.setStoredItemCount(drawer.getStoredItemCount() - stack.getCount());

        if (isRedstone() && getLevel() != null) {
            getLevel().updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
            getLevel().updateNeighborsAt(getBlockPos().below(), getBlockState().getBlock());
        }

        // TODO: Reset empty drawer in subclasses

        return stack;
    }

    public int putItemsIntoSlot (int slot, @NotNull ItemStack stack, int count) {
        IDrawer drawer = getGroup().getDrawer(slot);
        if (!drawer.isEnabled())
            return 0;

        if (drawer.isEmpty())
            drawer = drawer.setStoredItem(stack);

        if (!drawer.canItemBeStored(stack))
            return 0;

        int countAdded = Math.min(count, stack.getCount());
        if (!drawerAttributes.isVoid())
            countAdded = Math.min(countAdded, drawer.getRemainingCapacity());

        drawer.setStoredItemCount(drawer.getStoredItemCount() + countAdded);
        stack.shrink(countAdded);

        return countAdded;
    }

    public int interactPutCurrentItemIntoSlot (int slot, Player player) {
        IDrawer drawer = getDrawer(slot);
        if (!drawer.isEnabled())
            return 0;

        int count = 0;
        ItemStack playerStack = player.getInventory().getSelected();
        if (!playerStack.isEmpty())
            count = putItemsIntoSlot(slot, playerStack, playerStack.getCount());

        return count;
    }

    public int interactPutCurrentInventoryIntoSlot (int slot, Player player) {
        IDrawer drawer = getGroup().getDrawer(slot);
        if (!drawer.isEnabled())
            return 0;

        int count = 0;
        if (!drawer.isEmpty()) {
            for (int i = 0, n = player.getInventory().getContainerSize(); i < n; i++) {
                ItemStack subStack = player.getInventory().getItem(i);
                if (!subStack.isEmpty()) {
                    int subCount = putItemsIntoSlot(slot, subStack, subStack.getCount());
                    if (subCount > 0 && subStack.getCount() == 0)
                        player.getInventory().setItem(i, ItemStack.EMPTY);

                    count += subCount;
                }
            }
        }

//        if (count > 0)
//            StorageDrawers.proxy.updatePlayerInventory(player);

        return count;
    }

    public int interactPutItemsIntoSlot (int slot, Player player) {
        if (getLevel() == null)
            return 0;

        int count;
        if (getLevel().getGameTime() - lastClickTime < 10 && player.getUUID().equals(lastClickUUID))
            count = interactPutCurrentInventoryIntoSlot(slot, player);
        else
            count = interactPutCurrentItemIntoSlot(slot, player);

        lastClickTime = getLevel().getGameTime();
        lastClickUUID = player.getUUID();

        return count;
    }

    @Override
    public void readPortable (HolderLookup.Provider provider, CompoundTag tag) {
        loading = true;
        super.readPortable(provider, tag);

        //material = null;
        //if (tag.hasKey("Mat"))
        //    material = tag.getString("Mat");

        if (tag.contains("Lock")) {
            EnumSet<LockAttribute> attrs = LockAttribute.getEnumSet(tag.getByte("Lock"));
            if (attrs != null) {
                drawerAttributes.setItemLocked(LockAttribute.LOCK_EMPTY, attrs.contains(LockAttribute.LOCK_EMPTY));
                drawerAttributes.setItemLocked(LockAttribute.LOCK_POPULATED, attrs.contains(LockAttribute.LOCK_POPULATED));
            }
        } else {
            drawerAttributes.setItemLocked(LockAttribute.LOCK_EMPTY, false);
            drawerAttributes.setItemLocked(LockAttribute.LOCK_POPULATED, false);
        }

        if (tag.contains("Shr"))
            drawerAttributes.setIsConcealed(tag.getBoolean("Shr"));
        else
            drawerAttributes.setIsConcealed(false);


        if (tag.contains("Qua"))
            drawerAttributes.setIsShowingQuantity(tag.getBoolean("Qua"));
        else
            drawerAttributes.setIsShowingQuantity(false);

        /*owner = null;
        if (tag.hasKey("Own"))
            owner = UUID.fromString(tag.getString("Own"));

        securityKey = null;
        if (tag.hasKey("Sec"))
            securityKey = tag.getString("Sec");*/
        loading = false;
    }

    @Override
    public CompoundTag writePortable (HolderLookup.Provider provider, CompoundTag tag) {
        tag = super.writePortable(provider, tag);

        //if (material != null)
        //    tag.setString("Mat", material);

        EnumSet<LockAttribute> attrs = EnumSet.noneOf(LockAttribute.class);
        if (drawerAttributes.isItemLocked(LockAttribute.LOCK_EMPTY))
            attrs.add(LockAttribute.LOCK_EMPTY);
        if (drawerAttributes.isItemLocked(LockAttribute.LOCK_POPULATED))
            attrs.add(LockAttribute.LOCK_POPULATED);

        if (!attrs.isEmpty()) {
            tag.putByte("Lock", (byte)LockAttribute.getBitfield(attrs));
        }

        if (drawerAttributes.isConcealed())
            tag.putBoolean("Shr", true);

        if (drawerAttributes.isShowingQuantity())
            tag.putBoolean("Qua", true);

        /*if (owner != null)
            tag.setString("Own", owner.toString());

        if (securityKey != null)
            tag.setString("Sec", securityKey);*/

        return tag;
    }

    @Override
    public void setChanged () {
        if (isRedstone() && getLevel() != null) {
            getLevel().updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
            getLevel().updateNeighborsAt(getBlockPos().below(), getBlockState().getBlock());
        }

        super.setChanged();
    }

    protected void syncClientCount (int slot, int count) {
        if (getLevel() != null && getLevel().isClientSide)
            return;

        PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(
            getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 500, getLevel().dimension());
        MessageHandler.INSTANCE.send(new CountUpdateMessage(getBlockPos(), slot, count), PacketDistributor.NEAR.with(point));
    }

    @OnlyIn(Dist.CLIENT)
    public void clientUpdateCount (final int slot, final int count) {
        if (getLevel() == null || !getLevel().isClientSide)
            return;

        Minecraft.getInstance().tell(() -> BlockEntityDrawers.this.clientUpdateCountAsync(slot, count));
    }

    @OnlyIn(Dist.CLIENT)
    private void clientUpdateCountAsync (int slot, int count) {
        IDrawer drawer = getDrawer(slot);
        if (drawer.isEnabled() && drawer.getStoredItemCount() != count)
            drawer.setStoredItemCount(count);

    }

    //@Override
    public boolean dataPacketRequiresRenderUpdate () {
        return true;
    }

    //@Override
    //public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
    //    return oldState.getBlock() != newSate.getBlock();
    //}

    @Override
    @Deprecated
    public int getDrawerCount () {
        return getGroup().getDrawerCount();
    }

    @NotNull
    @Override
    @Deprecated
    public IDrawer getDrawer (int slot) {
        return getGroup().getDrawer(slot);
    }

    @Override
    @Deprecated
    public int[] getAccessibleDrawerSlots () {
        return getGroup().getAccessibleDrawerSlots();
    }

    /*@Override
    public String getName () {
        return customNameData.getName();
    }

    @Override
    public boolean hasCustomName () {
        return customNameData.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName () {
        return customNameData.getDisplayName();
    }

    public void setCustomName (ITextComponent name) {
        customNameData.setName(name);
    }*/

    public static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY= CapabilityManager.get(new CapabilityToken<>(){});

    private final LazyOptional<IDrawerGroup> capabilityGroup = LazyOptional.of(this::getGroup);

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing)
    {
        IDrawerCapability<T> dcap = Capabilities.fromNative(capability);
        if (dcap == Capabilities.DRAWER_GROUP)
            return capabilityGroup.cast();

        T cap = getGroup().getCapability(dcap);
        if (cap != null)
            return LazyOptional.of(() -> cap);

        return super.getCapability(capability, facing);
    }

    @NotNull
    @Override
    public ModelData getModelData () {
        return ModelData.builder()
            .with(ATTRIBUTES, drawerAttributes).build();
            /*.with(ITEM_LOCKED, drawerAttributes.isItemLocked(LockAttribute.LOCK_EMPTY))
            .with(SHROUDED, drawerAttributes.isConcealed())
            .with(VOIDING, drawerAttributes.isVoid()).build();*/
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        capabilityGroup.invalidate();
    }
}
