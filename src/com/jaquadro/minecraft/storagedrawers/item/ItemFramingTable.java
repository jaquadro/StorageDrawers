package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFramingTable extends ItemBlock
{
    public ItemFramingTable (Block block) {
        super(block);
        setMaxDamage(0);
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        newState = newState.withProperty(BlockFramingTable.RIGHT_SIDE, true);

        if (side == EnumFacing.DOWN)
            return false;
        if (side == EnumFacing.UP)
            side = EnumFacing.fromAngle(player.rotationYaw).getOpposite();

        newState = newState.withProperty(BlockFramingTable.FACING, side);

        int xOff = 0;
        int zOff = 0;

        if (side == EnumFacing.NORTH)
            xOff = 1;
        if (side == EnumFacing.SOUTH)
            xOff = -1;
        if (side == EnumFacing.WEST)
            zOff = -1;
        if (side == EnumFacing.EAST)
            zOff = 1;

        if (!world.isAirBlock(pos) || !world.isAirBlock(pos.add(0, 1, 0)))
            return false;
        if (!world.isAirBlock(pos.add(xOff, 0, zOff)) || !world.isAirBlock(pos.add(xOff, 1, zOff))) {
            if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH)
                xOff *= -1;
            if (side == EnumFacing.WEST || side == EnumFacing.EAST)
                zOff *= -1;
            if (!world.isAirBlock(pos.add(xOff, 0, zOff)) || !world.isAirBlock(pos.add(xOff, 1, zOff)))
                return false;

            newState = newState.withProperty(BlockFramingTable.RIGHT_SIDE, false);
        }

        if (!world.setBlockState(pos, newState, 3))
            return false;

        IBlockState altState = newState.withProperty(BlockFramingTable.RIGHT_SIDE, !newState.getValue(BlockFramingTable.RIGHT_SIDE));
        if (!world.setBlockState(pos.add(xOff, 0, zOff), altState, 3)) {
            world.setBlockToAir(pos);
            return false;
        }

        if (world.getBlockState(pos).getBlock() == block)
            block.onBlockPlacedBy(world, pos, newState, player, stack);

        return true;
    }
}
