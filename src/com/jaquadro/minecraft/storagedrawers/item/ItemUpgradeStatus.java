package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.chameleon.resources.IItemMeshMapper;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ItemUpgradeStatus extends Item implements IItemMeshMapper
{
    public ItemUpgradeStatus (String registryName, String unlocalizedName) {
        setRegistryName(StorageDrawers.MOD_ID, registryName);
        setUnlocalizedName(unlocalizedName);
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

    @Override
    public List<Pair<ItemStack, ModelResourceLocation>> getMeshMappings () {
        List<Pair<ItemStack, ModelResourceLocation>> mappings = new ArrayList<Pair<ItemStack, ModelResourceLocation>>();

        for (EnumUpgradeStatus type : EnumUpgradeStatus.values()) {
            ModelResourceLocation location = new ModelResourceLocation(getRegistryName().toString() + '_' + type.getName(), "inventory");
            mappings.add(Pair.of(new ItemStack(this, 1, type.getMetadata()), location));
        }

        return mappings;
    }
}
