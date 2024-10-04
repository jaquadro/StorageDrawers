package com.jaquadro.minecraft.storagedrawers.client.model.context;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public interface ModelContextSupplier<C extends ModelContext>
{
    C makeContext(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType type);

    C makeContext(ItemStack stack);
}
