package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotStorage extends Slot
{
    public SlotStorage (IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canTakeStack (EntityPlayer player) {
        return false;
    }
}
