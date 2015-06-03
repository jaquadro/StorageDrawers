package com.jaquadro.minecraft.storagedrawers.packs.forestry.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.core.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BlockTrimPack extends BlockTrim
{
    private String[] blockTextures;

    public BlockTrimPack (String name, int textureGroup) {
        super(name);

        if (textureGroup == 0)
            blockTextures = BlockDrawersPack.textureNames1;
        else if (textureGroup == 1)
            blockTextures = BlockDrawersPack.textureNames2;

        if (StorageDrawers.config.cache.addonSeparateVanilla)
            setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        else
            setCreativeTab(com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        if (!StorageDrawers.config.cache.addonShowVanilla)
            return;

        for (int i = 0; i < blockTextures.length; i++) {
            if (blockTextures[i] != null)
                list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta) {
        if (blockTextures == BlockDrawersPack.textureNames1)
            return ModBlocks.fullDrawers1A.getIcon(0, meta);
        else
            return ModBlocks.fullDrawers1B.getIcon(0, meta);
    }
}
