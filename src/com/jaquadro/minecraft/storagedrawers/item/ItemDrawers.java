package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
}
