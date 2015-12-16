package com.jaquadro.minecraft.storagedrawers.block.pack;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.*;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BlockDrawersPack extends BlockDrawers implements IPackBlock
{
    private IPackDataResolver resolver;

    public BlockDrawersPack (IPackDataResolver resolver, int drawerCount, boolean halfDepth) {
        super(resolver.getBlockName(BlockConfiguration.by(BlockType.Drawers, drawerCount, halfDepth)), drawerCount, halfDepth);

        this.resolver = resolver;

        CreativeTabs tabs = resolver.getCreativeTabs(BlockType.Drawers);
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
    public void registerBlockIcons (IIconRegister register) {
        super.registerBlockIcons(register);

        iconSide = new IIcon[16];
        iconSideH = new IIcon[16];
        iconSideV = new IIcon[16];
        iconFront1 = new IIcon[16];
        iconFront2 = new IIcon[16];
        iconFront4 = new IIcon[16];
        iconTrim = new IIcon[16];

        for (int i = 0; i < 16; i++) {
            if (!resolver.isValidMetaValue(i))
                continue;

            iconFront1[i] = register.registerIcon(resolver.getTexturePath(TextureType.Front1, i));
            iconFront2[i] = register.registerIcon(resolver.getTexturePath(TextureType.Front2, i));
            iconFront4[i] = register.registerIcon(resolver.getTexturePath(TextureType.Front4, i));
            iconSide[i] = register.registerIcon(resolver.getTexturePath(TextureType.Side, i));
            iconSideV[i] = register.registerIcon(resolver.getTexturePath(TextureType.SideVSplit, i));
            iconSideH[i] = register.registerIcon(resolver.getTexturePath(TextureType.SideHSplit, i));
            iconTrim[i] = register.registerIcon(resolver.getTexturePath(TextureType.TrimBorder, i));
        }
    }
}
