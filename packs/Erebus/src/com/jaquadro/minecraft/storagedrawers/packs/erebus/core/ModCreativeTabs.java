package com.jaquadro.minecraft.storagedrawers.packs.erebus.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public final class ModCreativeTabs
{
    private ModCreativeTabs () { }

    public static final CreativeTabs tabStorageDrawers = new CreativeTabs("storageDrawersErebus") {
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
        if (config.isBlockEnabled("fulldrawers1"))
            return Item.getItemFromBlock(ModBlocks.fullDrawers1);
        if (config.isBlockEnabled("halfdrawers2"))
            return Item.getItemFromBlock(ModBlocks.halfDrawers2);
        if (config.isBlockEnabled("halfdrawers4"))
            return Item.getItemFromBlock(ModBlocks.halfDrawers4);

        return Item.getItemFromBlock(Blocks.chest);
    }
}
