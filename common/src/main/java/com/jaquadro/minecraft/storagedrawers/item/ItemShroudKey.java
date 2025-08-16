package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.world.item.Item;

public class ItemShroudKey extends ItemKey
{
    public ItemShroudKey (Item.Properties properties) {
        super(properties);
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) {
        attrs.setIsConcealed(!attrs.isConcealed());
    }

    @Override
    public boolean isEnabled () {
        return ModCommonConfig.INSTANCE.TOOLS.concealmentKey.enable.get();
    }
}
