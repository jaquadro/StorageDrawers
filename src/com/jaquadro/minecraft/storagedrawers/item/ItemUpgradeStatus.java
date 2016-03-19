package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemUpgradeStatus extends Item
{
    public String[] getResourceVariants () {
        String[] variants = new String[EnumUpgradeStatus.values().length];
        int index = 0;

        for (EnumUpgradeStatus upgrade : EnumUpgradeStatus.values())
            variants[index++] = '_' + upgrade.getName();

        return variants;
    }

    public ItemUpgradeStatus (String name) {
        setUnlocalizedName(name);
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    public String getUnlocalizedName (ItemStack itemStack) {
        return super.getUnlocalizedName() + "." + EnumUpgradeStatus.byMetadata(itemStack.getMetadata()).getUnlocalizedName();
    }

    @Override
    public int getMetadata (int damage) {
        return damage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List<String> list, boolean par4) {
        String name = getUnlocalizedName(itemStack);
        list.add(I18n.translateToLocal(name + ".description"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
        for (EnumUpgradeStatus upgrade : EnumUpgradeStatus.values())
            list.add(new ItemStack(item, 1, upgrade.getMetadata()));
    }
}
