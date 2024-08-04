package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IDrawerGroup extends ICapabilityProvider
{
    /**
     * Gets the number of drawers contained within this group.
     */
    int getDrawerCount ();

    /**
     * Gets the drawer at the given slot within this group.
     */
    @NotNull
    IDrawer getDrawer (int slot);

    /**
     * Gets the list of available drawer slots in priority order.
     */
    int[] getAccessibleDrawerSlots ();

    /**
     * Checks if the group is still valid to use (e.g. if a backing tile is still present)
     */
    default boolean isGroupValid () {
        return true;
    };

    @Override
    @NotNull
    default <T> LazyOptional<T> getCapability (@NotNull final Capability<T> cap, final @Nullable Direction side) {
        return LazyOptional.empty();
    }
}
