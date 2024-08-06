package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public abstract class BlockEntityDataShim implements INBTSerializable<CompoundTag>
{
    public abstract void read (CompoundTag tag);

    public abstract CompoundTag write (CompoundTag tag);

    @Override
    public CompoundTag serializeNBT () {
        CompoundTag tag = new CompoundTag();
        return write(tag);
    }

    @Override
    public void deserializeNBT (CompoundTag nbt) {
        read(nbt);
    }
}
