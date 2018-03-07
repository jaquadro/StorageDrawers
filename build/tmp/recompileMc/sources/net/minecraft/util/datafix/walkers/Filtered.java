package net.minecraft.util.datafix.walkers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;

public abstract class Filtered implements IDataWalker
{
    private final String key;
    private final String value;

    public Filtered(String keyIn, String valueIn)
    {
        this.key = keyIn;
        this.value = valueIn;
    }

    public NBTTagCompound process(IDataFixer fixer, NBTTagCompound compound, int versionIn)
    {
        if (compound.getString(this.key).equals(this.value))
        {
            compound = this.filteredProcess(fixer, compound, versionIn);
        }

        return compound;
    }

    abstract NBTTagCompound filteredProcess(IDataFixer fixer, NBTTagCompound compound, int versionIn);
}