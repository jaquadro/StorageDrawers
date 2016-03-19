/*package com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class ItemUpgradeSorting extends Item
{
    public ItemUpgradeSorting (String name) {
        setUnlocalizedName(name);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setTextureName(StorageDrawers.MOD_ID + ":upgrade_sorting");
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        String name = getUnlocalizedName(itemStack);
        list.add(StatCollector.translateToLocalFormatted(name + ".description"));
    }

    @Override
    public boolean onItemUseFirst (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return false;

        if(world.getBlock(x, y, z) == ModBlocks.trim) {
            if(BlockSortingTrim.upgradeToSorting(world, x, y, z)) {
                stack.stackSize--;
                return true;
            }
        }

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null)
            return false;

        if (tile instanceof TileEntityDrawersStandard) {
            if (BlockSortingDrawers.upgradeToSorting(world, x, y, z)) {
                stack.stackSize--;
                return true;
            }
        } else if (tile instanceof TileEntityDrawersComp) {
            if (BlockSortingCompDrawers.upgradeToSorting(world, x, y, z)) {
                stack.stackSize--;
                return true;
            }
        }

        return false;
}
*/