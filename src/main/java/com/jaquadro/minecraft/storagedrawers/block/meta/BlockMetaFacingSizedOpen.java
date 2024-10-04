package com.jaquadro.minecraft.storagedrawers.block.meta;

import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BlockMetaFacingSizedOpen extends BlockMetaFacingSized
{
    public static final EnumProperty<EnumCompDrawer> SLOTS = EnumProperty.create("slots", EnumCompDrawer.class);

    public BlockMetaFacingSizedOpen (Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(HALF).add(SLOTS);
    }
}
