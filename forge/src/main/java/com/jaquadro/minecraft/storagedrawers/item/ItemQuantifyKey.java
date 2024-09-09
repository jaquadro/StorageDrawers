package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import net.minecraft.world.item.Item;

public class ItemQuantifyKey extends ItemKey
{
    public ItemQuantifyKey (Item.Properties properties) {
        super(properties);
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) {
        attrs.setIsShowingQuantity(!attrs.isShowingQuantity());
    }
}
