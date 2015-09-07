package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ISealable;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemTape extends Item
{
    public ItemTape (String name) {
        setUnlocalizedName(name);
        setMaxStackSize(1);
        setMaxDamage(8);
        setTextureName(StorageDrawers.MOD_ID + ":tape_roll");
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        String name = getUnlocalizedName(itemStack);
        list.add(StatCollector.translateToLocalFormatted(name + ".description"));
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!player.canPlayerEdit(x, y, z, side, stack))
            return false;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof ISealable) {
            ISealable tileseal = (ISealable) tile;
            if (tileseal.isSealed())
                return false;

            tileseal.setIsSealed(true);
            stack.damageItem(1, player);

            return true;
        }

        return false;
    }
}
