package net.minecraft.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IProgressUpdate
{
    /**
     * Shows the 'Saving level' string.
     */
    void displaySavingString(String message);

    /**
     * this string, followed by "working..." and then the "% complete" are the 3 lines shown. This resets progress to 0,
     * and the WorkingString to "working...".
     */
    @SideOnly(Side.CLIENT)
    void resetProgressAndMessage(String message);

    /**
     * Displays a string on the loading screen supposed to indicate what is being done currently.
     */
    void displayLoadingString(String message);

    /**
     * Updates the progress bar on the loading screen to the specified amount.
     */
    void setLoadingProgress(int progress);

    @SideOnly(Side.CLIENT)
    void setDoneWorking();
}