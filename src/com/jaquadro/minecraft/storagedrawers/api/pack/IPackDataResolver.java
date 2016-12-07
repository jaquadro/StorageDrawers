package com.jaquadro.minecraft.storagedrawers.api.pack;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public interface IPackDataResolver
{
    String getPackModID ();

    String getBlockName (BlockConfiguration blockConfig);

    CreativeTabs getCreativeTabs (BlockType type);

    boolean isValidMetaValue (int meta);

    String getUnlocalizedName (int meta);

    String getTexturePath (TextureType type, int meta);

    Block getBlock (BlockConfiguration blockConfig);

    Block getPlankBlock (int meta);

    Block getSlabBlock (int meta);

    int getPlankMeta (int meta);

    int getSlabMeta (int meta);
}
