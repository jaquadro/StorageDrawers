package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import com.jaquadro.minecraft.storagedrawers.storage.*;
import com.jaquadro.minecraft.storagedrawers.storage.IStorageProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class TileEntityDrawersStandard extends TileEntityDrawers
{
    private static final String[] GUI_IDS = new String[] {
        null, StorageDrawers.MOD_ID + ":basicDrawers1", StorageDrawers.MOD_ID + ":basicDrawers2", null, StorageDrawers.MOD_ID + ":basicDrawers4"
    };

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

    @Override
    public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {
        switch (getDrawerCount()) {
            case 1:
                return new ContainerDrawers1(playerInventory, this);
            case 2:
                return new ContainerDrawers2(playerInventory, this);
            case 4:
                return new ContainerDrawers4(playerInventory, this);
            default:
                return  null;
        }
    }

    @Override
    public String getGuiID () {
        return GUI_IDS[getDrawerCount()];
    }

    private class StandardStorageProvider extends DefaultStorageProvider
    {
        public StandardStorageProvider () {
            super(TileEntityDrawersStandard.this, TileEntityDrawersStandard.this);
        }

        @Override
        public int getSlotStackCapacity (int slot) {
            ConfigManager config = StorageDrawers.config;
            return getEffectiveStorageMultiplier() * getEffectiveDrawerCapacity();
        }

        @Override
        public boolean isLocked (int slot, LockAttribute attr) {
            return TileEntityDrawersStandard.this.isItemLocked(attr);
        }

        @Override
        public boolean isVoid (int slot) {
            return TileEntityDrawersStandard.this.isVoid();
        }

        @Override
        public boolean isShrouded (int slot) {
            return TileEntityDrawersStandard.this.isShrouded();
        }

        @Override
        public boolean setIsShrouded (int slot, boolean state) {
            TileEntityDrawersStandard.this.setIsShrouded(state);
            return true;
        }

        @Override
        public boolean isShowingQuantity (int slot) {
            return TileEntityDrawersStandard.this.isShowingQuantity();
        }

        @Override
        public boolean setIsShowingQuantity (int slot, boolean state) {
            return TileEntityDrawersStandard.this.setIsShowingQuantity(state);
        }

        @Override
        public boolean isStorageUnlimited (int slot) {
            return TileEntityDrawersStandard.this.isUnlimited();
        }

        @Override
        public boolean isVendingUnlimited (int slot) {
            return TileEntityDrawersStandard.this.isVending();
        }

        @Override
        public boolean isRedstone (int slot) {
            return TileEntityDrawersStandard.this.isRedstone();
        }
    }
}
