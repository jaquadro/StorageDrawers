package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;

public interface IDrawerGroup
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

    default <T> T getCapability(@NotNull BlockCapability<T, Void> capability) { return null; }
}
