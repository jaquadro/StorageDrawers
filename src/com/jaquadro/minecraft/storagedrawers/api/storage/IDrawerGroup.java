package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

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

    @Override
    default boolean hasCapability (@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Nullable
    @Override
    default <T> T getCapability (@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }
}
