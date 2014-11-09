package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemCompDrawers extends ItemBlock
{
    public ItemCompDrawers (Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
            return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(x, y, z);
        if (tile != null) {
            if (side > 1)
                tile.setDirection(side);

            int initCapacity = StorageDrawers.config.getBlockBaseStorage("compdrawers");
            tile.setDrawerCapacity(initCapacity);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        ConfigManager config = StorageDrawers.config;

        int count = config.getBlockBaseStorage("compdrawers");
        list.add(StatCollector.translateToLocalFormatted("storageDrawers.drawers.description", count));
    }
}
