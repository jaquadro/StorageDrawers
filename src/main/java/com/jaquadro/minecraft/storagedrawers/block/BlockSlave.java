package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSlave extends Block implements INetworked
{
    public BlockSlave (Block.Properties properties) {
        super(properties);
    }

    public void toggle (World world, BlockPos pos, PlayerEntity player, EnumKeyType keyType) {
        TileEntitySlave tile = getTileEntity(world, pos);
        if (tile == null)
            return;

        BlockPos controllerPos = tile.getControllerPos();
        if (controllerPos == null)
            return;

        Block block = world.getBlockState(controllerPos).getBlock();
        if (block instanceof BlockController) {
            BlockController controller = (BlockController)block;
            controller.toggle(world, controllerPos, player, keyType);
        }
    }

    @Override
    public boolean hasTileEntity (BlockState state) {
        return true;
    }

    @Override
    public TileEntitySlave createTileEntity (BlockState state, IBlockReader world) {
        return new TileEntitySlave();
    }

    public TileEntitySlave getTileEntity (IBlockReader blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        return (tile instanceof TileEntitySlave) ? (TileEntitySlave) tile : null;
    }

    public TileEntitySlave getTileEntitySafe (World world, BlockPos pos) {
        TileEntitySlave tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = createTileEntity(world.getBlockState(pos), world);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }
}
