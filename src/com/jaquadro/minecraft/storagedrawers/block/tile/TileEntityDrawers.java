package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.chameleon.block.ChamTileEntity;
import com.jaquadro.minecraft.chameleon.block.tiledata.CustomNameData;
import com.jaquadro.minecraft.chameleon.block.tiledata.LockableData;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroupInteractive;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.*;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.inventory.ISideManager;
import com.jaquadro.minecraft.storagedrawers.inventory.StorageInventory;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStatus;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.storage.IUpgradeProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;
import java.util.UUID;

public abstract class TileEntityDrawers extends ChamTileEntity implements ILockableContainer, IDrawerGroupInteractive, ISidedInventory, IUpgradeProvider, IItemLockable, ISealable, IProtectable, IQuantifiable
{
    private LockableData lockData = new LockableData();
    private CustomNameData customNameData = new CustomNameData("storageDrawers.container.drawers");

    private IDrawer[] drawers;
    private IDrawerInventory inventory;

    private int[] autoSides = new int[] { 0, 1, 2, 3, 4, 5 };

    private int direction;
    private String material;
    private int drawerCapacity = 1;
    private boolean shrouded = false;
    private boolean quantified = false;
    private boolean taped = false;
    private boolean hideUpgrade = false;
    private UUID owner;
    private String securityKey;

    private EnumSet<LockAttribute> lockAttributes = null;

    private ItemStack[] upgrades = new ItemStack[5];

    private long lastClickTime;
    private UUID lastClickUUID;

    private ItemStack materialSide;
    private ItemStack materialFront;
    private ItemStack materialTrim;

    protected TileEntityDrawers (int drawerCount) {
        injectData(lockData);
        injectData(customNameData);
        initWithDrawerCount(drawerCount);
    }

    protected abstract IDrawer createDrawer (int slot);

    protected ISideManager getSideManager () {
        return new DefaultSideManager();
    }

    protected void initWithDrawerCount (int drawerCount) {
        drawers = new IDrawer[drawerCount];
        for (int i = 0; i < drawerCount; i++)
            drawers[i] = createDrawer(i);

        inventory = new StorageInventory(this, getSideManager(), this);
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
            if (upgrade != null && upgrade.getItem() == ModItems.upgradeStorage)
                level += upgrade.getItemDamage();
        }

