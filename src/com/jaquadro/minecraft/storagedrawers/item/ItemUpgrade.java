package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemUpgrade extends Item
{
    public ItemUpgrade (String registryName, String unlocalizedName) {
        setRegistryName(registryName);
        setUnlocalizedName(unlocalizedName);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List<String> list, boolean par4) {
        String name = getUnlocalizedName(itemStack);
        list.add(I18n.format(name + ".description"));
    }
}
