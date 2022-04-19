package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
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

    public void toggle (Level world, BlockPos pos, Player player, EnumKeyType keyType) {
        TileEntitySlave tileEntitySlave = WorldUtils.getBlockEntity(world, pos, TileEntitySlave.class);
        if (tileEntitySlave == null)
            return;

        BlockPos controllerPos = tileEntitySlave.getControllerPos();
        if (controllerPos == null)
            return;

        Block block = world.getBlockState(controllerPos).getBlock();
        if (block instanceof BlockController blockController) {
            blockController.toggle(world, controllerPos, player, keyType);
        }
    }

    @Override
    public TileEntitySlave newBlockEntity (@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySlave(pos, state);
    }
}
