package com.jaquadro.minecraft.storagedrawers.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public abstract class PlatformBlockEntityDrawersComp extends BlockEntityDrawersComp
{
    public PlatformBlockEntityDrawersComp(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public static class Slot2 extends BlockEntityDrawersComp.Slot2 {
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
    }

    public static class Slot3 extends BlockEntityDrawersComp.Slot3 {
        public Slot3 (BlockPos pos, BlockState state) {
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
    }
}
