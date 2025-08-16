package com.jaquadro.minecraft.storagedrawers.block.meta;

import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class BlockMetaFacingSizedOpen
{
    public static class Open2 extends BlockMetaFacingSized {
        public static final EnumProperty<EnumCompDrawer> SLOTS =
            EnumProperty.create("slots", EnumCompDrawer.class, EnumCompDrawer.OPEN1, EnumCompDrawer.OPEN2);

        public Open2 (Properties properties) {
            super(properties);
        }

        @Override
        protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING).add(HALF).add(SLOTS);
        }
    }

    public static class Open3 extends BlockMetaFacingSized {
        public static final EnumProperty<EnumCompDrawer> SLOTS =
            EnumProperty.create("slots", EnumCompDrawer.class, EnumCompDrawer.OPEN1, EnumCompDrawer.OPEN2, EnumCompDrawer.OPEN3);

        public Open3 (Properties properties) {
            super(properties);
        }

        @Override
        protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING).add(HALF).add(SLOTS);
        }
    }
}