package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlockEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.FramedModelProperties;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class BlockEntityTrim extends BaseBlockEntity implements IFramedBlockEntity
{
    private MaterialData materialData = new MaterialData();

    protected BlockEntityTrim (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);

        injectPortableData(materialData);
    }

    public BlockEntityTrim (BlockPos pos, BlockState state) {
        this(ModBlockEntities.TRIM.get(), pos, state);
    }

    @Override
    public MaterialData material () {
        return materialData;
    }

    @Override
    public boolean dataPacketRequiresRenderUpdate () {
        return true;
    }

    @NotNull
    @Override
    public ModelData getModelData () {
        return FramedModelProperties.getModelData(this);
    }
}
