package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IDrawerGroup extends ICapabilityProvider
{
    /**
     * Gets the number of drawers contained within this group.
     */
    int getDrawerCount ();

    /**
     * Gets the drawer at the given slot within this group.
     */
    @Nonnull
    IDrawer getDrawer (int slot);

    /**
     * Gets the list of available drawer slots in priority order.
     */
    @Nonnull
    int[] getAccessibleDrawerSlots ();

    /**
     * Checks if the group is still valid to use (e.g. if a backing tile is still present)
     */
    default boolean isGroupValid () {
        return true;
    };

    @Nonnull
    @Override
    default <T> LazyOptional<T> getCapability (@Nonnull final Capability<T> cap, final @Nullable Direction side) {
        return LazyOptional.empty();
    }
}
