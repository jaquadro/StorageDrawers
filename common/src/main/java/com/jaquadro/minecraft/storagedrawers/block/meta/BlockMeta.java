package com.jaquadro.minecraft.storagedrawers.block.meta;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

public class BlockMeta extends Block
{
    public BlockMeta (Properties properties) {
        super(properties);
    }

    public static class Facing extends BlockMeta
    {
        public static final DirectionProperty FACING = BlockStateProperties.FACING;

        public Facing (Properties properties) {
            super(properties);
        }

        @Override
        protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(FACING);
        }

        @Override
        public @NotNull BlockState rotate (BlockState state, Rotation rotation) {
            return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
        }

        @Override
        public @NotNull BlockState mirror (BlockState state, Mirror mirror) {
            return state.rotate(mirror.getRotation(state.getValue(FACING)));
        }

        public static class Sized extends Facing {
            public static final BooleanProperty HALF = BooleanProperty.create("half");

            public Sized (Properties properties) {
                super(properties);
            }

            @Override
            protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(HALF);
            }
        }
    }
}
