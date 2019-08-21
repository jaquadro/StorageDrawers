package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class TileDataShim implements INBTSerializable<CompoundNBT>
{
    public abstract void read (CompoundNBT tag);

    public abstract CompoundNBT write (CompoundNBT tag);

    @Override
    public CompoundNBT serializeNBT () {
        CompoundNBT tag = new CompoundNBT();
        return write(tag);
    }

    @Override
    public void deserializeNBT (CompoundNBT nbt) {
        read(nbt);
    }
}
