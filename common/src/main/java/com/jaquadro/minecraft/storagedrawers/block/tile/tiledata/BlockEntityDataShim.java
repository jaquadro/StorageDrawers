package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public abstract class BlockEntityDataShim
{
    public abstract void read (HolderLookup.Provider provider, CompoundTag tag);

    public abstract CompoundTag write (HolderLookup.Provider provider, CompoundTag tag);

    public CompoundTag serializeNBT (HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        return write(provider, tag);
    }

    public void deserializeNBT (HolderLookup.Provider provider, CompoundTag nbt) {
        read(provider, nbt);
    }
}
