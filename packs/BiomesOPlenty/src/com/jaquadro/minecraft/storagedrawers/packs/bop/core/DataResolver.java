package com.jaquadro.minecraft.storagedrawers.packs.bop.core;

import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.pack.ExtendedDataResolver;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public class DataResolver extends ExtendedDataResolver
{
    private static String[] textureNames = new String[] {
        "sacredoak", "cherry", "dark", "fir", "ethereal", "magic", "mangrove", "palm", "redwood", "willow", null, "pine", "hellbark", "jacaranda", "mahogany"
    };

    public DataResolver (String modID) {
        super(modID, textureNames);
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
        for (int i = 0, p = 0, m = 0; i < 8; i++, p++, m++)
            setPlankSlab(i, GameRegistry.findBlock("BiomesOPlenty", "planks"), p, GameRegistry.findBlock("BiomesOPlenty", "woodenSingleSlab1"), m);
        for (int i = 8, p = 8, m = 0; i < 10; i++, p++, m++)
            setPlankSlab(i, GameRegistry.findBlock("BiomesOPlenty", "planks"), p, GameRegistry.findBlock("BiomesOPlenty", "woodenSingleSlab2"), m);
        for (int i = 11, p = 11, m = 3; i < textureNames.length; i++, p++, m++)
            setPlankSlab(i, GameRegistry.findBlock("BiomesOPlenty", "planks"), p, GameRegistry.findBlock("BiomesOPlenty", "woodenSingleSlab2"), m);
    }
}
