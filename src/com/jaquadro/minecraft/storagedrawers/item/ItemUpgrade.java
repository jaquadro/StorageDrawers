package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemUpgrade extends Item
{
    private boolean allowMultiple;

    public ItemUpgrade (String registryName, String unlocalizedName) {
        setRegistryName(registryName);
        setUnlocalizedName(unlocalizedName);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (@Nonnull ItemStack itemStack, @Nullable World world, List<String> list, ITooltipFlag advanced) {
        String name = getUnlocalizedName(itemStack);
        list.add(I18n.format(name + ".description"));
    }

    public void setAllowMultiple (boolean allow) {
        allowMultiple = allow;
    }

    public boolean getAllowMultiple () {
        return allowMultiple;
    }
}
