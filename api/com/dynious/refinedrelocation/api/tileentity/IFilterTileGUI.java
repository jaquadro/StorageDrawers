package com.dynious.refinedrelocation.api.tileentity;

import com.dynious.refinedrelocation.api.filter.IFilterGUI;
import net.minecraft.tileentity.TileEntity;

/**
 * If your TileEntity implements the interface it will be able to open the Filtering GUI.
 */
public interface IFilterTileGUI extends IFilterTile
{
    public IFilterGUI getFilter();

    public TileEntity getTileEntity();

    public void onFilterChanged();
}
