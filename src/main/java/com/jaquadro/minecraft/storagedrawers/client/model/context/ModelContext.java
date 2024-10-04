package com.jaquadro.minecraft.storagedrawers.client.model.context;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ModelContext
{
    private final BlockState state;
    private final Direction direction;
    private final RandomSource randomSource;
    private final RenderType renderType;

    public ModelContext () {
        this.state = Blocks.AIR.defaultBlockState();
        direction = null;
        randomSource = null;
        renderType = null;
    }

    public ModelContext (BlockState state) {
        this.state = state;
        direction = null;
        randomSource = null;
        renderType = null;
    }

    public ModelContext (BlockState state, Direction direction, RandomSource randomSource, RenderType renderType) {
        this.state = state;
        this.direction = direction;
        this.randomSource = randomSource;
        this.renderType = renderType;
    }

    public BlockState state () {
        return state;
    }

    public Direction direction () {
        return direction;
    }

    public RandomSource randomSource () {
        return randomSource;
    }

    public RenderType renderType () {
        return renderType;
    }
}
