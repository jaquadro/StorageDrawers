package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlatformBlockEntityDrawersStandard extends BlockEntityDrawersStandard
{
    public PlatformBlockEntityDrawersStandard (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public static class Slot1 extends BlockEntityDrawersStandard.Slot1 {
        public Slot1 (BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @NotNull
        @Override
        public ModelData getModelData () {
            return DrawerModelProperties.getModelData(this);
        }

        @Override
        protected void onAttributeChanged () {
            super.onAttributeChanged();
            requestModelDataUpdate();
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability (@NotNull Capability<T> cap, @Nullable Direction side) {
            return LazyOptional.of(() -> PlatformCapabilities.getCapability(cap, this));
        }
    }

    public static class Slot2 extends BlockEntityDrawersStandard.Slot2 {
        public Slot2 (BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @NotNull
        @Override
        public ModelData getModelData () {
            return DrawerModelProperties.getModelData(this);
        }

        @Override
        protected void onAttributeChanged () {
            super.onAttributeChanged();
            requestModelDataUpdate();
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability (@NotNull Capability<T> cap, @Nullable Direction side) {
            return LazyOptional.of(() -> PlatformCapabilities.getCapability(cap, this));
        }
    }

    public static class Slot4 extends BlockEntityDrawersStandard.Slot4 {
        public Slot4 (BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @NotNull
        @Override
        public ModelData getModelData () {
            return DrawerModelProperties.getModelData(this);
        }

        @Override
        protected void onAttributeChanged () {
            super.onAttributeChanged();
            requestModelDataUpdate();
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability (@NotNull Capability<T> cap, @Nullable Direction side) {
            return LazyOptional.of(() -> PlatformCapabilities.getCapability(cap, this));
        }
    }
}
