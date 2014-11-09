package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.storage.*;
import com.jaquadro.minecraft.storagedrawers.storage.IStorageProvider;

public class TileEntityDrawersComp extends TileEntityDrawers
{
    public TileEntityDrawersComp () {
        super(3);
    }

    @Override
    protected IStorageProvider getStorageProvider () {
        return new CompStorageProvider();
    }

    private class CompStorageProvider extends DefaultStorageProvider
    {
        public CompStorageProvider () {
            super(TileEntityDrawersComp.this, TileEntityDrawersComp.this);
        }
    }
}
