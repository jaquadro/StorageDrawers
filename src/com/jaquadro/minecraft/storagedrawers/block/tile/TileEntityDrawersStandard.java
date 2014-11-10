package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.storage.*;
import com.jaquadro.minecraft.storagedrawers.storage.IStorageProvider;
import net.minecraft.tileentity.TileEntity;

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
        return new StandardStorageProvider();
    }

    private class StandardStorageProvider extends DefaultStorageProvider {

        public StandardStorageProvider () {
            super(TileEntityDrawersStandard.this, TileEntityDrawersStandard.this);
        }

        @Override
        public int getSlotStackCapacity (int slot) {
            ConfigManager config = StorageDrawers.config;
            return config.getStorageUpgradeMultiplier(getStorageLevel()) * getDrawerCapacity();
        }
    }
}
