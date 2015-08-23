package com.jaquadro.minecraft.storagedrawers.packs.erebus.core;

import com.jaquadro.minecraft.storagedrawers.api.pack.StandardDataResolver;
import net.minecraft.creativetab.CreativeTabs;

public class DataResolver extends StandardDataResolver
{
    public static String[] blockNames = new String[] { "Baobab", "Eucalyptus", "Mahogany", "Mossbark", "Asper", "Cypress", null, "White", "Bamboo", "Rotten", "Marshwood", null, null, null, "Scorched", "Varnished" };
    public static String[] textureNames = new String[] { "baobab", "eucalyptus", "mahogany", "mossbark", "asper", "cypress", null, "white", "bamboo", "rotten", "marshwood", null, null, null, "scorched", "varnished" };

    public DataResolver (String modID, CreativeTabs creativeTab) {
        super(modID, blockNames, creativeTab);
    }

    @Override
    public String getUnlocalizedName (int meta) {
        if (!isValidMetaValue(meta))
            return null;

        return textureNames[meta];
    }
}
