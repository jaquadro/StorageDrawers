package com.jaquadro.minecraft.storagedrawers.block.meta;

import com.jaquadro.minecraft.storagedrawers.block.state.IntegerSetProperty;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public abstract class BlockMetaFacingSizedSlotted
{
    public static class Slots23 extends BlockMetaFacingSized {
        public static final IntegerSetProperty SLOTS = IntegerSetProperty.create("slots", "2,3");

        public Slots23 (BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING).add(HALF).add(SLOTS);
        }
    }

    public static class Slots124 extends BlockMetaFacingSized {
        public static final IntegerSetProperty SLOTS = IntegerSetProperty.create("slots", "1,2,4");

        public Slots124 (BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING).add(HALF).add(SLOTS);
        }
    }

    public static class Label extends BlockMetaFacingSized {
        public static final IntegerProperty SLOT = IntegerProperty.create("slot", 1, 6);

        public Label (BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING).add(HALF).add(SLOT);
        }
    }
}