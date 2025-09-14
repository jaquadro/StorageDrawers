package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.FramedModelProperties;
import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlatformBlockEntityControllerIO extends BlockEntityControllerIO
{
    public PlatformBlockEntityControllerIO (BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @NotNull
    @Override
    public ModelData getModelData () {
        return FramedModelProperties.getModelData(this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability (@NotNull Capability<T> cap, @Nullable Direction side) {
        if (!PlatformCapabilities.hasCapability(cap))
            return super.getCapability(cap, side);

        return LazyOptional.of(() -> PlatformCapabilities.getCapability(cap, this));
    }
}
