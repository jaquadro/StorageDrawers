package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.chameleon.resources.IItemMeshMapper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemUpgradeRedstone extends ItemUpgrade implements IItemMeshMapper
{
    public ItemUpgradeRedstone (String registryName, String unlocalizedName) {
        super(registryName, unlocalizedName);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName (@Nonnull ItemStack itemStack) {
        return super.getUnlocalizedName() + "." + EnumUpgradeRedstone.byMetadata(itemStack.getMetadata()).getUnlocalizedName();
    }

    @Override
    public int getMetadata (int damage) {
        return damage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        if (func_194125_a(creativeTabs)) {
            for (EnumUpgradeRedstone upgrade : EnumUpgradeRedstone.values())
                list.add(new ItemStack(this, 1, upgrade.getMetadata()));
        }
    }

    @Override
    public List<Pair<ItemStack, ModelResourceLocation>> getMeshMappings () {
        List<Pair<ItemStack, ModelResourceLocation>> mappings = new ArrayList<Pair<ItemStack, ModelResourceLocation>>();

        for (EnumUpgradeRedstone type : EnumUpgradeRedstone.values()) {
            ModelResourceLocation location = new ModelResourceLocation(getRegistryName().toString() + '_' + type.getName(), "inventory");
            mappings.add(Pair.of(new ItemStack(this, 1, type.getMetadata()), location));
        }

        return mappings;
    }
}
