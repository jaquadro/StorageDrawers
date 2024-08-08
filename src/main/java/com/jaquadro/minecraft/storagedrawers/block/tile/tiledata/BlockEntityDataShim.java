package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public abstract class BlockEntityDataShim implements INBTSerializable<CompoundTag>
{
    public abstract void read (HolderLookup.Provider provider, CompoundTag tag);

    public abstract CompoundTag write (HolderLookup.Provider provider, CompoundTag tag);

    @Override
    public CompoundTag serializeNBT (HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        return write(provider, tag);
    }

    @Override
    public void deserializeNBT (HolderLookup.Provider provider, CompoundTag nbt) {
        read(provider, nbt);
    }
}
