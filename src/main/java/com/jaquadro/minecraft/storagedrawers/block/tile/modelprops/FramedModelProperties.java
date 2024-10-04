package com.jaquadro.minecraft.storagedrawers.block.tile.modelprops;

import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlockEntity;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedMaterials;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContextSupplier;
import com.jaquadro.minecraft.storagedrawers.client.model.context.FramedModelContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

public class FramedModelProperties implements ModelContextSupplier<FramedModelContext>
{
    public static final FramedModelProperties INSTANCE = new FramedModelProperties();

    public static final ModelProperty<IFramedMaterials> MATERIAL = new ModelProperty<>();

    public static ModelData getModelData (IFramedBlockEntity blockEntity) {
        return ModelData.builder()
            .with(MATERIAL, blockEntity.material()).build();
    }

    @Override
    public FramedModelContext makeContext (@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType type) {
        return new FramedModelContext(state, side, rand, type)
            .materialData(new MaterialData(extraData.get(MATERIAL)));
    }

    @Override
    public FramedModelContext makeContext (ItemStack stack) {
        MaterialData data = new MaterialData();
        data.read(stack.getOrCreateTag());

        Block block = Blocks.AIR;
        if (stack.getItem() instanceof BlockItem blockItem)
            block = blockItem.getBlock();

        return new FramedModelContext(block.defaultBlockState())
            .materialData(data);
    }
}
