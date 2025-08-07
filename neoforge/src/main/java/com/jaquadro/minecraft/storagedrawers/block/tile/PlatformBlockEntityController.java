package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.FramedModelProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class PlatformBlockEntityController extends BlockEntityController
{
    public PlatformBlockEntityController (BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void onLoad () {
        super.onLoad();
        onEntityLoad();
    }

    @NotNull
    @Override
    public ModelData getModelData () {
        return FramedModelProperties.getModelData(this);
    }
}
