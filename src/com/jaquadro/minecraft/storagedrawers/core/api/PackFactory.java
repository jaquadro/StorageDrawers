package com.jaquadro.minecraft.storagedrawers.core.api;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlockFactory;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockSortingTrimPack;
import com.jaquadro.minecraft.storagedrawers.integration.IntegrationRegistry;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockSortingDrawersPack;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockTrimPack;
import com.jaquadro.minecraft.storagedrawers.integration.notenoughitems.NEIStorageDrawersConfig;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.SortingBlockRegistry;
import com.jaquadro.minecraft.storagedrawers.item.pack.ItemDrawersPack;
import com.jaquadro.minecraft.storagedrawers.item.pack.ItemSortingDrawersPack;
import com.jaquadro.minecraft.storagedrawers.item.pack.ItemSortingTrimPack;
import com.jaquadro.minecraft.storagedrawers.item.pack.ItemTrimPack;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class PackFactory implements IPackBlockFactory
{
    @Override
    public Block createBlock (BlockConfiguration blockConfig, IPackDataResolver dataResolver) {
        switch (blockConfig.getBlockType()) {
            case Drawers:
                return new BlockDrawersPack(dataResolver, blockConfig.getDrawerCount(), blockConfig.isHalfDepth()).setConfigName(getConfigName(blockConfig));
            case DrawersSorting:
                if (IntegrationRegistry.instance().isModLoaded("RefinedRelocation"))
                    return new BlockSortingDrawersPack(dataResolver, blockConfig.getDrawerCount(), blockConfig.isHalfDepth()).setConfigName(getConfigName(blockConfig));
                return null;
            case Trim:
                return new BlockTrimPack(dataResolver);
            case TrimSorting:
                if (IntegrationRegistry.instance().isModLoaded("RefinedRelocation"))
                    return new BlockSortingTrimPack(dataResolver);
                return null;
        }

        return null;
    }

    private static String getConfigName (BlockConfiguration blockConfig) {
        if (blockConfig.getDrawerCount() == 1)
            return "fullDrawers1";
        if (blockConfig.getDrawerCount() == 2 && !blockConfig.isHalfDepth())
            return "fullDrawers2";
        if (blockConfig.getDrawerCount() == 4 && !blockConfig.isHalfDepth())
            return "fullDrawers4";
        if (blockConfig.getDrawerCount() == 2 && blockConfig.isHalfDepth())
            return "halfDrawers2";
        if (blockConfig.getDrawerCount() == 4 && blockConfig.isHalfDepth())
            return "halfDrawers4";

        return "";
    }

    @Override
    public void registerBlock (Block block, String name) {
        if (block instanceof BlockSortingDrawersPack) {
            GameRegistry.registerBlock(block, ItemSortingDrawersPack.class, name);
            StorageDrawers.proxy.registerDrawer(block);
        }
        else if (block instanceof BlockDrawersPack) {
            GameRegistry.registerBlock(block, ItemDrawersPack.class, name);
            OreDictionary.registerOre("drawerBasic", new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
            StorageDrawers.proxy.registerDrawer(block);
        }
        else if (block instanceof BlockSortingTrimPack)
            GameRegistry.registerBlock(block, ItemSortingTrimPack.class, name);
        else if (block instanceof BlockTrimPack)
            GameRegistry.registerBlock(block, ItemTrimPack.class, name);
    }

    @Override
    public void bindSortingBlock (Block basicBlock, Block sortingBlock) {
        SortingBlockRegistry.register(basicBlock, sortingBlock);
    }

    @Override
    public void hideBlock (String blockID) {
        NEIStorageDrawersConfig.hideBlock(blockID);
    }
}
