package com.jaquadro.minecraft.storagedrawers.packs.forestry.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public final class ModCreativeTabs
{
    private ModCreativeTabs () { }

    public static final CreativeTabs tabStorageDrawers = new CreativeTabs("storageDrawersForestry") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem () {
            return getTabItem();
        }

        @Override
        public int func_151243_f () {
            return 1;
        }
    };

    private static Item getTabItem () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return Item.getItemFromBlock(Blocks.chest);

        IBlockConfig blockConfig = api.userConfig().blockConfig();

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull2)))
            return Item.getItemFromBlock(ModBlocks.fullDrawers2A);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull4)))
            return Item.getItemFromBlock(ModBlocks.fullDrawers4A);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull1)))
            return Item.getItemFromBlock(ModBlocks.fullDrawers1A);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf2)))
            return Item.getItemFromBlock(ModBlocks.halfDrawers2A);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf4)))
            return Item.getItemFromBlock(ModBlocks.halfDrawers4A);

        return Item.getItemFromBlock(Blocks.chest);
    }
}
