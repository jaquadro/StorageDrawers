package com.jaquadro.minecraft.storagedrawers.block.modeldata;

import com.jaquadro.minecraft.chameleon.model.ModelData;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Arrays;
import java.util.UUID;

public final class DrawerStateModelData extends ModelData
{
    @CapabilityInject(IDrawerAttributes.class)
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = null;

    private final boolean shroudedFlag;
    private final boolean lockedFlag;
    private final boolean voidFlag;
    private final boolean upgradesFlag;
    private final UUID owner;

    private final boolean[] emptyFlags;
    private final int[] upgradeLevels;


    public DrawerStateModelData (TileEntityDrawers tile) {
        IDrawerAttributes attr = null;
        boolean hasUpgrades = false;

        if (tile != null)
            attr = tile.getCapability(DRAWER_ATTRIBUTES_CAPABILITY, null);


        if (tile != null && attr != null) {
            shroudedFlag = attr.isConcealed();
            lockedFlag = attr.isItemLocked(LockAttribute.LOCK_POPULATED);
            voidFlag = attr.isVoid();

            owner = tile.getOwner();

            emptyFlags = new boolean[tile.getDrawerCount()];
            for (int i = 0; i < emptyFlags.length; i++) {
                IDrawer drawer = tile.getDrawer(i);
                emptyFlags[i] = (drawer == null) || drawer.isEmpty();
            }
            UpgradeData upgrades = tile.upgrades();
            upgradeLevels = new int[upgrades.getSlotCount()];

            for (int i = 0; i < upgradeLevels.length; i++) {
                ItemStack stack = upgrades.getUpgrade(i);
                if (stack.getItem() == ModItems.upgradeStorage) {
                    int level = EnumUpgradeStorage.byMetadata(stack.getMetadata()).getLevel();
                    upgradeLevels[i] = level;
                    hasUpgrades = true;
                }
                if (stack.getItem() == ModItems.upgradeOneStack) {
                    upgradeLevels[i] = 1;
                    hasUpgrades = true;
                }
            }
            Arrays.sort(upgradeLevels);
            upgradesFlag = hasUpgrades;
        }
        else {
            shroudedFlag = false;
            lockedFlag = false;
            voidFlag = false;
            upgradesFlag = false;
            owner = null;
            emptyFlags = new boolean[0];
            upgradeLevels = new int[0];
        }
    }

    public boolean isShrouded () {
        return shroudedFlag;
    }

    public boolean isItemLocked () {
        return lockedFlag;
    }

    public boolean isVoid () {
        return voidFlag;
    }

    public boolean isUpgraded () {
        return upgradesFlag;
    }

    public UUID getOwner () {
        return owner;
    }

    public int getDrawerCount () {
        return emptyFlags.length;
    }

    public int[] getUpgradeLevels () {
        return upgradeLevels;
    }

    public boolean isDrawerEmpty (int slot) {
        return slot < 0 || slot >= emptyFlags.length || emptyFlags[slot];
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        DrawerStateModelData other = (DrawerStateModelData)obj;
        if (shroudedFlag != other.shroudedFlag || lockedFlag != other.lockedFlag || voidFlag != other.voidFlag || upgradesFlag != other.upgradesFlag)
            return false;

        if (owner != null && !owner.equals(other.owner) || owner == null && other.owner != null)
            return false;

        return Arrays.equals(emptyFlags, other.emptyFlags) && Arrays.equals(upgradeLevels, other.upgradeLevels);
    }

    @Override
    public int hashCode () {
        int c = shroudedFlag ? 1 : 0;
        c = 37 * c + (lockedFlag ? 1 : 0);
        c = 37 * c + (voidFlag ? 1 : 0);
        c = 37 * c + (upgradesFlag ? 1 : 0);
        c = 37 * c + (owner != null ? owner.hashCode() : 0);

        for (boolean emptyFlag : emptyFlags)
            c = 37 * c + (emptyFlag ? 1 : 0);

        for (int level : upgradeLevels)
            c = 37 * c + level;

        return c;
    }
}
