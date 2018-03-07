package net.minecraft.world.border;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumBorderStatus
{
    GROWING(4259712),
    SHRINKING(16724016),
    STATIONARY(2138367);

    private final int id;

    private EnumBorderStatus(int id)
    {
        this.id = id;
    }

    /**
     * Returns an integer that represents the state of the world border. Growing, Shrinking and Stationary all have
     * unique values.
     */
    @SideOnly(Side.CLIENT)
    public int getID()
    {
        return this.id;
    }
}