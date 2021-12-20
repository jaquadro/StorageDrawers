package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockSlave extends Block implements INetworked, EntityBlock
{
    public BlockSlave (BlockBehaviour.Properties properties) {
        super(properties);
    }

    public void toggle (Level world, BlockPos pos, Player player, EnumKeyType keyType) {
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
    public TileEntitySlave newBlockEntity (BlockPos pos, BlockState state) {
        return new TileEntitySlave(pos, state);
    }

    public TileEntitySlave getTileEntity (BlockGetter blockAccess, BlockPos pos) {
        BlockEntity tile = blockAccess.getBlockEntity(pos);
        return (tile instanceof TileEntitySlave) ? (TileEntitySlave) tile : null;
    }

    public TileEntitySlave getTileEntitySafe (Level world, BlockPos pos) {
        TileEntitySlave tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = newBlockEntity(pos, world.getBlockState(pos));
            world.setBlockEntity(tile);
        }

        return tile;
    }
}
