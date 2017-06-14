package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.chameleon.resources.IItemMeshMapper;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public void addInformation (@Nonnull ItemStack itemStack, @Nullable World world, List<String> list, ITooltipFlag advanced) {
        EnumUpgradeStorage upgrade = EnumUpgradeStorage.byMetadata(itemStack.getMetadata());
        if (upgrade != null) {
            int mult = StorageDrawers.config.getStorageUpgradeMultiplier(upgrade.getLevel());
            list.add(I18n.format("storagedrawers.upgrade.description", mult));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        if (func_194125_a(creativeTabs)) {
            for (EnumUpgradeStorage upgrade : EnumUpgradeStorage.values())
                list.add(new ItemStack(this, 1, upgrade.getMetadata()));
        }
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
