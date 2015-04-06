package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
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
    };

    private static Item getTabItem () {
        ConfigManager config = StorageDrawers.config;

        if (config.isBlockEnabled("fulldrawers2"))
            return Item.getItemFromBlock(ModBlocks.fullDrawers2);
        if (config.isBlockEnabled("fulldrawers4"))
            return Item.getItemFromBlock(ModBlocks.fullDrawers4);
        if (config.isBlockEnabled("halfdrawers2"))
            return Item.getItemFromBlock(ModBlocks.halfDrawers2);
        if (config.isBlockEnabled("halfdrawers4"))
            return Item.getItemFromBlock(ModBlocks.halfDrawers4);

        return Item.getItemFromBlock(Blocks.chest);
    }
}
