package com.jaquadro.minecraft.storagedrawers.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public final class ModCreativeTabs
{
    private ModCreativeTabs () { }

    public static final CreativeTabs tabStorageDrawers = new CreativeTabs("storageDrawers") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem () {
            return Item.getItemFromBlock(ModBlocks.fullDrawers2);
        }
    };
}
