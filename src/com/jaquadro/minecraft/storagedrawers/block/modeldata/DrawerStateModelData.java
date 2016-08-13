package com.jaquadro.minecraft.storagedrawers.block.modeldata;

import com.jaquadro.minecraft.chameleon.model.ModelData;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;

import java.util.UUID;

public final class DrawerStateModelData extends ModelData
{
    private final boolean shroudedFlag;
    private final boolean lockedFlag;
    private final boolean voidFlag;
    private final UUID owner;

    private final boolean[] emptyFlags;

    public DrawerStateModelData (TileEntityDrawers tile) {
        if (tile != null) {
            shroudedFlag = tile.isShrouded();
            lockedFlag = tile.isItemLocked(LockAttribute.LOCK_POPULATED);
            voidFlag = tile.isVoid();
            owner = tile.getOwner();

            emptyFlags = new boolean[tile.getDrawerCount()];
            for (int i = 0; i < emptyFlags.length; i++) {
                IDrawer drawer = tile.getDrawer(i);
                emptyFlags[i] = (drawer == null) || drawer.isEmpty();
            }
        }
        else {
            shroudedFlag = false;
            lockedFlag = false;
            voidFlag = false;
            owner = null;
            emptyFlags = new boolean[0];
        }
    }

    public boolean isShrouded () {
        return shroudedFlag;
    }

    public boolean isItemLocked () {
        return lockedFlag;
    }

    public boolean isVoid () {
        return voidFlag;
    }

    public UUID getOwner () {
        return owner;
    }

    public int getDrawerCount () {
        return emptyFlags.length;
    }

    public boolean isDrawerEmpty (int slot) {
        return slot < 0 || slot >= emptyFlags.length || emptyFlags[slot];
    }
}
