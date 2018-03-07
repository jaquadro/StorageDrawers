package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData implements net.minecraftforge.common.util.INBTSerializable<NBTTagCompound>
{
    /** The name of the map data nbt */
    public final String mapName;
    /** Whether this MapDataBase needs saving to disk. */
    private boolean dirty;

    public WorldSavedData(String name)
    {
        this.mapName = name;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public abstract void readFromNBT(NBTTagCompound nbt);

    public abstract NBTTagCompound writeToNBT(NBTTagCompound compound);

    /**
     * Marks this MapDataBase dirty, to be saved to disk when the level next saves.
     */
    public void markDirty()
    {
        this.setDirty(true);
    }

    /**
     * Sets the dirty state of this MapDataBase, whether it needs saving to disk.
     */
    public void setDirty(boolean isDirty)
    {
        this.dirty = isDirty;
    }

    /**
     * Whether this MapDataBase needs saving to disk.
     */
    public boolean isDirty()
    {
        return this.dirty;
    }

    public void deserializeNBT(NBTTagCompound nbt)
    {
        this.readFromNBT(nbt);
    }

    public NBTTagCompound serializeNBT()
    {
        return this.writeToNBT(new NBTTagCompound());
    }
}