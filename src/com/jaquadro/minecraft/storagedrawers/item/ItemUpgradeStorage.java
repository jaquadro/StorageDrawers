package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemUpgradeStorage extends Item
{
    public String[] getResourceVariants () {
        String[] variants = new String[EnumUpgradeStorage.values().length];
        int index = 0;

        for (EnumUpgradeStorage upgrade : EnumUpgradeStorage.values())
            variants[index++] = '_' + upgrade.getName();

        return variants;
    }

    public ItemUpgradeStorage (String name) {
        setUnlocalizedName(name);
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    public String getUnlocalizedName (ItemStack itemStack) {
        return super.getUnlocalizedName() + "." + EnumUpgradeStorage.byMetadata(itemStack.getMetadata()).getUnlocalizedName();
    }

    @Override
    public int getMetadata (int damage) {
        return damage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        EnumUpgradeStorage upgrade = EnumUpgradeStorage.byMetadata(itemStack.getMetadata());
        if (upgrade != null) {
            int mult = StorageDrawers.config.getStorageUpgradeMultiplier(upgrade.getLevel());
            list.add(StatCollector.translateToLocalFormatted("storageDrawers.upgrade.description", mult));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item item, CreativeTabs creativeTabs, List list) {
        for (EnumUpgradeStorage upgrade : EnumUpgradeStorage.values())
            list.add(new ItemStack(item, 1, upgrade.getMetadata()));
    }
}
