package com.jaquadro.minecraft.storagedrawers.packs.misc.core;

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

    public static final CreativeTabs tabStorageDrawers = new CreativeTabs("storageDrawersMisc") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem () {
            return getTabItem();
        }

        @Override
        public int func_151243_f () {
            return 5;
        }
    };

    private static Item getTabItem () {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return Item.getItemFromBlock(Blocks.chest);

        IBlockConfig blockConfig = api.userConfig().blockConfig();

        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull2)))
            return Item.getItemFromBlock(ModBlocks.fullDrawers2[0]);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull4)))
            return Item.getItemFromBlock(ModBlocks.fullDrawers4[0]);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicFull1)))
            return Item.getItemFromBlock(ModBlocks.fullDrawers1[0]);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf2)))
            return Item.getItemFromBlock(ModBlocks.halfDrawers2[0]);
        if (blockConfig.isBlockEnabled(blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf4)))
            return Item.getItemFromBlock(ModBlocks.halfDrawers4[0]);

        return Item.getItemFromBlock(Blocks.chest);
    }
}
