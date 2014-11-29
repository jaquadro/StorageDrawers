package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.storage.*;
import com.jaquadro.minecraft.storagedrawers.storage.IStorageProvider;
import net.minecraft.tileentity.TileEntity;

public class TileEntityDrawersStandard extends TileEntityDrawers
{
    private IStorageProvider storageProvider = new StandardStorageProvider();

    public TileEntityDrawersStandard () {
        super(1);
    }

    public void setDrawerCount (int count) {
        initWithDrawerCount(count);
    }

    protected IStorageProvider getStorageProvider () {
        if (storageProvider == null)
            storageProvider = new StandardStorageProvider();
        return storageProvider;
    }

    @Override
    protected IDrawer createDrawer (int slot) {
        return new DrawerData(getStorageProvider(), slot);
    }

    private class StandardStorageProvider extends DefaultStorageProvider
    {
        public StandardStorageProvider () {
            super(TileEntityDrawersStandard.this, TileEntityDrawersStandard.this);
        }

        @Override
        public int getSlotStackCapacity (int slot) {
            ConfigManager config = StorageDrawers.config;
            return config.getStorageUpgradeMultiplier(getStorageLevel()) * getDrawerCapacity();
        }

        @Override
        public boolean isLocked (int slot) {
            return TileEntityDrawersStandard.this.isLocked();
        }
    }
}
