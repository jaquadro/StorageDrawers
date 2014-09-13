package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemUpgradeStatus extends Item
{
    public static final String[] iconNames = new String[] { null, "level1", "level2", "level3" };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemUpgradeStatus (String name) {
        setUnlocalizedName(name);
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int meta) {
        return icons[MathHelper.clamp_int(meta, 0, iconNames.length - 1)];
    }

    @Override
    public String getUnlocalizedName (ItemStack itemStack) {
        int meta = MathHelper.clamp_int(itemStack.getItemDamage(), 0, iconNames.length - 1);
        if (iconNames[meta] == null)
            return super.getUnlocalizedName();

        return super.getUnlocalizedName() + "." + iconNames[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        String name = getUnlocalizedName(itemStack);
        list.add(StatCollector.translateToLocalFormatted(name + ".description"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 0; i < iconNames.length; i++) {
            if (iconNames[i] != null)
                list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister register) {
        icons = new IIcon[iconNames.length];

        for (int i = 0; i < iconNames.length; i++) {
            if (iconNames[i] != null)
                icons[i] = register.registerIcon(StorageDrawers.MOD_ID + ":upgrade_status_" + iconNames[i]);
        }
    }
}
