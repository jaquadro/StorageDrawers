package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class ItemDrawers extends ItemMultiTexture
{
    public ItemDrawers (Block block) {
        super(block, block, BlockWood.field_150096_a);
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
            return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(x, y, z);
        if (tile != null) {
            if (side > 1)
                tile.setDirection(side);

            BlockDrawers block = (BlockDrawers) field_150939_a;
            tile.setDrawerCount(block.drawerCount);

            if (block.drawerCount == 2)
                tile.setDrawerCapacity(block.halfDepth ? 4 : 8);
            else if (block.drawerCount == 4)
                tile.setDrawerCapacity(block.halfDepth ? 2 : 4);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        ConfigManager config = StorageDrawers.config;

        int count = 0;
        if (block == ModBlocks.fullDrawers2)
            count = config.getBlockBaseStorage("fulldrawers2");
        else if (block == ModBlocks.fullDrawers4)
            count = config.getBlockBaseStorage("fulldrawers4");
        else if (block == ModBlocks.halfDrawers2)
            count = config.getBlockBaseStorage("halfdrawers2");
        else if (block == ModBlocks.halfDrawers4)
            count = config.getBlockBaseStorage("halfdrawers4");

        list.add(StatCollector.translateToLocalFormatted("storageDrawers.drawers.description", count));
    }
}
