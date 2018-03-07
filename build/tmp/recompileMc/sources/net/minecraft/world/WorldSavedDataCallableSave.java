package net.minecraft.world;

public class WorldSavedDataCallableSave implements Runnable
{
    private final WorldSavedData data;

    public WorldSavedDataCallableSave(WorldSavedData dataIn)
    {
        this.data = dataIn;
    }

    public void run()
    {
        this.data.markDirty();
    }
}