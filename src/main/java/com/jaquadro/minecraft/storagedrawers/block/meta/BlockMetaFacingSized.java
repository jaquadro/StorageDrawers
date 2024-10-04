package com.jaquadro.minecraft.storagedrawers.block.meta;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlockMetaFacingSized extends BlockMetaFacing
{
    public static final BooleanProperty HALF = BooleanProperty.create("half");

    public BlockMetaFacingSized (BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(HALF);
    }
}
