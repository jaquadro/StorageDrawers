package net.minecraft.stats;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IStatType
{
    /**
     * Formats a given stat for human consumption.
     */
    @SideOnly(Side.CLIENT)
    String format(int number);
}