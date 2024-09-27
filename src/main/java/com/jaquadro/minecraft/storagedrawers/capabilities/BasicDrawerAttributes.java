package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.EnumSet;

public class BasicDrawerAttributes implements IDrawerAttributesModifiable, INBTSerializable<CompoundTag>
{
    private EnumSet<LockAttribute> itemLock = EnumSet.noneOf(LockAttribute.class);
    private boolean isConcealed;
    private boolean isShowingQuantity;
    private boolean isVoid;
    private boolean isUnlimitedStorage;
    private boolean isUnlimitedVending;
    private boolean isConversion;
    private boolean hasFillLevel;
    private boolean hasBalancedFill;
    private int priority;

    @Override
    public boolean canItemLock (LockAttribute attr) {
        return true;
    }

    @Override
    public boolean isItemLocked (LockAttribute attr) {
        return itemLock.contains(attr);
    }

    @Override
    public boolean setItemLocked (LockAttribute attr, boolean isLocked) {
        if (isItemLocked(attr) != isLocked) {
            if (isLocked)
                itemLock.add(attr);
            else
                itemLock.remove(attr);

            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isConcealed () {
        return isConcealed;
    }

    @Override
    public boolean setIsConcealed (boolean state) {
        if (isConcealed != state) {
            isConcealed = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isVoid () {
        return isVoid;
    }

    @Override
    public boolean setIsVoid (boolean state) {
        if (isVoid != state) {
            isVoid = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean hasFillLevel () {
        return hasFillLevel;
    }

    @Override
    public boolean setHasFillLevel (boolean state) {
        if (hasFillLevel != state) {
            hasFillLevel = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isShowingQuantity () {
        return isShowingQuantity;
    }

    @Override
    public boolean setIsShowingQuantity (boolean state) {
        if (isShowingQuantity != state) {
            isShowingQuantity = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public int getPriority () {
        return priority;
    }

    @Override
    public boolean setPriority (int priority) {
        if (this.priority != priority) {
            this.priority = priority;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isUnlimitedStorage () {
        return isUnlimitedStorage;
    }

    @Override
    public boolean setIsUnlimitedStorage (boolean state) {
        if (isUnlimitedStorage != state) {
            isUnlimitedStorage = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isUnlimitedVending () {
        return isUnlimitedVending;
    }

    @Override
    public boolean setIsUnlimitedVending (boolean state) {
        if (isUnlimitedVending != state) {
            isUnlimitedVending = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isDictConvertible () {
        return isConversion;
    }

    @Override
    public boolean setIsDictConvertible (boolean state) {
        if (isConversion != state) {
            isConversion = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isBalancedFill () {
        return hasBalancedFill;
    }

    @Override
    public boolean setIsBalancedFill (boolean state) {
        if (hasBalancedFill != state) {
            hasBalancedFill = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public CompoundTag serializeNBT () {
        CompoundTag tag = new CompoundTag();

        tag.putInt("itemLock", LockAttribute.getBitfield(itemLock));
        tag.putBoolean("concealed", isConcealed);
        tag.putBoolean("void", isVoid);
        tag.putBoolean("quant", isShowingQuantity);
        tag.putBoolean("unlimited", isUnlimitedStorage);
        tag.putBoolean("vending", isUnlimitedVending);
        tag.putBoolean("conv", isConversion);
        tag.putBoolean("fillLevel", hasFillLevel);
        tag.putBoolean("balancedFill", hasBalancedFill);
        tag.putInt("priority", priority);

        return tag;
    }

    @Override
    public void deserializeNBT (CompoundTag nbt) {
        itemLock = LockAttribute.getEnumSet(nbt.getInt("itemLock"));
        isConcealed = nbt.getBoolean("concealed");
        isVoid = nbt.getBoolean("void");
        isShowingQuantity = nbt.getBoolean("quant");
        isUnlimitedStorage = nbt.getBoolean("unlimited");
        isUnlimitedVending = nbt.getBoolean("vending");
        isConversion = nbt.getBoolean("conv");
        hasFillLevel = nbt.getBoolean("fillLevel");
        hasBalancedFill = nbt.getBoolean("balancedFill");
        priority = nbt.getInt("priority");
    }

    protected void onAttributeChanged () { }
}
