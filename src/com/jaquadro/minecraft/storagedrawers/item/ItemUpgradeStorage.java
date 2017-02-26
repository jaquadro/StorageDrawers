package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.chameleon.resources.IItemMeshMapper;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemUpgradeStorage extends ItemUpgrade implements IItemMeshMapper
{
    public ItemUpgradeStorage (String registryName, String unlocalizedName) {
        super(registryName, unlocalizedName);
        setHasSubtypes(true);
        setAllowMultiple(true);
    }

    @Override
    public String getUnlocalizedName (@Nonnull ItemStack itemStack) {
        return super.getUnlocalizedName() + "." + EnumUpgradeStorage.byMetadata(itemStack.getMetadata()).getUnlocalizedName();
    }

    @Override
    public int getMetadata (int damage) {
        return damage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (@Nonnull ItemStack itemStack, EntityPlayer player, List<String> list, boolean par4) {
        EnumUpgradeStorage upgrade = EnumUpgradeStorage.byMetadata(itemStack.getMetadata());
        if (upgrade != null) {
            int mult = StorageDrawers.config.getStorageUpgradeMultiplier(upgrade.getLevel());
            list.add(I18n.format("storagedrawers.upgrade.description", mult));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item item, CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        for (EnumUpgradeStorage upgrade : EnumUpgradeStorage.values())
            list.add(new ItemStack(item, 1, upgrade.getMetadata()));
    }

    @Override
    public List<Pair<ItemStack, ModelResourceLocation>> getMeshMappings () {
        List<Pair<ItemStack, ModelResourceLocation>> mappings = new ArrayList<Pair<ItemStack, ModelResourceLocation>>();

        for (EnumUpgradeStorage type : EnumUpgradeStorage.values()) {
            ModelResourceLocation location = new ModelResourceLocation(getRegistryName().toString() + '_' + type.getName(), "inventory");
            mappings.add(Pair.of(new ItemStack(this, 1, type.getMetadata()), location));
        }

        return mappings;
    }
}
