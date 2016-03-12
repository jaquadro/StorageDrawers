package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemFramingTable extends ItemBlock
{
    public ItemFramingTable (Block block) {
        super(block);
        setMaxDamage(0);
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        metadata = 0;
        if (side == 0)
            return false;
        if (side == 1) {
            int quad = (MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
            if (quad == 0)
                side = 2;
            if (quad == 1)
                side = 5;
            if (quad == 2)
                side = 3;
            if (quad == 3)
                side = 4;
        }

        int xOff = 0;
        int zOff = 0;

        if (side == 2)
            xOff = 1;
        if (side == 3)
            xOff = -1;
        if (side == 4)
            zOff = -1;
        if (side == 5)
            zOff = 1;

        if (!world.isAirBlock(x, y, z) || !world.isAirBlock(x, y + 1, z))
            return false;
        if (!world.isAirBlock(x + xOff, y, z + zOff) || !world.isAirBlock(x + xOff, y + 1, z + zOff)) {
            if (side == 2 || side == 3)
                xOff *= -1;
            if (side == 4 || side == 5)
                zOff *= -1;
            if (!world.isAirBlock(x + xOff, y, z + zOff) || !world.isAirBlock(x + xOff, y + 1, z + zOff))
                return false;
            metadata = 8;
        }

        if (!world.setBlock(x, y, z, field_150939_a, metadata | side, 3))
            return false;
        if (!world.setBlock(x + xOff, y, z + zOff, field_150939_a, (8 - metadata) | side, 3)) {
            world.setBlockToAir(x, y, z);
            return false;
        }

        if (world.getBlock(x, y, z) == field_150939_a) {
            field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
            field_150939_a.onPostBlockPlaced(world, x, y, z, metadata | side);
        }

        return true;
    }
}
