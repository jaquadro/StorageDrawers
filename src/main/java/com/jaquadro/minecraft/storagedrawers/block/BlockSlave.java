package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockSlave extends Block implements INetworked, EntityBlock
{
    public BlockSlave (BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockController getController(Level world, BlockPos pos) {
        BlockEntitySlave blockEntity = WorldUtils.getBlockEntity(world, pos, BlockEntitySlave.class);
        if (blockEntity == null)
            return null;

        BlockPos controllerPos = blockEntity.getControllerPos();
        if (controllerPos == null)
            return null;

        Block block = world.getBlockState(controllerPos).getBlock();
        if (block instanceof BlockController c)
            return c;

        return null;
    }

    @Override
    public BlockEntitySlave newBlockEntity (@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BlockEntitySlave(pos, state);
    }
}
