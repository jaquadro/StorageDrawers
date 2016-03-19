package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.collect.Multimap;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemDrawerKey extends Item
{
    public ItemDrawerKey (String name) {
        setUnlocalizedName(name);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        String name = getUnlocalizedName(itemStack);
        list.add(I18n.translateToLocal(name + ".description"));
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D () {
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers (EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)2, 0));
        return multimap;
    }
}
