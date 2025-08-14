package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlockEntity;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.*;
import com.jaquadro.minecraft.storagedrawers.capabilities.BasicDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.ModSecurity;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawersComp3;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeRedstone;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeRemote;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.storage.StorageUtil;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import com.texelsaurus.minecraft.chameleon.inventory.ContentMenuProvider;
import com.texelsaurus.minecraft.chameleon.inventory.content.PositionContent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public abstract class BlockEntityDrawers extends BaseBlockEntity implements IDrawerGroup, IProtectable, INetworked, IFramedBlockEntity /*, INameable */
{
    private final CustomNameData customNameData = new CustomNameData();
    private final MaterialData materialData = new MaterialData();
    private final UpgradeData upgradeData = new DrawerUpgradeData();
    private final ControllerData controllerData = new ControllerData();

    //public final ControllerData controllerData = new ControllerData();

    //private int direction;
    //private String material;
    //private boolean taped = false;
    private UUID owner;
    private String securityKey;

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
                int currentUpgradeMult = upgradeData.getStorageMultiplier();
                return stackCapacityCheck(currentUpgradeMult);
            }

            return true;
        }

        @Override
        public boolean canRemoveUpgrade (int slot) {
            if (!super.canRemoveUpgrade(slot))
                return false;

            ItemStack upgrade = getUpgrade(slot);
            if (upgrade.getItem() instanceof ItemUpgradeStorage) {
                int currentUpgradeMult = upgradeData.getStorageMultiplier();

                int remLevel = ((ItemUpgradeStorage) upgrade.getItem()).level.getLevel();
                int remMult = ModCommonConfig.INSTANCE.UPGRADES.getLevelMult(remLevel);

                return stackCapacityCheck(getDrawerCapacity() * (currentUpgradeMult - remMult));
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

            int remLevel = ((ItemUpgradeStorage) upgrade.getItem()).level.getLevel();
            int remMult = ModCommonConfig.INSTANCE.UPGRADES.getLevelMult(remLevel);

            int addLevel = ((ItemUpgradeStorage) add.getItem()).level.getLevel();
            int addMult = ModCommonConfig.INSTANCE.UPGRADES.getLevelMult(addLevel);

            return stackCapacityCheck(getDrawerCapacity() * (currentUpgradeMult - remMult + addMult));
        }

        @Override
        protected void onUpgradeChanged (ItemStack oldUpgrade, ItemStack newUpgrade) {
            if (getLevel() != null && !getLevel().isClientSide) {
                checkBoundController();
                if (getBoundControlGroup() != null)
                    getBoundControlGroup().addRemoteNode(BlockEntityDrawers.this);

                setChanged();
                markBlockForUpdate();
            }
        }

        private boolean stackCapacityCheck (int stackCapacity) {
            for (int i = 0; i < getDrawerCount(); i++) {
                IDrawer drawer = getDrawer(i);
                if (!drawer.isEnabled() || drawer.isEmpty())
                    continue;

                int currentCount = drawer.getStoredItemCount();
                int newMaxCapacity = stackCapacity * drawer.getStoredItemStackSize();
                if (currentCount > newMaxCapacity)
                    return false;
            }

            return true;
        }
    }

    protected BlockEntityDrawers(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);

        drawerAttributes = new DrawerAttributes();

        upgradeData.setDrawerAttributes(drawerAttributes);

        injectPortableData(customNameData);
        injectPortableData(upgradeData);
        injectPortableData(controllerData);

        injectData(materialData);
    }

    private void checkBoundController () {
        if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
            ModServices.log.info("BlockEntityDrawers [{}] checkBoundController", getBlockPos());

        BlockEntityController controller = controllerData.getController(this);
        ItemStack remote = upgradeData.getRemoteUpgrade();
        if (remote == null && controller != null) {
            controller.invalidateRemoteNode(this);
            controllerData.bind(null);
            return;
        }

        if (remote != null && remote.getItem() instanceof ItemUpgradeRemote itemRemote) {
            BlockEntityController upgradeController = itemRemote.getBoundController(remote, level);
            if (controller != null && controller != upgradeController)
                controller.invalidateRemoteNode(this);

            if (upgradeController != null) {
                controllerData.bind(upgradeController);
                if (!upgradeController.addRemoteNode(this))
                    controllerData.bind(null);
            }

            if (itemRemote.isBound() && controllerData.getController(this) == null)
                upgradeData.unbindRemoteUpgrade();
        }
    }

    public void validateBoundController() {
        checkBoundController();
    }

    private boolean controllerInrange (BlockPos controllerPos) {
        BlockPos pos = getBlockPos();
        int distX = Math.abs(controllerPos.getX() - pos.getX());
        int distY = Math.abs(controllerPos.getY() - pos.getY());
        int distZ = Math.abs(controllerPos.getZ() - pos.getZ());

        int range = ModCommonConfig.INSTANCE.GENERAL.controllerRange.get();
        return distX <= range && distY <= range && distZ <= range;
    }

    @Override
    public boolean supportsDirectControllerLink () {
        return true;
    }

    @Override
    public IControlGroup getBoundControlGroup () {
        return controllerData.getController(this);
    }

    @Override
    public boolean canRecurseSearch () {
        ItemStack upgrade = upgradeData.getRemoteUpgrade();
        if (upgrade == null)
            return true;

        if (upgrade.getItem() instanceof ItemUpgradeRemote item)
            return item.isGroupUpgrade();

        return true;
    }

    @Override
    public void unbindControlGroup () {
        upgradeData.unbindRemoteUpgrade();
    }

    @NotNull
    public abstract IDrawerGroup getGroup ();

    public static IDrawerGroup getGroup(BlockEntityDrawers be) {
        if (be == null)
            return null;
        return be.getGroup();
    }

    @NotNull
    public IDrawerAttributes getDrawerAttributes () {
        return drawerAttributes;
    }

    public static IDrawerAttributes getDrawerAttributes(BlockEntityDrawers be) {
        if (be == null)
            return null;
        return be.getDrawerAttributes();
    }

    public UpgradeData upgrades () {
        return upgradeData;
    }

    @Override
    public MaterialData material () {
        return materialData;
    }

    public CustomNameData customName () {
        return customNameData;
    }

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

    @Override
    public UUID getOwner () {
        if (!ModCommonConfig.INSTANCE.GENERAL.enablePersonalKey.get())
            return null;

        return owner;
    }

    @Override
    public boolean setOwner (UUID owner) {
        if (!ModCommonConfig.INSTANCE.GENERAL.enablePersonalKey.get())
            return false;

        if ((this.owner != null && !this.owner.equals(owner)) || (owner != null && !owner.equals(this.owner))) {
            this.owner = owner;

            if (level != null && !level.isClientSide) {
                markBlockForUpdate();
            }
        }

        return true;
    }

    @Override
    public ISecurityProvider getSecurityProvider () {
        return ModSecurity.registry.getProvider(securityKey);
    }

    @Override
    public boolean setSecurityProvider (ISecurityProvider provider) {
        if (!ModCommonConfig.INSTANCE.GENERAL.enablePersonalKey.get())
            return false;

        String newKey = (provider == null) ? null : provider.getProviderID();
        if ((newKey != null && !newKey.equals(securityKey)) || (securityKey != null && !securityKey.equals(newKey))) {
            securityKey = newKey;

            if (level != null && !level.isClientSide) {
                markBlockForUpdate();
            }
        }

        return true;
    }

    protected void onAttributeChanged () {
        //requestModelDataUpdate();
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

        if (upgradeData.hasbalancedFillUpgrade() && !upgradeData.hasVendingUpgrade())
            StorageUtil.rebalanceDrawers(getGroup(), slot);

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

        if (!drawer.canItemBeStoredManual(stack, null))
            return 0;

        if (drawer.isEmpty())
            drawer = drawer.setStoredItem(stack);

        int countAdded = Math.min(count, stack.getCount());
        if (!drawerAttributes.isVoid())
            countAdded = Math.min(countAdded, drawer.getRemainingCapacity());

        drawer.setStoredItemCount(drawer.getStoredItemCount() + countAdded);
        stack.shrink(countAdded);

        if (upgradeData.hasbalancedFillUpgrade() && !upgradeData.hasVendingUpgrade())
            StorageUtil.rebalanceDrawers(getGroup(), slot);

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

    public boolean interactReplaceDrawer (int slot, ItemStack detachedDrawer) {
        IDrawer drawer = getDrawer(slot);
        if (!drawer.isMissing())
            return false;

        if (detachedDrawer.isEmpty())
            return false;

        DetachedDrawerData data = new DetachedDrawerData(detachedDrawer.getOrCreateTag());
        ItemStack proto = data.getStoredItemPrototype();
        int count = data.getStoredItemCount();

        if (count > drawer.getMaxCapacity(proto))
            return false;

        if (ModCommonConfig.INSTANCE.GENERAL.forceDetachedDrawersMaxCapacityCheck.get()) {
            int cap = getEffectiveDrawerCapacity() * upgradeData.getStorageMultiplier();
            if (data.getStorageMultiplier() < cap)
                return false;
        }

        drawer.setDetached(false);
        drawer.setStoredItem(proto, count);

        if (drawerAttributes.isBalancedFill())
            StorageUtil.rebalanceDrawers(getGroup(), slot);

        return true;
    }

    @Override
    public void readPortable (CompoundTag tag) {
        loading = true;
        super.readPortable(tag);

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

        owner = null;
        if (tag.contains("Own"))
            owner = UUID.fromString(tag.getString("Own"));

        securityKey = null;
        if (tag.contains("Sec"))
            securityKey = tag.getString("Sec");

        if (tag.contains("Pri"))
            drawerAttributes.setPriority(tag.getInt("Pri"));
        else
            drawerAttributes.setPriority(0);

        loading = false;
    }

    @Override
    public CompoundTag writePortable (CompoundTag tag) {
        tag = super.writePortable(tag);

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

        if (owner != null)
            tag.putString("Own", owner.toString());

        if (securityKey != null)
            tag.putString("Sec", securityKey);

        if (drawerAttributes.getPriority() != 0)
            tag.putInt("Pri", drawerAttributes.getPriority());

        return tag;
    }

    @Override
    protected void onLoadFinished () {
        super.onLoadFinished();
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

        ChameleonServices.NETWORK.sendToPlayersNear(new CountUpdateMessage(getBlockPos(), slot, count),
            (ServerLevel) getLevel(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 500);
    }

    public void clientUpdateCount (final int slot, final int count) {
        if (getLevel() == null || !getLevel().isClientSide)
            return;

        Minecraft.getInstance().tell(() -> BlockEntityDrawers.this.clientUpdateCountAsync(slot, count));
    }

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

    @Override
    public <T> T getCapability (ChameleonCapability<T> capability) {
        if (capability == null || level == null)
            return null;
        return capability.getCapability(level, getBlockPos());
    }

    public static class ContentProvider implements ContentMenuProvider<PositionContent>
    {
        private BlockEntityDrawers entity;

        public ContentProvider (BlockEntityDrawers entity) {
            this.entity = entity;
        }

        @Override
        public PositionContent createContent (ServerPlayer player) {
            return new PositionContent(entity.getBlockPos());
        }

        @Override
        public Component getDisplayName () {
            if (entity.customNameData.hasCustomName())
                return entity.customNameData.getDisplayName();

            ItemStack stack = new ItemStack(entity.getBlockState().getBlock());
            return stack.getItem().getName(stack);
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu (int id, Inventory inventory, Player player) {
            return switch (entity.getGroup().getDrawerCount()) {
                case 1 -> new ContainerDrawers1(id, inventory, entity);
                case 2 -> new ContainerDrawers2(id, inventory, entity);
                case 4 -> new ContainerDrawers4(id, inventory, entity);
                case 3 -> new ContainerDrawersComp3(id, inventory, entity);
                default -> null;
            };
        }
    }
}
