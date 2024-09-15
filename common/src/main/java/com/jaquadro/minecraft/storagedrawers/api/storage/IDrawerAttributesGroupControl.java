package com.jaquadro.minecraft.storagedrawers.api.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;

import java.util.EnumSet;

public interface IDrawerAttributesGroupControl
{
    default boolean toggleConcealed () {
        return false;
    }

    /**
     * Sets whether or not the drawer is currently concealed.
     * @return false if the operation is not supported, true otherwise.
     */
    default boolean setIsConcealed (boolean state) {
        return false;
    }

    default boolean toggleItemLocked (EnumSet<LockAttribute> attributes, LockAttribute attr) {
        return false;
    }

    /**
     * Sets the lock state of a drawer or group for the given lock attribute.
     * If canItemLock returns false, this is a no-op.
     * @return false if the operation is not supported, true otherwise.
     */
    default boolean setItemLocked (EnumSet<LockAttribute> attributes, LockAttribute attr, boolean isLocked) {
        return false;
    }

    default boolean toggleIsShowingQuantity () {
        return false;
    }

    /**
     * Sets whether or not the drawer is currently quantified.
     * @return false if the operation is not supported, true otherwise.
     */
    default boolean setIsShowingQuantity (boolean state) {
        return false;
    }
}
