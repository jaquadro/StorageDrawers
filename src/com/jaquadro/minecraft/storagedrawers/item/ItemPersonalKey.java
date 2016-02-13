package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.collect.Multimap;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.integration.IntegrationRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemPersonalKey extends Item
{
    public static final String[] iconNames = new String[] { "default", "cofh" };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemPersonalKey (String name) {
        setUnlocalizedName(name);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHasSubtypes(true);
        setMaxDamage(0);
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

    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public Multimap getAttributeModifiers (ItemStack item) {
        Multimap multimap = super.getAttributeModifiers(item);
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)2, 0));
        return multimap;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));
        if (StorageDrawers.config.cache.enableThermalFoundationIntegration && IntegrationRegistry.instance().isModLoaded("ThermalFoundation"))
            list.add(new ItemStack(item, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister register) {
        icons = new IIcon[iconNames.length];

        for (int i = 0, n = iconNames.length; i < n; i++) {
            if (iconNames[i] != null)
                icons[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawer_key_personal_" + iconNames[i]);
        }
    }

    public String getSecurityProviderKey (int meta) {
        switch (meta) {
            case 0: return null;
            case 1: return "cofh";
            default: return null;
        }
    }
}
