package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.core.Direction;
import net.minecraftforge.items.IItemHandler;

public class PlatformDrawerItemHandler extends DrawerItemHandler implements IItemHandler
{
    public PlatformDrawerItemHandler (IDrawerGroup group) {
        super(group);
    }

    private PlatformDrawerItemHandler (IDrawerGroup group, Direction dir) {
        super(group, dir);
    }

    public static DrawerItemHandler createDirectionalHandler (IDrawerGroup group, Direction dir) {
        return new PlatformDrawerItemHandler(group, dir);
    }
}
