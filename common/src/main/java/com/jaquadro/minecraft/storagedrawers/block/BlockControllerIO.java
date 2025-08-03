package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedSourceBlock;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityControllerIO;
import com.jaquadro.minecraft.storagedrawers.block.tile.util.FrameHelper;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.texelsaurus.minecraft.chameleon.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockControllerIO extends Block implements INetworked, EntityBlock, IFramedSourceBlock
{
    public BlockControllerIO (Properties properties) {
        super(properties);
    }

    public BlockEntityController getController(Level world, BlockPos pos) {
        BlockEntityControllerIO blockEntity = com.jaquadro.minecraft.storagedrawers.util.WorldUtils.getBlockEntity(world, pos, BlockEntityControllerIO.class);
        if (blockEntity == null)
            return null;

        return blockEntity.getController();
    }

    @Override
    public BlockEntityControllerIO newBlockEntity (@NotNull BlockPos pos, @NotNull BlockState state) {
        return ModServices.RESOURCE_FACTORY.createBlockEntityControllerIO().create(pos, state);
    }

    @Override
    public ItemStack makeFramedItem (ItemStack source, ItemStack matSide, ItemStack matTrim, ItemStack matFront) {
        return FrameHelper.makeFramedItem(ModBlocks.FRAMED_CONTROLLER_IO.get(), source, matSide, matTrim, matFront);
    }
}
