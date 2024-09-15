package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;

import java.util.EnumSet;

public class BasicDrawerAttributes implements IDrawerAttributesModifiable
{
    private EnumSet<LockAttribute> itemLock = EnumSet.noneOf(LockAttribute.class);
    private boolean isConcealed;
    private boolean isShowingQuantity;
    private boolean isVoid;
    private boolean isUnlimitedStorage;
    private boolean isUnlimitedVending;
    private boolean isConversion;
    private boolean hasFillLevel;

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

    protected void onAttributeChanged () { }
}
