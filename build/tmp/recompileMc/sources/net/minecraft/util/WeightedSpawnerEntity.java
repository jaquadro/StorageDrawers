package net.minecraft.util;

import net.minecraft.nbt.NBTTagCompound;

public class WeightedSpawnerEntity extends WeightedRandom.Item
{
    private final NBTTagCompound nbt;

    public WeightedSpawnerEntity()
    {
        super(1);
        this.nbt = new NBTTagCompound();
        this.nbt.setString("id", "Pig");
    }

    public WeightedSpawnerEntity(NBTTagCompound nbtIn)
    {
        this(nbtIn.hasKey("Weight", 99) ? nbtIn.getInteger("Weight") : 1, nbtIn.getCompoundTag("Entity"));
    }

    public WeightedSpawnerEntity(int itemWeightIn, NBTTagCompound nbtIn)
    {
        super(itemWeightIn);
        this.nbt = nbtIn;
    }

    public NBTTagCompound toCompoundTag()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setTag("Entity", this.nbt);
        nbttagcompound.setInteger("Weight", this.itemWeight);
        return nbttagcompound;
    }

    public NBTTagCompound getNbt()
    {
        return this.nbt;
    }
}