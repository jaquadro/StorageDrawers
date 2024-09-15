package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;

public class ItemQuantifyKey extends ItemKey
{
    public ItemQuantifyKey (Properties properties) {
        super(properties);
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) {
        attrs.setIsShowingQuantity(!attrs.isShowingQuantity());
    }
}
