package com.jaquadro.minecraft.storagedrawers.block.pack;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.*;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.BlockSortingTrim;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BlockSortingTrimPack extends BlockSortingTrim implements IPackBlock
{
    IPackDataResolver resolver;

    public BlockSortingTrimPack (IPackDataResolver resolver) {
        super(resolver.getBlockName(BlockConfiguration.TrimSorting));

        this.resolver = resolver;

        CreativeTabs tabs = resolver.getCreativeTabs(BlockType.TrimSorting);
        if (StorageDrawers.config.cache.addonSeparateVanilla && tabs != null)
            setCreativeTab(tabs);
        else
            setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    public String[] getUnlocalizedNames () {
        String[] names = new String[16];
        for (int i = 0; i < 16; i++) {
            if (resolver.isValidMetaValue(i))
                names[i] = resolver.getUnlocalizedName(i);
        }

        return names;
    }

    @Override
    public IPackDataResolver getDataResolver () {
        return resolver;
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        if (!StorageDrawers.config.cache.addonShowVanilla)
            return;

        for (int i = 0; i < 16; i++) {
            if (resolver.isValidMetaValue(i))
                list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta) {
        meta = meta % 16;
        return iconTrim[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister register) {
        iconTrim = new IIcon[16];

        for (int i = 0; i < 16; i++) {
            if (!resolver.isValidMetaValue(i))
                continue;

            iconTrim[i] = register.registerIcon(resolver.getTexturePath(TextureType.TrimBlock, i));
        }
    }
}
