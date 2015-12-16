package com.jaquadro.minecraft.storagedrawers.packs.misc.core;

import com.jaquadro.minecraft.storagedrawers.api.pack.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public class DataResolver extends ExtendedDataResolver
{
    public static String[][] textureBank = new String[][] {
        new String[] { "ebxl_redwood", "ebxl_fir", "ebxl_acacia", "ebxl_cypress", "ebxl_japanesemaple", "ebxl_rainboweucalyptus", "ebxl_autumn", "ebxl_baldcypress", "ebxl_sakura" },
        new String[] { "hl_yellow", "hl_white", "hl_red", "hl_grey",
            "tc_greatwood", "tc_silverwood",
            "witchery_rowan", "witchery_alder", "witchery_hawthorn",
            "immeng_treated",
            "botania_livingwood", "botania_dreamwood" }
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
                    setPlankSlab(i, GameRegistry.findBlock("ExtrabiomesXL", "planks"), p, GameRegistry.findBlock("ExtrabiomesXL", "woodslab"), m);
                for (int i = 8, p = 8, m = 0; i < 9; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("ExtrabiomesXL", "planks"), p, GameRegistry.findBlock("ExtrabiomesXL", "woodslab2"), m);
                break;
            case 1:
                for (int i = 0, p = 0, m = 0; i < 4; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("Highlands", "hl_woodPlanks"), p, GameRegistry.findBlock("Highlands", "hl_woodSlab"), m);
                for (int i = 4, p = 6, m = 0; i < 6; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("Thaumcraft", "blockWoodenDevice"), p, GameRegistry.findBlock("Thaumcraft", "blockCosmeticSlabWood"), m);
                for (int i = 6, p = 0, m = 0; i < 9; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("witchery", "witchwood"), p, GameRegistry.findBlock("witchery", "witchwoodslab"), m);
                for (int i = 9, p = 0, m = 2; i < 10; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("ImmersiveEngineering", "treatedWood"), p, GameRegistry.findBlock("ImmersiveEngineering", "woodenDecoration"), m);
                for (int i = 10, p = 1, m = 0; i < 11; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("Botania", "livingwood"), p, GameRegistry.findBlock("Botania", "livingwood1Slab"), m);
                for (int i = 11, p = 1, m = 0; i < 12; i++, p++, m++)
                    setPlankSlab(i, GameRegistry.findBlock("Botania", "dreamwood"), p, GameRegistry.findBlock("Botania", "dreamwood1Slab"), m);
                break;
        }
    }
}
