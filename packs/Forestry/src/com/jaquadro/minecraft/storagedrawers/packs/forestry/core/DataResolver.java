package com.jaquadro.minecraft.storagedrawers.packs.forestry.core;

import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.pack.ExtendedDataResolver;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public class DataResolver extends ExtendedDataResolver
{
    public static String[][] textureBank = new String[][] {
        new String[] { "larch", "teak", "acacia", "lime", "chestnut", "wenge", "baobab", "sequoia", "kapok", "ebony", "mahogany", "balsa", "willow", "walnut", "greenheart", "cherry" },
        new String[] { "mahoe", "poplar", "palm", "papaya", "pine", "plum", "maple", "citrus", "giganteum", "ipe", "padauk", "cocobolo", "zebrawood" }
    };

    private int bank;

    public DataResolver (String modID, int bankNumber) {
        super(modID, textureBank[bankNumber]);
        bank = bankNumber;
    }

    @Override
    public CreativeTabs getCreativeTabs (BlockType type) {
        return ModCreativeTabs.getTabStorageDrawers();
    }

    @Override
    public Block getBlock (BlockConfiguration blockConfig) {
        switch (blockConfig.getBlockType()) {
            case Drawers:
                if (blockConfig.getDrawerCount() == 1)
                    return ModBlocks.fullDrawers1[bank];
                if (blockConfig.getDrawerCount() == 2 && !blockConfig.isHalfDepth())
                    return ModBlocks.fullDrawers2[bank];
                if (blockConfig.getDrawerCount() == 4 && !blockConfig.isHalfDepth())
                    return ModBlocks.fullDrawers4[bank];
                if (blockConfig.getDrawerCount() == 2 && blockConfig.isHalfDepth())
                    return ModBlocks.halfDrawers2[bank];
                if (blockConfig.getDrawerCount() == 4 && blockConfig.isHalfDepth())
                    return ModBlocks.halfDrawers4[bank];
                break;
            case DrawersSorting:
                if (blockConfig.getDrawerCount() == 1)
                    return RefinedRelocation.fullDrawers1[bank];
                if (blockConfig.getDrawerCount() == 2 && !blockConfig.isHalfDepth())
                    return RefinedRelocation.fullDrawers2[bank];
                if (blockConfig.getDrawerCount() == 4 && !blockConfig.isHalfDepth())
                    return RefinedRelocation.fullDrawers4[bank];
                if (blockConfig.getDrawerCount() == 2 && blockConfig.isHalfDepth())
                    return RefinedRelocation.halfDrawers2[bank];
                if (blockConfig.getDrawerCount() == 4 && blockConfig.isHalfDepth())
                    return RefinedRelocation.halfDrawers4[bank];
                break;
            case Trim:
                return ModBlocks.trim[bank];
            case TrimSorting:
                return RefinedRelocation.trim[bank];
        }
        return null;
    }

    @Override
    public void init () {
        switch (bank) {
            case 0:
                for (int i = 0, p = 0, m = 0; i < 8; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("Forestry", "planks"), p, GameRegistry.findBlock("Forestry", "slabs1"), m);
                for (int i = 8, p = 8, m = 0; i < textureBank[0].length; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("Forestry", "planks"), p, GameRegistry.findBlock("Forestry", "slabs2"), m);
                break;
            case 1:
                for (int i = 0, p = 0, m = 0; i < 8; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("Forestry", "planks2"), p, GameRegistry.findBlock("Forestry", "slabs3"), m);
                for (int i = 8, p = 8, m = 0; i < textureBank[1].length; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("Forestry", "planks2"), p, GameRegistry.findBlock("Forestry", "slabs4"), m);
                break;
        }
    }
}
