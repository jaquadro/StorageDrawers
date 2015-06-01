package com.jaquadro.minecraft.storagedrawers.packs.forestry.block;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.StorageDrawersPack;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.core.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import java.util.List;

public class BlockDrawersPack extends BlockDrawers
{
    public static String[] textureNames1 = new String[] { "larch", "teak", "acacia", "lime", "chestnut", "wenge", "baobab", "sequoia", "kapok", "ebony", "mahogany", "balsa", "willow", "walnut", "greenheart", "cherry"};
    public static String[] textureNames2 = new String[] { "mahoe", "poplar", "palm", "papaya", "pine", "plum", "maple", "citrus", "giganteum", "ipe", "padauk", "cocobolo", "zebrawood" };

    private String[] blockTextures;

    public BlockDrawersPack (String blockName, int drawerCount, boolean halfDepth, int textureGroup) {
        super(Material.wood, blockName, drawerCount, halfDepth);

        if (textureGroup == 0)
            blockTextures = textureNames1;
        else if (textureGroup == 1)
            blockTextures = textureNames2;

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
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
        }
    }
}
