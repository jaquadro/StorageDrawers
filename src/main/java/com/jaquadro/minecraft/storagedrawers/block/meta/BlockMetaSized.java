package com.jaquadro.minecraft.storagedrawers.block.meta;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BlockMetaSized extends BlockMeta
{
    public static final MapCodec<BlockMeta> CODEC = simpleCodec(BlockMetaSized::new);

    public static final IntegerProperty SLOTS = IntegerProperty.create("slots", 1, 4);

    public BlockMetaSized (BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BlockMeta> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(HALF).add(SLOTS);
    }
}