        return Math.max(level, 1);
    }

    public int getEffectiveStorageMultiplier () {
        ConfigManager config = StorageDrawers.config;

        int multiplier = 0;
        for (ItemStack stack : upgrades) {
            if (stack != null && stack.getItem() == ModItems.upgradeStorage) {
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
            if (upgrade != null && upgrade.getItem() == ModItems.upgradeStatus)
                maxLevel = upgrade.getItemDamage();
        }

        if (maxLevel == -1)
            return 0;

        return EnumUpgradeStatus.byMetadata(maxLevel).getLevel();
    }

    public int getUpgradeSlotCount () {
        return 5;
    }

    public ItemStack getUpgrade (int slot) {
        slot = MathHelper.clamp_int(slot, 0, 4);
        return upgrades[slot];
    }

    public boolean addUpgrade (ItemStack upgrade) {
        int slot = getNextUpgradeSlot();
        if (slot == -1)
            return false;

        setUpgrade(slot, upgrade);
        return true;
    }

    public void setUpgrade (int slot, ItemStack upgrade) {
        slot = MathHelper.clamp_int(slot, 0, 4);

        if (upgrade != null) {
            upgrade = upgrade.copy();
            upgrade.stackSize = 1;
        }

        upgrades[slot] = upgrade;

        if (getWorld() != null) {
            if (!getWorld().isRemote) {
                markDirty();
                IBlockState state = getWorld().getBlockState(getPos());
                getWorld().notifyBlockUpdate(getPos(), state, state, 3);
            }
            getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType());
            getWorld().notifyNeighborsOfStateChange(getPos().down(), getBlockType());
        }

        attributeChanged();
    }

    public int getNextUpgradeSlot () {
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i] == null)
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
    public void setLockCode (LockCode code) {
        if (getOwner() == null)
            lockData.setLockCode(code);
    }

    @Override
    public LockCode getLockCode () {
        return getOwner() == null ? lockData.getLockCode() : null;
    }

    @Override
    public boolean isLocked () {
        return getOwner() == null ? lockData.isLocked() : false;
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

        IBlockState state = getWorld().getBlockState(getPos());
        if (isLocked && (lockAttributes == null || !lockAttributes.contains(attr))) {
            if (lockAttributes == null)
                lockAttributes = EnumSet.of(attr);
            else
                lockAttributes.add(attr);

            attributeChanged();

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                getWorld().notifyBlockUpdate(getPos(), state, state, 3);
            }
        }
        else if (!isLocked && lockAttributes != null && lockAttributes.contains(attr)) {
            lockAttributes.remove(attr);

            attributeChanged();

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                getWorld().notifyBlockUpdate(getPos(), state, state, 3);
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

                IBlockState state = getWorld().getBlockState(getPos());
                getWorld().notifyBlockUpdate(getPos(), state, state, 3);
            }
        }
    }

    public boolean isShowingQuantity () {
        if (!StorageDrawers.config.cache.enableQuantifiableUpgrades)
            return false;

        return quantified;
    }

    public boolean setIsShowingQuantity (boolean quantified) {
        if (!StorageDrawers.config.cache.enableQuantifiableUpgrades)
            return false;

        if (this.quantified != quantified) {
            this.quantified = quantified;

            attributeChanged();

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();

                IBlockState state = getWorld().getBlockState(getPos());
                getWorld().notifyBlockUpdate(getPos(), state, state, 3);
            }
        }

        return true;
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

                IBlockState state = getWorld().getBlockState(getPos());
                getWorld().notifyBlockUpdate(getPos(), state, state, 3);
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

                IBlockState state = getWorld().getBlockState(getPos());
                getWorld().notifyBlockUpdate(getPos(), state, state, 3);
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

            IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
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

                IBlockState state = getWorld().getBlockState(getPos());
                getWorld().notifyBlockUpdate(getPos(), state, state, 3);
            }
        }

        return true;
    }

    public boolean isVoid () {
        if (!StorageDrawers.config.cache.enableVoidUpgrades)
            return false;

        for (ItemStack stack : upgrades) {
            if (stack != null && stack.getItem() == ModItems.upgradeVoid)
                return true;
        }

        return false;
    }

    public boolean isUnlimited () {
        if (!StorageDrawers.config.cache.enableCreativeUpgrades)
            return false;

        for (ItemStack stack : upgrades) {
            if (stack != null && stack.getItem() == ModItems.upgradeCreative)
                return true;
        }

        return false;
    }

    public boolean isVending () {
        if (!StorageDrawers.config.cache.enableCreativeUpgrades)
            return false;

        for (ItemStack stack : upgrades) {
            if (stack != null && stack.getItem() == ModItems.upgradeCreative && stack.getItemDamage() == 1)
                return true;
        }

        return false;
    }

    public boolean isRedstone () {
        if (!StorageDrawers.config.cache.enableRedstoneUpgrades)
            return false;

        for (ItemStack stack : upgrades) {
            if (stack != null && stack.getItem() == ModItems.upgradeRedstone)
                return true;
        }

        return false;
    }

    public int getRedstoneLevel () {
        int redstoneType = -1;
        for (ItemStack stack : upgrades) {
            if (stack != null && stack.getItem() == ModItems.upgradeRedstone) {
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

    public ItemStack getMaterialSide () {
        return materialSide;
    }

    public ItemStack getMaterialFront () {
        return materialFront;
    }

    public ItemStack getMaterialTrim () {
        return materialTrim;
    }

    public ItemStack getEffectiveMaterialSide () {
        return materialSide;
    }

    public ItemStack getEffectiveMaterialFront () {
        return materialFront != null ? materialFront : materialSide;
    }

    public ItemStack getEffectiveMaterialTrim () {
        return materialTrim != null ? materialTrim : materialSide;
    }

    public void setMaterialSide (ItemStack material) {
        materialSide = material;
    }

    public void setMaterialFront (ItemStack material) {
        materialFront = material;
    }

    public void setMaterialTrim (ItemStack material) {
        materialTrim = material;
    }

    public ItemStack takeItemsFromSlot (int slot, int count) {
        if (slot < 0 || slot >= getDrawerCount())
            return null;

        ItemStack stack = getItemsFromSlot(slot, count);
        if (stack == null)
            return null;

        IDrawer drawer = drawers[slot];
        drawer.setStoredItemCount(drawer.getStoredItemCount() - stack.stackSize);

        if (isRedstone() && getWorld() != null) {
            getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType());
            getWorld().notifyNeighborsOfStateChange(getPos().down(), getBlockType());
        }

        // TODO: Reset empty drawer in subclasses

        return stack;
    }

    protected ItemStack getItemsFromSlot (int slot, int count) {
        if (drawers[slot].isEmpty())
            return null;

        ItemStack stack = drawers[slot].getStoredItemCopy();
        stack.stackSize = Math.min(stack.getMaxStackSize(), count);
        stack.stackSize = Math.min(stack.stackSize, drawers[slot].getStoredItemCount());

        return stack;
    }

    public int putItemsIntoSlot (int slot, ItemStack stack, int count) {
        if (slot < 0 || slot >= getDrawerCount())
            return 0;

        IDrawer drawer = drawers[slot];
        if (drawer.isEmpty())
            drawer.setStoredItem(stack, 0);

        if (!drawer.canItemBeStored(stack))
            return 0;

        int countAdded = Math.min(count, stack.stackSize);
        if (!isVoid())
            countAdded = Math.min(countAdded, drawer.getRemainingCapacity());

        drawer.setStoredItemCount(drawer.getStoredItemCount() + countAdded);
        stack.stackSize -= countAdded;

        return countAdded;
    }

    public int interactPutCurrentItemIntoSlot (int slot, EntityPlayer player) {
        int count = 0;
        ItemStack playerStack = player.inventory.getCurrentItem();
        if (playerStack != null)
            count = putItemsIntoSlot(slot, playerStack, playerStack.stackSize);

        return count;
    }

    public int interactPutCurrentInventoryIntoSlot (int slot, EntityPlayer player) {
        int count = 0;

        if (!drawers[slot].isEmpty()) {
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

        if (count > 0)
            StorageDrawers.proxy.updatePlayerInventory(player);

        return count;
    }

    public int interactPutItemsIntoSlot (int slot, EntityPlayer player) {
        int count = 0;
        if (getWorld().getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID))
            count = interactPutCurrentInventoryIntoSlot(slot, player);
        else
            count = interactPutCurrentItemIntoSlot(slot, player);

        lastClickTime = getWorld().getTotalWorldTime();
        lastClickUUID = player.getPersistentID();

        return count;
    }

    private void readLegacyUpgradeNBT (NBTTagCompound tag) {
        if (tag.hasKey("Lev") && tag.getByte("Lev") > 1)
            addUpgrade(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.byLevel(tag.getByte("Lev")).getMetadata()));
        if (tag.hasKey("Stat"))
            addUpgrade(new ItemStack(ModItems.upgradeStatus, 1, EnumUpgradeStatus.byLevel(tag.getByte("Stat")).getMetadata()));
        if (tag.hasKey("Void"))
            addUpgrade(new ItemStack(ModItems.upgradeVoid));
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
        tag.setByte("Dir", (byte) direction);

        if (taped)
            tag.setBoolean("Tape", taped);

        return tag;
    }

    @Override
    public void readFromPortableNBT (NBTTagCompound tag) {
        upgrades = new ItemStack[upgrades.length];

        material = null;
        if (tag.hasKey("Mat"))
            material = tag.getString("Mat");

        drawerCapacity = tag.getInteger("Cap");

        if (!tag.hasKey("Upgrades")) {
            readLegacyUpgradeNBT(tag);
        }
        else {
            NBTTagList upgradeList = tag.getTagList("Upgrades", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < upgradeList.tagCount(); i++) {
                NBTTagCompound upgradeTag = upgradeList.getCompoundTagAt(i);

                int slot = upgradeTag.getByte("Slot");
                setUpgrade(slot, ItemStack.loadItemStackFromNBT(upgradeTag));
            }
        }

        lockAttributes = null;
        if (tag.hasKey("Lock"))
            lockAttributes = LockAttribute.getEnumSet(tag.getByte("Lock"));

        shrouded = false;
        if (tag.hasKey("Shr"))
            shrouded = tag.getBoolean("Shr");

        quantified = false;
        if (tag.hasKey("Qua"))
            quantified = tag.getBoolean("Qua");

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

        inventory = new StorageInventory(this, getSideManager(), this);

        materialSide = null;
        if (tag.hasKey("MatS"))
            materialSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));

        materialFront = null;
        if (tag.hasKey("MatF"))
            materialFront = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatF"));

        materialTrim = null;
        if (tag.hasKey("MatT"))
            materialTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));

        attributeChanged();
    }

    @Override
    public NBTTagCompound writeToPortableNBT (NBTTagCompound tag) {
        super.writeToPortableNBT(tag);

        tag.setInteger("Cap", drawerCapacity);

        if (material != null)
            tag.setString("Mat", material);

        NBTTagList upgradeList = new NBTTagList();
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i] != null) {
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
            tag.setBoolean("Shr", shrouded);

        if (quantified)
            tag.setBoolean("Qua", true);

        if (owner != null)
            tag.setString("Own", owner.toString());

        if (securityKey != null)
            tag.setString("Sec", securityKey);

        if (hideUpgrade)
            tag.setBoolean("HideUp", hideUpgrade);

        NBTTagList slots = new NBTTagList();
        for (IDrawer drawer : drawers) {
            NBTTagCompound slot = new NBTTagCompound();
            drawer.writeToNBT(slot);
            slots.appendTag(slot);
        }

        tag.setTag("Slots", slots);

        if (materialSide != null) {
            NBTTagCompound itag = new NBTTagCompound();
            materialSide.writeToNBT(itag);
            tag.setTag("MatS", itag);
        }

        if (materialFront != null) {
            NBTTagCompound itag = new NBTTagCompound();
            materialFront.writeToNBT(itag);
            tag.setTag("MatF", itag);
        }

        if (materialTrim != null) {
            NBTTagCompound itag = new NBTTagCompound();
            materialTrim.writeToNBT(itag);
            tag.setTag("MatT", itag);
        }

        return tag;
    }

    @Override
    public void markDirty () {
        inventory.markDirty();
        if (isRedstone() && getWorld() != null) {
            getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType());
            getWorld().notifyNeighborsOfStateChange(getPos().down(), getBlockType());
        }

        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded () {
        if (inventory.syncInventoryIfNeeded()) {
            super.markDirty();
            return true;
        }

        return false;
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
        if (drawer != null && drawer.getStoredItemCount() != count)
            drawer.setStoredItemCount(count);
    }

    private void syncClientCount (int slot) {
        IMessage message = new CountUpdateMessage(getPos(), slot, drawers[slot].getStoredItemCount());
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 500);

        StorageDrawers.network.sendToAllAround(message, targetPoint);
    }

    public boolean canUpdate () {
        return false;
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

        if (getBlockType() instanceof BlockDrawersCustom && materialSide == null)
            return null;

        return drawers[slot];
    }

    @Override
    public IDrawerInventory getDrawerInventory () {
        return inventory;
    }

    @Override
    public boolean isDrawerEnabled (int slot) {
        return getDrawerIfEnabled(slot) != null;
    }

    @Override
    public int[] getSlotsForFace (EnumFacing side) {
        return inventory.getSlotsForFace(side);
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, EnumFacing side) {
        if (isSealed())
            return false;

        if (isItemLocked(LockAttribute.LOCK_EMPTY) && inventory instanceof StorageInventory) {
            IDrawer drawer = getDrawer(inventory.getDrawerSlot(slot));
            if (drawer != null && drawer.isEmpty())
                return false;
        }

        return inventory.canInsertItem(slot, stack, side);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, EnumFacing side) {
        if (isSealed())
            return false;

        return inventory.canExtractItem(slot, stack, side);
    }

    @Override
    public int getSizeInventory () {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        return inventory.decrStackSize(slot, count);
    }

    @Override
    public ItemStack removeStackFromSlot (int slot) {
        return inventory.removeStackFromSlot(slot);
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    public void setInventoryName (String name) {
        customNameData.setName(name);
    }

    @Override
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

    @Override
    public int getInventoryStackLimit () {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        if (getWorld().getTileEntity(getPos()) != this)
            return false;

        return player.getDistanceSq(getPos().getX() + .5, getPos().getY() + .5, getPos().getZ() + .5) <= 64;
    }

    @Override
    public void openInventory (EntityPlayer player) {
        inventory.openInventory(player);
    }

    @Override
    public void closeInventory (EntityPlayer player) {
        inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        return inventory.isItemValidForSlot(slot, stack);
    }

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
    public void clear () {
        inventory.clear();
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

    private class DefaultSideManager implements ISideManager
    {
        @Override
        public int[] getSlotsForSide (EnumFacing side) {
            return autoSides;
        }
    }
}
