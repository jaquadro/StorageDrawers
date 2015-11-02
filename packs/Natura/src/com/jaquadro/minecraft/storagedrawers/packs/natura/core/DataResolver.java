package com.jaquadro.minecraft.storagedrawers.packs.natura.core;

import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.pack.StandardDataResolver;
import net.minecraft.creativetab.CreativeTabs;

public class DataResolver extends StandardDataResolver
{
    public DataResolver (String modID, String[] unlocalizedNames) {
        super(modID, unlocalizedNames);
    }

    @Override
    public CreativeTabs getCreativeTabs (BlockType type) {
        return ModCreativeTabs.getTabStorageDrawers();
    }
}
