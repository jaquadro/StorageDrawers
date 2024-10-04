package com.jaquadro.minecraft.storagedrawers.client.model.context;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class FramedModelContext extends ModelContext
{
    private MaterialData materialData;

    public FramedModelContext (BlockState state, Direction direction, RandomSource randomSource, RenderType renderType) {
        super(state, direction, randomSource, renderType);
    }

    public FramedModelContext (BlockState state) {
        super(state);
    }

    public MaterialData materialData () {
        return materialData;
    }

    public FramedModelContext materialData (MaterialData materialData) {
        this.materialData = materialData;
        return this;
    }
}
