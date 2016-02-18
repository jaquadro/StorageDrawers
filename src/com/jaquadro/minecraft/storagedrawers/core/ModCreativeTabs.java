package com.jaquadro.minecraft.storagedrawers.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModCreativeTabs
{
    private ModCreativeTabs () { }

    public static final CreativeTabs tabStorageDrawers = new CreativeTabs("storageDrawers") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem () {
            return getTabItem();
        }

        @Override
        public int getIconItemDamage () {
            return 1;
        }
    };

    private static Item getTabItem () {
        return Item.getItemFromBlock(ModBlocks.basicDrawers);
    }
}
