package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class SlotStorage extends Slot
{
    private boolean missing = false;

    public SlotStorage (Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public SlotStorage (Container inventory, int index, int x, int y, boolean missing) {
        this(inventory, index, x, y);
        this.missing = missing;
    }

    @Override
    public boolean mayPickup (@NotNull Player player) {
        return false;
    }

    public boolean isMissing () {
        return missing;
    }
}
