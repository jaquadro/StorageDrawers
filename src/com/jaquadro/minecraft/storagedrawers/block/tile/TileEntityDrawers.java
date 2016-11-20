package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.chameleon.block.ChamLockableTileEntity;
import com.jaquadro.minecraft.chameleon.block.tiledata.CustomNameData;
import com.jaquadro.minecraft.chameleon.block.tiledata.LockableData;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroupInteractive;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IItemLockable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ISealable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.ControllerData;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStatus;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.storage.IUpgradeProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.UUID;

public abstract class TileEntityDrawers extends ChamLockableTileEntity implements IDrawerGroupInteractive, IUpgradeProvider, IItemLockable, ISealable, IProtectable
{
    private LockableData lockData = new LockableData();
    private CustomNameData customNameData = new CustomNameData("storagedrawers.container.drawers");
    public final ControllerData controllerData = new ControllerData();

    private IDrawer[] drawers;

    private int direction;
    private String material;
    private int drawerCapacity = 1;
    private boolean shrouded = false;
    private boolean taped = false;
    private boolean hideUpgrade = false;
    private UUID owner;
    private String securityKey;

    private EnumSet<LockAttribute> lockAttributes = null;

    private ItemStack[] upgrades = new ItemStack[5];

    private long lastClickTime;
    private UUID lastClickUUID;

    @Nonnull
    private ItemStack materialSide;
    @Nonnull
    private ItemStack materialFront;
    @Nonnull
    private ItemStack materialTrim;

    protected TileEntityDrawers (int drawerCount) {
        for (int i = 0; i < upgrades.length; i++)
            upgrades[i] = ItemStack.field_190927_a;

        materialSide = ItemStack.field_190927_a;
        materialFront = ItemStack.field_190927_a;
        materialTrim = ItemStack.field_190927_a;

        injectData(lockData);
        injectData(customNameData);
        injectData(controllerData);
        initWithDrawerCount(drawerCount);
    }

    protected abstract IDrawer createDrawer (int slot);

    protected void initWithDrawerCount (int drawerCount) {
        drawers = new IDrawer[drawerCount];
        for (int i = 0; i < drawerCount; i++)
            drawers[i] = createDrawer(i);
    }

    public int getDirection () {
        return direction;
    }

    public void setDirection (int direction) {
        this.direction = direction % 6;
    }

    public String getMaterial () {
        return material;
    }

    public String getMaterialOrDefault () {
        String mat = getMaterial();
        return (mat != null) ? mat : "oak";
    }

    public void setMaterial (String material) {
        this.material = material;
    }

    public int getMaxStorageLevel () {
        int maxLevel = 1;
        for (ItemStack upgrade : upgrades) {
            if (upgrade != null && upgrade.getItem() == ModItems.upgradeStorage)
                maxLevel = Math.max(maxLevel, upgrade.getItemDamage());
        }

        return maxLevel;
    }

    public int getEffectiveStorageLevel () {
        int level = 0;
        for (ItemStack upgrade : upgrades) {
            if (upgrade.getItem() == ModItems.upgradeStorage)
                level += upgrade.getItemDamage();
        }

        return Math.max(level, 1);
    }

    public int getEffectiveStorageMultiplier () {
        ConfigManager config = StorageDrawers.config;

        int multiplier = 0;
        for (ItemStack stack : upgrades) {
            if (stack.getItem() == ModItems.upgradeStorage) {
                int level = EnumUpgradeStorage.byMetadata(stack.getItemDamage()).getLevel();
                multiplier += config.getStorageUpgradeMultiplier(level);
            }
        }

        if (multiplier == 0)
            multiplier = config.getStorageUpgradeMultiplier(1);

        return multiplier;
    }

    public int getEffectiveStatusLevel () {
        int maxLevel = -1;
        for (ItemStack upgrade : upgrades) {
            if (upgrade.getItem() == ModItems.upgradeStatus)
                maxLevel = upgrade.getItemDamage();
        }

        if (maxLevel == -1)
            return 0;

        return EnumUpgradeStatus.byMetadata(maxLevel).getLevel();
    }

    public int getUpgradeSlotCount () {
        return 5;
    }

    @Nonnull
    public ItemStack getUpgrade (int slot) {
        slot = MathHelper.clamp(slot, 0, 4);
        return upgrades[slot];
    }

