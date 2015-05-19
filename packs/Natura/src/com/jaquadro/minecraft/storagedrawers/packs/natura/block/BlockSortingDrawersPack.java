package com.jaquadro.minecraft.storagedrawers.packs.natura.block;

import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.BlockSortingDrawers;
import com.jaquadro.minecraft.storagedrawers.packs.natura.StorageDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.natura.core.ModCreativeTabs;
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
    public BlockSortingDrawersPack (String blockName, int drawerCount, boolean halfDepth) {
        super(blockName, drawerCount, halfDepth);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 0; i < BlockDrawersPack.textureNames.length; i++)
            list.add(new ItemStack(item, 1, i));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister register) {
        super.registerBlockIcons(register);

        String[] subtex = BlockDrawersPack.textureNames;

        iconSide = new IIcon[subtex.length];
        iconSideH = new IIcon[subtex.length];
        iconSideV = new IIcon[subtex.length];
        iconFront1 = new IIcon[subtex.length];
        iconFront2 = new IIcon[subtex.length];
        iconFront4 = new IIcon[subtex.length];
        iconTrim = new IIcon[subtex.length];
        iconSort = new IIcon[subtex.length];

        for (int i = 0; i < subtex.length; i++) {
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
