package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;


public class ItemDrawerKey extends ItemKey
{
    public ItemDrawerKey (String registryName, String unlocalizedName) {
        setRegistryName(registryName);
        setUnlocalizedName(unlocalizedName);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setMaxDamage(0);
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) {
        boolean locked = attrs.isItemLocked(LockAttribute.LOCK_POPULATED);
        attrs.setItemLocked(LockAttribute.LOCK_EMPTY, !locked);
        attrs.setItemLocked(LockAttribute.LOCK_POPULATED, !locked);
    }
}
