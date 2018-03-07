package net.minecraft.world.storage;

import java.io.File;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISaveFormat
{
    /**
     * Returns the name of the save format.
     */
    @SideOnly(Side.CLIENT)
    String getName();

    /**
     * Returns back a loader for the specified save directory
     */
    ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata);

    @SideOnly(Side.CLIENT)
    List<WorldSummary> getSaveList() throws AnvilConverterException;

    /**
     * gets if the map is old chunk saving (true) or McRegion (false)
     */
    boolean isOldMapFormat(String saveName);

    @SideOnly(Side.CLIENT)
    void flushCache();

    /**
     * Returns the world's WorldInfo object
     */
    @SideOnly(Side.CLIENT)
    WorldInfo getWorldInfo(String saveName);

    @SideOnly(Side.CLIENT)
    boolean isNewLevelIdAcceptable(String saveName);

    /**
     * Deletes a world directory.
     */
    @SideOnly(Side.CLIENT)
    boolean deleteWorldDirectory(String saveName);

    /**
     * Renames the world by storing the new name in level.dat. It does *not* rename the directory containing the world
     * data.
     */
    @SideOnly(Side.CLIENT)
    void renameWorld(String dirName, String newName);

    @SideOnly(Side.CLIENT)
    boolean isConvertible(String saveName);

    /**
     * converts the map to mcRegion
     */
    boolean convertMapFormat(String filename, IProgressUpdate progressCallback);

    File getFile(String p_186352_1_, String p_186352_2_);

    /**
     * Return whether the given world can be loaded.
     */
    @SideOnly(Side.CLIENT)
    boolean canLoadWorld(String saveName);
}