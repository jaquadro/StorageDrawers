package com.jaquadro.minecraft.storagedrawers.packs.natura.core;

import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.pack.ExtendedDataResolver;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public class DataResolver extends ExtendedDataResolver
{
    private static String[] textureNames = new String[] {
        "eucalyptus", "sakura", "ghostwood", "redwood", "bloodwood", "hopseed", "maple", "silverbell", "purpleheart", "tigerwood", "willow", "darkwood", "fusewood"
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
            setPlankSlab(i, GameRegistry.findBlock("Natura", "planks"), p, GameRegistry.findBlock("Natura", "plankSlab1"), m);
        for (int i = 8, p = 8, m = 0; i < textureNames.length; i++, p++, m++)
            setPlankSlab(i, GameRegistry.findBlock("Natura", "planks"), p, GameRegistry.findBlock("Natura", "plankSlab2"), m);
    }
}
