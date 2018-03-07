package net.minecraft.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITabCompleter
{
    /**
     * Sets the list of tab completions, as long as they were previously requested.
     */
    void setCompletions(String... newCompletions);
}