package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeRedstone;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeRedstone;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class UpgradeData extends BlockEntityDataShim
{
    protected final ItemStack[] upgrades;
    private int storageMultiplier;
    private EnumUpgradeRedstone redstoneType;

    // TODO: Do we need to provide these?
    private boolean hasOneStack;
    private boolean hasVoid;
    private boolean hasUnlimited;
    private boolean hasVending;
    private boolean hasConversion;
    private boolean hasIllumination;
    private boolean hasFillLevel;

    private IDrawerAttributesModifiable attrs;

    public UpgradeData (int slotCount) {
        upgrades = new ItemStack[slotCount];
        Arrays.fill(upgrades, ItemStack.EMPTY);

        syncStorageMultiplier();
    }

    public void setDrawerAttributes (IDrawerAttributesModifiable attrs) {
        this.attrs = attrs;
        syncUpgrades();
    }

    public int getSlotCount () {
        return upgrades.length;
    }

    @NotNull
    public ItemStack getUpgrade (int slot) {
        slot = Mth.clamp(slot, 0, upgrades.length - 1);
        return upgrades[slot];
    }

    public boolean hasEmptySlot() {
        return getNextUpgradeSlot() != -1;
    }

    public boolean addUpgrade (@NotNull ItemStack upgrade) {
        int slot = getNextUpgradeSlot();
        if (slot == -1)
            return false;

        setUpgrade(slot, upgrade);
        return true;
    }

    public boolean setUpgrade (int slot, @NotNull ItemStack upgrade) {
        slot = Mth.clamp(slot, 0, upgrades.length - 1);

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

    public boolean canAddUpgrade (@NotNull ItemStack upgrade) {
        if (upgrade.isEmpty())
            return false;
        if (!(upgrade.getItem() instanceof ItemUpgrade candidate))
            return false;

        if (candidate.getAllowMultiple())
            return true;

        for (ItemStack stack : upgrades) {
            if (stack.isEmpty())
                continue;

            if (!(stack.getItem() instanceof ItemUpgrade reference))
                continue;

            if (candidate.getUpgradeGroup() == reference.getUpgradeGroup())
                return false;
        }

        return true;
    }

    public boolean canRemoveUpgrade (int slot) {
        slot = Mth.clamp(slot, 0, upgrades.length - 1);
        return !upgrades[slot].isEmpty();
    }

    /**
     * A util method, to see if the upgrades can be swapped
     * @param slot The slot where the upgrade is being removed
     * @param add The ItemStack of the upgrade being added
     * @return Whether the upgrades can be swapped
     */
    public boolean canSwapUpgrade (int slot, @Nonnull ItemStack add) {
        return canAddUpgrade(add) && canRemoveUpgrade(slot);
    }

    public int getStorageMultiplier () {
        return storageMultiplier;
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

    public boolean hasPortabilityUpgrade() {
        for (ItemStack stack : upgrades) {
            if (stack.getItem() == ModItems.PORTABILITY_UPGRADE.get())
                return true;
        }

        return false;
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
        syncRedstoneLevel();

        hasOneStack = false;
        hasVoid = false;
        hasUnlimited = false;
        hasVending = false;
        hasConversion = false;
        hasIllumination = false;
        hasFillLevel = false;

        for (ItemStack stack : upgrades) {
            Item item = stack.getItem();

            if (item == ModItems.ONE_STACK_UPGRADE.get())
                hasOneStack = true;
            else if (item == ModItems.VOID_UPGRADE.get())
                hasVoid = true;
            //else if (item == ModItems.CONVERSION_UPGRADE.get())
            //    hasConversion = true;
            else if (item == ModItems.CREATIVE_STORAGE_UPGRADE.get())
                hasUnlimited = true;
            else if (item == ModItems.CREATIVE_VENDING_UPGRADE.get())
                hasVending = true;
            else if (item == ModItems.ILLUMINATION_UPGRADE.get())
                hasIllumination = true;
            else if (item == ModItems.FILL_LEVEL_UPGRADE.get())
                hasFillLevel = true;
        }

        attrs.setIsVoid(hasVoid);
        attrs.setHasFillLevel(hasFillLevel);
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
    public void read (CompoundTag tag) {
        Arrays.fill(upgrades, ItemStack.EMPTY);

        if (!tag.contains("Upgrades"))
            return;

        ListTag tagList = tag.getList("Upgrades", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag upgradeTag = tagList.getCompound(i);

            int slot = upgradeTag.getByte("Slot");
            upgrades[slot] = ItemStack.of(upgradeTag);
        }

        syncUpgrades();
    }

    @Override
    public CompoundTag write (CompoundTag tag) {
        ListTag tagList = new ListTag();
        for (int i = 0; i < upgrades.length; i++) {
            if (!upgrades[i].isEmpty()) {
                CompoundTag upgradeTag = upgrades[i].save(new CompoundTag());
                upgradeTag.putByte("Slot", (byte)i);

                tagList.add(upgradeTag);
            }
        }

        tag.put("Upgrades", tagList);
        return tag;
    }

    protected void onUpgradeChanged (ItemStack oldUpgrade, ItemStack newUpgrade) { }
}
