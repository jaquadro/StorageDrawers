package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

public abstract class PlatformBlockEntityDrawersComp extends BlockEntityDrawersComp
{
    public static final ModelProperty<IDrawerAttributes> ATTRIBUTES = new ModelProperty<>();

    public PlatformBlockEntityDrawersComp(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public static class Slot3 extends BlockEntityDrawersComp.Slot3 {
        public Slot3 (BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @NotNull
        @Override
        public ModelData getModelData () {
            return ModelData.builder().with(ATTRIBUTES, drawerAttributes).build();
        }

        @Override
        protected void onAttributeChanged () {
            super.onAttributeChanged();
            requestModelDataUpdate();
        }
    }
}
