package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.storage.*;
import com.jaquadro.minecraft.storagedrawers.storage.IStorageProvider;

public class TileEntityDrawersStandard extends TileEntityDrawers
{
    public TileEntityDrawersStandard () {
        super(1);
    }

    public void setDrawerCount (int count) {
        initWithDrawerCount(count);
    }

    @Override
    protected IStorageProvider getStorageProvider () {
        return new DefaultStorageProvider(this, this);
    }
}
