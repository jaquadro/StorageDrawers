/*package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.pack.ExtendedDataResolver;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.integration.RefinedRelocation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;

public class DataResolver extends ExtendedDataResolver
{
    private static String[] textureNames = BlockWood.field_150096_a;

    public DataResolver (String modID) {
        super(modID, textureNames);
    }

    @Override
    public CreativeTabs getCreativeTabs (BlockType type) {
        return ModCreativeTabs.tabStorageDrawers;
    }

    @Override
    public Block getBlock (BlockConfiguration blockConfig) {
        switch (blockConfig.getBlockType()) {
            case Drawers:
                if (blockConfig.getDrawerCount() == 1)
                    return ModBlocks.fullDrawers1;
                if (blockConfig.getDrawerCount() == 2 && !blockConfig.isHalfDepth())
                    return ModBlocks.fullDrawers2;
                if (blockConfig.getDrawerCount() == 4 && !blockConfig.isHalfDepth())
                    return ModBlocks.fullDrawers4;
                if (blockConfig.getDrawerCount() == 2 && blockConfig.isHalfDepth())
                    return ModBlocks.halfDrawers2;
                if (blockConfig.getDrawerCount() == 4 && blockConfig.isHalfDepth())
                    return ModBlocks.halfDrawers4;
                break;
            case DrawersSorting:
                if (blockConfig.getDrawerCount() == 1)
                    return RefinedRelocation.fullDrawers1;
                if (blockConfig.getDrawerCount() == 2 && !blockConfig.isHalfDepth())
                    return RefinedRelocation.fullDrawers2;
                if (blockConfig.getDrawerCount() == 4 && !blockConfig.isHalfDepth())
                    return RefinedRelocation.fullDrawers4;
                if (blockConfig.getDrawerCount() == 2 && blockConfig.isHalfDepth())
                    return RefinedRelocation.halfDrawers2;
                if (blockConfig.getDrawerCount() == 4 && blockConfig.isHalfDepth())
                    return RefinedRelocation.halfDrawers4;
                break;
            case Trim:
                return ModBlocks.trim;
            case TrimSorting:
                return RefinedRelocation.trim;
        }
        return null;
    }

    @Override
    public void init () {
        for (int i = 0, p = 0, m = 0; i < 6; i++, p++, m++)
            setPlankSlab(i, Blocks.planks, p, Blocks.wooden_slab, m);
    }
}
*/