package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ConnectionMode;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.core.Direction;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

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
    private boolean hasBalancedFill;
    private boolean isHopper;
    private boolean isMagnet;
    private boolean isSuspended;
    private int priority;
    private Map<Direction, ConnectionMode> sidedConnections = new HashMap<>();

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
    public boolean isHopper () {
        return isHopper;
    }

    @Override
    public boolean setIsHopper (boolean state) {
        if (isHopper != state) {
            isHopper = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isMagnet () {
        return isMagnet;
    }

    @Override
    public boolean setIsMagnet (boolean state) {
        if (isMagnet != state) {
            isMagnet = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isSuspended () {
        return isSuspended;
    }

    @Override
    public boolean setIsSuspended (boolean state) {
        if (isSuspended != state) {
            isSuspended = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public ConnectionMode getSidedConnectionMode (Direction dir) {
        return sidedConnections.getOrDefault(dir, ConnectionMode.DEFAULT);
    }

    @Override
    public boolean setSidedConnectionMode (Direction dir, ConnectionMode mode) {
        ConnectionMode cur = sidedConnections.getOrDefault(dir, ConnectionMode.DEFAULT);
        if (cur != mode) {
            if (mode == ConnectionMode.DEFAULT)
                sidedConnections.remove(dir);
            else
                sidedConnections.put(dir, mode);

            onAttributeChanged();
        }

        return true;
    }

    protected void onAttributeChanged () { }
}
