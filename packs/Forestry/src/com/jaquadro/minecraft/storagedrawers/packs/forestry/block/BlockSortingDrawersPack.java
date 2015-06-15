package com.jaquadro.minecraft.storagedrawers.packs.forestry.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.BlockSortingDrawers;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.StorageDrawersPack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import java.util.List;

public class BlockSortingDrawersPack extends BlockSortingDrawers
{
    private String[] blockTextures;

    public BlockSortingDrawersPack (String blockName, int drawerCount, boolean halfDepth, int textureGroup) {
        super(blockName, drawerCount, halfDepth);

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
    public void registerBlockIcons (IIconRegister register) {
        super.registerBlockIcons(register);

        String[] subtex = blockTextures;

        iconSide = new IIcon[subtex.length];
        iconSideH = new IIcon[subtex.length];
        iconSideV = new IIcon[subtex.length];
        iconFront1 = new IIcon[subtex.length];
        iconFront2 = new IIcon[subtex.length];
        iconFront4 = new IIcon[subtex.length];
        iconTrim = new IIcon[subtex.length];
        iconSort = new IIcon[subtex.length];

        for (int i = 0; i < subtex.length; i++) {
            if (subtex[i] == null)
                continue;

            iconFront1[i] = register.registerIcon(StorageDrawersPack.MOD_ID + ":drawers_" + subtex[i] + "_front_1");
            iconFront2[i] = register.registerIcon(StorageDrawersPack.MOD_ID + ":drawers_" + subtex[i] + "_front_2");
            iconFront4[i] = register.registerIcon(StorageDrawersPack.MOD_ID + ":drawers_" + subtex[i] + "_front_4");
            iconSide[i] = register.registerIcon(StorageDrawersPack.MOD_ID + ":drawers_" + subtex[i] + "_side");
            iconSideV[i] = register.registerIcon(StorageDrawersPack.MOD_ID + ":drawers_" + subtex[i] + "_side_v");
            iconSideH[i] = register.registerIcon(StorageDrawersPack.MOD_ID + ":drawers_" + subtex[i] + "_side_h");
            iconTrim[i] = register.registerIcon(StorageDrawersPack.MOD_ID + ":drawers_" + subtex[i] + "_trim");
            iconSort[i] = register.registerIcon(StorageDrawersPack.MOD_ID + ":drawers_" + subtex[i] + "_sort");
        }
    }
}
