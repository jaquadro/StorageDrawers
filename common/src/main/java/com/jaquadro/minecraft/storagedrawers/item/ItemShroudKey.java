package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;

public class ItemShroudKey extends ItemKey
{
    public ItemShroudKey (Properties properties) {
        super(properties);
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) {
        attrs.setIsConcealed(!attrs.isConcealed());
    }
}
