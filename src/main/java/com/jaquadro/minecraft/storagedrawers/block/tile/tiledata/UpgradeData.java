package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public class UpgradeData extends TileDataShim
{
    private final ItemStack[] upgrades;
    private int storageMultiplier;
    private EnumUpgradeStatus statusType;
    private EnumUpgradeRedstone redstoneType;

    // TODO: Do we need to provide these?
    private boolean hasOneStack;
    private boolean hasVoid;
    private boolean hasUnlimited;
    private boolean hasVending;
    private boolean hasConversion;
    private boolean hasIllumination;

    private IDrawerAttributesModifiable attrs;

    public UpgradeData (int slotCount) {
        upgrades = new ItemStack[slotCount];
        for (int i = 0; i < upgrades.length; i++)
            upgrades[i] = ItemStack.EMPTY;

        syncStorageMultiplier();
    }

    public void setDrawerAttributes (IDrawerAttributesModifiable attrs) {
        this.attrs = attrs;
        syncUpgrades();
    }

    public int getSlotCount () {
        return upgrades.length;
    }

    @Nonnull
    public ItemStack getUpgrade (int slot) {
        slot = MathHelper.clamp(slot, 0, upgrades.length - 1);
        return upgrades[slot];
    }

    public boolean addUpgrade (@Nonnull ItemStack upgrade) {
        int slot = getNextUpgradeSlot();
        if (slot == -1)
            return false;

        setUpgrade(slot, upgrade);
        return true;
    }

    public boolean setUpgrade (int slot, @Nonnull ItemStack upgrade) {
        slot = MathHelper.clamp(slot, 0, upgrades.length - 1);

        if (!upgrade.isEmpty()) {
            upgrade = upgrade.copy();
            upgrade.setCount(1);
        }

        ItemStack prevUpgrade = upgrades[slot];
        if (!prevUpgrade.isEmpty() && !canRemoveUpgrade(slot))
            return false;

        upgrades[slot] = ItemStack.EMPTY;
        syncStorageMultiplier();

        if (!upgrade.isEmpty() && !canAddUpgrade(upgrade)) {
            upgrades[slot] = prevUpgrade;
            syncStorageMultiplier();
            return false;
        }

        upgrades[slot] = upgrade;

        syncUpgrades();
        onUpgradeChanged(prevUpgrade, upgrade);

        return true;
    }

    public boolean canAddUpgrade (@Nonnull ItemStack upgrade) {
        if (upgrade.isEmpty())
            return false;
        if (!(upgrade.getItem() instanceof ItemUpgrade))
            return false;

        ItemUpgrade candidate = (ItemUpgrade)upgrade.getItem();
        if (candidate.getAllowMultiple())
            return true;

        for (ItemStack stack : upgrades) {
            if (stack.isEmpty())
                continue;

            if (!(stack.getItem() instanceof ItemUpgrade))
                continue;

            ItemUpgrade reference = (ItemUpgrade)stack.getItem();
            if (candidate.getUpgradeGroup() == reference.getUpgradeGroup())
                return false;
        }

        return true;
    }

    public boolean canRemoveUpgrade (int slot) {
        slot = MathHelper.clamp(slot, 0, upgrades.length - 1);
        return !upgrades[slot].isEmpty();
    }

    public int getStorageMultiplier () {
        return storageMultiplier;
    }

    public EnumUpgradeStatus getStatusType () {
        return statusType;
    }

    public EnumUpgradeRedstone getRedstoneType () {
        return redstoneType;
    }

    public boolean hasOneStackUpgrade () {
        return hasOneStack;
    }

    public boolean hasUnlimitedUpgrade () {
        return hasUnlimited;
    }

    public boolean hasVendingUpgrade () {
        return hasVending;
    }

    public boolean hasConversionUpgrade () {
        return hasConversion;
    }

    public boolean hasIlluminationUpgrade () {
        return hasIllumination;
    }

    private int getNextUpgradeSlot () {
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i].isEmpty())
                return i;
        }

        return -1;
    }

    private void syncUpgrades () {
        if (this.attrs == null)
            return;

        syncStorageMultiplier();
        syncStatusLevel();
        syncRedstoneLevel();

        hasOneStack = false;
        hasVoid = false;
        hasUnlimited = false;
        hasVending = false;
        hasConversion = false;
        hasIllumination = false;

        for (ItemStack stack : upgrades) {
            Item item = stack.getItem();
            if (item == ModItems.ONE_STACK_UPGRADE)
                hasOneStack = true;
            else if (item == ModItems.VOID_UPGRADE)
                hasVoid = true;
            else if (item == ModItems.CONVERSION_UPGRADE)
                hasConversion = true;
            else if (item == ModItems.CREATIVE_STORAGE_UPGRADE)
                hasUnlimited = true;
            else if (item == ModItems.CREATIVE_VENDING_UPGRADE)
                hasVending = true;
            else if (item == ModItems.ILLUMINATION_UPGRADE)
                hasIllumination = true;
        }

        attrs.setIsVoid(hasVoid);
        attrs.setIsDictConvertible(hasConversion);
        attrs.setIsUnlimitedStorage(hasUnlimited);
        attrs.setIsUnlimitedVending(hasVending);
    }

    private void syncStorageMultiplier () {
        storageMultiplier = 0;

        for (ItemStack stack : upgrades) {
            if (stack.getItem() instanceof ItemUpgradeStorage) {
                int level = ((ItemUpgradeStorage) stack.getItem()).level.getLevel();
                storageMultiplier += CommonConfig.UPGRADES.getLevelMult(level);
            }
        }

        if (storageMultiplier == 0)
            storageMultiplier = CommonConfig.UPGRADES.getLevelMult(0);
    }

    private void syncStatusLevel () {
        statusType = null;

        for (ItemStack stack : upgrades) {
            if (stack.getItem() instanceof ItemUpgradeStatus) {
                statusType = ((ItemUpgradeStatus) stack.getItem()).level;
                break;
            }
        }
    }

    private void syncRedstoneLevel () {
        redstoneType = null;

        for (ItemStack stack : upgrades) {
            if (stack.getItem() instanceof ItemUpgradeRedstone) {
                redstoneType = ((ItemUpgradeRedstone) stack.getItem()).type;
                break;
            }
        }
    }

    @Override
    public void read (CompoundNBT tag) {
        for (int i = 0; i < upgrades.length; i++)
            upgrades[i] = ItemStack.EMPTY;

        if (!tag.contains("Upgrades"))
            return;

        ListNBT tagList = tag.getList("Upgrades", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundNBT upgradeTag = tagList.getCompound(i);

            int slot = upgradeTag.getByte("Slot");
            upgrades[slot] = ItemStack.read(upgradeTag);
        }

        syncUpgrades();
    }

    @Override
    public CompoundNBT write (CompoundNBT tag) {
        ListNBT tagList = new ListNBT();
        for (int i = 0; i < upgrades.length; i++) {
            if (!upgrades[i].isEmpty()) {
                CompoundNBT upgradeTag = upgrades[i].write(new CompoundNBT());
                upgradeTag.putByte("Slot", (byte)i);

                tagList.add(upgradeTag);
            }
        }

        tag.put("Upgrades", tagList);
        return tag;
    }

    protected void onUpgradeChanged (ItemStack oldUpgrade, ItemStack newUpgrade) { }
}
