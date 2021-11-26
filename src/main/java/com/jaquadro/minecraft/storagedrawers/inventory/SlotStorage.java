package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotStorage extends Slot
{
    public SlotStorage (Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPickup (Player player) {
        return false;
    }
}
