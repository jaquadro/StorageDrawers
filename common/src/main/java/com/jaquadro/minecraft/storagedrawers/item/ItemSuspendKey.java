package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;

public class ItemSuspendKey extends ItemKey
{
    public ItemSuspendKey (Properties properties) {
        super(properties);
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) {
        attrs.setIsSuspended(!attrs.isSuspended());
    }

    @Override
    public boolean isEnabled () {
        return ModCommonConfig.INSTANCE.TOOLS.suspendKey.enable.get();
    }
}
