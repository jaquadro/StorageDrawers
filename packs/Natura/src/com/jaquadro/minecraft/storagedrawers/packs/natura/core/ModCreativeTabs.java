package com.jaquadro.minecraft.storagedrawers.packs.natura.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public final class ModCreativeTabs
{
    private ModCreativeTabs () { }

    public static final CreativeTabs tabStorageDrawers = new CreativeTabs("storageDrawersNatura") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem () {
            return getTabItem();
        }
    };

    private static Item getTabItem () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return Item.getItemFromBlock(Blocks.chest);

        IBlockConfig blockConfig = api.userConfig().blockConfig();

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull2)))
            return Item.getItemFromBlock(ModBlocks.fullDrawers2);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull4)))
            return Item.getItemFromBlock(ModBlocks.fullDrawers4);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull1)))
            return Item.getItemFromBlock(ModBlocks.fullDrawers1);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf2)))
            return Item.getItemFromBlock(ModBlocks.halfDrawers2);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf4)))
            return Item.getItemFromBlock(ModBlocks.halfDrawers4);

        return Item.getItemFromBlock(Blocks.chest);
    }
}
