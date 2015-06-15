package com.jaquadro.minecraft.storagedrawers.block.pack;
/*
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.api.pack.TextureType;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BlockDrawersPack extends BlockDrawers
{
    private IPackDataResolver resolver;

    public BlockDrawersPack (IPackDataResolver resolver) {
        super(resolver.getBlockName());

        setDefaultState(blockState.getBaseState().withProperty(BLOCK, EnumBasicDrawer.FULL2));

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
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        if (!StorageDrawers.config.cache.addonShowVanilla)
            return;

        for (int i = 0; i < 16; i++) {
            if (resolver.isValidMetaValue(i))
                list.add(new ItemStack(item, 1, i));
        }
    }
}
*/