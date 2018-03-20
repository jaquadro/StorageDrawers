package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class WorldGeneratorBonusChest extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 1; iblockstate = worldIn.getBlockState(position))
        {
            position = position.down();
        }

        if (position.getY() < 1)
        {
            return false;
        }
        else
        {
            position = position.up();

            for (int i = 0; i < 4; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(3) - rand.nextInt(3), rand.nextInt(4) - rand.nextInt(4));

                if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).isSideSolid(worldIn, blockpos.down(), net.minecraft.util.EnumFacing.UP))
                {
                    worldIn.setBlockState(blockpos, Blocks.CHEST.getDefaultState(), 2);
                    TileEntity tileentity = worldIn.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityChest)
                    {
                        ((TileEntityChest)tileentity).setLootTable(LootTableList.CHESTS_SPAWN_BONUS_CHEST, rand.nextLong());
                    }

                    BlockPos blockpos1 = blockpos.east();
                    BlockPos blockpos2 = blockpos.west();
                    BlockPos blockpos3 = blockpos.north();
                    BlockPos blockpos4 = blockpos.south();

                    if (worldIn.isAirBlock(blockpos2) && worldIn.getBlockState(blockpos2.down()).isSideSolid(worldIn, blockpos2.down(), net.minecraft.util.EnumFacing.UP))
                    {
                        worldIn.setBlockState(blockpos2, Blocks.TORCH.getDefaultState(), 2);
                    }

                    if (worldIn.isAirBlock(blockpos1) && worldIn.getBlockState(blockpos1.down()).isSideSolid(worldIn, blockpos1.down(), net.minecraft.util.EnumFacing.UP))
                    {
                        worldIn.setBlockState(blockpos1, Blocks.TORCH.getDefaultState(), 2);
                    }

                    if (worldIn.isAirBlock(blockpos3) && worldIn.getBlockState(blockpos3.down()).isSideSolid(worldIn, blockpos3.down(), net.minecraft.util.EnumFacing.UP))
                    {
                        worldIn.setBlockState(blockpos3, Blocks.TORCH.getDefaultState(), 2);
                    }

                    if (worldIn.isAirBlock(blockpos4) && worldIn.getBlockState(blockpos4.down()).isSideSolid(worldIn, blockpos4.down(), net.minecraft.util.EnumFacing.UP))
                    {
                        worldIn.setBlockState(blockpos4, Blocks.TORCH.getDefaultState(), 2);
                    }

                    return true;
                }
            }

            return false;
        }
    }
}