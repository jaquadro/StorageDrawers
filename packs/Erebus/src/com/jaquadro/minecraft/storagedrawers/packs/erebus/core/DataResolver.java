package com.jaquadro.minecraft.storagedrawers.packs.erebus.core;

import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.pack.ExtendedDataResolver;
import com.jaquadro.minecraft.storagedrawers.api.pack.StandardDataResolver;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public class DataResolver extends ExtendedDataResolver
{
    public static String[] blockNames = new String[] { "Baobab", "Eucalyptus", "Mahogany", "Mossbark", "Asper", "Cypress", null, "White", "Bamboo", "Rotten", "Marshwood", null, null, null, "Scorched", "Varnished" };
    public static String[] textureNames = new String[] { "baobab", "eucalyptus", "mahogany", "mossbark", "asper", "cypress", null, "white", "bamboo", "rotten", "marshwood", null, null, null, "scorched", "varnished" };

    public DataResolver (String modID) {
        super(modID, blockNames);
    }

    @Override
    public CreativeTabs getCreativeTabs (BlockType type) {
        return ModCreativeTabs.getTabStorageDrawers();
    }

    @Override
    public String getUnlocalizedName (int meta) {
        if (!isValidMetaValue(meta))
            return null;

        return textureNames[meta];
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
        Block planks = GameRegistry.findBlock("erebus", "planks");
        for (int i = 0, p = 0; i < 6; i++, p++)
            setPlankSlab(i, planks, p, GameRegistry.findBlock("erebus", getPlankName(getUnlocalizedName(i))), 0);
        for (int i = 7, p = 7; i < 11; i++, p++)
            setPlankSlab(i, planks, p, GameRegistry.findBlock("erebus", getPlankName(getUnlocalizedName(i))), 0);
        for (int i = 14, p = 14; i < 15; i++, p++)
            setPlankSlab(i, GameRegistry.findBlock("erebus", "planks_scorched"), p, GameRegistry.findBlock("erebus", getPlankName(getUnlocalizedName(i))), 0);
        for (int i = 15, p = 15; i < 16; i++, p++)
            setPlankSlab(i, GameRegistry.findBlock("erebus", "planks_varnished"), p, GameRegistry.findBlock("erebus", getPlankName(getUnlocalizedName(i))), 0);
    }

    private String getPlankName (String name) {
        return "slabPlanks" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
