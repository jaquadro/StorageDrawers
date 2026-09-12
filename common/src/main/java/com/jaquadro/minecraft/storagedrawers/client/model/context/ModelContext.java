package com.jaquadro.minecraft.storagedrawers.client.model.context;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ModelContext
{
    private final BlockState state;
    private final RandomSource randomSource;

    public ModelContext () {
        this.state = Blocks.AIR.defaultBlockState();
        randomSource = null;
    }

    public ModelContext (BlockState state) {
        this.state = state;
        randomSource = null;
    }

    public ModelContext (BlockState state, RandomSource randomSource) {
        this.state = state;
        this.randomSource = randomSource;
    }

    public BlockState state () {
        return state;
    }

    public RandomSource randomSource () {
        return randomSource;
    }
}