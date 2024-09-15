package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.neoforged.neoforge.items.IItemHandler;

public class PlatformDrawerItemHandler extends DrawerItemHandler implements IItemHandler
{
    public PlatformDrawerItemHandler (IDrawerGroup group) {
        super(group);
    }
}