    public boolean addUpgrade (@Nonnull ItemStack upgrade) {
        int slot = getNextUpgradeSlot();
        if (slot == -1)
            return false;

        setUpgrade(slot, upgrade);
        return true;
    }

    public void setUpgrade (int slot, @Nonnull ItemStack upgrade) {
        slot = MathHelper.clamp(slot, 0, 4);

        if (!upgrade.func_190926_b()) {
            upgrade = upgrade.copy();
            upgrade.func_190920_e(1);
        }

        upgrades[slot] = upgrade;

        if (getWorld() != null) {
            if (!getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
            getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
            getWorld().notifyNeighborsOfStateChange(getPos().down(), getBlockType(), false);
        }

        attributeChanged();
    }

    public int getNextUpgradeSlot () {
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i].func_190926_b())
                return i;
        }

        return -1;
    }

    public int getDrawerCapacity () {
        return drawerCapacity;
    }

    public void setDrawerCapacity (int stackCount) {
        drawerCapacity = stackCount;
        attributeChanged();
    }

    private void attributeChanged () {
        for (int i = 0; i < getDrawerCount(); i++) {
            IDrawer drawer = getDrawer(i);
            if (drawer == null)
                continue;

            drawer.attributeChanged();
        }
    }

    @Override
    public boolean isItemLocked (LockAttribute attr) {
        if (!StorageDrawers.config.cache.enableLockUpgrades || lockAttributes == null)
            return false;

        return lockAttributes.contains(attr);
    }

    @Override
    public boolean canItemLock (LockAttribute attr) {
        if (!StorageDrawers.config.cache.enableLockUpgrades)
            return false;

        return true;
    }

    @Override
    public void setItemLocked (LockAttribute attr, boolean isLocked) {
        if (!StorageDrawers.config.cache.enableLockUpgrades)
            return;

        if (isLocked && (lockAttributes == null || !lockAttributes.contains(attr))) {
            if (lockAttributes == null)
                lockAttributes = EnumSet.of(attr);
            else
                lockAttributes.add(attr);

            attributeChanged();

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }
        else if (!isLocked && lockAttributes != null && lockAttributes.contains(attr)) {
            lockAttributes.remove(attr);

            attributeChanged();

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }
    }

    public boolean isShrouded () {
        if (!StorageDrawers.config.cache.enableShroudUpgrades)
            return false;

        return shrouded;
    }

    public void setIsShrouded (boolean shrouded) {
        if (this.shrouded != shrouded) {
            this.shrouded = shrouded;

            attributeChanged();

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }
    }

    @Override
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

            attributeChanged();

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
    public ILockableContainer getLockableContainer () {
        return this;
    }

    @Override
    public boolean setSecurityProvider (ISecurityProvider provider) {
        if (!StorageDrawers.config.cache.enablePersonalUpgrades)
            return false;

        String newKey = (provider == null) ? null : provider.getProviderID();
        if ((newKey != null && !newKey.equals(securityKey)) || (securityKey != null && !securityKey.equals(newKey))) {
            securityKey = newKey;

            attributeChanged();

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }

        return true;
    }

    public boolean shouldHideUpgrades () {
        return hideUpgrade;
    }

    public void setShouldHideUpgrades (boolean hide) {
        hideUpgrade = hide;

        if (getWorld() != null && !getWorld().isRemote) {
            markDirty();
            markBlockForUpdate();
        }
    }

    public boolean isSealed () {
        if (!StorageDrawers.config.cache.enableTape)
            return false;

        return taped;
    }

    public boolean setIsSealed (boolean sealed) {
        if (!StorageDrawers.config.cache.enableTape)
            return false;

        if (this.taped != sealed) {
            this.taped = sealed;

            attributeChanged();

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }

        return true;
    }

    public boolean isVoid () {
        if (!StorageDrawers.config.cache.enableVoidUpgrades)
            return false;

        for (ItemStack stack : upgrades) {
            if (stack.getItem() == ModItems.upgradeVoid)
                return true;
        }

        return false;
    }

    public boolean isUnlimited () {
        if (!StorageDrawers.config.cache.enableCreativeUpgrades)
            return false;

        for (ItemStack stack : upgrades) {
            if (stack.getItem() == ModItems.upgradeCreative)
                return true;
        }

        return false;
    }

    public boolean isVending () {
        if (!StorageDrawers.config.cache.enableCreativeUpgrades)
            return false;

        for (ItemStack stack : upgrades) {
            if (stack.getItem() == ModItems.upgradeCreative && stack.getItemDamage() == 1)
                return true;
        }

        return false;
    }

    public boolean isRedstone () {
        if (!StorageDrawers.config.cache.enableRedstoneUpgrades)
            return false;

        for (ItemStack stack : upgrades) {
            if (stack.getItem() == ModItems.upgradeRedstone)
                return true;
        }

        return false;
    }

    public int getRedstoneLevel () {
        int redstoneType = -1;
        for (ItemStack stack : upgrades) {
            if (stack.getItem() == ModItems.upgradeRedstone) {
                redstoneType = stack.getItemDamage();
                break;
            }
        }

        switch (redstoneType) {
            case 0:
                return getCombinedRedstoneLevel();
            case 1:
                return getMaxRedstoneLevel();
            case 2:
                return getMinRedstoneLevel();
            default:
                return 0;
        }
    }

    protected int getCombinedRedstoneLevel () {
        int active = 0;
        float fillRatio = 0;

        for (int i = 0; i < getDrawerCount(); i++) {
            IDrawer drawer = getDrawerIfEnabled(i);
            if (drawer == null)
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
            IDrawer drawer = getDrawerIfEnabled(i);
            if (drawer == null)
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
            IDrawer drawer = getDrawerIfEnabled(i);
            if (drawer == null)
                continue;

            if (drawer.getMaxCapacity() > 0)
                maxRatio = Math.max(maxRatio, (float)drawer.getStoredItemCount() / drawer.getMaxCapacity());
        }

        if (maxRatio == 1)
            return 15;

        return (int)Math.ceil(maxRatio * 14);
    }

    public boolean isSorting () {
        return false;
    }

    @Nonnull
    public ItemStack getMaterialSide () {
        return materialSide;
    }

    @Nonnull
    public ItemStack getMaterialFront () {
        return materialFront;
    }

    @Nonnull
    public ItemStack getMaterialTrim () {
        return materialTrim;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialSide () {
        return materialSide;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialFront () {
        return !materialFront.func_190926_b() ? materialFront : materialSide;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialTrim () {
        return !materialTrim.func_190926_b() ? materialTrim : materialSide;
    }

    public void setMaterialSide (@Nonnull ItemStack material) {
        materialSide = material;
    }

    public void setMaterialFront (@Nonnull ItemStack material) {
        materialFront = material;
    }

    public void setMaterialTrim (@Nonnull ItemStack material) {
        materialTrim = material;
    }

    @Nonnull
    public ItemStack takeItemsFromSlot (int slot, int count) {
        if (slot < 0 || slot >= getDrawerCount())
            return ItemStack.field_190927_a;

        IDrawer drawer = drawers[slot];
        if (drawer.isEmpty())
            return ItemStack.field_190927_a;

        ItemStack stack = drawer.getStoredItemPrototype().copy();
        stack.func_190920_e(Math.min(count, drawers[slot].getStoredItemCount()));

        drawer.setStoredItemCount(drawer.getStoredItemCount() - stack.func_190916_E());

        if (isRedstone() && getWorld() != null) {
            getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
            getWorld().notifyNeighborsOfStateChange(getPos().down(), getBlockType(), false);
        }

        // TODO: Reset empty drawer in subclasses

        return stack;
    }

    @Nonnull
    protected ItemStack getItemsFromSlot (int slot, int count) {
        if (drawers[slot].isEmpty())
            return ItemStack.field_190927_a;

        ItemStack stack = drawers[slot].getStoredItemPrototype().copy();
        stack.func_190920_e(Math.min(count, drawers[slot].getStoredItemCount()));

        return stack;
    }

    public int putItemsIntoSlot (int slot, @Nonnull ItemStack stack, int count) {
        if (slot < 0 || slot >= getDrawerCount())
            return 0;

        IDrawer drawer = drawers[slot];
        if (drawer.isEmpty())
            drawer.setStoredItem(stack, 0);

        if (!drawer.canItemBeStored(stack))
            return 0;

        int countAdded = Math.min(count, stack.func_190916_E());
        if (!isVoid())
            countAdded = Math.min(countAdded, drawer.getRemainingCapacity());

        drawer.setStoredItemCount(drawer.getStoredItemCount() + countAdded);
        stack.func_190918_g(countAdded);

        return countAdded;
    }

    public int interactPutCurrentItemIntoSlot (int slot, EntityPlayer player) {
        int count = 0;
        ItemStack playerStack = player.inventory.getCurrentItem();
        if (!playerStack.func_190926_b())
            count = putItemsIntoSlot(slot, playerStack, playerStack.func_190916_E());

        if (count > 0)
            markDirty();

        return count;
    }

    public int interactPutCurrentInventoryIntoSlot (int slot, EntityPlayer player) {
        int count = 0;

        if (!drawers[slot].isEmpty()) {
            for (int i = 0, n = player.inventory.getSizeInventory(); i < n; i++) {
                ItemStack subStack = player.inventory.getStackInSlot(i);
                if (!subStack.func_190926_b()) {
                    int subCount = putItemsIntoSlot(slot, subStack, subStack.func_190916_E());
                    if (subCount > 0 && subStack.func_190916_E() == 0)
                        player.inventory.setInventorySlotContents(i, ItemStack.field_190927_a);

                    count += subCount;
                }
            }
        }

        if (count > 0)
            StorageDrawers.proxy.updatePlayerInventory(player);

        markDirty();
        return count;
    }

    public int interactPutItemsIntoSlot (int slot, EntityPlayer player) {
        int count;
        if (getWorld().getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID))
            count = interactPutCurrentInventoryIntoSlot(slot, player);
        else
            count = interactPutCurrentItemIntoSlot(slot, player);

        lastClickTime = getWorld().getTotalWorldTime();
        lastClickUUID = player.getPersistentID();

        return count;
    }

    @Override
    protected void readFromFixedNBT (NBTTagCompound tag) {
        super.readFromFixedNBT(tag);

        setDirection(tag.getByte("Dir"));

        taped = false;
        if (tag.hasKey("Tape"))
            taped = tag.getBoolean("Tape");
    }

    @Override
    protected NBTTagCompound writeToFixedNBT (NBTTagCompound tag) {
        tag = super.writeToFixedNBT(tag);

        tag.setByte("Dir", (byte) direction);

        if (taped)
            tag.setBoolean("Tape", true);

        return tag;
    }

    @Override
    public void readFromPortableNBT (NBTTagCompound tag) {
        super.readFromPortableNBT(tag);

        upgrades = new ItemStack[upgrades.length];
        for (int i = 0; i < upgrades.length; i++)
            upgrades[i] = ItemStack.field_190927_a;

        material = null;
        if (tag.hasKey("Mat"))
            material = tag.getString("Mat");

        drawerCapacity = tag.getInteger("Cap");

        NBTTagList upgradeList = tag.getTagList("Upgrades", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < upgradeList.tagCount(); i++) {
            NBTTagCompound upgradeTag = upgradeList.getCompoundTagAt(i);

            int slot = upgradeTag.getByte("Slot");
            setUpgrade(slot, new ItemStack(upgradeTag));
        }

        lockAttributes = null;
        if (tag.hasKey("Lock"))
            lockAttributes = LockAttribute.getEnumSet(tag.getByte("Lock"));

        shrouded = false;
        if (tag.hasKey("Shr"))
            shrouded = tag.getBoolean("Shr");

        owner = null;
        if (tag.hasKey("Own"))
            owner = UUID.fromString(tag.getString("Own"));

        securityKey = null;
        if (tag.hasKey("Sec"))
            securityKey = tag.getString("Sec");

        hideUpgrade = false;
        if (tag.hasKey("HideUp"))
            hideUpgrade = tag.getBoolean("HideUp");

        NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);

        drawers = new IDrawer[slots.tagCount()];
        for (int i = 0, n = drawers.length; i < n; i++) {
            NBTTagCompound slot = slots.getCompoundTagAt(i);
            drawers[i] = createDrawer(i);
            drawers[i].readFromNBT(slot);
        }

        materialSide = ItemStack.field_190927_a;
        if (tag.hasKey("MatS"))
            materialSide = new ItemStack(tag.getCompoundTag("MatS"));

        materialFront = ItemStack.field_190927_a;
        if (tag.hasKey("MatF"))
            materialFront = new ItemStack(tag.getCompoundTag("MatF"));

        materialTrim = ItemStack.field_190927_a;
        if (tag.hasKey("MatT"))
            materialTrim = new ItemStack(tag.getCompoundTag("MatT"));

        attributeChanged();
    }

    @Override
    public NBTTagCompound writeToPortableNBT (NBTTagCompound tag) {
        tag = super.writeToPortableNBT(tag);

        tag.setInteger("Cap", drawerCapacity);

        if (material != null)
            tag.setString("Mat", material);

        NBTTagList upgradeList = new NBTTagList();
        for (int i = 0; i < upgrades.length; i++) {
            if (!upgrades[i].func_190926_b()) {
                NBTTagCompound upgradeTag = upgrades[i].writeToNBT(new NBTTagCompound());
                upgradeTag.setByte("Slot", (byte)i);

                upgradeList.appendTag(upgradeTag);
            }
        }

        if (upgradeList.tagCount() > 0)
            tag.setTag("Upgrades", upgradeList);

        if (lockAttributes != null)
            tag.setByte("Lock", (byte)LockAttribute.getBitfield(lockAttributes));

        if (shrouded)
            tag.setBoolean("Shr", true);

        if (owner != null)
            tag.setString("Own", owner.toString());

        if (securityKey != null)
            tag.setString("Sec", securityKey);

        if (hideUpgrade)
            tag.setBoolean("HideUp", true);

        NBTTagList slots = new NBTTagList();
        for (IDrawer drawer : drawers) {
            NBTTagCompound slot = new NBTTagCompound();
            drawer.writeToNBT(slot);
            slots.appendTag(slot);
        }

        tag.setTag("Slots", slots);

        if (!materialSide.func_190926_b()) {
            NBTTagCompound itag = new NBTTagCompound();
            materialSide.writeToNBT(itag);
            tag.setTag("MatS", itag);
        }

        if (!materialFront.func_190926_b()) {
            NBTTagCompound itag = new NBTTagCompound();
            materialFront.writeToNBT(itag);
            tag.setTag("MatF", itag);
        }

        if (!materialTrim.func_190926_b()) {
            NBTTagCompound itag = new NBTTagCompound();
            materialTrim.writeToNBT(itag);
            tag.setTag("MatT", itag);
        }

        return tag;
    }

    @Override
    public void markDirty () {
        if (isRedstone() && getWorld() != null) {
            getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
            getWorld().notifyNeighborsOfStateChange(getPos().down(), getBlockType(), false);
        }

        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded () {
        super.markDirty();
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void clientUpdateCount (final int slot, final int count) {
        if (!getWorld().isRemote)
            return;

        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run () {
                TileEntityDrawers.this.clientUpdateCountAsync(slot, count);
            }
        });
    }

    @SideOnly(Side.CLIENT)
    private void clientUpdateCountAsync (int slot, int count) {
        IDrawer drawer = getDrawerIfEnabled(slot);
        if (drawer != null && drawer.getStoredItemCount() != count) {
            drawer.setStoredItemCount(count);

            switch (getEffectiveStatusLevel()) {
                case 1:
                    if (drawer.getStoredItemCount() == 0 || drawer.getRemainingCapacity() == 0)
                        markBlockForUpdateClient();
                    break;
                case 2:
                    markBlockForRenderUpdate();
                    break;
            }
        }
    }

    @Override
    public boolean dataPacketRequiresRenderUpdate () {
        return true;
    }

    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public int getDrawerCount () {
        return drawers.length;
    }

    @Override
    public IDrawer getDrawer (int slot) {
        if (slot < 0 || slot >= drawers.length)
            return null;

        return drawers[slot];
    }

    @Override
    public IDrawer getDrawerIfEnabled (int slot) {
        if (slot < 0 || slot >= drawers.length)
            return null;

        if (isSealed())
            return null;

        if (getBlockType() instanceof BlockDrawersCustom && materialSide.func_190926_b())
            return null;

        return drawers[slot];
    }

    @Override
    public boolean isDrawerEnabled (int slot) {
        return getDrawerIfEnabled(slot) != null;
    }

    private net.minecraftforge.items.IItemHandler itemHandler;

    protected IItemHandler createUnSidedHandler () {
        return new DrawerItemHandler(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) (itemHandler == null ? (itemHandler = createUnSidedHandler()) : itemHandler);
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
